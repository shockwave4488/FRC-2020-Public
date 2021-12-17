package org.usfirst.frc.team4488.robot.systems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import org.usfirst.frc.team4488.lib.loops.Loop;
import org.usfirst.frc.team4488.lib.loops.Looper;
import org.usfirst.frc.team4488.robot.RobotMap;

public class ControlPanelSpinner implements org.usfirst.frc.team4488.robot.systems.Subsystem {

  private static ControlPanelSpinner instance;
  private static final double ON_POWER = 0.5;
  private static final double OFF_POWER = 0.0;

  private WPI_VictorSPX spinnerMotor;
  private boolean doSpin;

  public static synchronized ControlPanelSpinner getInstance() {
    if (instance == null) instance = new ControlPanelSpinner();
    return instance;
  }

  public ControlPanelSpinner() {
    spinnerMotor = new WPI_VictorSPX(RobotMap.ControlPanelSpinnerMotor);
    doSpin = false;
  }

  private Loop loop =
      new Loop() {
        public void onStart(double timestamp) {
          spinnerMotor.set(ControlMode.PercentOutput, 0);
        }

        public void onLoop(double timestamp) {
          // Ternary statement. If doSpin = true, then power is set to ON_POWER, OFF_POWER
          // otherwise.
          double power = doSpin ? ON_POWER : OFF_POWER;
          spinnerMotor.set(ControlMode.PercentOutput, power);
        }

        public void onStop(double timestamp) {
          doSpin = false;
          spinnerMotor.set(ControlMode.PercentOutput, 0);
        }
      };

  public boolean isSpinning() {
    return doSpin;
  }

  public void startSpin() {
    doSpin = true;
  }

  public void stopSpin() {
    doSpin = false;
  }

  public void registerEnabledLoops(Looper enabledLooper) {
    enabledLooper.register(loop);
  }

  @Override
  public void writeToLog() {}

  @Override
  public void updateSmartDashboard() {}

  @Override
  public void stop() {}

  @Override
  public void zeroSensors() {}

  @Override
  public void updatePrefs() {}

  @Override
  public void reset() {}

  @Override
  public void setUpTrackables() {}
}
