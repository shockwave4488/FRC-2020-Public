package org.usfirst.frc.team4488.robot.routines;

import org.usfirst.frc.team4488.robot.systems.ControlPanelSpinner;

public class ControlPanelSpinnerRoutine extends Routine {

  private ControlPanelSpinner instance = ControlPanelSpinner.getInstance();

  public ControlPanelSpinnerRoutine() {
    requireSystems(instance);
  }

  @Override
  public void start() {
    instance.startSpin();
  }

  @Override
  public void update() {
    instance.startSpin();
  }

  @Override
  public void done() {
    instance.stopSpin();
  }

  @Override
  public boolean isFinished() {
    return false;
  }

  @Override
  public void abort() {
    done();
  }
}
