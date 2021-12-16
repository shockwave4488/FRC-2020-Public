package org.usfirst.frc.team4488.robot.autonomous.actions;

public class stopPathAction implements Action {

  public stopPathAction(DrivePathAction path) {
    path.stopPath();
  }

  @Override
  public boolean isFinished() {
    return true;
  }

  @Override
  public void update() {}

  @Override
  public void done() {}

  @Override
  public void start() {}
}
