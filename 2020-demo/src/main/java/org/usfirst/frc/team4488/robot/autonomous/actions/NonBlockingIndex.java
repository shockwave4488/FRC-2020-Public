package org.usfirst.frc.team4488.robot.autonomous.actions;

import org.usfirst.frc.team4488.lib.flowcontrol.EdgeTrigger;
import org.usfirst.frc.team4488.robot.systems.Indexer;

public class NonBlockingIndex implements Action {

  private static final int TRANSITION_BALL_SPACING = 1250;

  private Indexer indexer = Indexer.getInstance();
  private EdgeTrigger ballTransitionedTrigger = new EdgeTrigger(false);

  private boolean ballTransitioning = false;
  private double ballTransitionedTicks = 0;

  @Override
  public void start() {
    indexer.stopConveyor();
    ballTransitioning = false;
    ballTransitionedTicks = 0;
  }

  @Override
  public void update() {
    boolean upperConveyorFull = indexer.fourthBeamBroken();
    boolean ballOnTransition = indexer.secondBeamBroken();

    boolean ballTransitioned = ballTransitionedTrigger.getFallingUpdate(ballOnTransition);
    if (ballTransitioned) {
      ballTransitionedTicks = indexer.getShooterConveyorTicks();
      ballTransitioning = true;
    }

    if (ballTransitioning) {
      if (!upperConveyorFull) {
        indexer.moveLowerConveyor(false);
        indexer.moveUpperConveyor(false);

        if (indexer.getShooterConveyorTicks() - ballTransitionedTicks > TRANSITION_BALL_SPACING) {
          ballTransitioning = false;
        }
      } else {
        ballTransitioning = false;
        indexer.stopUpperConveyor();
      }
    } else {
      if (!ballOnTransition) {
        // no ball in lower conveyor
        indexer.moveLowerConveyor(false);
        indexer.stopUpperConveyor();
      } else if (!upperConveyorFull) {
        // ball in lower conveyor, space in upper
        indexer.moveLowerConveyor(false);
        indexer.moveUpperConveyor(false);
      } else if (upperConveyorFull) {
        // cant hold any more
        indexer.stopConveyor();
      }
    }
  }

  public void done() {
    indexer.stopConveyor();
  }

  public boolean isFinished() {
    return true;
  }
}
