package org.usfirst.frc.team4488.robot.routines;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.lib.operator.Controllers;
import org.usfirst.frc.team4488.lib.sensors.Limelight;
import org.usfirst.frc.team4488.lib.sensors.Limelight.LedControl;
import org.usfirst.frc.team4488.lib.util.MedianFilter;
import org.usfirst.frc.team4488.robot.systems.LimelightManager;
import org.usfirst.frc.team4488.robot.systems.SubsystemManager;
import org.usfirst.frc.team4488.robot.systems.drive.SmartDrive;
import org.usfirst.frc.team4488.robot.systems.drive.WestCoastDrive;

public class AlignDriveWithCamera extends Routine {

  private static enum State {
    FindingTarget,
    LineUp,
    OnTarget
  }

  private State state;

  private Controllers xbox = Controllers.getInstance();
  private WestCoastDrive drive = (WestCoastDrive) SubsystemManager.getInstance().getDrive();
  private SmartDrive smartDrive = new SmartDrive(drive);
  private LimelightManager limelightManager = LimelightManager.getInstance();
  private Limelight limelight = limelightManager.getFrontLimelight();

  // private static final double ZOOM_DISTANCE_THRESHOLD = 240;
  // private static final double ZOOM_ANGLE_THRESHOLD = 13;
  private static final double OUTER_TO_INNER = 28;
  private static final double PARALLAX_ANGLE_CUTOFF = 24;
  private static final double SLOPPY_DONE_RANGE = 0.6;
  private static final double PARALLAX_DONE_RANGE = 0.3;
  private static final double DRIVE_CRAWL = -0.055;
  // private static final int CAM_DONE_CYCLES = 25;
  private static final int DISTANCE_UPDATE_THRESHOLD = 20;
  // private static final boolean ZOOM_ENABLED = false;
  private static final int MEDIAN_FILTER_SAMPLES = 10;
  private static final int CHECK_AGAIN_THRESH = 5;
  private static final double ANGLE_OFFSET = -0.83;

  private MedianFilter medianFilter = new MedianFilter(MEDIAN_FILTER_SAMPLES);

  private double estimatedDistance;
  private final boolean isParallaxAdjust;
  private boolean foundTarget = false;
  private boolean parallaxAdjust;
  private boolean noMoreChecking = false;
  private double targetAngle = 0;
  private int checkAgainCycles = 0;

  public AlignDriveWithCamera(boolean parallaxAdjust) {
    isParallaxAdjust = parallaxAdjust;
    requireSystems(drive, limelightManager);
    smartDrive.setTurnDoneRange(parallaxAdjust ? PARALLAX_DONE_RANGE : SLOPPY_DONE_RANGE);
  }

  public void start() {
    medianFilter.clear();

    foundTarget = false;
    estimatedDistance = 0;
    targetAngle = 0;
    parallaxAdjust = isParallaxAdjust;
    state = State.FindingTarget;
    limelight.setLed(LedControl.ForceOn);
    drive.setPowers(0, 0);
    Logging.getInstance()
        .writeToLogFormatted(this, "AlignDriveWithCamera", "Drive, Limelight", state.name());
  }

  public void update() {
    SmartDashboard.putBoolean("Shooting inner", parallaxAdjust);
    switch (state) {
      case FindingTarget:
        double turn = xbox.deadzone(xbox.getRightStickX(xbox.m_primary));
        drive.setPowers(turn, -turn);
        SmartDashboard.putNumber(
            "parallax angle", calcParallaxAngle(limelight.getX() - ANGLE_OFFSET));
        if (limelight.hasTarget()) {
          state = State.LineUp;
          Logging.getInstance()
              .writeToLogFormatted(this, "AlignDriveWithCamera", "Drive, Limelight", state.name());
        }
        break;

      case LineUp:
        double llAngle = limelight.getX() - ANGLE_OFFSET;
        SmartDashboard.putNumber("Align target angle", targetAngle);

        if (parallaxAdjust
            && Math.abs(drive.getAngleRotation2d().getDegrees() - llAngle) > PARALLAX_ANGLE_CUTOFF
            && limelight.hasTarget()) {
          parallaxAdjust = false;
          Logging.getInstance()
              .writeToLogFormatted(
                  this, "AlignDriveWithCamera", "Drive, Limelight", "Not shooting inner");
        } else if (isParallaxAdjust
            && Math.abs(drive.getAngleRotation2d().getDegrees() - llAngle) < PARALLAX_ANGLE_CUTOFF
            && limelight.hasTarget()) {
          parallaxAdjust = true;
          Logging.getInstance()
              .writeToLogFormatted(
                  this, "AlignDriveWithCamera", "Drive, Limelight", "Shooting inner");
        }

        if (!limelight.hasTarget()) {
          medianFilter.clear();
        }

        medianFilter.update(llAngle);

        if (medianFilter.isFull() && !noMoreChecking) {
          foundTarget = true;
          double median = medianFilter.getMedian();
          targetAngle =
              parallaxAdjust
                  ? calcParallaxAngle(median)
                  : drive.getAngleRotation2d().getDegrees() - median;
        }

        if (!noMoreChecking
            && foundTarget
            && Math.abs(drive.getAngleRotation2d().getDegrees() - targetAngle) < 1)
          checkAgainCycles++;
        else checkAgainCycles = 0;

        if (checkAgainCycles > CHECK_AGAIN_THRESH) {
          noMoreChecking = true;
        }

        if (foundTarget) {
          if (SmartDashboard.getBoolean("crawl?", true))
            smartDrive.turnToAngleCrawl(DRIVE_CRAWL, targetAngle);
          else smartDrive.turnToAngle(targetAngle);

          if (!limelight.hasTarget()) {
            state = State.FindingTarget;
            Logging.getInstance()
                .writeToLogFormatted(
                    this, "AlignDriveWithCamera", "Drive, Limelight", state.name());
          }
          if (noMoreChecking && smartDrive.isTurnDone() && limelight.hasTarget()) {
            state = State.OnTarget;
          }
        }
        /*
        double angle =
            parallaxAdjust
                ? calcParallaxAngle(llAngle)
                : drive.getAngleRotation2d().getDegrees() - median;
        SmartDashboard.putNumber("parallax angle", angle);
        smartDrive.turnToAngle(angle);
         SmartDashboard.putNumber("median", median);
        if (!limelight.hasTarget()) {
          state = State.FindingTarget;
          Logging.getInstance()
              .writeToLogFormatted(this, "AlignDriveWithCamera", "Drive, Limelight", state.name());
        }
        if (smartDrive.isTurnDone() && limelight.hasTarget() && foundTarget) {
          state = State.OnTarget;
        }
        */
        break;

      case OnTarget:
        break;
    }
  }

  public void done() {
    limelight.setPipeline(0);
    noMoreChecking = false;
    limelight.setLed(LedControl.ForceOff);
    drive.setPowers(0, 0);
    Logging.getInstance()
        .writeToLogFormatted(this, "AlignDriveWithCamera", "Drive, Limelight", state.name());
  }

  public void abort() {
    done();
  }

  public boolean isFinished() {
    return state == State.OnTarget;
  }

  private double calcParallaxAngle(double llAngle) {
    if (Math.abs(limelight.getEstimatedDistance() - estimatedDistance)
        > DISTANCE_UPDATE_THRESHOLD) {
      estimatedDistance = limelight.getEstimatedDistance();
    }
    double robotAngle = drive.getAngleRotation2d().getRadians();
    double theta = robotAngle - Math.toRadians(llAngle);
    SmartDashboard.putNumber("theta", Math.toDegrees(theta));
    double toAdjust =
        Math.toDegrees(
            Math.atan(
                (estimatedDistance * Math.sin(theta))
                    / (estimatedDistance * Math.cos(theta) + OUTER_TO_INNER)));
    SmartDashboard.putNumber("adjusted", toAdjust);
    return toAdjust;
  }

  // private void handleZoom() {
  //   if (ZOOM_ENABLED) {
  //     boolean shouldZoom =
  //         limelight.hasTarget()
  //             && limelight.getEstimatedDistance() > ZOOM_DISTANCE_THRESHOLD
  //             && Math.abs(limelight.getX()) < ZOOM_ANGLE_THRESHOLD;
  //     limelight.setPipeline(shouldZoom ? 1 : 0);
  //   }
  // }
}
