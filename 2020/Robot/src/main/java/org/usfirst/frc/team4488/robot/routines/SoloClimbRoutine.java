package org.usfirst.frc.team4488.robot.routines;

import org.usfirst.frc.team4488.lib.operator.Controllers;
import org.usfirst.frc.team4488.robot.systems.Climber;
import org.usfirst.frc.team4488.robot.systems.Climber.HookPositions;
import org.usfirst.frc.team4488.robot.systems.Indexer;
import org.usfirst.frc.team4488.robot.systems.LEDController;
import org.usfirst.frc.team4488.robot.systems.LEDController.Color;
import org.usfirst.frc.team4488.robot.systems.drive.FalconDrive;

public class SoloClimbRoutine extends Routine {

  private static enum State {
    Attaching,
    Climbing,
    Done
  }

  private State state;

  private Controllers xbox = Controllers.getInstance();
  private Climber climber = Climber.getInstance();
  private Indexer indexer = Indexer.getInstance();
  private FalconDrive drive = FalconDrive.getInstance();

  public SoloClimbRoutine() {
    requireSystems(climber, drive, indexer);
  }

  public void start() {
    state = State.Attaching;
    climber.setHooksPosition(HookPositions.Attaching);
  }

  public void update() {
    switch (state) {
      case Attaching:
        double forward = xbox.deadzone(xbox.getLeftStickY(xbox.m_primary));
        double strafe = xbox.deadzone(xbox.getLeftStickX(xbox.m_primary));
        double turn = xbox.deadzone(xbox.getRightStickX(xbox.m_primary));
        drive.controllerUpdate(strafe, forward, turn);

        if (xbox.getY(xbox.m_primary)) state = State.Climbing;
        break;

      case Climbing:
        LEDController.getInstance().setColor(Color.Rainbow);
        climber.setHooksPosition(HookPositions.Climbing);
        if (climber.hooksAreIn()) state = State.Done;
        break;

      case Done:
        break;
    }
  }

  public void done() {}

  public void abort() {
    switch (state) {
      case Attaching:
        climber.setHooksPosition(HookPositions.Resting);
        break;

      case Climbing:
      case Done:
        // too late
        break;
    }
  }

  public boolean isFinished() {
    return state == State.Done;
  }
}
