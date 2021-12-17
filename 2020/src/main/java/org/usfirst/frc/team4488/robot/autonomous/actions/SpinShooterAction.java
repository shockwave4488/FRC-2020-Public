package org.usfirst.frc.team4488.robot.autonomous.actions;

import org.usfirst.frc.team4488.robot.systems.Shooter;

public class SpinShooterAction extends RunOnceAction {

  double rpms;

  public SpinShooterAction(double rpms) {
    this.rpms = rpms;
  }

  public void runOnce() {
    Shooter.getInstance().setSpeed(rpms);
  }
}
