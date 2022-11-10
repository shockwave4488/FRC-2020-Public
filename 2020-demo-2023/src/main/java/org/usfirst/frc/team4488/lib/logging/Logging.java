package org.usfirst.frc.team4488.lib.logging;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Logging {

  public static final String defaultPath = "/home/lvuser/logs";
  public static Logging instance;

  public String startPath;
  public String fullPath;
  private double initializedTimestamp;
  private boolean createdFiles;
  private BufferedWriter mainWriter;
  private ArrayList<Tracker> trackers = new ArrayList<Tracker>();
  private ArrayList<StringTracker> stringTrackers = new ArrayList<StringTracker>();

  public static synchronized Logging getInstance() {
    if (instance == null) {
      instance = new Logging(defaultPath);
    }

    return instance;
  }

  public static Logging getInstance(String path) {
    if (instance == null) {
      instance = new Logging(path);
    }

    return instance;
  }

  public boolean testLogging() {
    return instance != null;
  }

  private Logging(String startPath) {
    this.startPath = startPath;
  }

  public void createFiles() {
    String date = dateStamp();

    String matchType = DriverStation.getMatchType().name() + "_";
    String matchNumber = DriverStation.getMatchNumber() + "_";

    if (matchType.equals("None")) {
      matchType = "";
      matchNumber = "";
    }

    fullPath = startPath + "/" + date + "/" + (matchType + matchNumber) + timeStamp();

    File directory = new File(fullPath);
    if (!directory.exists()) {
      directory.mkdirs();
    }

    try {
      mainWriter = new BufferedWriter(new FileWriter(fullPath + "/main.txt"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (Tracker tracker : trackers) tracker.createFile(fullPath);
    for (StringTracker tracker : stringTrackers) tracker.createFile(fullPath);

    createdFiles = true;
  }

  /**
   * Initializing before creating files is expensive, ~0.8 seconds Reinitializing will recreate
   * files, so it is also expensive
   */
  public void initialize() {
    if (initialized()) {
      flush();
      closeWriters();
      createdFiles = false;
    }

    if (!createdFiles) {
      createFiles();
    }

    initializedTimestamp = Timer.getFPGATimestamp();
  }

  private void closeWriters() {
    try {
      mainWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (Tracker tracker : trackers) tracker.closeWriter();
    for (StringTracker tracker : stringTrackers) tracker.closeWriter();
  }

  public boolean initialized() {
    return mainWriter != null;
  }

  public void update() {
    for (int counter = 0; counter < trackers.size(); counter++) {
      trackers.get(counter).update(secondStamp());
    }
    for (int counter = 0; counter < stringTrackers.size(); counter++) {
      stringTrackers.get(counter).update(secondStamp());
    }
  }

  public void addTrackable(Trackable target, String name, int frequency) {
    Tracker newTracker = new Tracker(target, name, frequency);
    trackers.add(newTracker);
  }

  public void addStringTrackable(
      StringTrackable target, String name, int frequency, String header) {
    StringTracker newStringTracker = new StringTracker(target, name, frequency, header);
    stringTrackers.add(newStringTracker);
  }

  public void writeToLogFormatted(Object caller, String message) {
    String callerName;
    if (caller == null) {
      callerName = "null";
    } else if (caller instanceof String) {
      callerName = (String) caller;
    } else {
      callerName = caller.getClass().getSimpleName();
    }

    if (!initialized()) {
      System.out.println("Writing to main log before initializing it!");
      return;
    }

    try {
      mainWriter.write(secondStamp() + "\t" + callerName + "\t" + message + "\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Formatted version of logs that adheres to strategy programming standards. For use in mock build
   * week.
   *
   * @param callClass - the top level class that this function is being called from
   * @param routine - the current routine the robot is executing
   * @param system - the specific subsystem that is involved
   * @param message - the state
   */
  public void writeToLogFormatted(Object callClass, Object routine, Object system, Object message) {
    // Stores important information from object parameters
    if (callClass == null) {
      callClass = "null";
    } else if (!(callClass instanceof String)) {
      callClass = callClass.getClass().getSimpleName();
    }
    if (routine == null) {
      routine = "null";
    } else if (!(routine instanceof String)) {
      routine = routine.getClass().getSimpleName();
    }
    if (system == null) {
      system = "null";
    } else if (!(system instanceof String)) {
      system = system.getClass().getSimpleName();
    }
    if (message == null) {
      message = "null";
    } else if (!(system instanceof String)) {
      message = message.toString();
    }

    if (!initialized()) {
      System.out.println("Writing to main log before initializing it!");
      return;
    }

    try {
      mainWriter.write(
          secondStamp()
              + "\t"
              + callClass
              + "\t"
              + routine
              + "\t"
              + system
              + "\t"
              + message
              + "\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void writeRaw(String message) {
    if (!initialized()) {
      System.out.println("Writing to main log before initializing it!");
      return;
    }

    try {
      mainWriter.write(message + "\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void flush() {
    if (!initialized()) {
      System.out.println("Flushing logs before they are initialized!");
      return;
    }

    try {
      mainWriter.flush();
      for (Tracker tracker : trackers) {
        tracker.flush();
      }
      for (StringTracker stringTracker : stringTrackers) {
        stringTracker.flush();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String secondStamp() {
    double time = Timer.getFPGATimestamp() - initializedTimestamp;
    time = Math.round(time * 1000.0) / 1000.0;
    return Double.toString(time);
  }

  private String timeStamp() {
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("HH-mm-ss-SSSS");
    String dateString = sdf.format(date);
    dateString.replaceAll(":", "-");
    return dateString;
  }

  private String dateStamp() {
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String dateString = sdf.format(date);
    dateString.replaceAll(":", "-");
    return dateString;
  }
}
