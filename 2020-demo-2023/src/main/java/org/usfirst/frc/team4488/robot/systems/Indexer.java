package org.usfirst.frc.team4488.robot.systems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Map;
import org.usfirst.frc.team4488.lib.PreferencesParser;
import org.usfirst.frc.team4488.lib.flowcontrol.EdgeTrigger;
import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.lib.loops.Loop;
import org.usfirst.frc.team4488.lib.loops.Looper;
import org.usfirst.frc.team4488.lib.operator.Tuner;
import org.usfirst.frc.team4488.lib.sensors.BeamBreak;
import org.usfirst.frc.team4488.robot.RobotMap;
import org.usfirst.frc.team4488.robot.routines.defaults.DefaultIndexerRoutine;
import org.usfirst.frc.team4488.robot.routines.defaults.DefaultRoutine;

/**
 * indexer subsystem for robot cellSensor for telling if a ball enters the system conveyer to
 * transfer balls from intake to shooter kickwheel to move balls from conveyor to shooter.
 */
public class Indexer implements Subsystem {

  private static enum Mode {
    Shoot,
    Index,
    Off;
  }

  private Mode lowerMode = Mode.Off;
  private Mode upperMode = Mode.Off;

  private BeamBreak firstCellSensor;
  private BeamBreak secondCellSensor;
  private BeamBreak thirdCellSensor;
  private BeamBreak fourthCellSensor;
  private WPI_TalonSRX indexConveyor;
  private WPI_TalonSRX shooterConveyor;
  private EdgeTrigger lowerBeamBreakChanged = new EdgeTrigger(false);
  private EdgeTrigger upperBeamBreakChanged = new EdgeTrigger(false);
  private EdgeTrigger transitionBeamBreakChanged = new EdgeTrigger(false);
  private DefaultIndexerRoutine defaultRoutine = new DefaultIndexerRoutine(this);
  private Tuner tuner = new Tuner(this::updateTuner, 2);

  private int robotCellCount = 0;
  private static final double LOWER_SHOOT_POWER = 0.3;
  private static final double UPPER_SHOOT_VEL = 3000; // ticks/100ms
  private static final double LOWER_INDEX_POWER = .30;
  private static final double UPPER_INDEX_POWER = .35;
  private static final int REALLY_LOW_NUMBER = -99999;
  private int ballPassedLowerTicks = 0;
  private int ballPassedUpperTicks = 0;
  // private int ballPassedTransitionTicks = 0;

  private static Indexer sInstance;

  /** @return instance of indexer */
  public static synchronized Indexer getInstance() {
    if (sInstance == null) {
      sInstance = new Indexer();
    }

    return sInstance;
  }

  public Indexer() {
    indexConveyor = new WPI_TalonSRX(RobotMap.IndexConveyor);
    shooterConveyor = new WPI_TalonSRX(RobotMap.ShooterConveyor);
    shooterConveyor.setInverted(true);
    indexConveyor.setSensorPhase(true);
    shooterConveyor.setSensorPhase(true);
    indexConveyor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
    shooterConveyor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

    firstCellSensor = new BeamBreak(RobotMap.IndexerCellSensor);
    secondCellSensor = new BeamBreak(RobotMap.TransitionCellSensor);
    thirdCellSensor = new BeamBreak(RobotMap.MidShooterCellSensor);
    fourthCellSensor = new BeamBreak(RobotMap.ShooterCellSensor);

    tuner.addValueFromPrefs("ConveyorP", 0);
    tuner.addValueFromPrefs("ConveyorI", 0);
    tuner.addValueFromPrefs("ConveyorD", 0);
    tuner.addValueFromPrefs("ConveyorF", 0);
    tuner.start();
  }

  private Loop mLoop =
      new Loop() {
        @Override
        public void onStart(double timestamp) {
          ballPassedLowerTicks = REALLY_LOW_NUMBER;
          ballPassedUpperTicks = REALLY_LOW_NUMBER;
          ballPassedLowerTicks = REALLY_LOW_NUMBER;
        }

        @Override
        public void onLoop(double timestamp) {
          // To keep track of ball count and make sure the ball count goes up once per ball
          if (lowerBeamBreakChanged.getRising(firstBeamBroken())) {
            robotCellCount++;
          }

          if (lowerBeamBreakChanged.getFallingUpdate(firstBeamBroken())) {
            ballPassedLowerTicks = getTicks();
            ballPassedUpperTicks = getShooterConveyorTicks();
          }

          if (transitionBeamBreakChanged.getFallingUpdate(secondBeamBroken())) {
            // ballPassedTransitionTicks = getShooterConveyorTicks();
          }

          if (upperBeamBreakChanged.getFallingUpdate(fourthBeamBroken())) {
            robotCellCount--;
          }

          if (lowerMode == Mode.Shoot) {
            indexConveyor.set(ControlMode.PercentOutput, LOWER_SHOOT_POWER);
          } else if (lowerMode == Mode.Index) {
            indexConveyor.set(ControlMode.PercentOutput, LOWER_INDEX_POWER);
          } else {
            indexConveyor.set(ControlMode.PercentOutput, 0);
          }

          if (upperMode == Mode.Shoot) {
            shooterConveyor.set(ControlMode.Velocity, UPPER_SHOOT_VEL);
          } else if (upperMode == Mode.Index) {
            shooterConveyor.set(ControlMode.PercentOutput, UPPER_INDEX_POWER);
          } else {
            shooterConveyor.set(ControlMode.PercentOutput, 0);
          }
        }

        @Override
        public void onStop(double timestamp) {
          stopConveyor();
          indexConveyor.set(ControlMode.PercentOutput, 0);
        }
      };

  public void stopConveyor() {
    stopLowerConveyor();
    stopUpperConveyor();
  }

  public void moveConveyor(boolean shoot) {
    moveLowerConveyor(shoot);
    moveUpperConveyor(shoot);
  }

  public void moveLowerConveyor(boolean shoot) {
    lowerMode = shoot ? Mode.Shoot : Mode.Index;
  }

  public void moveUpperConveyor(boolean shoot) {
    upperMode = shoot ? Mode.Shoot : Mode.Index;
  }

  public void stopLowerConveyor() {
    lowerMode = Mode.Off;
  }

  public void stopUpperConveyor() {
    upperMode = Mode.Off;
  }

  /**
   * gets ticks of conveyor
   *
   * @return how many ticks conveyor has moved
   */
  public int getconveyorTicks() {
    return (int) Math.round(indexConveyor.getSelectedSensorPosition());
  }

  public int getShooterConveyorTicks() {
    return (int) Math.round(shooterConveyor.getSelectedSensorPosition());
  }

  public int getBallPassedLowerTicks() {
    return ballPassedLowerTicks;
  }

  public int getBallPassedUpperTicks() {
    return ballPassedUpperTicks;
  }

  /**
   * gets beam break state
   *
   * @return if beam break is triggered
   */
  public boolean firstBeamBroken() {
    return !firstCellSensor.get();
  }

  public boolean secondBeamBroken() {
    return !secondCellSensor.get();
  }

  public boolean thirdBeamBroken() {
    return !thirdCellSensor.get();
  }

  public boolean fourthBeamBroken() {
    return !fourthCellSensor.get();
  }

  /**
   * Gets the number of power cells that are in the conveyor (which is going to the shooter)
   *
   * @return The number of power cells that are in the conveyor
   */
  public int getCellCount() {
    return robotCellCount;
  }

  /**
   * Decreases the number of power cells the robot thinks it has by 1. To be ran by the shooter
   * routine after a ball is shot.
   */
  public void decreaseCellCount() {
    robotCellCount--;
  }

  private int getTicks() {
    return (int) Math.round(indexConveyor.getSelectedSensorPosition());
  }

  @Override
  public void updatePrefs() {
    PreferencesParser prefs = PreferencesParser.getInstance();
    double p = prefs.tryGetDouble("ConveyorP", 0);
    double i = prefs.tryGetDouble("ConveyorI", 0);
    double d = prefs.tryGetDouble("ConveyorD", 0);
    double f = prefs.tryGetDouble("ConveyorF", 0);
    updatePIDF(p, i, d, f);
  }

  @Override
  public void writeToLog() {}

  @Override
  public void updateSmartDashboard() {
    SmartDashboard.putNumber("indexer ticks", getTicks());
    SmartDashboard.putNumber("shooter conveyor ticks", getShooterConveyorTicks());
    SmartDashboard.putNumber("shooter conveyor speed", shooterConveyor.getSelectedSensorVelocity());
    SmartDashboard.putNumber("num balls", getCellCount());
    SmartDashboard.putBoolean("first bb tripped", firstBeamBroken());
    SmartDashboard.putBoolean("second bb tripped", secondBeamBroken());
    SmartDashboard.putBoolean("third bb tripped", thirdBeamBroken());
    SmartDashboard.putBoolean("fourth bb tripped", fourthBeamBroken());
  }

  @Override
  public void stop() {}

  @Override
  public void zeroSensors() {
    indexConveyor.getSensorCollection().setQuadraturePosition(0, 0);
    shooterConveyor.getSensorCollection().setQuadraturePosition(0, 0);
  }

  @Override
  public void registerEnabledLoops(Looper enabledLooper) {
    enabledLooper.register(mLoop);
  }

  @Override
  public void reset() {}

  @Override
  public void setUpTrackables() {
    Logging.getInstance().addTrackable(() -> this.getCellCount(), "RobotCellCount", 20);
    Logging.getInstance()
        .addStringTrackable(() -> this.lowerMode.toString(), "LowerConveyorMode", 20, "");
    Logging.getInstance()
        .addStringTrackable(() -> this.upperMode.toString(), "UpperConveyorMode", 20, "");
  }

  @Override
  public DefaultRoutine getDefaultRoutine() {
    return defaultRoutine;
  }

  private void updateTuner(Map<String, Double> vals) {
    double p = vals.get("ConveyorP");
    double i = vals.get("ConveyorI");
    double d = vals.get("ConveyorD");
    double f = vals.get("ConveyorF");
    updatePIDF(p, i, d, f);
  }

  private void updatePIDF(double p, double i, double d, double f) {
    shooterConveyor.config_kP(0, p);
    shooterConveyor.config_kI(0, i);
    shooterConveyor.config_kD(0, d);
    shooterConveyor.config_kF(0, f);
  }
}
