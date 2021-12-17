package org.usfirst.frc.team4488.robot.systems;

import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.lib.loops.Loop;
import org.usfirst.frc.team4488.lib.loops.Looper;
import org.usfirst.frc.team4488.lib.sensors.Limelight;
import org.usfirst.frc.team4488.lib.sensors.Limelight.LedControl;
import org.usfirst.frc.team4488.robot.Constants;
import org.usfirst.frc.team4488.robot.RobotMap;
import org.usfirst.frc.team4488.robot.RobotName;

public class LimelightManager implements Subsystem {

  private static LimelightManager instance = null;

  public static synchronized LimelightManager getInstance() {
    if (instance == null) instance = new LimelightManager();
    return instance;
  }

  private Limelight frontLimelight;
  private Limelight backLimelight;

  private Loop loop =
      new Loop() {
        public void onStart(double timestamp) {
          frontLimelight.updateTables();
          frontLimelight.setLed(LedControl.ForceOff);
          backLimelight.updateTables();
          backLimelight.setLed(LedControl.ForceOff);
        }

        public void onLoop(double timestamp) {}

        public void onStop(double timestamp) {
          frontLimelight.setLed(LedControl.ForceOff);
          backLimelight.setLed(LedControl.ForceOff);
        }
      };

  private LimelightManager() {
    frontLimelight =
        new Limelight(
            RobotMap.FrontLimelightName,
            RobotMap.robotName == RobotName.Practice
                ? Constants.practiceFrontLimelightConstants
                : Constants.compFrontLimelightConstants,
            RobotMap.FrontLimelightLed);
    backLimelight = new Limelight(RobotMap.BackLimelightName, Constants.backLimelightInterpTable());
  }

  public Limelight getFrontLimelight() {
    return frontLimelight;
  }

  public Limelight getBackLimelight() {
    return backLimelight;
  }

  @Override
  public void writeToLog() {}

  @Override
  public void updateSmartDashboard() {
    frontLimelight.updateSmartDashboard();
    backLimelight.updateSmartDashboard();
  }

  @Override
  public void stop() {}

  @Override
  public void zeroSensors() {}

  @Override
  public void registerEnabledLoops(Looper enabledLooper) {
    enabledLooper.register(loop);
  }

  @Override
  public void updatePrefs() {}

  @Override
  public void reset() {}

  @Override
  public void setUpTrackables() {
    Logging logger = Logging.getInstance();
    logger.addTrackable(backLimelight::getEstimatedDistance, "BackLLEstimatedDistance", 10);
    logger.addTrackable(frontLimelight::getEstimatedDistance, "FrontLLEstimatedDistance", 10);
    logger.addTrackable(backLimelight::getX, "BackLLX", 10);
    logger.addTrackable(frontLimelight::getX, "FrontLLX", 10);
    logger.addTrackable(backLimelight::getY, "BackLLY", 10);
    logger.addTrackable(frontLimelight::getY, "FrontLLY", 10);
  }
}
