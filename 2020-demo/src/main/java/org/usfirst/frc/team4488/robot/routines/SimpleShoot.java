package org.usfirst.frc.team4488.robot.routines;

import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.robot.systems.Indexer;
import org.usfirst.frc.team4488.robot.systems.LEDController;
import org.usfirst.frc.team4488.robot.systems.LEDController.Color;
import org.usfirst.frc.team4488.robot.systems.Shooter;
import org.usfirst.frc.team4488.robot.systems.SmartPCM;

public class SimpleShoot extends Routine {
  private final double rpm;

  private Shooter shooter = Shooter.getInstance();
  private Indexer indexer = Indexer.getInstance();

  public SimpleShoot(double rpm) {
    this.rpm = rpm;
    requireSystems(shooter, indexer);
  }

  public void start() {
    LEDController.getInstance().setColor(Color.Purple);
    Logging.getInstance()
    .writeToLogFormatted(this, "SimpleShoot", "Shooter", "Shooter Started");;
    SmartPCM.getInstance().stopCompressor();

    shooter.setSpeed(rpm);
    indexer.moveConveyor(true);
  }

  @Override
  public void update() {}

  private void stop() {
    SmartPCM.getInstance().startCompressor();
    LEDController.getInstance().setColor(Color.Default);
    shooter.setSpeed(0);
    indexer.stopConveyor();
  }

  public void done() {
    stop();
    Logging.getInstance()
        .writeToLogFormatted(this, "SimpleShoot", "Shooter", "Shooter Stopped");
  }

  public void abort() {
    stop();
    Logging.getInstance()
        .writeToLogFormatted(this, "ShootingRoutine", "Shooter", "Shooter Aborted");
  }

  @Override
  public boolean isFinished() {
      return false;
  }
}
