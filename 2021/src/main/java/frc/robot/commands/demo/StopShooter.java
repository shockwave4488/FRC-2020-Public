package frc.robot.commands.demo;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.demo.Shooter;

public class StopShooter extends CommandBase {

  private final Shooter shooterSubsystem;

  public StopShooter(Shooter subsystem) {
    shooterSubsystem = subsystem;
    addRequirements(shooterSubsystem);
  }

  public void execute() {
    shooterSubsystem.setSpeed(0);
  }
}
