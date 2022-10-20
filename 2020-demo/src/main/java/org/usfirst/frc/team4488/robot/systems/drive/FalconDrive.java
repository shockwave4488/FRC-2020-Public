package org.usfirst.frc.team4488.robot.systems.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.ArrayList;
import java.util.Optional;
import org.usfirst.frc.team4488.lib.PreferencesParser;
import org.usfirst.frc.team4488.lib.app.math.Rotation2d;
import org.usfirst.frc.team4488.lib.drive.DriveHelper;
import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.lib.logging.StringTrackable;
import org.usfirst.frc.team4488.lib.loops.Loop;
import org.usfirst.frc.team4488.lib.loops.Looper;
import org.usfirst.frc.team4488.lib.sensors.NavX;
import org.usfirst.frc.team4488.robot.Constants;
import org.usfirst.frc.team4488.robot.RobotMap;
import org.usfirst.frc.team4488.robot.app.WestCoastStateEstimator;
import org.usfirst.frc.team4488.robot.routines.defaults.DefaultRoutine;
import org.usfirst.frc.team4488.robot.routines.defaults.FalconDriveRoutine;
import org.usfirst.frc.team4488.robot.systems.SmartPDP;

public class FalconDrive extends WestCoastDrive {

  private static FalconDrive inst = null;

  public static synchronized FalconDrive getInstance() {
    if (inst == null) inst = new FalconDrive();
    return inst;
  }

  private TalonFX leftMaster;
  private TalonFX leftFollower;
  private TalonFX rightMaster;
  private TalonFX rightFollower;

  private Optional<Solenoid> shifter;

  private FalconDriveRoutine defaultRoutine = new FalconDriveRoutine(this);

  private static final int gearShiftThresh = 90; // inch per sec

  private boolean autoShifterModeEnabled = false;

  private boolean prevAutoCycle;

  private DriveGear newGear;
  private DriveGear currentGear;

  private WestCoastStateEstimator stateEstimator = new WestCoastStateEstimator(this);
  private DriveHelper driveHelper = new DriveHelper(this, 0.1, 0.1);

  private final double wheelDiameter;
  private final double ticksPerRotation;
  private static final double rampRate = 0.5;

  private Loop loop =
      new Loop() {
        public void onStart(double timestamp) {
          updatePID();
          rightMaster.setNeutralMode(NeutralMode.Brake);
          leftMaster.setNeutralMode(NeutralMode.Brake);
          stop();
        }

        public void onLoop(double timestamp) {
          if (newGear != currentGear) {
            gearShiftUpdate();
          }
        }

        public void onStop(double timestamp) {
          rightMaster.setNeutralMode(NeutralMode.Coast);
          leftMaster.setNeutralMode(NeutralMode.Coast);
          stop();
        }
      };

  public FalconDrive() {
    leftMaster = new TalonFX(RobotMap.FalconDriveLeftM);
    leftFollower = new TalonFX(RobotMap.FalconDriveLeftF);
    rightMaster = new TalonFX(RobotMap.FalconDriveRightM);
    rightFollower = new TalonFX(RobotMap.FalconDriveRightF);

    if (RobotMap.hasShifters) {
      shifter =
          Optional.of(new Solenoid(PneumaticsModuleType.CTREPCM, RobotMap.DriveGearShiftSolenoid));
    } else {
      shifter = Optional.empty();
    }

    rightMaster.setInverted(true);
    rightFollower.setInverted(true);

    leftFollower.follow(leftMaster);
    rightFollower.follow(rightMaster);

    rightMaster.configOpenloopRamp(rampRate);
    leftMaster.configOpenloopRamp(rampRate);

    rightMaster.setNeutralMode(NeutralMode.Brake);
    leftMaster.setNeutralMode(NeutralMode.Brake);

    PreferencesParser prefs = PreferencesParser.getInstance();
    wheelDiameter = prefs.getDouble("DriveWheelDiameter");
    ticksPerRotation = prefs.getDouble("DriveTicksPerRotation");
  }

  public void stop() {
    setPowers(0, 0);
  }

  public WestCoastStateEstimator getStateEstimator() {
    return stateEstimator;
  }

  public void controllerUpdate(double leftStickX, double leftStickY, double rightStickX) {
    driveHelper.Drive(leftStickY, rightStickX);
  }

  public void configForPathFollowing() {
    configVelocity();
    updatePID();
  }

  public void configPercentVbus() {}

  public void configVelocity() {}

  public void setPowers(double leftPower, double rightPower) {
    leftMaster.set(ControlMode.PercentOutput, leftPower);
    rightMaster.set(ControlMode.PercentOutput, rightPower);
  }

  public synchronized void updateVelocitySetpoint(
      double left_inches_per_sec, double right_inches_per_sec) {
    final double max_desired =
        Math.max(Math.abs(left_inches_per_sec), Math.abs(right_inches_per_sec));
    final double scale =
        max_desired > Constants.kDriveHighGearMaxSetpoint
            ? Constants.kDriveHighGearMaxSetpoint / max_desired
            : 1.0;
    double leftVelocitySetpoint = inchesPerSecondToTicksPer100MS(left_inches_per_sec * scale);
    double rightVelocitySetpoint = inchesPerSecondToTicksPer100MS(right_inches_per_sec * scale);
    SmartDashboard.putNumber("left vel", leftVelocitySetpoint);
    SmartDashboard.putNumber("right vel", rightVelocitySetpoint);
    leftMaster.set(ControlMode.Velocity, leftVelocitySetpoint);
    rightMaster.set(ControlMode.Velocity, rightVelocitySetpoint);
  }

  private double inchesPerSecondToTicksPer100MS(double inches_per_second) {
    return (inches_per_second / (wheelDiameter * Math.PI) * ticksPerRotation) / 10;
  }

  private double ticksPer100MsToInchesPerSecond(double ticksPer100Ms) {
    return (ticksPer100Ms / ticksPerRotation) * wheelDiameter * Math.PI * 10;
  }

  public Rotation2d getAngleRotation2d() {
    return Rotation2d.fromDegrees(-1 * NavX.getInstance().getAHRS().getYaw());
  }

  public void updatePID() {
    PreferencesParser prefs = PreferencesParser.getInstance();
    double p = prefs.tryGetDouble("DriveVelocityP", 0);
    double i = prefs.tryGetDouble("DriveVelocityI", 0);
    double d = prefs.tryGetDouble("DriveVelocityD", 0);
    double f = prefs.tryGetDouble("DriveVelocityF", 0);
    int iZone = (int) prefs.tryGetDouble("DriveVelocityIZone", 0);
    double ramp = prefs.tryGetDouble("DriveVelocityRamp", 0);

    leftMaster.config_kP(0, p, 0);
    leftMaster.config_kI(0, i, 0);
    leftMaster.config_kD(0, d, 0);
    leftMaster.config_kF(0, f, 0);
    leftMaster.config_IntegralZone(0, iZone, 0);
    leftMaster.configClosedloopRamp(ramp, 0);

    leftFollower.configNeutralDeadband(0, 0);

    rightMaster.config_kP(0, p, 0);
    rightMaster.config_kI(0, i, 0);
    rightMaster.config_kD(0, d, 0);
    rightMaster.config_kF(0, f, 0);
    rightMaster.config_IntegralZone(0, iZone, 0);
    rightMaster.configClosedloopRamp(ramp, 0);

    rightFollower.configNeutralDeadband(0, 0);
  }

  public double getLeftDistance() {
    return (leftMaster.getSelectedSensorPosition(0)) * wheelDiameter * Math.PI / ticksPerRotation;
  }

  public double getRightDistance() {
    return (rightMaster.getSelectedSensorPosition(0)) * wheelDiameter * Math.PI / ticksPerRotation;
  }

  public double getLeftSpeed() {
    return ticksPer100MsToInchesPerSecond(leftMaster.getSelectedSensorVelocity(0));
  }

  public double getRightSpeed() {
    return ticksPer100MsToInchesPerSecond(rightMaster.getSelectedSensorVelocity(0));
  }

  /** @return Linear speed in inches per second. */
  public double getLinearSpeed() {
    return (getLeftSpeed() + getRightSpeed()) / 2;
  }

  public void writeToLog() {}

  public void updateSmartDashboard() {
    SmartDashboard.putNumber("gyro", getAngleRotation2d().getDegrees());
    SmartDashboard.putNumber("left speed", getLeftSpeed());
    SmartDashboard.putNumber("right speed", getRightSpeed());
    SmartDashboard.putNumber("left ticks", leftMaster.getSelectedSensorPosition(0));
    SmartDashboard.putNumber("right ticks", rightMaster.getSelectedSensorPosition(0));
  }

  public void zeroSensors() {
    leftMaster.getSensorCollection().setIntegratedSensorPosition(0, 0);
    rightMaster.getSensorCollection().setIntegratedSensorPosition(0, 0);
  }

  public void reset() {}

  @Override
  public void setUpTrackables() {
    Logging logger = Logging.getInstance();
    StringTrackable powerTrackable =
        () -> {
          double leftPower = Math.round(leftMaster.getMotorOutputPercent() * 100) / 100;
          double rightPower = Math.round(rightMaster.getMotorOutputPercent() * 100) / 100;
          String message = String.valueOf(leftPower);
          message += ", ";
          message += String.valueOf(rightPower);
          return message;
        };

    logger.addStringTrackable(powerTrackable, "DrivePowers", 10, "LeftPower, RightPower");
    logger.addStringTrackable(
        () -> currentGear == DriveGear.HighGear ? "high" : "low", "DriveGear", 10, "Gear Low/High");
  }

  public void updatePrefs() {}

  public void resetAngle() {
    NavX.getInstance().zeroYaw();
  }

  public void registerEnabledLoops(Looper enabledLoop) {
    enabledLoop.register(loop);
  }

  public DefaultRoutine getDefaultRoutine() {
    return defaultRoutine;
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

  public void setDriveGears(boolean toggleManualMode, boolean toggleAutoMode) {
    if (toggleAutoMode && !prevAutoCycle) {
      autoShifterModeEnabled = !autoShifterModeEnabled;
    }

    if (autoShifterModeEnabled) {
      if (Math.abs(getLinearSpeed()) > gearShiftThresh || toggleManualMode) {
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

  public ArrayList<TalonFX> getFXs() {
    ArrayList<TalonFX> list = new ArrayList<TalonFX>();
    list.add(leftMaster);
    list.add(leftFollower);
    list.add(rightMaster);
    list.add(rightFollower);
    return list;
  }

  public double getCurrentDraw() {
    double currentDraw = 0;
    for (int i : RobotMap.FalconDrivePDPPorts) {
      currentDraw += SmartPDP.getInstance().getCurrent(i);
    }
    return currentDraw;
  }
}
