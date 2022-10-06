package org.usfirst.frc.team4488.robot.routines;

import java.util.ArrayList;
import org.usfirst.frc.team4488.robot.systems.Subsystem;

public abstract class SeriesRoutine extends Routine {

  private ArrayList<Routine> subRoutines = new ArrayList<Routine>();
  private int currentRoutine = 0;

  protected void appendRoutine(Routine routine) {
    subRoutines.add(routine);
    for (Subsystem system : routine.getRequiredSystems()) requireSystem(system);
  }

  public void start() {
    constantStart();
    currentRoutine = 0;
    if (!doneWithSubRoutines()) getCurrentSubRoutine().start();
  }

  public void update() {
    if (!doneWithSubRoutines()) {
      Routine currentSubRoutine = getCurrentSubRoutine();
      currentSubRoutine.update();
      if (currentSubRoutine.isFinished()) {
        currentSubRoutine.done();
        currentRoutine++;
        if (!doneWithSubRoutines()) {
          getCurrentSubRoutine().start();
        }
      }
    }

    constantUpdate();
  }

  public void done() {
    constantDone();
  }

  public void abort() {
    if (!doneWithSubRoutines()) getCurrentSubRoutine().abort();
    constantAbort();
  }

  public boolean isFinished() {
    return doneWithSubRoutines() && constantIsFinished();
  }

  protected boolean doneWithSubRoutines() {
    return currentRoutine >= subRoutines.size();
  }

  protected Routine getCurrentSubRoutine() {
    return subRoutines.get(currentRoutine);
  }

  protected abstract void constantStart();

  protected abstract void constantUpdate();

  protected abstract void constantDone();

  protected abstract void constantAbort();

  protected abstract boolean constantIsFinished();
}
