package org.usfirst.frc.team4488.robot.routines.defaults;

import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.lib.operator.Controllers;
import org.usfirst.frc.team4488.robot.systems.drive.FalconDrive;

public class FalconDriveRoutine extends DefaultRoutine {

  private FalconDrive drive;
  private Controllers xbox = Controllers.getInstance();

  public FalconDriveRoutine(FalconDrive drive) {
    requireSystem(drive);
    this.drive = drive;
  }

  public void start() {
    Logging.getInstance()
        .writeToLogFormatted(
            this, "Falcon Drive", "Drive", "Started falcon drive (manual control)");
  }

  public void update() {
    /* Commented to temporarily disable auto shifting
    FalconDrive.getInstance()
        .setDriveGears(xbox.getLeftBumper(xbox.m_primary), xbox.getRightBumper(xbox.m_primary));
        */
    FalconDrive.getInstance().setDriveGears(xbox.getLeftBumper(xbox.m_primary), false);

    double forward = xbox.deadzone(xbox.getLeftStickY(xbox.m_primary));
    double strafe = xbox.deadzone(xbox.getLeftStickX(xbox.m_primary));
    double turn = xbox.deadzone(xbox.getRightStickX(xbox.m_primary));
    drive.controllerUpdate(strafe, forward, turn);
  }

  public void abort() {
    Logging.getInstance()
        .writeToLogFormatted(
            this, "Falcon Drive", "Drive", "Stopped falcon drive (manual control)");
  }
}
