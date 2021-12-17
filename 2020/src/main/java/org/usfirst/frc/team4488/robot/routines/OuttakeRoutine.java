package org.usfirst.frc.team4488.robot.routines;

import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.robot.systems.Intake;

public class OuttakeRoutine extends Routine {

  private Intake intake = Intake.getInstance();

  public OuttakeRoutine() {
    requireSystems(intake);
  }

  public void start() {
    intake.intakeOut();
    Logging.getInstance().writeToLogFormatted(this, "Outtake Routine", "Intake", "Intake Started");
  }

  public void update() {
    if (intake.isIntakeReady()) {
      intake.setIntakeReverse();
    }
  }

  public void done() {
    intake.setIntakeOff();
    Logging.getInstance().writeToLogFormatted(this, "Outtake Routine", "Intake", "Intake Stopped");
  }

  public void abort() {
    done();
  }

  public boolean isFinished() {
    return false;
  }
}
