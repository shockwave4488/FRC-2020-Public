package org.usfirst.frc.team4488.robot.routines;

import java.util.ArrayList;
import org.usfirst.frc.team4488.lib.app.control.Path;
import org.usfirst.frc.team4488.lib.app.control.PathFollower;
import org.usfirst.frc.team4488.lib.app.math.RigidTransform2d;
import org.usfirst.frc.team4488.lib.app.math.Translation2d;
import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.lib.operator.Controllers;
import org.usfirst.frc.team4488.lib.sensors.Limelight;
import org.usfirst.frc.team4488.lib.sensors.Limelight.LedControl;
import org.usfirst.frc.team4488.robot.Constants;
import org.usfirst.frc.team4488.robot.app.RobotStateEstimatorBase;
import org.usfirst.frc.team4488.robot.app.paths.PathBuilder;
import org.usfirst.frc.team4488.robot.app.paths.PathBuilder.Waypoint;
import org.usfirst.frc.team4488.robot.autonomous.actions.DrivePathAction;
import org.usfirst.frc.team4488.robot.loops.RobotStateLoop;
import org.usfirst.frc.team4488.robot.systems.LEDController;
import org.usfirst.frc.team4488.robot.systems.LEDController.Color;
import org.usfirst.frc.team4488.robot.systems.LimelightManager;
import org.usfirst.frc.team4488.robot.systems.SubsystemManager;
import org.usfirst.frc.team4488.robot.systems.drive.WestCoastDrive;

public class ArcToStation extends Routine {

  private static final int PATH_SPEED = 60;
  private static final int PATH_END_SPEED = 30;
  private static final int MIDPOINT_RADIUS = 5;
  private static final int CURRENT_THRESHOLD = 30;
  private static final int MAX_ACCELERATION = 15;
  private static final String END_OF_PATH_MARK = "nearEndOfPath";

  private DrivePathAction driveAction;
  private Limelight limelight = LimelightManager.getInstance().getBackLimelight();
  private WestCoastDrive drive = (WestCoastDrive) SubsystemManager.getInstance().getDrive();
  private Controllers xbox = Controllers.getInstance();

  private static enum State {
    FindingTarget,
    StartingPath,
    DrivingPath,
    Done
  }

  private State state;

  public ArcToStation() {
    requireSystems(LimelightManager.getInstance(), drive);
  }

  @Override
  public boolean isFinished() {
    return state == State.Done;
  }

  @Override
  public void update() {
    switch (state) {
      case FindingTarget:
        double turn = xbox.getRightStickX(xbox.m_primary);
        drive.setPowers(turn, -turn);
        if (limelight.hasTarget()) {
          Logging.getInstance()
              .writeToLogFormatted(
                  this, "ArcToStation", "Drive, Limelight", "Starting arcing to Loading Bay");
          state = State.StartingPath;
        }
        break;

      case StartingPath:
        drive.setPowers(0, 0);
        Path path = generatePath();
        PathFollower.Parameters params = Constants.defaultAppParameters();
        params.profile_max_abs_acc = MAX_ACCELERATION;
        driveAction = new DrivePathAction(path, true, drive);
        driveAction.start();
        state = State.DrivingPath;
        // fallthrough intended

      case DrivingPath:
        driveAction.update();
        if (driveAction.isFinished()) {
          Logging.getInstance()
              .writeToLogFormatted(
                  this,
                  "ArcToStation",
                  "Drive, Limelight",
                  "Done arcing to Loading Bay by path ending");
          state = State.Done;
        } else if ((drive.getCurrentDraw() > CURRENT_THRESHOLD
            && driveAction.getAppController().hasPassedMarker(END_OF_PATH_MARK))) {
          Logging.getInstance()
              .writeToLogFormatted(
                  this,
                  "ArcToStation",
                  "Drive, Limelight",
                  "Done arcing to Loading Bay by current threshold");
          state = State.Done;
        }
        break;

      case Done:
        drive.setPowers(0, 0);
        break;
    }
  }

  @Override
  public void done() {
    abort();
  }

  @Override
  public void start() {
    LEDController.getInstance().setColor(Color.Green);
    state = State.FindingTarget;
    limelight.setLed(LedControl.ForceOn);
    Logging.getInstance()
        .writeToLogFormatted(
            this, "ArcToStation", "Drive, Limelight", "Finding Loading Bay target");
  }

  @Override
  public void abort() {
    LEDController.getInstance().setColor(Color.Default);
    driveAction = null;
    limelight.setLed(LedControl.ForceOff);
    drive.setPowers(0, 0);
  }

  private Path generatePath() {
    ArrayList<Waypoint> points = new ArrayList<Waypoint>();

    RobotStateEstimatorBase stateEstimator = RobotStateLoop.getInstance().getEstimator();
    RigidTransform2d currentPose = stateEstimator.getLatestFieldToVehicle().getValue();
    Translation2d currentTrans = currentPose.getTranslation();

    double currentAngle = drive.getAngleRotation2d().getRadians();
    double llAngle = Math.toRadians(limelight.getX());
    double angleToStation = currentAngle - llAngle;
    double llDist = limelight.getEstimatedDistance();

    double l =
        (llDist * Math.sin(angleToStation))
            - (Constants.limelightLensToFrontOfRobot * Math.sin(currentAngle));
    double w =
        (llDist * Math.cos(angleToStation))
            - (Constants.limelightLensToFrontOfRobot * Math.cos(llAngle));
    double actualDist = Math.sqrt(Math.pow(l, 2) + Math.pow(w, 2));

    points.add(new Waypoint(currentTrans, 0, PATH_SPEED));

    double stationDX = Math.cos(angleToStation) * actualDist * -1;
    double stationDY = Math.sin(angleToStation) * actualDist * -1;
    Translation2d stationTrans =
        new Translation2d(currentTrans.x() + stationDX, currentTrans.y() + stationDY);

    double midpoint1X = currentTrans.x() + (stationDX / 3);
    double midpoint1Y = currentTrans.y() + (Math.tan(currentAngle) * (stationDX / 3));
    points.add(new Waypoint(midpoint1X, midpoint1Y, MIDPOINT_RADIUS, PATH_SPEED));

    double midpoint2X = currentTrans.x() + (2 * stationDX / 3);
    double midpoint2Y = stationTrans.y();
    points.add(new Waypoint(midpoint2X, midpoint2Y, MIDPOINT_RADIUS, PATH_SPEED, END_OF_PATH_MARK));

    points.add(new Waypoint(stationTrans, 0, PATH_END_SPEED));

    Logging logger = Logging.getInstance();
    logger.writeToLogFormatted(
        this,
        "ArcToStation",
        "Drive, Limelight",
        "("
            + currentTrans.x()
            + ","
            + currentTrans.y()
            + "), ("
            + midpoint1X
            + ","
            + midpoint1Y
            + "), ("
            + midpoint2X
            + ","
            + midpoint2Y
            + "), ("
            + stationTrans.x()
            + ","
            + stationTrans.y()
            + ")");

    /*
    System.out.println("Start: (" + currentTrans.x() + ", " + currentTrans.y() + ")");
    System.out.println("Mid 1: (" + midpoint1X + ", " + midpoint1Y + ")");
    System.out.println("Mid 2: (" + midpoint2X + ", " + midpoint2Y + ")");
    System.out.println("End: (" + stationTrans.x() + ", " + stationTrans.y() + ")");
    /**/

    return PathBuilder.buildPathFromWaypoints(points);
  }
}
