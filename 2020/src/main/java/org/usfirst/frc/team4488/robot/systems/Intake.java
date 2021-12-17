package org.usfirst.frc.team4488.robot.systems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.lib.loops.Loop;
import org.usfirst.frc.team4488.lib.loops.Looper;
import org.usfirst.frc.team4488.robot.RobotMap;

public class Intake implements Subsystem {

  public static Intake instance;

  public static synchronized Intake getInstance() {
    if (instance == null) instance = new Intake();
    return instance;
  }

  static enum IntakeState {
    Reverse(-1),
    Off(0),
    On(1);

    public int power;

    private IntakeState(int power) {
      this.power = power;
    }
  }

  private IntakeState intakeState = IntakeState.Off;

  private WPI_VictorSPX intake;
  private Solenoid intakePiston;
  private WPI_VictorSPX hopperMotor;
  private boolean hopperOn;
  private boolean intakeOut;
  private boolean isIntakeRollerReady = false;
  private static final double REALLY_BIG_NUMBER = 99999;
  private double intakeTimer = REALLY_BIG_NUMBER;
  private static final double INTAKE_OFFSET = 0.5;

  public void intakeOut() {
    if (!intakeOut) intakeTimer = Timer.getFPGATimestamp();
    intakeOut = true;
  }

  public void intakeIn() {
    intakeOut = false;
    intakeTimer = REALLY_BIG_NUMBER;
  }

  public boolean intakeIsOut() {
    return intakeOut;
  }

  public void setIntakeOn() {
    intakeState = IntakeState.On;
  }

  public void setIntakeReverse() {
    intakeState = IntakeState.Reverse;
  }

  public void setIntakeOff() {
    intakeState = IntakeState.Off;
  }

  public IntakeState getIntakePower() {
    return intakeState;
  }

  public void hopperOn() {
    hopperOn = true;
  }

  public void hopperOff() {
    hopperOn = false;
  }

  public boolean hopperIsOn() {
    return hopperOn;
  }

  public boolean isIntakeReady() {
    return isIntakeRollerReady;
  }

  public Loop loop =
      new Loop() {
        public void onStart(double timestamp) {
          setIntakeOff();
          intakeIn();
          hopperOff();
        }

        public void onLoop(double timestamp) {
          isIntakeRollerReady = Timer.getFPGATimestamp() > intakeTimer + INTAKE_OFFSET;

          intakePiston.set(intakeOut);

          if (hopperOn) {
            hopperMotor.set(ControlMode.PercentOutput, 1);
          } else {
            hopperMotor.set(ControlMode.PercentOutput, 0);
          }

          intake.set(ControlMode.PercentOutput, intakeState.power);
        }

        public void onStop(double timestamp) {
          setIntakeOff();
          intakeIn();
          hopperOff();
        }
      };

  Intake() {
    intake = new WPI_VictorSPX(RobotMap.FrontRollerMotor);
    intakePiston = new Solenoid(RobotMap.PCM, RobotMap.IntakePiston);
    hopperMotor = new WPI_VictorSPX(RobotMap.HopperMotor);
    hopperMotor.setInverted(false);
  }

  public void writeToLog() {}

  public void updateSmartDashboard() {
    SmartDashboard.putNumber("Intake Power", intakeState.power);
  }

  public void stop() {
    intakeState = IntakeState.Off;
  }

  public void zeroSensors() {}

  public void registerEnabledLoops(Looper enabledLooper) {
    enabledLooper.register(loop);
  }

  public void updatePrefs() {}

  public void reset() {}

  @Override
  public void setUpTrackables() {
    Logging.getInstance()
        .addTrackable(() -> Intake.getInstance().intakeIsOut() ? 1 : 0, "IntakePositionedOut", 50);
    Logging.getInstance()
        .addTrackable(() -> Intake.getInstance().hopperIsOn() ? 1 : 0, "HopperOn", 50);
    Logging.getInstance().addTrackable(() -> intakeState.power, "IntakeIntaking", 50);
  }
}
