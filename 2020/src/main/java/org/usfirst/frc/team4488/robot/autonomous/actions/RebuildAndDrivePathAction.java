package org.usfirst.frc.team4488.robot.autonomous.actions;

import org.usfirst.frc.team4488.lib.app.control.Path;
import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.robot.app.paths.PathContainer;
import org.usfirst.frc.team4488.robot.autonomous.AppController;
import org.usfirst.frc.team4488.robot.systems.drive.WestCoastDrive;

/**
 * Drives the robot along the Path defined in the PathContainer object. The action finishes once the
 * robot reaches the end of the path.
 *
 * @see PathContainer
 * @see Path
 * @see Action
 */
public class RebuildAndDrivePathAction implements Action {
  Logging logger = Logging.getInstance();

  private PathContainer mPathContainer;
  private Path mPath;
  private WestCoastDrive drive;
  private AppController appController;

  public RebuildAndDrivePathAction(PathContainer p, WestCoastDrive drive) {
    mPathContainer = p;
    this.drive = drive;
    appController = new AppController(this.drive);
  }

  @Override
  public boolean isFinished() {
    return appController.doneWithPath();
  }

  @Override
  public void update() {
    // Nothing done here, controller updates in mEnabedLooper in robot
  }

  @Override
  public void done() {
    // TODO: Perhaps set wheel velocity to 0?
    logger.writeToLogFormatted(this, "done()");
  }

  @Override
  public void start() {
    logger.writeToLogFormatted(this, "start()");
    mPath = mPathContainer.buildPath();
    appController.setPath(mPath, mPathContainer.isReversed());
  }
}
