package org.usfirst.frc.team4488.robot.routines;

import java.util.function.DoubleSupplier;
import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.robot.systems.Indexer;
import org.usfirst.frc.team4488.robot.systems.LEDController;
import org.usfirst.frc.team4488.robot.systems.LEDController.Color;
import org.usfirst.frc.team4488.robot.systems.Shooter;
import org.usfirst.frc.team4488.robot.systems.SmartPCM;

public class SimpleShoot extends Routine {
  private final DoubleSupplier rpm;

  private Shooter shooter = Shooter.getInstance();
  private Indexer indexer = Indexer.getInstance();

  public SimpleShoot(DoubleSupplier rpm) {
    this.rpm = rpm;
    requireSystems(shooter, indexer);
  }

  @Override
  public void start() {
    LEDController.getInstance().setColor(Color.Purple);
    Logging.getInstance().writeToLogFormatted(this, "SimpleShoot", "Shooter", "Shooter Started");
    SmartPCM.getInstance().stopCompressor();

    indexer.moveConveyor(true);
  }

  @Override
  public void update() {
    shooter.setSpeed(rpm.getAsDouble());
  }

  @Override
  public void done() {
    SmartPCM.getInstance().startCompressor();
    LEDController.getInstance().setColor(Color.Default);
    shooter.setSpeed(0);
    indexer.stopConveyor();
  }

  @Override
  public void abort() {
    done();
    Logging.getInstance().writeToLogFormatted(this, "SimpleShoot", "Shooter", "Shooter Aborted");
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
