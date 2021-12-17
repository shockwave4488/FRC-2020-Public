package org.usfirst.frc.team4488.robot.routines;

import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.robot.systems.Intake;

public class IntakeRoutine extends Routine {

  private Intake intake = Intake.getInstance();

  public IntakeRoutine() {
    requireSystems(intake);
  }

  public void start() {
    intake.intakeOut();
    Logging.getInstance().writeToLogFormatted(this, "Intake Routine", "Intake", "Intake Started");
  }

  public void update() {
    if (intake.isIntakeReady()) {
      intake.setIntakeOn();
      intake.hopperOn();
    }
  }

  public void done() {
    intake.hopperOff();
    intake.setIntakeOff();
    Logging.getInstance().writeToLogFormatted(this, "Intake Routine", "Intake", "Intake Stopped");
  }

  public void abort() {
    done();
  }

  public boolean isFinished() {
    return false;
  }
}
