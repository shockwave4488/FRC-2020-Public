package org.usfirst.frc.team4488.robot.routines;

import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.lib.operator.Controllers;
import org.usfirst.frc.team4488.robot.systems.Climber;
import org.usfirst.frc.team4488.robot.systems.Climber.HookPositions;
import org.usfirst.frc.team4488.robot.systems.Indexer;
import org.usfirst.frc.team4488.robot.systems.Intake;
import org.usfirst.frc.team4488.robot.systems.Shooter;
import org.usfirst.frc.team4488.robot.systems.drive.Drive;

public class ClimbRoutine extends Routine {

  private Controllers xbox = Controllers.getInstance();
  private Climber climber = Climber.getInstance();
  private Drive drive = Drive.getInstance();
  private Logging logger = Logging.getInstance();

  // power doubles are between 0 (no power) and 1 (max power)
  private static final double STICK_DEADZONE = 0.2;
  private static final double MAX_STICK_POWER = 0.5;

  private enum ClimbState {
    AligningDrive,
    DeployingForks,
    AttachingHooks,
    WaitingForBuddy,
    Lifting,
    Done
  }

  private ClimbState climbState;

  public ClimbRoutine() {
    requireSystems(
        drive, climber, Indexer.getInstance(), Intake.getInstance(), Shooter.getInstance());
  }

  public void start() {
    climbState = ClimbState.AligningDrive;
    logger.writeToLogFormatted(this, "ClimbRoutine", "Climber", climbState.name());
  }

  public void update() {
    switch (climbState) {
      case AligningDrive:
        double forward = xbox.deadzone(xbox.getLeftStickY(xbox.m_primary));
        double strafe = xbox.deadzone(xbox.getLeftStickX(xbox.m_primary));
        double turn = xbox.deadzone(xbox.getRightStickX(xbox.m_primary));
        drive.controllerUpdate(strafe, forward, turn);

        // Turn off drive powers and initate climbing process
        if (xbox.getA(xbox.m_primary)) {
          drive.setPowers(0, 0);
          climbState = ClimbState.DeployingForks;
          logger.writeToLogFormatted(this, "ClimbRoutine", "Climber", climbState.name());
        }
        break;

      case DeployingForks:
        climber.forksOut();
        if (climber.forksAreOut()) {
          climbState = ClimbState.AttachingHooks;
          logger.writeToLogFormatted(this, "ClimbRoutine", "Climber", climbState.name());
        }
        break;

      case AttachingHooks:
        climber.setHooksPosition(HookPositions.Attaching);
        if (climber.hooksAreAttached()) {
          climbState = ClimbState.Lifting;
          logger.writeToLogFormatted(this, "ClimbRoutine", "Climber", climbState.name());
        }
        break;

      case Lifting:
        double power = xbox.getLeftStickY(xbox.m_primary);
        if (Math.abs(power) < STICK_DEADZONE) power = 0;
        power = (power - STICK_DEADZONE) / (1 - STICK_DEADZONE); // scale 0 to 1
        power *= MAX_STICK_POWER;
        climber.setHooksPosition(HookPositions.Climbing);
        if (climber.hooksAreIn()) {
          climbState = ClimbState.Done;
          logger.writeToLogFormatted(this, "ClimbRoutine", "Climber", climbState.name());
        }
        break;

      case Done:
        break;
    }
  }

  public void done() {}

  public void abort() {
    switch (climbState) {
      case AligningDrive:
        // Climb hasnt started yet
        break;

      case DeployingForks:
        climber.forksIn();
        break;

      case AttachingHooks:
      case WaitingForBuddy:
      case Lifting:
      case Done:
        // Too far into the climb to go back
        break;
    }
  }

  public boolean isFinished() {
    return false;
  }
}
