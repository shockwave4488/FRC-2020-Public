package frc.robot.commands.demo;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.demo.Shooter;

public class RevShooterCommand extends CommandBase {

  private final Shooter shooterSubsystem;

  private double revupShooterSpeed = 0; // this must be changed

  public RevShooterCommand(Shooter subsystem) {
    shooterSubsystem = subsystem;
    addRequirements(shooterSubsystem);
  }

  public void execute() {
    shooterSubsystem.setSpeed(revupShooterSpeed);
  }
}
