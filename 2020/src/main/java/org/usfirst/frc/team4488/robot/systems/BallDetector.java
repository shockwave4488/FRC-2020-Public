package org.usfirst.frc.team4488.robot.systems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.ArrayList;
import java.util.List;
import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.lib.loops.Loop;
import org.usfirst.frc.team4488.lib.loops.Looper;

public class BallDetector implements Subsystem {

  public class Ball {
    public int x;
    public int y;
    public double area;

    public Ball(int x, int y, double area) {
      this.x = x;
      this.y = y;
      this.area = area;
    }
  }

  private ArrayList<Ball> detectedBalls = new ArrayList<Ball>();

  private static BallDetector instance = null;

  public static synchronized BallDetector getInstance() {
    if (instance == null) instance = new BallDetector();
    return instance;
  }

  private BallDetector() {
    // TODO
  }

  private Loop loop = new Loop() { // TODO

        @Override
        public void onStart(double timestamp) {}

        @Override
        public void onLoop(double timestamp) {
          /*
           * Every loop this should grab the most recent update from whatever coproc we
           * use and store all the detected balls inside the detectedBalls array list.
           * This should be implemented once we know what coproc we are using
           */
        }

        @Override
        public void onStop(double timestamp) {}
      };

  /**
   * Get a list of all the balls the camera sees
   *
   * @return list of all the balls the camera sees
   */
  public List<Ball> getAllBalls() {
    return detectedBalls;
  }

  /**
   * Get the nearest detected ball
   *
   * @return the nearest detected ball, based on area
   */
  public Ball getNearestBall() {
    if (numDetectedBalls() == 0) return null;

    Ball nearestBall = detectedBalls.get(0);
    for (int i = 0; i < numDetectedBalls(); i++) {
      Ball currBall = detectedBalls.get(i);
      if (currBall.area > nearestBall.area) nearestBall = currBall;
    }

    return new Ball(0, 0, 0);
  }

  /**
   * Gets the number of detected balls
   *
   * @return number of detected balls
   */
  public int numDetectedBalls() {
    return detectedBalls.size();
  }

  @Override
  public void writeToLog() {}

  @Override
  public void updateSmartDashboard() {
    SmartDashboard.putNumber("NumDetectedBalls", numDetectedBalls());
  }

  @Override
  public void stop() {}

  @Override
  public void zeroSensors() {}

  @Override
  public void registerEnabledLoops(Looper enabledLooper) {
    enabledLooper.register(loop);
  }

  @Override
  public void updatePrefs() {}

  @Override
  public void reset() {}

  @Override
  public void setUpTrackables() {
    Logging.getInstance().addTrackable(() -> this.numDetectedBalls(), "NumDetectedBalls", 4);
  }
}
