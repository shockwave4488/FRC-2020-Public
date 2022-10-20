package org.usfirst.frc.team4488.robot.routines;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team4488.lib.controlsystems.SetPointProfile;
import org.usfirst.frc.team4488.lib.flowcontrol.EdgeTrigger;
import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.lib.operator.Controllers;
import org.usfirst.frc.team4488.lib.sensors.Limelight;
import org.usfirst.frc.team4488.lib.sensors.Limelight.LedControl;
import org.usfirst.frc.team4488.lib.util.MedianFilter;
import org.usfirst.frc.team4488.robot.RobotMap;
import org.usfirst.frc.team4488.robot.RobotName;
import org.usfirst.frc.team4488.robot.systems.Indexer;
import org.usfirst.frc.team4488.robot.systems.Intake;
import org.usfirst.frc.team4488.robot.systems.LEDController;
import org.usfirst.frc.team4488.robot.systems.LEDController.Color;
import org.usfirst.frc.team4488.robot.systems.LimelightManager;
import org.usfirst.frc.team4488.robot.systems.Shooter;
import org.usfirst.frc.team4488.robot.systems.SmartPCM;

public class SimpleShoot extends Routine {

  private static final int PRECISE_DONE_RANGE = 50;
  private static final int SLOPPY_DONE_RANGE = 100;
  private static final int PRECISE_DONE_CYCLES = 20;
  private static final int SLOPPY_DONE_CYCLES = 5;
  private static final int MEDIAN_FILTER_SIZE = 5;
  private static final int FIRST_BALL_OFFSET = -20;
  private static final int REALLY_BIG_NUMBER = 999999;
  private static final double LOWER_CONVEYOR_DELAY = 0.25;

  private final int doneRange;
  private final int doneCycles;
  private boolean rapidFire;
  private boolean startedShooting;
  private boolean firstBallShot;
  private boolean usingTime = false;
  private double time = 0;
  private boolean forceRpm = false;
  private double forcedRpm = 0;
  private double startShootingTime = 0;
  private boolean foundTarget = false;

  private Shooter shooter = Shooter.getInstance();
  private Intake intake = Intake.getInstance();
  private Indexer indexer = Indexer.getInstance();
  private LimelightManager llManager = LimelightManager.getInstance();
  private Limelight limelight = llManager.getFrontLimelight();

  private SetPointProfile distanceToRpm = new SetPointProfile();
  private EdgeTrigger edgeTriggerShot = new EdgeTrigger(false);
  private MedianFilter medianFilter = new MedianFilter(MEDIAN_FILTER_SIZE);

  public SimpleShoot(boolean precise, boolean rapidFire) {
    this.rapidFire = rapidFire;
    doneCycles = precise ? PRECISE_DONE_CYCLES : SLOPPY_DONE_CYCLES;
    doneRange = precise ? PRECISE_DONE_RANGE : SLOPPY_DONE_RANGE;

    requireSystems(shooter, indexer, llManager, intake);

    if (RobotMap.robotName == RobotName.Competition) {
      /*
      distanceToRpm.add(132, 4150);
      distanceToRpm.add(154, 4150);
      distanceToRpm.add(171, 4000);
      distanceToRpm.add(215, 3700);
      distanceToRpm.add(238, 3700);
      distanceToRpm.add(267, 3790);
      distanceToRpm.add(315, 3840);
      distanceToRpm.add(356, 3910);
      distanceToRpm.add(400, 4120);
      distanceToRpm.add(450, 4360);
      */
      distanceToRpm.add(127, 5500);
      distanceToRpm.add(135, 4800);
      distanceToRpm.add(143, 4500);
      distanceToRpm.add(160, 4400);
      distanceToRpm.add(180, 4360);
      distanceToRpm.add(200, 4300);
      distanceToRpm.add(220, 4230);
      distanceToRpm.add(240, 4050);
      distanceToRpm.add(260, 3990);
      distanceToRpm.add(280, 4000);
      distanceToRpm.add(400, 4500);
    } else {
      distanceToRpm.add(144, 4750);
      distanceToRpm.add(170, 4700);
      distanceToRpm.add(195, 4210);
      distanceToRpm.add(255, 3770);
      distanceToRpm.add(270, 3840);
      distanceToRpm.add(285, 4000);
      distanceToRpm.add(310, 4120);
      distanceToRpm.add(340, 4200);
      distanceToRpm.add(384, 4380);
      distanceToRpm.add(404, 4410);
    }
  }

  public SimpleShoot(boolean precise, boolean rapidFire, double time) {
    this(precise, rapidFire);
    usingTime = true;
    this.time = time;
  }

  public SimpleShoot(boolean precise, boolean rapidFire, double forcedRpm, boolean sorry) {
    this(precise, rapidFire);
    this.forceRpm = true;
    this.forcedRpm = forcedRpm;
  }

  public SimpleShoot(boolean precise, boolean rapidFire, double time, double forcedRpm) {
    this(precise, rapidFire);
    usingTime = true;
    this.time = time;
    forceRpm = true;
    this.forcedRpm = forcedRpm;
  }

  public void start() {
    foundTarget = false;
    medianFilter.clear();
    Logging.getInstance()
        .writeToLogFormatted(this, "ShootingRoutine", "Shooter", "Shooter Started");
    LEDController.getInstance().setColor(Color.Purple);
    limelight.setLed(LedControl.ForceOn);
    startedShooting = false;
    firstBallShot = false;
    SmartPCM.getInstance().stopCompressor();
    intake.hopperOn();
    startShootingTime = REALLY_BIG_NUMBER;
  }

  public void update() {
    if (intake.intakeIsOut()) intake.setIntakeOn();

    if (edgeTriggerShot.getFallingUpdate(indexer.fourthBeamBroken())) {
      Logging.getInstance()
          .writeToLogFormatted(this, "ShootingRoutine", "Indexer, Shooter", "Power Cell Shot");
      firstBallShot = true;
    }

    shooter.setSpeed(getSpeed());

    boolean onTarget = shooter.onTarget(doneRange, doneCycles) && foundTarget;
    SmartDashboard.putBoolean("on target", onTarget);
    if (onTarget && !startedShooting) {
      startShootingTime = Timer.getFPGATimestamp();
      startedShooting = true;
    }

    if (onTarget || (startedShooting && rapidFire)) {
      indexer.moveUpperConveyor(true);
      if (Timer.getFPGATimestamp() - startShootingTime > LOWER_CONVEYOR_DELAY)
        indexer.moveLowerConveyor(true);
    } else indexer.stopConveyor();
  }

  private void stop() {
    SmartPCM.getInstance().startCompressor();
    LEDController.getInstance().setColor(Color.Default);
    limelight.setLed(LedControl.ForceOff);
    shooter.setSpeed(0);
    indexer.stopConveyor();
    intake.hopperOff();
    intake.setIntakeOff();
  }

  public void done() {
    stop();
    Logging.getInstance()
        .writeToLogFormatted(this, "ShootingRoutine", "Shooter", "Shooter Stopped");
  }

  public void abort() {
    stop();
    Logging.getInstance()
        .writeToLogFormatted(this, "ShootingRoutine", "Shooter", "Shooter Aborted");
  }

  public boolean isFinished() {
    if (usingTime) {
      return Timer.getFPGATimestamp() - startShootingTime > time;
    } else {
      Controllers xbox = Controllers.getInstance();
      double throttle = Math.abs(xbox.getLeftStickY(xbox.m_primary));
      double turn = Math.abs(xbox.getRightStickX(xbox.m_primary));
      return throttle > 0.2 || turn > 0.2;
    }
  }

  private double getSpeed() {
    if (forceRpm) {
      foundTarget = true;
      return forcedRpm;
    }

    double dist = limelight.getEstimatedDistance();
    medianFilter.update(dist);

    if (medianFilter.isFull()) foundTarget = true;

    double speed = distanceToRpm.get(medianFilter.getMedian());
    if (rapidFire && !firstBallShot) speed += FIRST_BALL_OFFSET;

    return speed;
  }
}
