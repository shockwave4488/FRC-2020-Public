package org.usfirst.frc.team4488.robot.systems.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Optional;
import org.usfirst.frc.team4488.lib.PreferencesParser;
import org.usfirst.frc.team4488.lib.app.control.Path;
import org.usfirst.frc.team4488.lib.app.control.PathFollower;
import org.usfirst.frc.team4488.lib.app.math.Rotation2d;
import org.usfirst.frc.team4488.lib.controlsystems.SimPID;
import org.usfirst.frc.team4488.lib.drive.DriveHelper;
import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.lib.loops.Loop;
import org.usfirst.frc.team4488.lib.loops.Looper;
import org.usfirst.frc.team4488.lib.operator.Controllers;
import org.usfirst.frc.team4488.lib.sensors.NavX;
import org.usfirst.frc.team4488.robot.Constants;
import org.usfirst.frc.team4488.robot.RobotMap;
import org.usfirst.frc.team4488.robot.RobotName;
import org.usfirst.frc.team4488.robot.app.WestCoastStateEstimator;
import org.usfirst.frc.team4488.robot.routines.defaults.DefaultRoutine;
import org.usfirst.frc.team4488.robot.routines.defaults.WestCoastRoutine;
import org.usfirst.frc.team4488.robot.systems.SmartPDP;

public class Drive extends WestCoastDrive {

  public enum DriveControlState {
    PathFollowing,
    PercentVbus,
    VelocitySetpoint;
  }

  private DriveControlState driveControlState;

  private Controllers xbox;

  private static final double RAMPRATE = 0.1; // Seconds from 0 to full power

  private WPI_TalonSRX m_left;
  private WPI_TalonSRX m_right;
  private WPI_TalonSRX rightSlave1;
  private WPI_TalonSRX rightSlave2;
  private WPI_TalonSRX leftSlave1;
  private WPI_TalonSRX leftSlave2;

  private DriveGear newGear;
  private DriveGear currentGear;

  private SimPID driveStraightFieldController;

  private boolean autoModeEnabled = false;

  private boolean prevAutoCycle;

  private double wheelDiameter;
  private double ticksPerRotation;

  private NavX rioNavx;

  private PathFollower mPathFollower;
  private Path mCurrentPath = null;
  private WestCoastStateEstimator stateEstimator = new WestCoastStateEstimator(this);

  private Logging logger;

  private SmartDrive smartDrive;
  private static final double speedDeadzone = 0.1;
  private static final double turnDeadzone = 0.1;
  private DriveHelper driveHelper = new DriveHelper(this, speedDeadzone, turnDeadzone);

  private static final double defaultDriveStraightP = 0.015;
  private static final double defaultDriveStraightI = 0;
  private static final double defaultDriveStraightD = 0;

  private Optional<Solenoid> shifter;

  private static final int gearShiftThresh = 90; // inch per sec

  /*
   * Main State Machine Loop for Drive.
   */
  private Loop mLoop =
      new Loop() {
        @Override
        public void onStart(double timestamp) {
          xbox = Controllers.getInstance();
          logger.writeToLogFormatted(Drive.this, "begin Loop.onStart");

          stop();
          breakModeAll();
          synchronized (Drive.this) {
          }
        }

        /**
         * Check if we want to change the gear, if the gear wants to be changed, it updates the
         * gears. TODO rest of it.
         */
        @Override
        public void onLoop(double timestamp) {
          synchronized (Drive.this) {
            if (newGear != currentGear) {
              gearShiftUpdate();
            }
          }
        }

        @Override
        public void onStop(double timestamp) {
          stop();
          unBreakModeAll();
          logger.writeToLogFormatted(Drive.this, "ending Loop.onStop");
        }
      };

  private static Drive sInstance = null;

  /** @return An instance of drive. */
  public static synchronized Drive getInstance() {
    if (sInstance == null) {
      sInstance = new Drive();
    }
    return sInstance;
  }

  public Drive() {
    logger = Logging.getInstance();
    logger.writeToLogFormatted(this, "constructor started");
    rioNavx = NavX.getInstance();

    PreferencesParser prefs = PreferencesParser.getInstance();
    wheelDiameter = prefs.getDouble("DriveWheelDiameter");
    ticksPerRotation = prefs.getDouble("DriveTicksPerRotation");

    m_left = new WPI_TalonSRX(RobotMap.DriveMotorLeftM);
    leftSlave1 = new WPI_TalonSRX(RobotMap.DriveMotorLeftF1);
    leftSlave2 = new WPI_TalonSRX(RobotMap.DriveMotorLeftF2);
    leftSlave1.follow(m_left);
    leftSlave2.follow(m_left);

    m_right = new WPI_TalonSRX(RobotMap.DriveMotorRightM);
    rightSlave1 = new WPI_TalonSRX(RobotMap.DriveMotorRightF1);
    rightSlave2 = new WPI_TalonSRX(RobotMap.DriveMotorRightF2);
    rightSlave1.follow(m_right);
    rightSlave2.follow(m_right);

    NeutralMode neutralModeToUse = NeutralMode.Brake;
    m_left.setNeutralMode(neutralModeToUse);
    leftSlave1.setNeutralMode(neutralModeToUse);
    leftSlave2.setNeutralMode(neutralModeToUse);
    m_right.setNeutralMode(neutralModeToUse);
    rightSlave1.setNeutralMode(neutralModeToUse);
    rightSlave2.setNeutralMode(neutralModeToUse);

    m_left.configOpenloopRamp(RAMPRATE, 0);
    leftSlave1.configOpenloopRamp(RAMPRATE, 0);
    leftSlave2.configOpenloopRamp(RAMPRATE, 0);
    m_right.configOpenloopRamp(RAMPRATE, 0);
    rightSlave1.configOpenloopRamp(RAMPRATE, 0);
    rightSlave2.configOpenloopRamp(RAMPRATE, 0);

    // inverts for test platform and practice bot are switched
    if (RobotMap.robotName == RobotName.Practice || RobotMap.robotName == RobotName.Competition) {
      m_left.setInverted(false);
      leftSlave1.setInverted(false);
      leftSlave2.setInverted(false);
      m_right.setInverted(true);
      rightSlave1.setInverted(true);
      rightSlave2.setInverted(true);

      m_right.setSensorPhase(true);
      m_left.setSensorPhase(true);
    } else {
      m_left.setInverted(false);
      leftSlave1.setInverted(false);
      leftSlave2.setInverted(false);
      m_right.setInverted(true);
      rightSlave1.setInverted(true);
      rightSlave2.setInverted(true);

      m_right.setSensorPhase(false);
      m_left.setSensorPhase(true);
    }

    m_left.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_100Ms, 0); // 254 uses 10 MS
    m_left.configVelocityMeasurementWindow(64, 0);
    m_right.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_100Ms, 0); // 254 uses 10 MS
    m_right.configVelocityMeasurementWindow(64, 0);

    // Set the feedback encoder type for the close-loop velocity control
    m_left.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
    m_right.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);

    reloadGains(); // update PID constants in talons

    if (RobotMap.hasShifters) {
      shifter = Optional.of(new Solenoid(RobotMap.PCM, RobotMap.DriveGearShiftSolenoid));
    } else {
      shifter = Optional.empty();
    }

    newGear = DriveGear.LowGear;
    currentGear = DriveGear.LowGear;
    driveControlState = DriveControlState.PathFollowing;

    configPercentVbus();

    resetEncoders();
    resetAngle();

    smartDrive = new SmartDrive(this);

    driveStraightFieldController = new SimPID();
    double p = prefs.tryGetDouble("DriveStraightFieldP", defaultDriveStraightP);
    double i = prefs.tryGetDouble("DriveStraightFieldI", defaultDriveStraightI);
    double d = prefs.tryGetDouble("DriveStraightFieldD", defaultDriveStraightD);
    driveStraightFieldController.setConstants(p, i, d);
  }

  @Override
  /**
   * Sets the CIM motors on the West Coast Drive train to the leftPower and rightPower. Essential
   * for the TankDrive interface
   */
  public void setPowers(double leftPower, double rightPower) {
    if (driveControlState != DriveControlState.PercentVbus) {
      configPercentVbus();
    }
    m_left.set(ControlMode.PercentOutput, leftPower);
    m_right.set(ControlMode.PercentOutput, rightPower * Constants.driftCorrection);
  }

  /** @return Angle of robot. */
  public double getAngle() {
    return rioNavx.getAHRS().getYaw();
  }

  public void setGyroAngle(Rotation2d angle) {
    rioNavx.reset();
    rioNavx.setAngleAdjustment(angle);
  }

  /*
   * Returns a Rotation2d object that contains the sin and cos of the current
   * robot angle. Used by RobotState to determine current robot angle
   */
  public Rotation2d getAngleRotation2d() {
    return Rotation2d.fromDegrees(-1 * getAngle());
  }

  public void resetAngle() {
    rioNavx.zeroYaw();
  }

  /** @return Gyroscope. */
  public AHRS getGyroscope() {
    return rioNavx.getAHRS();
  }

  /** @return Left distance traveled in inches. */
  public double getLeftDistance() {
    return (m_left.getSelectedSensorPosition(0)) * wheelDiameter * Math.PI / ticksPerRotation;
  }

  /** @return Right distance traveled in inches. */
  public double getRightDistance() {
    return (m_right.getSelectedSensorPosition(0))
        * wheelDiameter
        * Math.PI
        / ticksPerRotation; // In
    // inches
  }

  /** @return Left Speed in inches per second. */
  public double getLeftSpeed() {
    return ticksPer100MsToInchesPerSecond(m_left.getSelectedSensorVelocity(0));
  }

  /** @return Right speed in inches per second. */
  public double getRightSpeed() {
    return ticksPer100MsToInchesPerSecond(m_right.getSelectedSensorVelocity(0));
  }

  /** @return Linear distance in inches. */
  public double getLinearDistance() {
    return (getLeftDistance() + getRightDistance()) / 2;
  }

  /** @return Linear speed in inches per second. */
  public double getLinearSpeed() {
    double leftSpeed = getLeftSpeed();
    double rightSpeed = getRightSpeed();
    double linearSpeed = (leftSpeed + rightSpeed) / 2;
    return linearSpeed;
  }

  public void resetEncoders() {
    m_left.getSensorCollection().setQuadraturePosition(0, 0);
    m_right.getSensorCollection().setQuadraturePosition(0, 0);
  }

  public void breakModeAll() {
    m_left.setNeutralMode(NeutralMode.Brake);
    leftSlave1.setNeutralMode(NeutralMode.Brake);
    leftSlave2.setNeutralMode(NeutralMode.Brake);
    m_right.setNeutralMode(NeutralMode.Brake);
    rightSlave1.setNeutralMode(NeutralMode.Brake);
    rightSlave2.setNeutralMode(NeutralMode.Brake);
  }

  public void unBreakModeAll() {
    m_left.setNeutralMode(NeutralMode.Coast);
    leftSlave1.setNeutralMode(NeutralMode.Coast);
    leftSlave2.setNeutralMode(NeutralMode.Coast);
    m_right.setNeutralMode(NeutralMode.Coast);
    rightSlave1.setNeutralMode(NeutralMode.Coast);
    rightSlave2.setNeutralMode(NeutralMode.Coast);
  }

  private void gearShiftUpdate() {
    if (newGear == DriveGear.HighGear) {
      shifter.ifPresent(solenoid -> solenoid.set(true));
      currentGear = DriveGear.HighGear;
    } else {
      shifter.ifPresent(solenoid -> solenoid.set(false));
      currentGear = DriveGear.LowGear;
    }
  }

  /**
   * sets buttons to use for manually and automatically shifting drive gear
   *
   * @param manual button to use for manually setting the gear
   * @param auto button to use for automatically setting the gear
   */
  public void setDriveGears(boolean toggleManualMode, boolean toggleAutoMode) {
    if (toggleAutoMode && !prevAutoCycle) {
      autoModeEnabled = !autoModeEnabled;
    }

    if (autoModeEnabled) {
      if (Math.abs(getLinearSpeed()) > gearShiftThresh) {
        newGear = DriveGear.HighGear;
      } else {
        newGear = DriveGear.LowGear;
      }
    } else if (toggleManualMode) {
      newGear = DriveGear.HighGear;
    } else {
      newGear = DriveGear.LowGear;
    }
    prevAutoCycle = toggleAutoMode;
  }

  /** @return newGear as GearState. */
  public DriveGear gearState() {
    return newGear;
  }

  @Override
  public void updateSmartDashboard() {

    /*
     * double left_speed = getLeftSpeed(); double right_speed = getRightSpeed();
     * SmartDashboard.putNumber("DriveL Speed", left_speed);
     * SmartDashboard.putNumber("DriveR Speed", right_speed);
     * SmartDashboard.putNumber("Drive Speed", getLinearSpeed());
     * SmartDashboard.putNumber("DriveL Pos", getLeftDistance());
     * SmartDashboard.putNumber("DriveR Pos", getRightDistance())
     * SmartDashboard.putNumber("Drive Pos", getLinearDistance());
     * SmartDashboard.putNumber("Drive Angle", getAngle());
     * SmartDashboard.putNumber("NavX YAW", m_navx.getAHRS().getYaw());
     * SmartDashboard.putNumber("Raw Ticks", m_left.getSelectedSensorPosition(0));
     */

    SmartDashboard.putNumber("Rio Yaw", rioNavx.getAHRS().getYaw());

    synchronized (this) {
      if (usesTalonVelocityControl(DriveControlState.PathFollowing) && mPathFollower != null) {
        /*
         * Cross Track Error is the distance from the tip of the look ahead to the
         * nearest point on the path If things are going smoothly this will be very
         * small
         */
        // SmartDashboard.putNumber("drive CTE", mPathFollower.getCrossTrackError());
        // SmartDashboard.putNumber("drive ATE", mPathFollower.getAlongTrackError());
      } else {
        // SmartDashboard.putNumber("drive CTE", 0.0);
        // SmartDashboard.putNumber("drive ATE", 0.0);
      }
    }
  }

  @Override
  public void stop() {}

  @Override
  public void zeroSensors() {
    resetEncoders();
  }

  @Override
  public void registerEnabledLoops(Looper enabledLooper) {
    enabledLooper.register(mLoop);
  }

  @Override
  public void updatePrefs() {
    smartDrive.updatePrefs();
  }

  /*
   * Sets both the left and right motor velocities when the robot is in velocity
   * control mode. This is used during an APP path following operation to
   * continually set the motor speeds.
   */
  public synchronized void updateVelocitySetpoint(
      double left_inches_per_sec, double right_inches_per_sec) {
    if (usesTalonVelocityControl(driveControlState)) {
      final double max_desired =
          Math.max(Math.abs(left_inches_per_sec), Math.abs(right_inches_per_sec));
      final double scale =
          max_desired > Constants.kDriveHighGearMaxSetpoint
              ? Constants.kDriveHighGearMaxSetpoint / max_desired
              : 1.0;
      double leftVelocityTicksPer100MS =
          inchesPerSecondToTicksPer100MS(left_inches_per_sec * scale);
      double rightVelocityTicksPer100MS =
          inchesPerSecondToTicksPer100MS(right_inches_per_sec * scale);
      m_left.set(ControlMode.Velocity, leftVelocityTicksPer100MS);
      m_right.set(ControlMode.Velocity, rightVelocityTicksPer100MS);
    } else {
      m_left.set(0);
      m_right.set(0);
    }
  }

  /**
   * @return True if we are in velcoity control mode (mode used during an APP path following
   *     operation)
   */
  protected static boolean usesTalonVelocityControl(DriveControlState state) {
    if (state == DriveControlState.VelocitySetpoint || state == DriveControlState.PathFollowing) {
      return true;
    }
    return false;
  }

  private double ticksPer100MsToInchesPerSecond(double ticksPer100Ms) {
    double inchesPerSecond;
    inchesPerSecond = (ticksPer100Ms / ticksPerRotation) * wheelDiameter * Math.PI * 10; // Gives
    return inchesPerSecond;
  }

  private double inchesPerSecondToTicksPer100MS(double inches_per_second) {
    return (inches_per_second / (wheelDiameter * Math.PI) * ticksPerRotation) / 10;
  }

  /**
   * @ Used to switch our motor controllers into velocity control mode (called before we begin
   * following a path in APP). In velocity mode the Talons use a PID with feedforward gain to
   * maintain a specified velocity. Gains are set in the Drive constructor, velocities are updated
   * in updateVelocitySetpoints()
   */
  public void configureTalonsForSpeedControl() {
    logger.writeToLogFormatted(this, "start of configureTalonsForSpeedControl");
    if (!usesTalonVelocityControl(driveControlState)) {
      // We entered a velocity control state.
      m_left.set(ControlMode.Velocity, 0);
      m_left.configNominalOutputReverse(0, 0);
      m_left.configNominalOutputForward(0, 0);
      m_left.configPeakOutputReverse(-1, 0);
      m_left.configPeakOutputForward(1, 0);
      m_left.selectProfileSlot(0, 0);

      m_right.set(ControlMode.Velocity, 0);
      m_right.configNominalOutputReverse(0, 0);
      m_right.configNominalOutputForward(0, 0);
      m_right.configPeakOutputReverse(-1, 0);
      m_right.configPeakOutputForward(1, 0);
      m_right.selectProfileSlot(0, 0);
      breakModeAll();
      reloadGains();
    }
  }

  public synchronized void setVelocitySetpoint(
      double left_inches_per_sec, double right_inches_per_sec) {
    configureTalonsForSpeedControl();
    driveControlState = DriveControlState.VelocitySetpoint;
    updateVelocitySetpoint(left_inches_per_sec, right_inches_per_sec);
  }

  public double[] calcPowers(double throttle, double turn) {
    double rightPower = 0;
    if (turn < -0.1) {
      rightPower = 0.9 * turn - 0.1;
    } else if (turn > 0.1) {
      rightPower = 0.9 * turn + 0.1;
    }
    double leftPower = Math.pow(throttle, 2) * (throttle < 0 ? -1 : 1);
    return new double[] {leftPower, rightPower};
  }

  public void configPercentVbus() {
    driveControlState = DriveControlState.PercentVbus;
  }

  public void configForPathFollowing() {
    driveControlState = DriveControlState.PathFollowing;
    configureTalonsForSpeedControl();
    setVelocitySetpoint(0, 0);
    reloadGains();
  }

  /*
   * initializes the PID and feed forward gain for the Talons when they are used
   * in velocity control mode.
   */
  public synchronized void reloadGains() {
    PreferencesParser prefs = PreferencesParser.getInstance();
    double p = prefs.tryGetDouble("DriveVelocityP", 0);
    double i = prefs.tryGetDouble("DriveVelocityI", 0);
    double d = prefs.tryGetDouble("DriveVelocityD", 0);
    double f = prefs.tryGetDouble("DriveVelocityF", 0);
    int iZone = (int) prefs.tryGetDouble("DriveVelocityIZone", 0);
    double ramp = prefs.tryGetDouble("DriveVelocityRamp", 0);

    m_left.config_kP(0, p, 0);
    m_left.config_kI(0, i, 0);
    m_left.config_kD(0, d, 0);
    m_left.config_kF(0, f, 0);
    m_left.config_IntegralZone(0, iZone, 0);
    m_left.configClosedloopRamp(ramp, 0);

    m_right.config_kP(0, p, 0);
    m_right.config_kI(0, i, 0);
    m_right.config_kD(0, d, 0);
    m_right.config_kF(0, f, 0);
    m_right.config_IntegralZone(0, iZone, 0);
    m_right.configClosedloopRamp(ramp, 0);
  }

  @Override
  public void writeToLog() {}

  @Override
  public void reset() {}

  @Override
  public void setUpTrackables() {}

  public void driveStraightField(double throttle) {
    double mod = 0;

    driveStraightFieldController.setDesiredValue(0);
    if (getAngle() > -90 && getAngle() < 90) {
      mod = driveStraightFieldController.calcPID(getAngle());
    } else {
      double closestGoal = -180;
      if (getAngle() > 0) closestGoal = 180;
      driveStraightFieldController.setDesiredValue(closestGoal);
      mod = driveStraightFieldController.calcPID(getAngle());
    }

    setPowers(throttle + mod, throttle - mod);
  }

  public void driveToAngle(double throttle, double angle) {
    double p = 0.008;
    double mod = (angle - getAngle()) * p;
    setPowers(throttle + mod, throttle - mod);
  }

  @Override
  public WestCoastStateEstimator getStateEstimator() {
    return stateEstimator;
  }

  @Override
  public void controllerUpdate(double leftStickX, double leftStickY, double rightStickX) {
    setDriveGears(xbox.getLeftBumper(xbox.m_primary), xbox.getRightBumper(xbox.m_primary));
    double[] powers = calcPowers(leftStickY, rightStickX);
    driveHelper.Drive(powers[0], powers[1]);
  }

  public DefaultRoutine getDefaultRoutine() {
    return new WestCoastRoutine();
  }

  public double getCurrentDraw() {
    double currentDraw = 0;
    for (int i : RobotMap.DrivePDPPorts) {
      currentDraw += SmartPDP.getInstance().getCurrent(i);
    }
    return currentDraw;
  }
}
