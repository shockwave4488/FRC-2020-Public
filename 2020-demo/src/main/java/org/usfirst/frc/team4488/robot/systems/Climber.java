package org.usfirst.frc.team4488.robot.systems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.lib.loops.Loop;
import org.usfirst.frc.team4488.lib.loops.Looper;
import org.usfirst.frc.team4488.lib.sensors.HallEffect;
import org.usfirst.frc.team4488.robot.RobotMap;

public class Climber implements Subsystem {

  private static Climber instance = null;

  public static synchronized Climber getInstance() {
    if (instance == null) instance = new Climber();
    return instance;
  }

  public static enum HookPositions {
    Resting(0),
    Attaching(1),
    Climbing(-2);

    public double height;

    private HookPositions(double height) {
      this.height = height;
    }
  }

  private final TalonFX buddyForkMotor;
  private final TalonFX hookMotor;

  private final DoubleSolenoid hookLockSolenoid;

  // private final HallEffect buddyForksIn;
  // private final HallEffect buddyForksOut;
  // private final HallEffect hooksAttached;
  private final HallEffect hooksIn;
  // private final HallEffect flagTouchingBar;

  private static final double TICKS_PER_INCH = 12500;
  private static final double HOOKS_OUT_POWER = 0.1;
  private static final double HOOKS_IN_POWER = -0.5;
  private static final double HOOKS_MAINTAIN_POWER = -0.1;
  private static final DoubleSolenoid.Value HOOKS_LOCKED = DoubleSolenoid.Value.kForward;

  private boolean desiredForksOut = false;
  private boolean doneClimbing = false;
  private HookPositions hooksPosition = HookPositions.Resting;

  private Loop loop =
      new Loop() {
        public void onStart(double timestamp) {
          buddyForkMotor.set(ControlMode.PercentOutput, 0);
          hookMotor.set(ControlMode.Position, inchesToTicks(HookPositions.Resting.height));
          hookLockSolenoid.set(HOOKS_LOCKED);
        }

        public void onLoop(double timestamp) {
          /*
          boolean forksLocked = false;
          double forksPower = 0;
          if ((desiredForksOut && forksAreOut()) || (!desiredForksOut && forksAreIn())) {
            forksLocked = true;
          } else if (desiredForksOut) {
            forksPower = FORKS_OUT_POWER;
          } else if (!desiredForksOut) {
            forksPower = FORKS_IN_POWER;
          }
          hookLockSolenoid.set(forksLocked ? HOOKS_LOCKED : HOOKS_UNLOCKED);
          */

          /*
          buddyForkMotor.set(ControlMode.PercentOutput, forksPower);
          hookMotor.set(ControlMode.PercentOutput, hooksPower);
          */

          if (hooksPosition == HookPositions.Resting) {

            if (getHeightInches() > HookPositions.Resting.height) {
              hookMotor.set(ControlMode.PercentOutput, HOOKS_MAINTAIN_POWER);
            } else {
              hookMotor.set(ControlMode.PercentOutput, 0);
            }

          } else if (hooksPosition == HookPositions.Attaching) {

            if (getHeightInches() < HookPositions.Attaching.height) {
              hookMotor.set(ControlMode.PercentOutput, HOOKS_OUT_POWER);
            } else {
              hookMotor.set(ControlMode.PercentOutput, 0);
            }

          } else if (hooksPosition == HookPositions.Climbing) {

            if (hooksAreIn()) {
              doneClimbing = true;
            }
            if (doneClimbing) hookMotor.set(ControlMode.PercentOutput, 0);
            else hookMotor.set(ControlMode.PercentOutput, HOOKS_IN_POWER);
          }
        }

        public void onStop(double timestamp) {
          buddyForkMotor.set(ControlMode.PercentOutput, 0);
          hookMotor.set(ControlMode.PercentOutput, 0);

          hookLockSolenoid.set(HOOKS_LOCKED);
        }
      };

  private Climber() {
    buddyForkMotor = new TalonFX(RobotMap.ClimberBuddyForkMotor);
    hookMotor = new TalonFX(RobotMap.ClimberHookMotor);

    buddyForkMotor.setNeutralMode(NeutralMode.Brake);
    hookMotor.setNeutralMode(NeutralMode.Brake);
    hookMotor.setInverted(true);

    hookLockSolenoid =
        new DoubleSolenoid(
            RobotMap.PCM,
            RobotMap.ClimberHookLockForwardSolenoid,
            RobotMap.ClimberHookLockReverseSolenoid);

    // buddyForksIn = new HallEffect(RobotMap.ClimberBuddyForksInHallEffect);
    // buddyForksOut = new HallEffect(RobotMap.ClimberBuddyForksOutHallEffect);
    // hooksAttached = new HallEffect(RobotMap.ClimberHooksAttachedHallEffect);
    hooksIn = new HallEffect(RobotMap.ClimberHooksInHallEffect);
    // flagTouchingBar = new HallEffect(RobotMap.ClimberFlagTouchingBarHallEffect);
  }

  private double inchesToTicks(double inches) {
    return inches * TICKS_PER_INCH;
  }

  private double ticksToInches(double ticks) {
    return ticks / TICKS_PER_INCH;
  }

  private double getHeightTicks() {
    return hookMotor.getSelectedSensorPosition();
  }

  public double getHeightInches() {
    return ticksToInches(getHeightTicks());
  }

  public void setHooksPosition(HookPositions pos) {
    hooksPosition = pos;
  }

  public void forksOut() {
    desiredForksOut = true;
  }

  public void forksIn() {
    desiredForksOut = false;
  }

  public boolean hooksAreAttached() {
    return false;
    // return hooksAttached.get();
  }

  public boolean hooksAreIn() {
    return hooksIn.get();
  }

  public boolean forksAreOut() {
    return false;
    // return buddyForksOut.get();
  }

  public boolean forksAreIn() {
    return false;
    // return buddyForksIn.get();
  }

  public boolean flagTouchingBar() {
    return false;
    // return flagTouchingBar.get();
  }

  public void reset() {}

  public void zeroSensors() {
    hookMotor.getSensorCollection().setIntegratedSensorPosition(0, 0);
  }

  public void updatePrefs() {}

  public void setUpTrackables() {
    Logging.getInstance().addTrackable(() -> hookMotor.getSupplyCurrent(), "climber_current", 20);
  }

  public void stop() {}

  public void writeToLog() {}

  public void updateSmartDashboard() {
    SmartDashboard.putNumber("Climber Height", getHeightInches());
    SmartDashboard.putBoolean("Desired Forks Out", desiredForksOut);
    SmartDashboard.putBoolean("climber hall effect", hooksAreIn());
  }

  public void registerEnabledLoops(Looper enabledLoop) {
    enabledLoop.register(loop);
  }
}
