package org.usfirst.frc.team4488.robot.systems.drive;

import org.usfirst.frc.team4488.lib.app.math.Rotation2d;
import org.usfirst.frc.team4488.lib.drive.interfaces.TankDrive;
import org.usfirst.frc.team4488.robot.app.WestCoastStateEstimator;

public abstract class WestCoastDrive extends DriveBase implements TankDrive {

  public abstract void setPowers(double leftPower, double rightPower);

  public abstract void updateVelocitySetpoint(
      double left_inches_per_second, double right_inches_per_second);

  public abstract void configForPathFollowing();

  public abstract double getLeftDistance();

  public abstract double getRightDistance();

  public double getLinearDistance() {
    return (getLeftDistance() + getRightDistance()) / 2;
  }

  public abstract double getLeftSpeed();

  public abstract double getRightSpeed();

  public double getLinearSpeed() {
    return (getLeftSpeed() + getRightSpeed()) / 2;
  }

  public abstract Rotation2d getAngleRotation2d();

  public abstract WestCoastStateEstimator getStateEstimator();

  public abstract double getCurrentDraw();
}
