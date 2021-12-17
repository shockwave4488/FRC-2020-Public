package org.usfirst.frc.team4488.robot.autonomous.actions;

import org.usfirst.frc.team4488.robot.systems.Intake;

public class IntakeOut extends RunOnceAction {

  @Override
  public void runOnce() {
    Intake.getInstance().intakeOut();
  }
}
