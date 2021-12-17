package org.usfirst.frc.team4488.robot.routines;

import org.usfirst.frc.team4488.lib.flowcontrol.EdgeTrigger;
import org.usfirst.frc.team4488.robot.systems.Indexer;
import org.usfirst.frc.team4488.robot.systems.Intake;
import org.usfirst.frc.team4488.robot.systems.Shooter;
import org.usfirst.frc.team4488.robot.systems.SubsystemManager;
import org.usfirst.frc.team4488.robot.systems.drive.WestCoastDrive;

public class LineUpAndShoot extends SeriesRoutine {

  private final double DEFAULT_PRESPIN_SPEED = 4200; // unknown
  private double prespinSpeed = DEFAULT_PRESPIN_SPEED;

  private final Intake intake = Intake.getInstance();
  private final Indexer indexer = Indexer.getInstance();
  private final Shooter shooter = Shooter.getInstance();
  private final WestCoastDrive drive = (WestCoastDrive) SubsystemManager.getInstance().getDrive();

  private final AlignDriveWithCamera alignRoutine;
  private final ShootingRoutine shootRoutine;

  private EdgeTrigger startingShootingRoutine = new EdgeTrigger(false);
  private boolean startedShooter = false;

  public LineUpAndShoot(boolean parallaxAdj, boolean precise) {
    requireSystems(intake, indexer, shooter, drive);

    alignRoutine = new AlignDriveWithCamera(parallaxAdj);
    shootRoutine = new ShootingRoutine(precise, true);
    appendRoutine(alignRoutine);
    appendRoutine(shootRoutine);
  }

  public LineUpAndShoot(boolean parallaxAdj, boolean precise, double time) {
    requireSystems(indexer, shooter, drive);

    alignRoutine = new AlignDriveWithCamera(parallaxAdj);
    shootRoutine = new ShootingRoutine(precise, true, time);
    appendRoutine(alignRoutine);
    appendRoutine(shootRoutine);
  }

  public LineUpAndShoot(boolean parallaxAdj, boolean precise, double forcedRpm, boolean sorry) {
    requireSystems(indexer, shooter, drive);
    prespinSpeed = forcedRpm;

    alignRoutine = new AlignDriveWithCamera(parallaxAdj);
    shootRoutine = new ShootingRoutine(precise, true, forcedRpm, false);
    appendRoutine(alignRoutine);
    appendRoutine(shootRoutine);
  }

  public LineUpAndShoot(boolean parallaxAdj, boolean precise, double time, double forcedRpm) {
    requireSystems(indexer, shooter, drive);
    prespinSpeed = forcedRpm;

    alignRoutine = new AlignDriveWithCamera(parallaxAdj);
    shootRoutine = new ShootingRoutine(precise, true, time, forcedRpm);
    appendRoutine(alignRoutine);
    appendRoutine(shootRoutine);
  }

  public void constantStart() {
    shooter.setSpeed(prespinSpeed);
  }

  public void constantUpdate() {}

  public void constantDone() {}

  public void constantAbort() {}

  public boolean constantIsFinished() {
    return true;
  }
  /*
  public void start() {
    startedShooter = false;
    startingShootingRoutine.update(false);
    alignRoutine.start();
  }

  public void update() {
    alignRoutine.update();
    boolean onTarget = alignRoutine.isFinished();

    if(!startedShooter) {
      shooter.setSpeed(PRESPIN_SPEED);
    }

    if(onTarget) {
      if(!startedShooter) {
        shootRoutine.start();
        startedShooter = true;
      }
      shootRoutine.update();
    } else if(precise) {
      indexer.stopConveyor();
    }
  }

  public void done() {
    abort();
  }

  public void abort() {
    alignRoutine.abort();
    indexer.stopConveyor();
    shooter.setSpeed(0);
  }

  public boolean isFinished() {
    return shootRoutine.isFinished();
  }
  //*/
}
