package org.usfirst.frc.team4488.lib.app;

import java.util.List;

/** Contains basic functions that are used often. */
public class Util {
  /** Prevent this class from being instantiated. */
  private Util() {}

  public static final double kEpsilon = 1e-12;

  /** Limits the given input to the given magnitude. */
  public static double limit(double v, double maxMagnitude) {
    return limit(v, -maxMagnitude, maxMagnitude);
  }

  public static double limit(double v, double min, double max) {
    return Math.min(max, Math.max(min, v));
  }

  public static String joinStrings(String delim, List<?> strings) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < strings.size(); ++i) {
      sb.append(strings.get(i).toString());
      if (i < strings.size() - 1) {
        sb.append(delim);
      }
    }
    return sb.toString();
  }

  public static boolean epsilonEquals(double a, double b, double epsilon) {
    return (a - epsilon <= b) && (a + epsilon >= b);
  }

  public static boolean epsilonEquals(double a, double b) {
    return epsilonEquals(a, b, kEpsilon);
  }

  public static boolean allCloseTo(List<Double> list, double value, double epsilon) {
    boolean result = true;
    for (Double value_in : list) {
      result &= epsilonEquals(value_in, value, epsilon);
    }
    return result;
  }

  public static double interpolate(double a, double b, double x) {
    x = limit(x, 0.0, 1.0);
    return a + (b - a) * x;
  }

  public static double deadBand(double val, double deadband) {
    return (Math.abs(val) > Math.abs(deadband)) ? val : 0.0;
  }
}
