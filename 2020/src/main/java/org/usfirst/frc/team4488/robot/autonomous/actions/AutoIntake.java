package org.usfirst.frc.team4488.robot.autonomous.actions;

import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team4488.robot.routines.Routine;
import org.usfirst.frc.team4488.robot.systems.Intake;

public class AutoIntake extends Routine {

  private Intake intake = Intake.getInstance();
  private boolean isDone = false;

  private double timeAtStart;

  private double runTime;

  public AutoIntake(double timeToRun) {
    requireSystems(intake);
    runTime = timeToRun;
  }

  public void start() {
    timeAtStart = Timer.getFPGATimestamp();
    intake.intakeOut();
    intake.setIntakeOn();
    intake.hopperOn();
    isDone = false;
  }

  public void update() {
    if (timeAtStart + runTime < Timer.getFPGATimestamp()) isDone = true;
  }

  public void done() {
    intake.hopperOff();
    intake.setIntakeOff();
  }

  public void abort() {
    done();
  }

  public boolean isFinished() {
    return isDone;
  }
}
