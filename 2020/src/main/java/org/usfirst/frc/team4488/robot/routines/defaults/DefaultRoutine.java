package org.usfirst.frc.team4488.robot.routines.defaults;

import org.usfirst.frc.team4488.robot.routines.Routine;

public abstract class DefaultRoutine extends Routine {

  @Override
  public boolean isFinished() {
    return false;
  }

  @Override
  public void done() {
    abort();
  }
}
