package frc.robot.subsystems.drive;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.SwerveDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.SwerveModuleState;
import frc.lib.logging.Logger;
import frc.lib.sensors.NavX;
import frc.lib.util.app.Util;
import frc.lib.wpiextensions.ShockwaveSubsystemBase;

/** Represents a swerve drive style drivetrain. */
public class SwerveDrive extends ShockwaveSubsystemBase {
  public static final double kMaxSpeed = 3.0; // 3 meters per second
  public static final double kMaxAngularSpeed = 2 * Math.PI; // 1 rotation per second

  private static final double kTrackLength = 0.5715; // front to back
  private static final double kTrackWidth = 0.4826; // left to right

  private static final double kMinimumInputValue = 0.01;

  private final Translation2d m_frontLeftLocation =
      new Translation2d(kTrackLength / 2, kTrackWidth / 2);
  private final Translation2d m_frontRightLocation =
      new Translation2d(kTrackLength / 2, -kTrackWidth / 2);
  private final Translation2d m_backLeftLocation =
      new Translation2d(-kTrackLength / 2, kTrackWidth / 2);
  private final Translation2d m_backRightLocation =
      new Translation2d(-kTrackLength / 2, -kTrackWidth / 2);

  private final ISwerveModule m_frontLeft;
  private final ISwerveModule m_frontRight;
  private final ISwerveModule m_backLeft;
  private final ISwerveModule m_backRight;

  private final NavX m_gyro;
  private final SwerveDriveKinematics m_kinematics;
  private final SwerveDriveOdometry m_odometry;
  private final Logger logger;

  private Pose2d currentPose;
  private double fieldPoseX;
  private double fieldPoseY;

  /**
   * The drive class for swerve robots
   *
   * @param gyro A NavX that's used to get the angle of the robot
   * @param parameters Parameters that are passed to SwerveModules for their setup
   */
  public SwerveDrive(NavX gyro, ISwerveModule[] modules, Logger logger) {
    m_gyro = gyro;
    this.logger = logger;
    m_kinematics =
        new SwerveDriveKinematics(
            m_frontLeftLocation, m_frontRightLocation, m_backLeftLocation, m_backRightLocation);
    m_odometry =
        new SwerveDriveOdometry(m_kinematics, new Rotation2d(m_gyro.getYaw().getRadians()));

    m_frontLeft = modules[0];
    m_frontRight = modules[1];
    m_backLeft = modules[2];
    m_backRight = modules[3];
  }

  /**
   * Method to drive the robot using joystick info.
   *
   * @param xSpeed Speed of the robot in the x direction (forward).
   * @param ySpeed Speed of the robot in the y direction (sideways).
   * @param rot Angular rate of the robot.
   * @param fieldRelative Whether the provided x and y speeds are relative to the field.
   */
  @SuppressWarnings("ParameterName")
  public void drive(double xSpeed, double ySpeed, double rot, boolean fieldRelative) {
    if (Util.epsilonEquals(xSpeed, 0, kMinimumInputValue)
        && Util.epsilonEquals(ySpeed, 0, kMinimumInputValue)
        && Util.epsilonEquals(rot, 0, kMinimumInputValue)) {
      m_frontLeft.stop();
      m_frontRight.stop();
      m_backLeft.stop();
      m_backRight.stop();
    }

    var swerveModuleStates =
        m_kinematics.toSwerveModuleStates(
            fieldRelative
                ? ChassisSpeeds.fromFieldRelativeSpeeds(
                    xSpeed, ySpeed, rot, new Rotation2d(m_gyro.getYaw().getRadians()))
                : new ChassisSpeeds(xSpeed, ySpeed, rot));
    SwerveDriveKinematics.normalizeWheelSpeeds(swerveModuleStates, kMaxSpeed);
    m_frontLeft.setDesiredState(swerveModuleStates[0]);
    m_frontRight.setDesiredState(swerveModuleStates[1]);
    m_backLeft.setDesiredState(swerveModuleStates[2]);
    m_backRight.setDesiredState(swerveModuleStates[3]);
  }

  /** Updates the field relative position of the robot. */
  public void updateOdometry() {
    m_odometry.update(
        new Rotation2d(m_gyro.getYaw().getRadians()),
        m_frontLeft.getState(),
        m_frontRight.getState(),
        m_backLeft.getState(),
        m_backRight.getState());
  }

  @Override
  public void onStart() {}

  @Override
  public void onStop() {}

  @Override
  public void zeroSensors() {
    m_gyro.reset();
  }

  @Override
  public void updateSmartDashboard() {}

  @Override
  public void setUpTrackables() {
    int loggingFrequency = 10;
    logger.addStringTrackable(
        () -> (fieldPoseX + "," + fieldPoseY + "," + m_gyro.getYaw().getDegrees()),
        "Swerve Drive Position",
        loggingFrequency,
        "Robot X Coordinate,Robot Y Coordinate,Robot Angle (degrees)");
    /*
    The complicated trackable setup shown below is an example of the creation of four StringTrackables. To obtain each
    double in each StringTrackable, first a lambda is used, then the swerve module is specified (frontLeft, backRight, etc),
    then the correct method from the swerve module is called. Also, each double is separated by commas.

    The use of StringTrackables allows multiple doubles to be logged in a single file, which is useful when logging
    related pieces of data (such as the robot's x position, y position, and angle, as shown above)
    */
    logger.addStringTrackable(
        () ->
            (m_frontLeft.getDesiredSpeed()
                + ","
                + m_frontRight.getDesiredSpeed()
                + ","
                + m_backLeft.getDesiredSpeed()
                + ","
                + m_backRight.getDesiredSpeed()),
        "Swerve Module Desired Speeds",
        loggingFrequency,
        "Front Left Desired Speed, Front Right Desired Speed, Back Left Desired Speed, Back Right Desired Speed");
    logger.addStringTrackable(
        () ->
            (m_frontLeft.getDesiredAngle()
                + ","
                + m_frontRight.getDesiredAngle()
                + ","
                + m_backLeft.getDesiredAngle()
                + ","
                + m_backRight.getDesiredAngle()),
        "Swerve Module Desired Angles",
        loggingFrequency,
        "Front Left Desired Angle, Front Right Desired Angle, Back Left Desired Angle, Back Right Desired Angle");
    logger.addStringTrackable(
        () ->
            (m_frontLeft.getSpeed()
                + ","
                + m_frontRight.getSpeed()
                + ","
                + m_backLeft.getSpeed()
                + ","
                + m_backRight.getSpeed()),
        "Swerve Module Actual Speeds",
        loggingFrequency,
        "Front Left Actual Speed, Front Right Actual Speed, Back Left Actual Speed, Back Right Actual Speed");
    logger.addStringTrackable(
        () ->
            (m_frontLeft.getAngleDegrees()
                + ","
                + m_frontRight.getAngleDegrees()
                + ","
                + m_backLeft.getAngleDegrees()
                + ","
                + m_backRight.getAngleDegrees()),
        "Swerve Module Actual Angles",
        loggingFrequency,
        "Front Left Actual Angle, Front Right Actual Angle, Back Left Actual Angle, Back Right Actual Angle");
  }

  public SwerveDriveKinematics getKinematics() {
    return m_kinematics;
  }

  public Pose2d getOdometry() {
    return m_odometry.getPoseMeters();
  }

  /**
   * Used to directly set the state of (and move) the swerve modules
   *
   * @param desiredStates A list of desired states for each of the swerve modules, following the
   *     order front left, front right, back left, back right.
   */
  public void setModuleStates(SwerveModuleState[] desiredStates) {
    SwerveDriveKinematics.normalizeWheelSpeeds(desiredStates, 1);
    m_frontLeft.setDesiredState(desiredStates[0]);
    m_frontRight.setDesiredState(desiredStates[1]);
    m_backLeft.setDesiredState(desiredStates[2]);
    m_backRight.setDesiredState(desiredStates[3]);
  }

  @Override
  public void periodic() {
    updateOdometry();
    currentPose = m_odometry.getPoseMeters();
    fieldPoseX = currentPose.getX();
    fieldPoseY = currentPose.getY();
  }

  /**
   * Sets your position on the field
   *
   * @param pose The position on the field you want the robot to think it's at
   */
  public void resetOdometry(Pose2d pose) {
    m_odometry.resetPosition(pose, new Rotation2d(m_gyro.getYaw().getRadians()));
  }
}
