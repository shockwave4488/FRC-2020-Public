package org.usfirst.frc.team4488.robot.systems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.playingwithfusion.TimeOfFlight;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Map;
import org.usfirst.frc.team4488.lib.PreferencesParser;
import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.lib.loops.Loop;
import org.usfirst.frc.team4488.lib.loops.Looper;
import org.usfirst.frc.team4488.lib.operator.Tuner;
import org.usfirst.frc.team4488.robot.RobotMap;

public class Shooter implements Subsystem {

  private static Shooter instance;

  /**
   * Get the instance of the shooter
   *
   * @return the instance of the shooter
   */
  public static synchronized Shooter getInstance() {
    if (instance == null) instance = new Shooter();
    return instance;
  }

  private TimeOfFlight controlPanelTof;

  /** Motors of the subsystem */
  private WPI_TalonFX master;

  private WPI_TalonFX follower;

  /** Manager variables for the motors */
  private double shooterTargetSpeed = 0;

  /** Magic numbers */
  private static final double pulleyRatio = 1;

  private static final double encoderTicksPerRotation = 2048;
  private static final double DEFAULT_SPEED_TOLERANCE = 10;
  private static final double SPEED_LIMIT = 5500;
  private static final int DEFAULT_DONE_CYCLES = 5;
  private static final int RESTING_RPMS = 0;
  private static final double motorRampRateSec = 1;
  private static final double integralRPM = 200;
  private static final double TOF_WARNING_RANGE = 8;
  private double doneRange = DEFAULT_SPEED_TOLERANCE;
  private int doneCycles = 0;

  private Tuner tuner = null;

  private Loop loop =
      new Loop() {
        public void onStart(double timestamp) {
          off();
          master.set(ControlMode.Velocity, 0);
          SmartDashboard.putBoolean("crawl?", true);
        }

        public void onLoop(double timestamp) {
          /**
           * Subsystem level loop that runs continously - other classes set the the speed of the
           * motors
           */
          if (shooterTargetSpeed < rpmToTicksPer100ms(RESTING_RPMS))
            shooterTargetSpeed = rpmToTicksPer100ms(RESTING_RPMS);
          if (shooterTargetSpeed == 0) {
            master.set(ControlMode.PercentOutput, 0);
          } else {
            master.set(
                ControlMode.Velocity,
                shooterTargetSpeed
                    + rpmToTicksPer100ms(SmartDashboard.getNumber("Shooter Speed Offset", 0)));
          }
        }

        public void onStop(double timestamp) {
          off();
          master.set(ControlMode.Velocity, 0);
        }
      };

  private Shooter() {
    master = new WPI_TalonFX(RobotMap.ShooterMotor1);
    follower = new WPI_TalonFX(RobotMap.ShooterMotor2);

    master.configClosedloopRamp(motorRampRateSec);
    master.config_IntegralZone(0, (int) rpmToTicksPer100ms(integralRPM));
    follower.configClosedloopRamp(motorRampRateSec);
    follower.config_IntegralZone(0, (int) rpmToTicksPer100ms(integralRPM));

    master.setInverted(InvertType.InvertMotorOutput);
    follower.follow(master);
    follower.setInverted(InvertType.OpposeMaster);

    updatePrefs();

    controlPanelTof = new TimeOfFlight(RobotMap.ControlPanelTof);

    tuner = new Tuner(this::updateTuner, 1);

    tuner.addValueFromPrefs("ShooterP", 0);
    tuner.addValueFromPrefs("ShooterI", 0);
    tuner.addValueFromPrefs("ShooterD", 0);
    tuner.addValueFromPrefs("ShooterF", 0);

    tuner.start();

    SmartDashboard.putNumber("Shooter Speed Offset", 0);
  }

  private double ticksPer100msToRpm(double ticks) {
    return (ticks / encoderTicksPerRotation) * pulleyRatio * 600;
  }

  private double rpmToTicksPer100ms(double rpm) {
    return ((rpm / 600) / pulleyRatio) * encoderTicksPerRotation;
  }

  /**
   * Set the target speed of the flywheel
   *
   * @param speed speed in rpms
   */
  public void setSpeed(double speed) {
    shooterTargetSpeed = rpmToTicksPer100ms(Math.min(speed, SPEED_LIMIT));
  }

  /** Sets the target speed to 0 */
  public void off() {
    shooterTargetSpeed = 0;
  }

  /**
   * Get the target speed of the flywheel in rpms
   *
   * @return target speed of the flywheel in rpms
   */
  public double getTargetSpeed() {
    return ticksPer100msToRpm(shooterTargetSpeed);
  }

  /**
   * Get the actual current speed of the flywheel in rpms
   *
   * @return the current speed of the flywheel in rpms
   */
  public double getCurrentSpeed() {
    return ticksPer100msToRpm(master.getSelectedSensorVelocity());
  }

  /**
   * Checks if the flywheel is spinning at the target speed Note: The done cycles count is only
   * updated when this method is called, so it should only be called in a periodic context.
   *
   * @return whether current speed ~= target speed
   */
  public boolean onTarget() {
    return onTarget(DEFAULT_DONE_CYCLES);
  }

  public boolean onTarget(int reqDoneCycles) {
    if (Math.abs(getTargetSpeed() - getCurrentSpeed()) < doneRange) doneCycles++;
    else doneCycles = 0;
    return doneCycles >= reqDoneCycles;
  }

  /**
   * Checks if the flywheel speed is within [tolerance] rpms of the target speed Note: The done
   * cycles count is only updated when this method is called, so it should only be called in a
   * periodic context.
   *
   * @param tolerance the tolerance in rpms
   * @return whether current speed is within [tolerance] rpms of target speed
   */
  public boolean onTarget(double tolerance, int reqDoneCycles) {
    doneRange = tolerance;
    return onTarget(reqDoneCycles);
  }

  public void updateSmartDashboard() {
    SmartDashboard.putNumber("Shooter Target Speed", getTargetSpeed());
    SmartDashboard.putNumber("Shooter Current Speed", getCurrentSpeed());
    SmartDashboard.putBoolean("Shooter On Target", onTarget());
    SmartDashboard.putNumber("tof", controlPanelTof.getRange());
    SmartDashboard.putBoolean(
        "Control Panel TOF Warning",
        controlPanelTof.getRange() < Units.inchesToMeters(TOF_WARNING_RANGE) * 1000);
  }

  public void writeToLog() {}

  public void stop() {
    off();
  }

  public void zeroSensors() {}

  public void updatePrefs() {
    PreferencesParser prefs = PreferencesParser.getInstance();
    double p = prefs.tryGetDouble("ShooterP", 0);
    double i = prefs.tryGetDouble("ShooterI", 0);
    double d = prefs.tryGetDouble("ShooterD", 0);
    double f = prefs.tryGetDouble("ShooterF", 0);

    updatePIDF(p, i, d, f);
  }

  public void updateTuner(Map<String, Double> vals) {
    double p = vals.get("ShooterP");
    double i = vals.get("ShooterI");
    double d = vals.get("ShooterD");
    double f = vals.get("ShooterF");

    updatePIDF(p, i, d, f);
  }

  private synchronized void updatePIDF(double p, double i, double d, double f) {
    master.config_kF(0, f);
    master.config_kP(0, p);
    master.config_kI(0, i);
    master.config_kD(0, d);

    follower.config_kF(0, f);
    follower.config_kP(0, p);
    follower.config_kI(0, i);
    follower.config_kD(0, d);
  }

  public void reset() {
    off();
  }

  public void setUpTrackables() {
    Logging.getInstance().addTrackable(this::getCurrentSpeed, "ActualShooterRPM", 20);
    Logging.getInstance().addTrackable(this::getTargetSpeed, "TargetShooterRPM", 20);
    Logging.getInstance().addTrackable(() -> this.onTarget() ? 1 : 0, "ShooterOnTarget", 20);
  }

  public void registerEnabledLoops(Looper enabledLooper) {
    enabledLooper.register(loop);
  }
}
