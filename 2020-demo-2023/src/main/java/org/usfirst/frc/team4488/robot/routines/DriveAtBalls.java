package org.usfirst.frc.team4488.robot.routines;

import org.usfirst.frc.team4488.lib.PreferencesParser;
import org.usfirst.frc.team4488.lib.controlsystems.SimPID;
import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.robot.systems.BallDetector;
import org.usfirst.frc.team4488.robot.systems.BallDetector.Ball;
import org.usfirst.frc.team4488.robot.systems.drive.Drive;

public class DriveAtBalls extends Routine {

  private BallDetector ballDetector = BallDetector.getInstance();
  private Drive drive = Drive.getInstance();

  private SimPID pid = new SimPID();

  public interface ThrottleSource {
    public double getThrottle();
  }

  private ThrottleSource throttleSource;

  public DriveAtBalls(ThrottleSource throttleSource) {
    requireSystems(drive, ballDetector);
    this.throttleSource = throttleSource;
    updatePID();
  }

  public DriveAtBalls(double throttle) {
    this(() -> throttle);
  }

  public void start() {
    updatePID();
    drive.setPowers(0, 0);
    Logging.getInstance()
        .writeToLogFormatted(this, "DriveAtBalls", "Drive, BallDetector", "Driving at power cells");
  }

  public void update() {
    Ball nearestBall = ballDetector.getNearestBall();
    pid.setDesiredValue(0);
    double turn = pid.calcPID(nearestBall.x);
    double throttle = throttleSource.getThrottle();
    double leftPower = Math.max(Math.min(throttle + turn, 1), -1);
    double rightPower = Math.max(Math.min(throttle - turn, 1), -1);
    drive.setPowers(leftPower, rightPower);
  }

  public void done() {
    drive.setPowers(0, 0);
    Logging.getInstance()
        .writeToLogFormatted(
            this, "DriveAtBalls", "Drive, BallDetector", "Done driving at power cells");
  }

  public void abort() {
    drive.setPowers(0, 0);
  }

  public boolean isFinished() {
    return false;
  }

  private void updatePID() {
    PreferencesParser prefs = PreferencesParser.getInstance();
    double p = prefs.tryGetDouble("CamAlignP", 0);
    double i = prefs.tryGetDouble("CamAlignI", 0);
    double d = prefs.tryGetDouble("CamAlignD", 0);
    pid.setConstants(p, i, d);
  }
}
