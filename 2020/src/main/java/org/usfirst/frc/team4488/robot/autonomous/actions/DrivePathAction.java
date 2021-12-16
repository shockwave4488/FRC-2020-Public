package org.usfirst.frc.team4488.robot.autonomous.actions;

import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team4488.lib.app.control.Path;
import org.usfirst.frc.team4488.lib.app.control.PathFollower;
import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.robot.Constants;
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
public class DrivePathAction implements Action {
  Logging logger = Logging.getInstance();

  private boolean reversed;
  private Path mPath;
  private AppController appController;
  private PathFollower.Parameters params = Constants.defaultAppParameters();

  private boolean manualStop = false;

  public void stopPath() {
    manualStop = true;
  }

  public DrivePathAction(PathContainer p, WestCoastDrive drive) {
    mPath = p.buildPath();
    reversed = p.isReversed();
    appController = new AppController(drive);
  }

  public DrivePathAction(Path p, boolean isReversed, WestCoastDrive drive) {
    mPath = p;
    reversed = isReversed;
    appController = new AppController(drive);
  }

  public DrivePathAction(
      Path p, boolean isReversed, WestCoastDrive drive, PathFollower.Parameters params) {
    mPath = p;
    reversed = isReversed;
    appController = new AppController(drive);
    this.params = params;
  }

  @Override
  public boolean isFinished() {
    return appController.doneWithPath() || manualStop;
  }

  @Override
  public void update() {
    appController.updatePathFollower(Timer.getFPGATimestamp());
  }

  @Override
  public void done() {
    // TODO: Perhaps set wheel velocity to 0?
    logger.writeToLogFormatted(this, "done()");
  }

  @Override
  public void start() {
    logger.writeToLogFormatted(this, "start()");
    appController.setPath(mPath, reversed, params);
  }

  public AppController getAppController() {
    return appController;
  }
}
