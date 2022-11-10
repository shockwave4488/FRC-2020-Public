package org.usfirst.frc.team4488.robot.autonomous.actions;

import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.robot.systems.Intake;

public class NonBlockingIntake implements Action {

  private Intake intake = Intake.getInstance();

  private double rollerTimer;
  private static final double rollerDelay = 0.5;

  private boolean canLogMessage;

  public void start() {
    intake.intakeOut();
    intake.hopperOn();
    rollerTimer = Timer.getFPGATimestamp();
    Logging.getInstance()
        .writeToLogFormatted(this, "Intake Routine", "Intake, Indexer", "Intake Started");
  }

  public void update() {
    if (Timer.getFPGATimestamp() > rollerTimer + rollerDelay) {
      intake.setIntakeOn();
    }
    intake.hopperOn();
    if (canLogMessage) {
      Logging.getInstance()
          .writeToLogFormatted(this, "Intake Routine", "Intake, Indexer", "Intaking");
      canLogMessage = false;
    }
  }

  public void done() {
    intake.hopperOff();
    intake.setIntakeOff();
    Logging.getInstance()
        .writeToLogFormatted(this, "Intake Routine", "Intake, Indexer", "Intake Stopped");
  }

  public void abort() {
    done();
  }

  public boolean isFinished() {
    return true;
  }
}
