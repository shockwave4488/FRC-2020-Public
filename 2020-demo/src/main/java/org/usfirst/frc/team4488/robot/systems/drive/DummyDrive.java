package org.usfirst.frc.team4488.robot.systems.drive;

import org.usfirst.frc.team4488.lib.loops.Looper;
import org.usfirst.frc.team4488.lib.sensors.NavX;
import org.usfirst.frc.team4488.robot.app.DummyStateEstimator;
import org.usfirst.frc.team4488.robot.app.RobotStateEstimatorBase;

public class DummyDrive extends DriveBase {

  private DummyStateEstimator stateEstimator = new DummyStateEstimator();
  private static DummyDrive sInstance;

  public static synchronized DummyDrive getInstance() {
    if (sInstance == null) {
      sInstance = new DummyDrive();
    }

    return sInstance;
  }

  @Override
  public void configPercentVbus() {}

  @Override
  public void resetAngle() {
    NavX.getInstance().reset();
  }

  @Override
  public RobotStateEstimatorBase getStateEstimator() {
    return stateEstimator;
  }

  @Override
  public void controllerUpdate(double leftStickX, double leftStickY, double rightStickX) {}

  @Override
  public void reset() {}

  @Override
  public void zeroSensors() {}

  @Override
  public void updateSmartDashboard() {}

  @Override
  public void writeToLog() {}

  @Override
  public void stop() {}

  @Override
  public void updatePrefs() {}

  @Override
  public void registerEnabledLoops(Looper looper) {}

  @Override
  public void setUpTrackables() {}
}
