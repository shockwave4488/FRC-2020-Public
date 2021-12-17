package org.usfirst.frc.team4488.robot.autonomous.actions;

import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.robot.systems.SubsystemManager;
import org.usfirst.frc.team4488.robot.systems.drive.SmartDrive;
import org.usfirst.frc.team4488.robot.systems.drive.WestCoastDrive;

/**
 * Turns the robot to a specified heading
 *
 * @see Action
 */
public class TurnToHeadingAction implements Action {
  Logging logger = Logging.getInstance();

  private int mTargetHeading;
  private SmartDrive smartDrive;

  public TurnToHeadingAction(int heading) {
    mTargetHeading = heading;
    smartDrive = new SmartDrive((WestCoastDrive) SubsystemManager.getInstance().getDrive());
  }

  @Override
  public boolean isFinished() {
    return smartDrive.isTurnDone();
  }

  @Override
  public void update() {
    smartDrive.turnToAngle(mTargetHeading);
  }

  @Override
  public void done() {
    smartDrive.stop();
  }

  @Override
  public void start() {
    smartDrive.turnToAngle(mTargetHeading);
  }
}
