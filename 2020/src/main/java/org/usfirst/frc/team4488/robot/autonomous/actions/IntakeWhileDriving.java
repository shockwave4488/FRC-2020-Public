package org.usfirst.frc.team4488.robot.autonomous.actions;

import org.usfirst.frc.team4488.robot.app.paths.PathContainer;
import org.usfirst.frc.team4488.robot.systems.drive.WestCoastDrive;

public class IntakeWhileDriving implements Action {

  private static final int REALLY_LONG_AMOUNT_OF_TIME = 99999;

  private Action driveAction;
  private Action intakeAction;

  public IntakeWhileDriving(PathContainer path, WestCoastDrive drive) {
    driveAction = new DrivePathAction(path, drive);
    intakeAction = new AutoIntake(REALLY_LONG_AMOUNT_OF_TIME);
  }

  public void start() {
    driveAction.start();
    intakeAction.start();
  }

  public void update() {
    driveAction.update();
    intakeAction.update();
  }

  public void done() {
    driveAction.done();
    intakeAction.done();
  }

  public boolean isFinished() {
    return driveAction.isFinished();
  }
}
