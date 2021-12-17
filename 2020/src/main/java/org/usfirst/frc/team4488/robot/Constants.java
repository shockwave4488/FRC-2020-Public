package org.usfirst.frc.team4488.robot;

import org.usfirst.frc.team4488.lib.app.control.Lookahead;
import org.usfirst.frc.team4488.lib.app.control.PathFollower;
import org.usfirst.frc.team4488.lib.app.math.Pose2d;
import org.usfirst.frc.team4488.lib.app.math.Rotation2d;
import org.usfirst.frc.team4488.lib.app.math.Translation2d;
import org.usfirst.frc.team4488.lib.controlsystems.SetPointProfile;
import org.usfirst.frc.team4488.lib.sensors.Limelight.DistanceEstimationConstants;

public class Constants {

  // VPP Poses
  public static final Pose2d kRobotLeftStartingPose =
      new Pose2d(new Translation2d(10, 10), Rotation2d.fromDegrees(0));

  /*
   *  Drive Subsystem Constants
   */

  public static final double driftCorrection = 1;

  public static final double turnPowerPercentage = 80;
  /*
   * APP Constants
   */
  public static final boolean kIsUsingTractionWheels = false;
  public static final double brakeFactor = 1.0; // used to limit APP speeds during testing

  public static final double kTrackLengthInches = 27.375; // Distance from front to back robot
  public static final double kTrackWidthInches =
      27.375; // Distance from left to right tank drive wheels
  public static final double kTrackScrubFactor =
      0.924; // Factor to compensate for slipping of wheels while turning, must be adjusted
  // experimentally

  public static final double kDriveHighGearMaxSetpoint =
      17.0 * 12.0 / brakeFactor; // inches per second, speed limit for our robot

  // Path following constants
  public static final double kPathLookaheadTime = 0.25;
  public static final double kMinLookAhead = 12.0; // inches
  public static final double kMinLookAheadSpeed = 9.0; // inches per second
  public static final double kMaxLookAhead = 48.0; // inches
  public static final double kMaxLookAheadSpeed = 120.0; // inches per second
  public static double kDeltaLookAhead = kMaxLookAhead - kMinLookAhead; // inches
  public static double kDeltaLookAheadSpeed =
      kMaxLookAheadSpeed - kMinLookAheadSpeed; // inches per second

  // Path Follower Motion Profiling Constants
  public static final double kInertiaSteeringGain = 0.0;
  public static final double kSegmentCompletionTolerance = 0.1; // inches
  public static final double kPathFollowingMaxAccel = 90.0 / brakeFactor; // inches per second^2
  public static final double kPathFollowingMaxVel = 120.0 / brakeFactor; // inches per second
  public static final double kPathFollowingProfileKp = 5.00;
  public static final double kPathFollowingProfileKi = 0.03;
  public static final double kPathFollowingProfileKv = 0.02;
  public static final double kPathFollowingProfileKffv = 1.0;
  public static final double kPathFollowingProfileKffa = 0.05;
  public static final double kPathFollowingGoalPosTolerance = 0.75;
  public static final double kPathFollowingGoalVelTolerance = 12.0;
  public static final double kPathStopSteeringDistance = 9.0;

  public static final double kMaxGoalTrackAge = 1.0;
  public static final double kMaxTrackerDistance = 18.0;
  public static double kTimeoutMs = 0.0;

  public static PathFollower.Parameters defaultAppParameters() {
    return new PathFollower.Parameters(
        new Lookahead(
            Constants.kMinLookAhead,
            Constants.kMaxLookAhead,
            Constants.kMinLookAheadSpeed,
            Constants.kMaxLookAheadSpeed),
        Constants.kInertiaSteeringGain,
        Constants.kPathFollowingProfileKp,
        Constants.kPathFollowingProfileKi,
        Constants.kPathFollowingProfileKv,
        Constants.kPathFollowingProfileKffv,
        Constants.kPathFollowingProfileKffa,
        Constants.kPathFollowingMaxVel,
        Constants.kPathFollowingMaxAccel,
        Constants.kPathFollowingGoalPosTolerance,
        Constants.kPathFollowingGoalVelTolerance,
        Constants.kPathStopSteeringDistance);
  }

  public static double climberP = 0.0007;
  public static double climberI = 0.00001;
  public static double climberD = 0;

  public static double climbLineUp = 3;
  public static int climberDoneTicks = 6750; // 7000

  public static double climberPower = 0.7; // 0.7

  public static double armExtenderP = 0.1;
  public static double armExtenderI = 0;
  public static double armExtenderD = 0;

  public static double armAnglerP = 6;
  public static double armAnglerI = 0.01;
  public static double armAnglerD = 0;

  /** Indexer Constants */
  public static double kickWheelPower = 0.3;

  public static double zeroCompAngleOffset = 750.25;
  public static double zeroPracticeAngleOffset = 806.25;

  // safety
  public static final boolean[][][] armSafety = {
    {
      // turret safe
      //    arm angle
      // low  mid   midmid high
      {false, true, true, true}, // short
      {false, true, true, true}, // mid1   arm extension
      {false, true, true, true}, // mid2
      {true, true, true, true} // full
    },
    {
      // turret not safe
      //    arm angle
      // low  mid   midmid high
      {false, false, true, false}, // short
      {false, true, true, false}, // mid1   arm extension
      {false, true, true, true}, // mid2
      {false, true, true, true} // full
    }
  };
  // Arm constants
  public static final double armExtendLowMid1 = 5.5;
  public static final double armExtendMid1Mid2 = 10.0;
  public static final double armExtendMid2High = 12.0;

  public static final double armAngleThreshLowMid = -2;
  public static final double armAngleThreshMidMid = 12;
  public static final double armAngleThreshMidHigh = 43;

  public static final double turretSafe1 = -5;
  public static final double turretSafe2 = 5;
  public static final double turretSafe3 = -175;
  public static final double turretSafe4 = -185;
  public static final double turretSafe5 = 175;
  public static final double turretSafe6 = 185;
  public static final double turretSafe7 = 85;
  public static final double turretSafe8 = 95;
  public static final double turretSafe9 = -85;
  public static final double turretSafe10 = -95;
  public static final double turretSafe11 = 265;
  public static final double turretSafe12 = 275;
  public static final double turretSafe13 = -265;
  public static final double turretSafe14 = -275;

  public static double safeDesDiffArmExt = 3; // will be changed
  public static double safeDesDiffArmAng = 15; // will be changed
  public static double safeDesDiffTurret = 60; // will be changed

  // Our constants
  public static final double kLooperDt = 0.005;
  public static final String prefsPath = "/home/admin/Preferences.json";
  public static final int generatedPathSpeed = 30;
  public static final int generatedPathResolution = 3; // one point every x inches
  public static final int generatedPathPadding =
      12; // center of robot will not go within x inches of obstacles

  public static final double rocketScoreAngle = 35;
  public static final double rocketScoreExtension = 3;
  public static final double cargoShipScoreAngle = 57;
  public static final double cargoShipScoreExtension = 10.1;

  public static final DistanceEstimationConstants practiceFrontLimelightConstants =
      new DistanceEstimationConstants(34, 90, 14.27);
  public static final DistanceEstimationConstants compFrontLimelightConstants =
      new DistanceEstimationConstants(34, 90, 13.96); // 13.39

  public static SetPointProfile backLimelightInterpTable() {
    SetPointProfile profile = new SetPointProfile();
    profile.add(2.2, 51.5);
    profile.add(0.49, 109);
    profile.add(0.08, 236);
    return profile;
  }

  public static final double limelightLensToFrontOfRobot = 15;

  // limit switches (possible rename)

  public static final double armCompExtenderMax = 13.8;
  public static final double armCompExtenderMin = 3;
  public static final double armCompAngleMax = 45;

  public static final double armPracticeExtenderMax = 14;
  public static final double armPracticeExtenderMin = 2;
  public static final double armPracticeAngleMax = 55;

  public static final double armAngleMin = -16.0;

  // current
  public static final double armExtendHighCurrent = 3; // wrong
  public static final double armExtendPlacementCurrent = 3.5;
  public static final double turretCurrentThreshold = 9.0; // not correct

  public static final double armStationaryRange = 0.1;

  public static final double swerveTopSpeed = 12; // not final - should be measured

  public static final double defaultStickDeadzone = 0.2;
}
