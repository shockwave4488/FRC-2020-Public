package org.usfirst.frc.team4488.lib.controlsystems;

/** Describes a set point for a profiling system. */
public class Setpoint {
  public Setpoint() {}

  /** Location or set point */
  public double Point;
  /** Value at the set point */
  public double Value;

  /**
   * new SetPoint
   *
   * @param point
   * @param value
   */
  public Setpoint(double point, double value) {
    Point = point;
    Value = value;
  }
}
