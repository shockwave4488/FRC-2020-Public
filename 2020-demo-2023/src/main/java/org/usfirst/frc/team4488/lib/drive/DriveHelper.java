package org.usfirst.frc.team4488.lib.drive;

import org.usfirst.frc.team4488.lib.drive.interfaces.TankDrive;
import org.usfirst.frc.team4488.robot.Constants;

/**
 * Does all calculations for driving the robot split-arcade style. Contains a TankDrive object. Does
 * not handle shifting
 */
public class DriveHelper {

  private TankDrive drive;
  private double m_speedDeadZone = 0.02;
  private double m_turnDeadZone = 0.02;

  public DriveHelper(TankDrive drive, double speedDeadzone, double turnDeadzone) {
    this.drive = drive;
    m_speedDeadZone = speedDeadzone;
    m_turnDeadZone = turnDeadzone;
  }

  public TankDrive getDrive() {
    return drive;
  }

  public void Drive(double throttle, double turn) {
    turn = handleDeadband(turn, m_turnDeadZone);
    throttle = handleDeadband(throttle, m_speedDeadZone);

    double leftPwm, rightPwm;
    double angularPower;
    double linearPower;

    linearPower = throttle;
    angularPower = Math.signum(turn) * (1 - Math.cos(turn * Math.PI / 2));
    angularPower *= Constants.turnPowerPercentage / 100;
    rightPwm = leftPwm = linearPower;
    leftPwm += angularPower;
    rightPwm -= angularPower;

    if (leftPwm > 1.0) {
      leftPwm = 1.0;
    } else if (leftPwm < -1.0) {
      leftPwm = -1.0;
    }
    if (rightPwm > 1.0) {
      rightPwm = 1.0;
    } else if (rightPwm < -1.0) {
      rightPwm = -1.0;
    }

    drive.setPowers(leftPwm, rightPwm);
  }

  private static double handleDeadband(double val, double deadband) {
    if (Math.abs(val) > Math.abs(deadband)) {
      if (val > 0) {
        return (val - deadband) / (1 - deadband);
      } else {
        return (val + deadband) / (1 - deadband);
      }
    } else {
      return 0.0;
    }
  }
}
