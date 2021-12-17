package org.usfirst.frc.team4488.robot.autonomous.actions;

import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.robot.autonomous.AppController;

/**
 * Waits for the robot to pass by a provided path marker (i.e. a waypoint on the field). This action
 * routinely compares to the crossed path markers provided by the drivetrain (in Path Control mode)
 * and returns if the parameter path marker is inside the drivetrain's Path Markers Crossed list
 *
 * @param A Path Marker to determine if crossed
 */
public class WaitForPathMarkerAction implements Action {
  Logging logger = Logging.getInstance();

  private AppController appController;
  private String mMarker;

  public WaitForPathMarkerAction(AppController appController, String marker) {
    this.appController = appController;
    mMarker = marker;
  }

  @Override
  public boolean isFinished() {
    return appController.hasPassedMarker(mMarker);
  }

  @Override
  public void update() {}

  @Override
  public void done() {
    logger.writeToLogFormatted(this, "done()");
  }

  @Override
  public void start() {
    logger.writeToLogFormatted(this, "start()");
  }
}
