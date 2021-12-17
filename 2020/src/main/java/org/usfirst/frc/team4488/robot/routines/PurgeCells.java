package org.usfirst.frc.team4488.robot.routines;

import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.lib.operator.Controllers;
import org.usfirst.frc.team4488.robot.systems.Indexer;
import org.usfirst.frc.team4488.robot.systems.Intake;
import org.usfirst.frc.team4488.robot.systems.Shooter;

public class PurgeCells extends Routine {

  private static final int REALLY_BIG_NUMBER = 999999;
  private static final double LOWER_CONVEYOR_DELAY = 0.25;

  private Indexer indexer = Indexer.getInstance();
  private Shooter shooter = Shooter.getInstance();
  private Intake intake = Intake.getInstance();

  private Controllers xbox = Controllers.getInstance();

  private double timeAtStart;
  private double runTime;
  private boolean isAuto;
  private boolean isDone;
  private double startedConveyorTime;

  public PurgeCells() {
    requireSystem(indexer);
    requireSystem(shooter);
    requireSystem(intake);
    isAuto = false;
  }

  public PurgeCells(double timeToRun) {
    requireSystem(indexer);
    requireSystem(shooter);
    requireSystem(intake);
    isAuto = true;
    runTime = timeToRun;
  }

  @Override
  public void start() {
    startedConveyorTime = REALLY_BIG_NUMBER;
    timeAtStart = Timer.getFPGATimestamp();
    isDone = false;
    shooter.setSpeed(1000);
    intake.hopperOn();
    Logging.getInstance()
        .writeToLogFormatted(
            this, "PurgeCells", "Intake, Indexer, Shooter", "Ejecting all power cells");
  }

  @Override
  public void update() {
    if (shooter.getCurrentSpeed() > 800) {
      if (startedConveyorTime == REALLY_BIG_NUMBER) startedConveyorTime = Timer.getFPGATimestamp();
      if (Timer.getFPGATimestamp() - startedConveyorTime > LOWER_CONVEYOR_DELAY)
        indexer.moveLowerConveyor(true);
      indexer.moveUpperConveyor(true);

    } else indexer.stopConveyor();
    if (isAuto) {
      if (timeAtStart + runTime < Timer.getFPGATimestamp()) {
        isDone = true;
      }
    } else {
      if (!xbox.getY(xbox.m_secondary)) {
        isDone = true;
      }
    }
  }

  @Override
  public void done() {
    shooter.setSpeed(0);
    intake.hopperOff();
    indexer.stopConveyor();
  }

  @Override
  public void abort() {
    done();
  }

  @Override
  public boolean isFinished() {
    return isDone;
  }
}
