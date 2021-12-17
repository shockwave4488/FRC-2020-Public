import os
import plotly
import pandas
import argparse


class LogFileParser:

    """
    Utility to parse Shockwave robot subsystem logs. Will merge logs to a single CSV and plot the result.
    """

    # Constants used by this class
    STACK_FILE_NAME = "stack.csv"
    SPLIT_FILE_NAME = "split.csv"
    PLOT_FILE_NAME = "plot.html"
    MAIN_FILE_NAME = "main.txt"
    FILES_TO_EXCLUDE = [MAIN_FILE_NAME, STACK_FILE_NAME, PLOT_FILE_NAME, SPLIT_FILE_NAME]

    def __init__(self, directory, timescale):
        # type: (str, str) -> None
        self._directory = directory
        self._timescale = timescale

    def align_log_files(self, quiet=False):
        """
        Aligns all measurements to the same timescale and plots the results.
        :param quiet: If true, will not automatically open the resulting plot.
        :return: None
        """
        # type: bool -> None
        # Read in data from the log files
        raw_data = []
        for filename in os.listdir(os.path.normpath(self._directory)):
            if filename in self.FILES_TO_EXCLUDE:
                continue
            for line in open(os.path.normpath(os.path.join(self._directory, filename))).readlines():
                line_data = line.strip().split(" ")
                if len(line_data) != 2:
                    print("Warning: Corrupted data line detected in " + str(os.path.join(self._directory, filename))
                          + ". Skipping...")
                else:
                    try:
                        raw_data.append([float(line_data[0]), float(line_data[1]), filename.split(".")[0]])
                    except ValueError:
                        print("Warning: Corrupted data line detected in " + str(os.path.join(self._directory, filename))
                              + ". Skipping...")
        if len(raw_data) < 1:
            print("Empty log " + str(self._directory) + " detected. Skipping...")
            return

        # Convert list to data frame and convert timestamps to time deltas
        columns = ["time", "measurement", "tag"]
        df = pandas.DataFrame(raw_data, columns=columns)
        df['time'] = pandas.to_timedelta(df['time'], unit="s")

        # Split data into columns per subsystem measurement
        df = df.pivot(index="time", columns="tag", values="measurement")

        # Interpolate intermediate data points since frequency may be higher than some or all measurements
        processed = df.resample(self._timescale).mean().interpolate(method="linear", limit_area="inside")

        # Write final CSV and HTML output
        with open(os.path.normpath(os.path.join(self._directory,self.SPLIT_FILE_NAME)), "w") as output:
            output.write(processed.to_csv())
        plotly.offline.plot([{'x': processed.index, 'y': processed[col], 'name': col}
                             for col in processed.columns], filename=os.path.join(self._directory, self.PLOT_FILE_NAME),
                            auto_open=not quiet)

    @staticmethod
    def create_arg_parser():
        """
        Create an argparse.ArgumentParse object with this utilities' required command line arguments.
        :return: argparse.ArgumentParse object
        """
        # type: () -> argparse.ArgumentParser
        parser = argparse.ArgumentParser(description="Utility to stack robot log files for analysis.")
        parser.add_argument("directory", help="Path to the log file directory")
        parser.add_argument("--timescale", default="20ms", help="Timescale desired for the plot (see pandas)")
        parser.add_argument("--quiet", action="store_true", default=False, help="Suppress graph display")
        parser.add_argument("--multiple", action="store_true", default=False,
                            help="directory points to directory of directories")
        return parser

    @classmethod
    def main(cls):
        """
        Parse robot logs and generate the result.
        :return: None
        """
        parser = cls.create_arg_parser()
        args = parser.parse_args()
        if not args.multiple:
            lfp = cls(args.directory, args.timescale)
            lfp.align_log_files(args.quiet)
        else:
            sub_folders = [f.path for f in os.scandir(os.path.normpath(args.directory)) if f.is_dir()]
            for folder in sub_folders:
                lfp = cls(folder, args.timescale)
                lfp.align_log_files(True)


if __name__ == "__main__":
    LogFileParser.main()
