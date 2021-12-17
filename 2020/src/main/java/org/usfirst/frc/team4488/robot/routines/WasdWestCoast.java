package org.usfirst.frc.team4488.robot.routines;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team4488.robot.systems.drive.Drive;

public class WasdWestCoast extends Routine {

  public void start() {}

  public void update() {
    if (!SmartDashboard.getBoolean("keyboardDriveRunning", false)) return;

    boolean w = SmartDashboard.getBoolean("wPressed", false);
    boolean a = SmartDashboard.getBoolean("aPressed", false);
    boolean s = SmartDashboard.getBoolean("sPressed", false);
    boolean d = SmartDashboard.getBoolean("dPressed", false);

    double forward = 0;
    double turn = 0;

    if (w) {
      forward = 0.75;
    } else if (s) {
      forward = -0.75;
    }

    if (a) {
      turn = -0.50;
    } else if (d) {
      turn = 0.50;
    }

    Drive.getInstance().controllerUpdate(0, forward, turn);
  }

  public void done() {
    Drive.getInstance().controllerUpdate(0, 0, 0);
  }

  public void abort() {
    done();
  }

  public boolean isFinished() {
    return false;
  }
}
