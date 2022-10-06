package org.usfirst.frc.team4488.robot.routines;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.lib.operator.Controllers;
import org.usfirst.frc.team4488.robot.systems.Indexer;
import org.usfirst.frc.team4488.robot.systems.Shooter;

public class TestShootingRoutine extends Routine {

  /* Controller Instance */
  private Controllers xbox = Controllers.getInstance();

  /* Subsystem Instances */
  private Shooter shooter = Shooter.getInstance();

  private Indexer indexer = Indexer.getInstance();

  /* Class variables */
  private double shooterTargetSpeed = 0; /* stores the target speed of the shooter wheel */

  /* RPM magic numbers - variables are int to purposely limit precision */
  private static final int shooterStageOne = 0;

  private static final int shooterStageTwo = 3000;
  private static final int shooterStageThree = 4700;

  private final int rpmIncrement = 10;
  private double shooterRecoveryTime;
  private double shooterStartTime = Timer.getFPGATimestamp();
  private boolean wasOnTarget = true;
  private double lowestRPM;

  public TestShootingRoutine() {
    requireSystems(indexer, shooter);
  }

  @Override
  public void start() {
    /*
     * Erases previous values of target speeds, so that when the robot enables, the motors do not
     * automatically go to the previously stored speeds.
     */
    Logging.getInstance()
        .writeToLogFormatted(this, this, "Shooter, Indexer", "Test shooting routine started");
    shooterTargetSpeed = 4300;

    shooter.setSpeed(shooterTargetSpeed);
  }

  @Override
  public void update() {
    if (!xbox.getLeftTrigger(xbox.m_primary)) indexer.stopConveyor();
    else indexer.moveConveyor(true);

    shooter.setSpeed(shooterTargetSpeed);

    /* Control mechanims to quickly change shooter RPM speeds */
    if (xbox.getA(xbox.m_primary)) {
      shooterTargetSpeed = shooterStageOne;
    } else if (xbox.getB(xbox.m_primary)) {
      shooterTargetSpeed = shooterStageTwo;
    } else if (xbox.getY(xbox.m_primary)) {
      shooterTargetSpeed = shooterStageThree;
    }

    if (xbox.getRightBumper(xbox.m_primary)) {
      shooterTargetSpeed += rpmIncrement;
    } else if (xbox.getLeftBumper(xbox.m_primary)) {
      shooterTargetSpeed -= rpmIncrement;
    }

    if (!shooter.onTarget() && wasOnTarget) {
      shooterStartTime = Timer.getFPGATimestamp();
      wasOnTarget = false;
      lowestRPM = shooter.getCurrentSpeed();
    }
    if (shooter.onTarget() && !wasOnTarget) {
      shooterRecoveryTime = Timer.getFPGATimestamp() - shooterStartTime;
      wasOnTarget = true;
      SmartDashboard.putNumber("Shooter Recovery Time", shooterRecoveryTime);
    }
    if (!shooter.onTarget() && shooter.getCurrentSpeed() < shooterTargetSpeed) {
      double currentRPM = shooter.getCurrentSpeed();
      lowestRPM = currentRPM < lowestRPM ? currentRPM : lowestRPM;
      SmartDashboard.putNumber("Lowest Shooter RPM", lowestRPM);
    }
  }

  @Override
  public void done() {
    indexer.stopConveyor();
    shooter.setSpeed(0);
    shooter.off();
    Logging.getInstance()
        .writeToLogFormatted(this, this, "Shooter, Indexer", "Test shooting routine done");
  }

  @Override
  public void abort() {
    done();
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
