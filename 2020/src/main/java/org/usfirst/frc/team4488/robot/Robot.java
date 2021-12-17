package org.usfirst.frc.team4488.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.lib.loops.Looper;
import org.usfirst.frc.team4488.lib.operator.Controllers;
import org.usfirst.frc.team4488.robot.autonomous.AutoModeSelector;
import org.usfirst.frc.team4488.robot.loops.RobotStateLoop;
import org.usfirst.frc.team4488.robot.systems.SubsystemManager;
import org.usfirst.frc.team4488.robot.systems.drive.DriveBase;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the IterativeRobot documentation. If you change the name of this class
 * or the package after creating this project, you must also update the manifest file in the
 * resource directory.
 */
public class Robot extends TimedRobot {

  private Looper looper;
  private Looper constantLooper;

  private Logging logger = Logging.getInstance();
  private SubsystemManager subsystemManager;
  private DriveBase drive;

  /** Run once as soon as code is loaded onto the RoboRio */
  @Override
  public void robotInit() {
    looper = new Looper();
    constantLooper = new Looper();

    RobotInfo info = RobotMap.robotSelector();
    drive = info.drive;
    subsystemManager = SubsystemManager.createInstance(info);
    Bindings.addBindings();
    subsystemManager.addRoutines(RobotMap.bindedRoutines);
    looper.register(RobotStateLoop.createInstance(drive.getStateEstimator()));

    AutoModeSelector.init();

    subsystemManager.registerEnabledLoops(looper);
    subsystemManager.zeroSensors();
    subsystemManager.setUpTrackables();

    /*
    logger.addStringTrackable(
        () -> Controllers.getInstance().getPrimaryControllerLogging(),
        "PrimaryContLog",
        20,
        "TimeStamp,A,B,X,Y,LBump,RBump,LTrig,RTrig,LStickPress,RStickPress,Start,Select,LStickX,LStickY,RStickX,RStickY,Dpad\n");
    logger.addStringTrackable(
        () -> Controllers.getInstance().getSecondaryControllerLogging(),
        "SecondaryContLog",
        20,
        "TimeStamp,A,B,X,Y,LBump,RBump,LTrig,RTrig,LStickPress,RStickPress,Start,Select,LStickX,LStickY,RStickX,RStickY,Dpad\n");
        */

    // Register anything for the constant looper here
    constantLooper.start();

    logger.createFiles();
  }

  /** This function is called periodically during all modes */
  @Override
  public void robotPeriodic() {
    subsystemManager.updateSmartDashboard();
  }

  /** Run once at the beginning of autonomous mode */
  @Override
  public void autonomousInit() {
    drive.resetAngle();
    drive.configPercentVbus();
    logger.initialize();
    logger.writeToLogFormatted(this, "Autonomous Init");

    looper.start();
    /*
     * if (mAutoModeExecuter != null) { mAutoModeExecuter.stop(); }
     */

    // mAutoModeExecuter = null;
    subsystemManager.zeroSensors();
    wait(100); // Encoder values and other sensor data is not valid until 250ms after they are
    // reset
    /*
     * mAutoModeExecuter = new AutoModeExecuter();
     * mAutoModeExecuter.setAutoMode(AutoModeSelector.getSelectedAutoMode());
     * mAutoModeExecuter.start();
     */

    subsystemManager.startAutoRoutine(AutoModeSelector.getSelectedAutoMode());
  }

  @Override
  public void teleopInit() {
    if (!logger.initialized()) logger.initialize();

    subsystemManager.endAutoRoutine();
    logger.writeToLogFormatted(this, "Teleop Init");

    drive.configPercentVbus();

    looper.start();
  }

  /** This function is called periodically during autonomous */
  @Override
  public void autonomousPeriodic() {
    subsystemManager.controllerUpdates();
    logger.update();
  }
  /** This function is called periodically during operator control */
  @Override
  public void teleopPeriodic() {
    Controllers xbox = Controllers.getInstance();

    if (xbox.getStart(xbox.m_primary)) {
      subsystemManager.reset();
    }

    subsystemManager.controllerUpdates();
    logger.update();
  }

  @Override
  public void testInit() {
    logger.writeToLogFormatted(this, "Test Init");
  }

  /** This function is called periodically during test mode */
  @Override
  public void testPeriodic() {}

  /** This function is called once as soon as the robot is disabled */
  @Override
  public void disabledInit() {
    logger.writeToLogFormatted(this, "Robot Disabled!");
    subsystemManager.endAutoRoutine();

    subsystemManager.updatePrefs();
    subsystemManager.reset();
    subsystemManager.stop();
    looper.stop();

    if (logger.initialized()) logger.flush();
  }

  @Override
  public void disabledPeriodic() {}

  public void addTrackables(Logging logger) {
    /*logger.addTrackable(
        () ->
            RobotStateLoop.getInstance()
                .getEstimator()
                .getLatestFieldToVehicle()
                .getValue()
                .getRotation()
                .getDegrees(),
        "PoseTheta",
        5);
    logger.addTrackable(
        () ->
            RobotStateLoop.getInstance()
                .getEstimator()
                .getLatestFieldToVehicle()
                .getValue()
                .getTranslation()
                .x(),
        "PoseX",
        5);
    logger.addTrackable(
        () ->
            RobotStateLoop.getInstance()
                .getEstimator()
                .getLatestFieldToVehicle()
                .getValue()
                .getTranslation()
                .y(),
        "PoseY",
        5);*/
  }

  private void wait(int milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
