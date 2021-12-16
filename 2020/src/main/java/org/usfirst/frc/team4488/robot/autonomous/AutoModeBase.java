package org.usfirst.frc.team4488.robot.autonomous;

import java.util.ArrayList;
import java.util.Iterator;
import org.usfirst.frc.team4488.robot.autonomous.actions.Action;
import org.usfirst.frc.team4488.robot.routines.Routine;

/**
 * An abstract class that is the basis of the robot's autonomous routines. This is implemented in
 * auto modes (which are routines that do actions).
 */
public abstract class AutoModeBase extends Routine {

  private ArrayList<Action> actions = new ArrayList<Action>();
  private Iterator<Action> actionQueue;
  private Action currAction;

  protected void addAction(Action action) {
    actions.add(action);
  }

  public void start() {
    modeStart();
    actionQueue = actions.iterator();
    if (!actionsFinished()) startNextAction();
  }

  private void startNextAction() {
    currAction = actionQueue.next();
    currAction.start();
  }

  public void update() {
    modeUpdate();
    if (!actionsFinished()) {
      currAction.update();
      if (currAction.isFinished()) {
        currAction.done();
        if (actionQueue.hasNext()) startNextAction();
        else currAction = null;
      }
    }
  }

  private boolean actionsFinished() {
    return currAction == null && !actionQueue.hasNext();
  }

  public void done() {
    modeDone();
  }

  public void abort() {
    if (currAction instanceof Routine) ((Routine) currAction).abort();
    modeAbort();
  }

  public boolean isFinished() {
    return actionsFinished() && modeIsFinished();
  }

  protected abstract void modeStart();

  protected abstract void modeUpdate();

  protected abstract void modeDone();

  protected abstract void modeAbort();

  protected abstract boolean modeIsFinished();
}
