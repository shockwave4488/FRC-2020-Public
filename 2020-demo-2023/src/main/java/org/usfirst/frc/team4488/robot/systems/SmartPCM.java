package org.usfirst.frc.team4488.robot.systems;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import org.usfirst.frc.team4488.lib.loops.Loop;
import org.usfirst.frc.team4488.lib.loops.Looper;

public class SmartPCM implements Subsystem {

  public static SmartPCM instance;

  private Compressor compressor = new Compressor(PneumaticsModuleType.CTREPCM);

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
    compressor.enableDigital();
  }

  public void stopCompressor() {
    compressor.disable();
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
