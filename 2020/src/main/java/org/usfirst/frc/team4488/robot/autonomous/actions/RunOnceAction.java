package org.usfirst.frc.team4488.robot.autonomous.actions;

import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.robot.routines.Routine;

/**
 * Template action for something that only needs to be done once and has no need for updates.
 *
 * @see Action
 */
public abstract class RunOnceAction extends Routine implements Action {
  Logging logger = Logging.getInstance();

  @Override
  public boolean isFinished() {
    return true;
  }

  @Override
  public void update() {}

  @Override
  public void done() {
    logger.writeToLogFormatted(this, "done()");
  }

  @Override
  public void start() {
    logger.writeToLogFormatted(this, "start()");
    runOnce();
  }

  public void abort() {}

  public abstract void runOnce();
}
