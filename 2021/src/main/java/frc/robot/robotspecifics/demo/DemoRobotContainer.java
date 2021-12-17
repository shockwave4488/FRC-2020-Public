// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.robotspecifics.demo;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.lib.BaseRobotContainer;
import frc.lib.PreferencesParser;
import frc.lib.drive.SwerveParameters;
import frc.lib.drive.SwerveParameters.ModulePosition;
import frc.lib.logging.Logger;
import frc.lib.operator.SquareDeadzoneCalculator;
import frc.lib.sensors.NavX;
import frc.robot.Constants.DriveTrainConstants;
import frc.robot.Constants.OIConstants;
import frc.robot.commands.DefaultSwerveDrive;
import frc.robot.commands.demo.DynamicShoot;
import frc.robot.commands.demo.RevShooterCommand;
import frc.robot.commands.demo.StopShooter;
import frc.robot.subsystems.blackout.Indexer;
import frc.robot.subsystems.demo.Shooter;
import frc.robot.subsystems.drive.ISwerveModule;
import frc.robot.subsystems.drive.SwerveDrive;
import frc.robot.subsystems.drive.SwerveModuleFalcons;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class DemoRobotContainer extends BaseRobotContainer {
  // The robot's subsystems and commands are defined here...
  private final Indexer indexer;
  private final NavX gyro;
  private final Shooter shooter;
  private final SquareDeadzoneCalculator squareDeadzone;
  private final SwerveDrive swerve;
  private final XboxController driverJoystick;
  private final ISwerveModule m_frontLeft = new SwerveModuleFalcons(getSwerveParameters()[0]);
  private final ISwerveModule m_frontRight = new SwerveModuleFalcons(getSwerveParameters()[1]);
  private final ISwerveModule m_backLeft = new SwerveModuleFalcons(getSwerveParameters()[2]);
  private final ISwerveModule m_backRight = new SwerveModuleFalcons(getSwerveParameters()[3]);

  private SwerveParameters[] getSwerveParameters() {
    return new SwerveParameters[] {
      new SwerveParameters(5, 4, 2, 2800, 4096, 0.1016, 8, ModulePosition.FRONT_LEFT),
      new SwerveParameters(3, 2, 1, 2860, 4096, 0.1016, 8, ModulePosition.FRONT_RIGHT),
      new SwerveParameters(7, 6, 3, 910, 4096, 0.1016, 8, ModulePosition.BACK_LEFT),
      new SwerveParameters(1, 10, 0, 3200, 4096, 0.1016, 8, ModulePosition.BACK_RIGHT),
    };
  }

  private ISwerveModule[] getSwerveModules() {
    return new ISwerveModule[] {
      m_frontLeft, m_frontRight, m_backLeft, m_backRight,
    };
  }

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public DemoRobotContainer(PreferencesParser prefs, Logger logger) {
    super(prefs, logger);

    driverJoystick = new XboxController(OIConstants.DRIVER_CONTROLLER_PORT);
    indexer = new Indexer(prefs, logger);
    gyro = new NavX(SPI.Port.kMXP);
    shooter = new Shooter();
    squareDeadzone = new SquareDeadzoneCalculator(OIConstants.DEFAULT_CONTROLLER_DEADZONE);
    swerve = new SwerveDrive(gyro, getSwerveModules(), logger);

    swerve.setDefaultCommand(
        new DefaultSwerveDrive(
            swerve,
            DriveTrainConstants.SWERVE_DRIVE_SPEED,
            DriveTrainConstants.SWERVE_ROTATION_SPEED * -1,
            () -> squareDeadzone.deadzone(driverJoystick.getY(GenericHID.Hand.kLeft) * -1),
            () -> squareDeadzone.deadzone(driverJoystick.getX(GenericHID.Hand.kLeft) * -1),
            () -> squareDeadzone.deadzone(driverJoystick.getX(GenericHID.Hand.kRight)),
            () -> driverJoystick.getStartButtonPressed()));

    shooter.setDefaultCommand(new RevShooterCommand(shooter));

    addSubsystems();
    configureButtonBindings();
  }

  protected void addSubsystems() {
    subsystems.add(indexer);
    subsystems.add(swerve);
    subsystems.add(shooter);
  }

  protected void configureButtonBindings() {
    new JoystickButton(driverJoystick, Button.kA.value).whenPressed(new StopShooter(shooter));

    // Code for analog trigger on controller, gets the triggers analog value
    Trigger shootTrigger = new Trigger(() -> driverJoystick.getTriggerAxis(Hand.kLeft) > 0);
    shootTrigger.whenActive(
        new DynamicShoot(shooter, () -> driverJoystick.getTriggerAxis(Hand.kLeft)));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return null;
  }
}
