package org.usfirst.frc.team4488.lib.app.math;

import org.usfirst.frc.team4488.lib.app.CSVWritable;
import org.usfirst.frc.team4488.lib.app.Interpolable;

public interface State<S> extends Interpolable<S>, CSVWritable {
  double distance(final S other);

  boolean equals(final Object other);

  String toString();

  String toCSV();
}
