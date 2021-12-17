package frc.robot.commands.demo;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.demo.Shooter;
import java.util.function.DoubleSupplier;

public class DynamicShoot extends CommandBase {

  private final Shooter shooterSubsystem;

  private static final double STRONG_SHOOT = 0.75;
  private static final double WEAK_SHOOT = 0.25;
  private static final double SHOOT_THRESHOLD = 0.5;

  private DoubleSupplier perceivedPower;

  public DynamicShoot(Shooter subsystem, DoubleSupplier power) {
    shooterSubsystem = subsystem;
    addRequirements(shooterSubsystem);

    perceivedPower = power;
  }

  public void execute() {
    // sets shooter to one of two values based on strength of analog trigger input
    double value = perceivedPower.getAsDouble();
    if (value >= SHOOT_THRESHOLD) {
      shooterSubsystem.setSpeed(STRONG_SHOOT);
    } else if (value < SHOOT_THRESHOLD) {
      shooterSubsystem.setSpeed(WEAK_SHOOT);
    }
  }
}
