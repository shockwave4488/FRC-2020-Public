package org.usfirst.frc.team4488.robot.routines;

import org.usfirst.frc.team4488.lib.flowcontrol.EdgeTrigger;
import org.usfirst.frc.team4488.robot.systems.Indexer;
import org.usfirst.frc.team4488.robot.systems.Shooter;
import org.usfirst.frc.team4488.robot.systems.SubsystemManager;
import org.usfirst.frc.team4488.robot.systems.drive.WestCoastDrive;

public class ConstantLineUpAndShoot extends Routine {

  private final double PRESPIN_SPEED = 3000; // unknown

  private final Indexer indexer = Indexer.getInstance();
  private final Shooter shooter = Shooter.getInstance();
  private final WestCoastDrive drive = (WestCoastDrive) SubsystemManager.getInstance().getDrive();

  private final AlignDriveWithCamera alignRoutine;
  private final ShootingRoutine shootRoutine;

  private EdgeTrigger startingShootingRoutine = new EdgeTrigger(false);
  private boolean startedShooter = false;
  private final boolean precise;

  public ConstantLineUpAndShoot(boolean precise) {
    this.precise = precise;
    requireSystems(indexer, shooter, drive);
    alignRoutine = new AlignDriveWithCamera(true);
    shootRoutine = new ShootingRoutine(precise, true);
  }

  public void start() {
    startedShooter = false;
    startingShootingRoutine.update(false);
    alignRoutine.start();
  }

  public void update() {
    alignRoutine.update();
    boolean onTarget = alignRoutine.isFinished();

    if (!startedShooter) {
      shooter.setSpeed(PRESPIN_SPEED);
    }

    if (onTarget) {
      if (!startedShooter) {
        shootRoutine.start();
        startedShooter = true;
      }
      shootRoutine.update();
    } else if (precise) {
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
}
