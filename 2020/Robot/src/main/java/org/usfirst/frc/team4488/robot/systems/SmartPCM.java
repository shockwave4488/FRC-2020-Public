package org.usfirst.frc.team4488.robot.systems;

import edu.wpi.first.wpilibj.Compressor;
import org.usfirst.frc.team4488.lib.loops.Loop;
import org.usfirst.frc.team4488.lib.loops.Looper;
import org.usfirst.frc.team4488.robot.RobotMap;

public class SmartPCM implements Subsystem {

  public static SmartPCM instance;

  private Compressor compressor = new Compressor(RobotMap.PCM);

  public static SmartPCM getInstance() {
    if (instance == null) instance = new SmartPCM();
    return instance;
  }

  private SmartPCM() {}

  private Loop loop =
      new Loop() {
        public void onStart(double timestamp) {
          startCompressor();
        }

        public void onLoop(double timestamp) {}

        public void onStop(double timestamp) {
          startCompressor();
        }
      };

  public void startCompressor() {
    compressor.start();
  }

  public void stopCompressor() {
    compressor.stop();
  }

  @Override
  public void writeToLog() {}

  @Override
  public void updateSmartDashboard() {}

  @Override
  public void stop() {
    startCompressor();
  }

  @Override
  public void zeroSensors() {}

  @Override
  public void registerEnabledLoops(Looper enabledLooper) {
    enabledLooper.register(loop);
  }

  @Override
  public void updatePrefs() {}

  @Override
  public void reset() {
    startCompressor();
  }

  @Override
  public void setUpTrackables() {}
}
