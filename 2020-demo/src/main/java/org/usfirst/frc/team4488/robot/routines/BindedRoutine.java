package org.usfirst.frc.team4488.robot.routines;

import org.usfirst.frc.team4488.robot.autonomous.AutoModeBase;
import org.usfirst.frc.team4488.robot.autonomous.actions.RunOnceAction;
import org.usfirst.frc.team4488.robot.systems.Subsystem;

public class BindedRoutine extends Routine {

  private final Routine routine;
  private final Bindable bind;
  private final boolean interruptable;
  private final boolean shouldHold;

  private boolean running;

  /**
   * 
   * @param routine
   * @param shouldHold If true, the routine will be ended immediently when releasing the button - otherwise it will wait for the routine to finish
   * @param bind
   * @param interruptable
   */
  public BindedRoutine(Routine routine, boolean shouldHold, Bindable bind, boolean interruptable) {
    this.routine = routine;
    this.bind = bind;
    this.interruptable = interruptable;
    this.shouldHold = shouldHold;

    for (Subsystem system : routine.getRequiredSystems()) {
      requireSystem(system);
    }
  }

  public void start() {
    routine.start();
    running = true;
  }

  public void update() {
    routine.update();
  }

  public void done() {
    routine.done();
    running = false;
  }

  public void abort() {
    routine.abort();
    running = false;
  }

  public boolean isFinished() {
    return routine.isFinished();
  }

  public boolean wantsStart() {
    return bind.get();
  }

  public boolean wantsRun() {
    // Running, hold -> bind
    // Running, !hold -> true
    // !Running, hold -> bind
    // !Running, !hold -> bind
    // If this doesn't have to be held, and this is already running, continue running
    // Otherwise, check the binding
    // "hold" = holding the button
    // "hold" doesn't mean continue running the routine
    return (!running || (running && shouldHold)) ? bind.get() : true;
  }

  public boolean isInterruptable() {
    return interruptable;
  }

  public boolean isAuto() {
    return routine instanceof AutoModeBase;
  }

  public boolean isRunOnce() {
    return routine instanceof RunOnceAction;
  }

  public String getRoutineName() {
    return routine.getClass().getSimpleName();
  }
}
