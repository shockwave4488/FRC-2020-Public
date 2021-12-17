package frc.robot.robotspecifics.swerve;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryUtil;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SwerveControllerCommand;
import frc.lib.BaseRobotContainer;
import frc.lib.PreferencesParser;
import frc.lib.drive.SwerveParameters;
import frc.lib.logging.Logger;
import frc.lib.operator.IDeadzoneCalculator;
import frc.lib.operator.SquareDeadzoneCalculator;
import frc.lib.sensors.NavX;
import frc.robot.Constants.DriveTrainConstants;
import frc.robot.Constants.OIConstants;
import frc.robot.commands.DefaultSwerveDrive;
import frc.robot.subsystems.drive.ISwerveModule;
import frc.robot.subsystems.drive.SwerveDrive;
import frc.robot.subsystems.drive.SwerveModuleNeos;
import java.io.IOException;
import java.nio.file.Path;
import org.json.simple.JSONObject;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class SwerveRobotContainer extends BaseRobotContainer {
  private final NavX m_gyro;
  private final SwerveDrive swerve;
  private final SwerveParameters[] swerveParameters;
  private final IDeadzoneCalculator deadzone;
  private final XboxController driverJoystick;
  private final ISwerveModule m_frontLeft;
  private final ISwerveModule m_frontRight;
  private final ISwerveModule m_backLeft;
  private final ISwerveModule m_backRight;

  private SwerveParameters[] getSwerveParameters() {
    JSONObject swerveParametersJSONFL = prefs.getJSONObject("SwerveParametersFL");
    SwerveParameters swerveParametersFL = new SwerveParameters(swerveParametersJSONFL);
    JSONObject swerveParametersJSONFR = prefs.getJSONObject("SwerveParametersFR");
    SwerveParameters swerveParametersFR = new SwerveParameters(swerveParametersJSONFR);
    JSONObject swerveParametersJSONBL = prefs.getJSONObject("SwerveParametersBL");
    SwerveParameters swerveParametersBL = new SwerveParameters(swerveParametersJSONBL);
    JSONObject swerveParametersJSONBR = prefs.getJSONObject("SwerveParametersBR");
    SwerveParameters swerveParametersBR = new SwerveParameters(swerveParametersJSONBR);
    return new SwerveParameters[] {
      swerveParametersFL, swerveParametersFR, swerveParametersBL, swerveParametersBR
    };
  }

  private ISwerveModule[] getSwerveModules() {
    return new ISwerveModule[] {
      m_frontLeft, m_frontRight, m_backLeft, m_backRight,
    };
  }

  /**
   * The robot container for our basic swerve drive robot, this is where all classes relevant to
   * this robot are created and where its default command(s) are set
   */
  public SwerveRobotContainer(PreferencesParser prefs, Logger logger) {
    super(prefs, logger);

    m_gyro = new NavX(SPI.Port.kMXP);
    deadzone = new SquareDeadzoneCalculator(OIConstants.DEFAULT_CONTROLLER_DEADZONE);
    swerveParameters = getSwerveParameters();
    m_frontLeft = new SwerveModuleNeos(swerveParameters[0], logger, prefs);
    m_frontRight = new SwerveModuleNeos(swerveParameters[1], logger, prefs);
    m_backLeft = new SwerveModuleNeos(swerveParameters[2], logger, prefs);
    m_backRight = new SwerveModuleNeos(swerveParameters[3], logger, prefs);

    swerve = new SwerveDrive(m_gyro, getSwerveModules(), logger);
    driverJoystick = new XboxController(OIConstants.DRIVER_CONTROLLER_PORT);

    /*
    The multiplication of SWERVE_ROTATION_SPEED by -1 and the first two getY/getX values being switched and multiplied by -1 are intentional.
    Multiplying SWERVE_ROTATION_SPEED by -1 corrects the direction of rotation of our robot, and we switch getY/getX and multiply them by -1 because the controller input is
    90 degrees off compared to the values WPILib utilities expect (particularly ChassisSpeeds)
    */
    swerve.setDefaultCommand(
        new DefaultSwerveDrive(
            swerve,
            DriveTrainConstants.SWERVE_DRIVE_SPEED,
            DriveTrainConstants.SWERVE_ROTATION_SPEED * -1,
            () -> deadzone.deadzone(driverJoystick.getY(GenericHID.Hand.kLeft) * -1),
            () -> deadzone.deadzone(driverJoystick.getX(GenericHID.Hand.kLeft) * -1),
            () -> deadzone.deadzone(driverJoystick.getX(GenericHID.Hand.kRight)),
            () -> driverJoystick.getStartButtonPressed()));

    addSubsystems();
    configureButtonBindings();
  }

  protected void addSubsystems() {
    subsystems.add(swerve);
  }

  protected void configureButtonBindings() {}

  public Command getAutonomousCommand() {
    /*
    There are two important things to remember when setting up an autonomous command for swerve:
      1. The robot will turn to the angle of the final pose of the Trajectory object as it moves through the trajectory (path),
      all of the angles in between the start and end points of the trajectory don't matter.
      2. Don't forget to reset the robot's odometry to the initial pose of the first trajectory.
    */
    String trajectoryJSON = "paths/TestAuto.wpilib.json";
    Trajectory pathWeaverTrajectory = new Trajectory();

    try {
      Path trajectoryPath = Filesystem.getDeployDirectory().toPath().resolve(trajectoryJSON);
      pathWeaverTrajectory = TrajectoryUtil.fromPathweaverJson(trajectoryPath);
    } catch (IOException ex) {
      DriverStation.reportError("Unable to open trajectory: " + trajectoryJSON, ex.getStackTrace());
    }

    /* How to set up trajectories without PathWeaver:

    TrajectoryConfig config = new TrajectoryConfig(1, 1).setKinematics(swerve.getKinematics());

    Trajectory trajectoryExampleForward =
        TrajectoryGenerator.generateTrajectory(
            new Pose2d(0, 0, new Rotation2d(0)),
            List.of(new Translation2d(1, 0.5), new Translation2d(2, -0.5)),
            new Pose2d(3, 0, new Rotation2d(Math.PI)),
            config);
    */

    ProfiledPIDController thetaController =
        new ProfiledPIDController(1, 0, 0, new TrapezoidProfile.Constraints(Math.PI, Math.PI));
    thetaController.enableContinuousInput(-Math.PI, Math.PI);

    SwerveControllerCommand swerveControllerCommandPathWeaverTest =
        new SwerveControllerCommand(
            pathWeaverTrajectory,
            swerve::getOdometry, // Functional interface to feed supplier
            swerve.getKinematics(),

            // Position controllers
            new PIDController(0.5, 0.0001, 0.01),
            new PIDController(0.5, 0.0001, 0.01),
            thetaController,
            swerve::setModuleStates,
            swerve);

    // Reset odometry to the starting pose of the trajectory.
    swerve.resetOdometry(pathWeaverTrajectory.getInitialPose());

    // Run path following command, then stop at the end.
    return swerveControllerCommandPathWeaverTest.andThen(() -> swerve.drive(0, 0, 0, false));
  }
}
