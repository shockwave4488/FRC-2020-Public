package org.usfirst.frc.team4488.robot.autonomous;

import org.usfirst.frc.team4488.lib.app.control.Path;
import org.usfirst.frc.team4488.lib.app.control.PathFollower;
import org.usfirst.frc.team4488.lib.app.math.RigidTransform2d;
import org.usfirst.frc.team4488.lib.app.math.Twist2d;
import org.usfirst.frc.team4488.robot.Constants;
import org.usfirst.frc.team4488.robot.app.RobotStateEstimatorBase;
import org.usfirst.frc.team4488.robot.app.WestCoastKinematics;
import org.usfirst.frc.team4488.robot.systems.drive.WestCoastDrive;

public class AppController {

  private PathFollower pathFollower;
  private WestCoastDrive drive;
  private RobotStateEstimatorBase stateEstimator;
  // private Path currentPath;
  private boolean runningPath;

  public AppController(WestCoastDrive drive) {
    this.drive = drive;
    this.stateEstimator = drive.getStateEstimator();
  }

  public void setPath(Path path, boolean reversed) {
    setPath(path, reversed, Constants.defaultAppParameters());
  }

  public void setPath(Path path, boolean reversed, PathFollower.Parameters params) {
    if (runningPath) return;

    drive.configForPathFollowing();
    stateEstimator.resetDistanceDriven();

    pathFollower = new PathFollower(path, reversed, params);

    // currentPath = path;
    runningPath = true;
  }

  public void updatePathFollower(double timestamp) {
    RigidTransform2d robot_pose = stateEstimator.getLatestFieldToVehicle().getValue();
    Twist2d command =
        pathFollower.update(
            timestamp,
            robot_pose,
            stateEstimator.getDistanceDriven(),
            stateEstimator.getPredictedVelocity().dx);
    if (!pathFollower.isFinished()) {
      WestCoastKinematics.DriveVelocity setpoint = WestCoastKinematics.inverseKinematics(command);
      drive.updateVelocitySetpoint(setpoint.left, setpoint.right);
    } else {
      runningPath = false;
      drive.updateVelocitySetpoint(0, 0);
    }
  }

  public boolean doneWithPath() {
    return runningPath ? pathFollower.isFinished() : true;
  }

  public boolean hasPassedMarker(String marker) {
    return runningPath ? pathFollower.hasPassedMarker(marker) : false;
  }
}
