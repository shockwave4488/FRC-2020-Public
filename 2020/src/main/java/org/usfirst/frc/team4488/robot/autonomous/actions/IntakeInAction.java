package org.usfirst.frc.team4488.robot.autonomous.actions;

import org.usfirst.frc.team4488.robot.systems.Intake;

public class IntakeInAction extends RunOnceAction {

  public IntakeInAction() {
    requireSystem(Intake.getInstance());
  }

  public void runOnce() {
    Intake.getInstance().intakeIn();
    Intake.getInstance().setIntakeOff();
  }
}
