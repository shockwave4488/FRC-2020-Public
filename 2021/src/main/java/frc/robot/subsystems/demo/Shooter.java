package frc.robot.subsystems.demo;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import edu.wpi.first.wpilibj.SlewRateLimiter;
import frc.lib.wpiextensions.ShockwaveSubsystemBase;
import frc.robot.Constants.ShooterConstants;

/**
 * This class represents the Shooter Subsystem.
 *
 * <p>Assumptions: this class assumes that there are two motors dedicated to shooting
 *
 * <p>Controls: Controller's analog trigger/button are used to trigger commands
 *
 * <p>Commands: DynamicShoot: would ramp up the shooter's speed to one of two values depending on
 * analog feedback from trigger RevShooter: revs the shooter up, useful for keeping the shooter
 * prepped.
 */
public class Shooter extends ShockwaveSubsystemBase {

  private WPI_TalonFX master;
  private WPI_TalonFX follower;

  private double shooterSpeed;
  private SlewRateLimiter limiter;

  public Shooter() {
    master = new WPI_TalonFX(ShooterConstants.MASTER_PORT);
    follower = new WPI_TalonFX(ShooterConstants.FOLLOWER_PORT);

    limiter = new SlewRateLimiter(ShooterConstants.RAMP_RATE);

    follower.follow(master);
    shooterSpeed = 0;

    // baselines limiter to start at shooterSpeed, which is zero.
    limiter.calculate(shooterSpeed);
  }

  public void periodic() {
    // limits the maximum possible change in shooterSpeed to protect physical motors
    master.set(limiter.calculate(shooterSpeed));
  }

  @Override
  public void onStop() {
    master.set(0);
  }

  /** @param newSpeed - a number between 0 and 1, sets the power percentage of the motor */
  public void setSpeed(double newSpeed) {
    shooterSpeed = newSpeed;
  }

  /** @return the shooter speed of the motor, useful for basing logic around */
  public double getSpeed() {
    return shooterSpeed;
  }

  @Override
  public void onStart() {}

  @Override
  public void zeroSensors() {}

  @Override
  public void updateSmartDashboard() {}

  @Override
  public void setUpTrackables() {}
}
