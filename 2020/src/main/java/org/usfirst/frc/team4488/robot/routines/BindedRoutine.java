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
