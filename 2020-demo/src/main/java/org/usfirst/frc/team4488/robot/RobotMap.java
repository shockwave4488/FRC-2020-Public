package org.usfirst.frc.team4488.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.ArrayList;
import org.usfirst.frc.team4488.lib.PreferenceDoesNotExistException;
import org.usfirst.frc.team4488.lib.PreferencesParser;
import org.usfirst.frc.team4488.robot.routines.BindedRoutine;
import org.usfirst.frc.team4488.robot.systems.Climber;
import org.usfirst.frc.team4488.robot.systems.ControlPanelSpinner;
import org.usfirst.frc.team4488.robot.systems.Indexer;
import org.usfirst.frc.team4488.robot.systems.Intake;
import org.usfirst.frc.team4488.robot.systems.LEDController;
import org.usfirst.frc.team4488.robot.systems.Shooter;
import org.usfirst.frc.team4488.robot.systems.SmartPCM;
import org.usfirst.frc.team4488.robot.systems.Subsystem;
import org.usfirst.frc.team4488.robot.systems.drive.Drive;
import org.usfirst.frc.team4488.robot.systems.drive.DriveBase;
import org.usfirst.frc.team4488.robot.systems.drive.DummyDrive;
import org.usfirst.frc.team4488.robot.systems.drive.FalconDrive;
import org.usfirst.frc.team4488.robot.systems.drive.SwerveDrive;
import org.usfirst.frc.team4488.robot.systems.drive.SwerveModule;
import org.usfirst.frc.team4488.robot.systems.drive.SwerveModule.SwerveParameters;

public class RobotMap {
  // should never change this line
  public static RobotName robotName;

  public static final int PDP = 8;
  public static final int PCM = 9;

  // talons
  public static final int DriveMotorRightM = 0;
  public static final int DriveMotorRightF1 = 1;
  public static final int DriveMotorRightF2 = 2;
  public static final int DriveMotorLeftF2 = 3;
  public static final int DriveMotorLeftF1 = 4;
  public static final int DriveMotorLeftM = 5;
  public static final int[] DrivePDPPorts = new int[] {0, 2, 3, 12, 13, 14};

  public static final int ClimberVacuumMotor = 0;

  public static final int SpinnerMotor = 10; // not right
  public static final int FrontRollerMotor = 35;
  public static final int HopperMotor = 37;
  public static final int ShooterMotor1 = 25;
  public static final int ShooterMotor2 = 26;
  public static final int IndexConveyor = 30;
  public static final int ShooterConveyor = 31;

  public static final int FalconDriveRightM = 13;
  public static final int FalconDriveRightF = 12;
  public static final int FalconDriveLeftM = 10;
  public static final int FalconDriveLeftF = 11;
  public static final int[] FalconDrivePDPPorts = new int[] {0, 1, 14, 15};

  public static final int ClimberBuddyForkMotor = 20;
  public static final int ClimberHookMotor = 21;

  public static final int ControlPanelSpinnerMotor = 36;

  // beam breaks
  public static final int IndexerCellSensor = 0;
  public static final int TransitionCellSensor = 4;
  public static final int MidShooterCellSensor = 5;
  public static final int ShooterCellSensor = 1;

  // swerve
  public static final SwerveParameters[] swerveParameters =
      new SwerveParameters[] {
        // throttle id, angle id, pot id, pot offset
        new SwerveModule.SwerveParameters(5, 4, 2, 2746), // 0 front left
        new SwerveModule.SwerveParameters(3, 2, 1, 2952), // 1 front right
        new SwerveModule.SwerveParameters(7, 6, 3, 3964), // 2 back left
        new SwerveModule.SwerveParameters(1, 10, 0, 2186) // 3 back right
      };

  // solenoids
  public static final int DriveGearShiftSolenoid = 0;
  public static final int IntakePiston = 1;
  public static final int SpinnerActuator = 2; // not right

  public static final int ClimberHookLockForwardSolenoid = 2;
  public static final int ClimberHookLockReverseSolenoid = 3;

  // analog sensors
  public static final int ClimberPressureSensor = 0;

  // servos
  public static final int ClimberServo = 1;

  // DIOs
  public static final int ClimberBuddyForksInHallEffect = 0; // wrong
  public static final int ClimberBuddyForksOutHallEffect = 0; // wrong
  public static final int ClimberHooksAttachedHallEffect = 0; // wrong
  public static final int ClimberHooksInHallEffect = 2;
  public static final int ClimberFlagTouchingBarHallEffect = 0; // wrong
  public static final int FrontLimelightLed = 7;

  public static final int LedDio1 = 6;
  public static final int LedDio2 = 8;
  public static final int LedDio3 = 9;

  public static final int ControlPanelTof = 0;

  // Limelight host names
  public static final String FrontLimelightName = "limelight-front";
  public static final String BackLimelightName = "limelight-back";

  // logic to determine which features to enable
  public static boolean driveExists = false;

  private static String roboNameKey = "RobotName";

  public static final ArrayList<BindedRoutine> bindedRoutines = new ArrayList<BindedRoutine>();
  private static ArrayList<Subsystem> addedSystems = new ArrayList<Subsystem>();

  public static final boolean hasShifters =
      PreferencesParser.getInstance().tryGetBoolean("HasShifters", false);

  public static RobotInfo robotSelector() {
    try {
      final String name = PreferencesParser.getInstance().getString("RobotName");
      if (name.equals("Competition")) {
        robotName = RobotName.Competition;
      } else if (name.equals("Practice")) {
        robotName = RobotName.Practice;
      } else if (name.equals("BareRio")) {
        robotName = RobotName.BareRoboRIO;
      } else if (name.equals("ProgrammingPlatform")) {
        robotName = RobotName.ProgrammingPlatform;
      } else if (name.equals("SwervePlatform")) {
        robotName = RobotName.SwervePlatform;
      } else if (name.equals("MockPlatform")) {
        robotName = RobotName.MockPlatform;
      } else {
        robotName = RobotName.BareRoboRIO;
      }
    } catch (PreferenceDoesNotExistException e) {
      robotName = RobotName.BareRoboRIO;
    }

    DriveBase driveToUse = DummyDrive.getInstance();

    switch (RobotMap.robotName) {
      case BareRoboRIO:
        driveExists = true;
        // addSystem(Shooter.getInstance());
        // addSystem(Indexer.getInstance());
        // addSystem(Spinner.getInstance());
        SmartDashboard.putString(roboNameKey, "BareRoboRIO");
        break;

      case ProgrammingPlatform:
        driveExists = true;
        driveToUse = Drive.getInstance();
        SmartDashboard.putString(roboNameKey, "ProgrammingPlatform");
        break;

      case SwervePlatform:
        driveExists = true;
        driveToUse = SwerveDrive.getInstance();
        SmartDashboard.putString(roboNameKey, "SwervePlatform");
        break;

      case MockPlatform:
        driveExists = true;
        driveToUse = Drive.getInstance();
        // addSystem(LimelightManager.getInstance());
        SmartDashboard.putString(roboNameKey, "MockPlatform");
        break;

      case Competition:
      case Practice:
        driveExists = true;
        driveToUse = FalconDrive.getInstance();
        addSystem(Intake.getInstance());
        addSystem(Shooter.getInstance());
        addSystem(Indexer.getInstance());
        addSystem(Climber.getInstance());
        addSystem(LEDController.getInstance());
        // addSystem(LimelightManager.getInstance());
        addSystem(SmartPCM.getInstance());
        addSystem(ControlPanelSpinner.getInstance());
        SmartDashboard.putString(roboNameKey, "Practice/Competition");
        break;
    }

    return new RobotInfo(driveToUse, addedSystems);
  }

  private static void addSystem(Subsystem system) {
    addedSystems.add(system);
  }
}
