package org.usfirst.frc.team4488.robot.systems;

import org.usfirst.frc.team4488.lib.loops.Looper;
import org.usfirst.frc.team4488.robot.routines.NoOpRoutine;
import org.usfirst.frc.team4488.robot.routines.defaults.DefaultRoutine;

/**
 * The Subsystem interface serves as a basic framework for all robot subsystems. Each subsystem
 * outputs commands to SmartDashboard, has a stop routine (for after each match), and a routine to
 * zero all sensors, which helps with calibration.
 *
 * <p>All Subsystems only have one instance (after all, one robot does not have two drivetrains),
 * and functions get the instance of the drivetrain and act accordingly. Subsystems are also a state
 * machine with a desired state and actual state; the robot code will try to match the two states
 * with actions. Each Subsystem also is responsible for instantializing all member components at the
 * start of the match.
 */
public interface Subsystem {

  public abstract void writeToLog();

  public abstract void updateSmartDashboard();

  public abstract void stop();

  /** If you need to reset/zero any sensors do it here */
  public abstract void zeroSensors();

  /**
   * If you have a loop in your class you need to register it here. Do the following command (inside
   * the method): enabledLooper.register(); In the parenthesis put the name of your loop.
   *
   * @param enabledLooper
   */
  public abstract void registerEnabledLoops(Looper enabledLooper);

  public abstract void updatePrefs();

  public abstract void reset();

  /** Add any trackables (for logging) in this method */
  public abstract void setUpTrackables();

  public default DefaultRoutine getDefaultRoutine() {
    return new NoOpRoutine(this);
  }
}
