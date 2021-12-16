package org.usfirst.frc.team4488.robot.routines;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team4488.robot.systems.drive.SwerveDrive;

public class WasdSwerve extends Routine {

  public void start() {}

  public void update() {
    if (!SmartDashboard.getBoolean("keyboardDriveRunning", false)) return;

    boolean w = SmartDashboard.getBoolean("wPressed", false);
    boolean a = SmartDashboard.getBoolean("aPressed", false);
    boolean s = SmartDashboard.getBoolean("sPressed", false);
    boolean d = SmartDashboard.getBoolean("dPressed", false);
    boolean left = SmartDashboard.getBoolean("leftPressed", false);
    boolean right = SmartDashboard.getBoolean("rightPressed", false);

    double forward = 0;
    double strafe = 0;
    double turn = 0;

    if (w) {
      forward = 0.3;
    } else if (s) {
      forward = -0.3;
    }

    if (a) {
      strafe = -0.3;
    } else if (d) {
      strafe = 0.3;
    }

    if (left) {
      turn = -0.2;
    } else if (right) {
      turn = 0.2;
    }

    SwerveDrive.getInstance().controllerUpdate(strafe, forward, turn);
  }

  public void done() {
    SwerveDrive.getInstance().controllerUpdate(0, 0, 0);
  }

  public void abort() {
    done();
  }

  public boolean isFinished() {
    return false;
  }
}
