package org.usfirst.frc.team4488.robot.systems.drive;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team4488.lib.PreferencesParser;
import org.usfirst.frc.team4488.lib.controlsystems.SimPID;
import org.usfirst.frc.team4488.lib.loops.Looper;
import org.usfirst.frc.team4488.lib.sensors.NavX;
import org.usfirst.frc.team4488.robot.systems.Subsystem;

public class SmartDrive implements Subsystem {

  private WestCoastDrive m_drive;
  private SimPID m_turnController;
  private SimPID m_driveController;
  private SimPID m_driveSpeedController;
  private SimPID m_straightController;
  private SimPID singleSideController;
  private PreferencesParser prefs;
  private AHRS m_navx;

  // private AnalogInput USRight;
  // private AnalogInput USLeft;

  private static final int USLeftSampleLength = 5;
  private double[] USLeftSamples;
  private int USLeftSampleIndex;

  private static final int USRightSampleLength = 5;
  private double[] USRightSamples;
  private int USRightSampleIndex;

  private double m_driveTurnCrawlPower; // Used to break static friction and

  private double stationAngle;

  public SmartDrive(WestCoastDrive drive) {
    m_drive = drive;
    m_navx = NavX.getInstance().getAHRS();
    // USRight = new AnalogInput(RobotMap.kUltraSonicRight);
    // USLeft = new AnalogInput(RobotMap.kUltraSonicLeft);
    stationAngle = 63.3;
    try {
      prefs = PreferencesParser.getInstance();

      double turnP = prefs.tryGetDouble("DriveTurnP", 0);
      double turnI = prefs.tryGetDouble("DriveTurnI", 0);
      double turnD = prefs.tryGetDouble("DriveTurnD", 0);
      double turnEps = prefs.tryGetDouble("DriveTurnEps", 0);
      double turnIZone = prefs.tryGetDouble("DriveTurnIZone", 10);
      double turnMaxOutput = prefs.tryGetDouble("DriveTurnMaxOutput", 1);
      m_turnController = new SimPID(turnP, turnI, turnD, turnEps);
      m_turnController.setMaxOutput(1.0);
      m_turnController.setDoneRange(prefs.tryGetDouble("DriveTurnDoneRange", 1));
      m_turnController.setMinDoneCycles(10);
      m_turnController.setWrapAround(0, 360);
      m_turnController.setIZone(turnIZone);
      m_turnController.setMaxOutput(turnMaxOutput);

      double driveP = prefs.tryGetDouble("DriveP", 0);
      double driveI = prefs.tryGetDouble("DriveI", 0);
      double driveD = prefs.tryGetDouble("DriveD", 0);
      double driveEps = prefs.tryGetDouble("DriveEps", 0);
      m_driveController = new SimPID(driveP, driveI, driveD, driveEps);
      m_driveController.setMaxOutput(prefs.getDouble("DriveMaxOutput"));
      m_driveController.setDoneRange(0.5);
      m_driveController.setMinDoneCycles(5);

      double speedP = prefs.tryGetDouble("DriveSpeedP", 0);
      double speedI = prefs.tryGetDouble("DriveSpeedI", 0);
      double speedD = prefs.tryGetDouble("DriveSpeedD", 0);
      double speedEps = prefs.tryGetDouble("DriveSpeedEps", 0);
      m_driveSpeedController = new SimPID(speedP, speedI, speedD, speedEps);
      m_driveSpeedController.setMaxOutput(1);

      double straightP = prefs.tryGetDouble("DriveStraightP", 0);
      double straightI = prefs.tryGetDouble("DriveStraightI", 0);
      double straightD = prefs.tryGetDouble("DriveStraightD", 0);
      double straightEps = prefs.tryGetDouble("DriveStraightEps", 0);
      m_straightController = new SimPID(straightP, straightI, straightD, straightEps);
      m_straightController.setMaxOutput(0.20);

      m_driveTurnCrawlPower = .2;
      singleSideController = new SimPID(.11, .01, .1, 0);
      singleSideController.setMaxOutput(.67);
      singleSideController.setDoneRange(1.5);
      singleSideController.setMinDoneCycles(5);

      USLeftSamples = new double[USLeftSampleLength];
      for (int i = 0; i < USLeftSampleLength; i++) {
        USLeftSamples[i] = 0;
      }
      USLeftSampleIndex = 0;

      USRightSamples = new double[USRightSampleLength];
      for (int i = 0; i < USRightSampleLength; i++) {
        USRightSamples[i] = 0;
      }
      USRightSampleIndex = 0;
    } catch (Exception e) {
      System.out.println("Oops");
      e.printStackTrace();
    }
  }

  /** @return average of filtered ultra sonics */
  public double getUSLeftFiltered() {
    // USLeftSamples[USLeftSampleIndex] = getUSLeftDistance();
    USLeftSampleIndex = (USLeftSampleIndex + 1) % USLeftSampleLength;
    double m_tempSensorSum = 0;
    for (int i = 0; i < USLeftSampleLength; i++) {
      m_tempSensorSum += USLeftSamples[i];
    }

    return (m_tempSensorSum / USLeftSampleLength) + 1.75;
  }

  /** @return average of filtered ultra sonics */
  public double getUSRightFiltered() {
    // USRightSamples[USRightSampleIndex] = getUSRightDistance();
    USRightSampleIndex = (USRightSampleIndex + 1) % USRightSampleLength;
    double m_tempSensorSum = 0;
    for (int i = 0; i < USRightSampleLength; i++) {
      m_tempSensorSum += USRightSamples[i];
    }

    return (m_tempSensorSum / USRightSampleLength) + 1.75;
  }

  public void turnToAngleCrawl(double linearPower, double angle) {
    m_turnController.setDesiredValue(angle);
    double power = m_turnController.calcPID(m_drive.getAngleRotation2d().getDegrees());

    if (m_turnController.isDone()) {
      power = 0;
    }

    m_drive.setPowers(linearPower - power, linearPower + power);
  }

  public void setLinearDoneRange(double doneRange) {
    m_driveController.setDoneRange(doneRange);
  }

  /*
   * private double getUSLeftDistance() { return (USLeft.getVoltage() * 1000.0) /
   * 9.8; }
   *
   * private double getUSRightDistance() { return (USRight.getVoltage() * 1000.0)
   * / 9.8; }
   *
   * public double getUSDistAverage() { return (getUSLeftFiltered() +
   * getUSRightFiltered()) / 2; }
   *
   * public double getUSLeftVoltage() { return USLeft.getVoltage(); }
   *
   * public double getUSRightVoltage() { return USRight.getVoltage(); }
   */
  /** @return drive */
  public WestCoastDrive getDrive() {
    return m_drive;
  }

  public void centerToWall(double side, boolean hopper) {
    m_turnController.setDesiredValue(BoundAngleNeg180To180Degrees(stationAngle * side));
    double power = m_turnController.calcPID(m_drive.getAngleRotation2d().getDegrees());
    if (m_turnController.isDone()) power = 0;
    m_drive.setPowers(power, -power);
  }

  /** @return the desired PID value of where to drive */
  public double getTurnSetpoint() {
    return m_turnController.getDesiredVal();
  }

  public void arcDrive(double heading, boolean reverse) {
    double power = 0.0;
    m_straightController.resetErrorSum();
    m_straightController.resetPreviousVal();

    m_straightController.setDesiredValue(BoundAngleNeg180To180Degrees(heading));
    double angleCorrection =
        m_straightController.calcPID(m_drive.getAngleRotation2d().getDegrees());
    if (reverse) {
      power = -0.3;
    } else {
      power = 0.3;
    }
    final double angleCorrectionMultiplier = 3;
    angleCorrection = angleCorrection * angleCorrectionMultiplier;
    if ((angleCorrection + power) > 1) {
      m_drive.setPowers(1 / 1.5, (power - angleCorrection) / 1.5);
    } else if ((angleCorrection - power) < -1) {
      m_drive.setPowers((power + angleCorrection) / 1.5, -1 / 1.5);
    } else {
      m_drive.setPowers((power + angleCorrection) / 1.5, (power - angleCorrection) / 1.5);
    }
  }

  public void driveToDistance(double distance) {
    m_driveController.setDesiredValue(distance);
    double power = m_driveController.calcPID(m_drive.getLinearDistance());
    m_drive.setPowers(power, power);
  }

  /** @return the desired distance to travel */
  public double getDesiredDriveDistance() {
    return m_driveController.getDesiredVal();
  }

  public void driveToDistance(double distance, double heading) {
    m_driveController.setDesiredValue(distance);
    m_straightController.setDesiredValue(heading);
    double power = m_driveController.calcPID(m_drive.getLinearDistance());
    double angleCorrection =
        m_straightController.calcPID(m_drive.getAngleRotation2d().getDegrees());
    m_drive.setPowers(power + angleCorrection, power - angleCorrection);
  }

  public void driveToSpeed(double speed, double heading) {
    m_driveSpeedController.setDesiredValue(speed);
    m_straightController.setDesiredValue(heading);
    double power = m_driveSpeedController.calcPID(m_drive.getLinearSpeed());
    double angleCorrection =
        m_straightController.calcPID(m_drive.getAngleRotation2d().getDegrees());
    m_drive.setPowers(power + angleCorrection, power - angleCorrection);
  }

  public void turnToAngle(double angle) {
    m_turnController.setDesiredValue(angle);
    double power = m_turnController.calcPID(m_drive.getAngleRotation2d().getDegrees());
    m_drive.setPowers((-power), (power));
  }

  public void turnToAngleLeftSide(double angle) {
    singleSideController.setDesiredValue(angle);
    double power = singleSideController.calcPID(m_drive.getAngleRotation2d().getDegrees());
    m_drive.setPowers((power + m_driveTurnCrawlPower), 0);
  }

  public void turnToAngleRightSide(double angle) {
    singleSideController.setDesiredValue(angle);
    double power = singleSideController.calcPID(m_drive.getAngleRotation2d().getDegrees());
    m_drive.setPowers(0, (-power - m_driveTurnCrawlPower));
  }

  public void stop() {
    m_drive.setPowers(0, 0);
  }

  public void resetAll() {
    m_drive.resetAngle();
    m_drive.zeroSensors();
  }

  /** @return true when target is found */
  public boolean TargetFound() {
    if (Math.abs(SmartDashboard.getNumber("BoilerDegToCenterOfTarget", 0)) < 29) {
      return true;
    } else return false;
  }

  /** @return true if drive distance is done */
  public boolean isDriveDistanceDone() {
    return m_driveController.isDone();
  }

  /**
   * @param heading
   * @return true if drive turn is done
   */
  public boolean isDriveTurnDone(double heading) {
    if (heading > 0) {
      if (m_navx.getYaw() >= heading) {
        return true;
      } else {
        return false;
      }
    } else {
      if (m_navx.getYaw() <= heading) {
        return true;
      } else {
        return false;
      }
    }
  }

  /** @return true if turn is done */
  public boolean isTurnDone() {
    return m_turnController.isDone();
  }

  /** @return true if single side turn is done */
  public boolean isSingleSideTurnDone() {
    return singleSideController.isDone();
  }

  public void setDriveMaxOutput(double max) {
    m_driveController.setMaxOutput(max);
  }

  /** @return Drive max output */
  public double getDriveMaxOutput() {
    return m_driveController.getMaxOutputVal();
  }

  public void setTurnDoneRange(double range) {
    m_turnController.setDoneRange(range);
  }

  /** @return Turn done range */
  public double getTurnDoneRange() {
    return m_turnController.getDoneRangeVal();
  }

  public void setDriveDoneRange(double range) {
    m_driveController.setDoneRange(range);
  }

  /** @return Drive done range */
  public double getDriveDoneRange() {
    return m_driveController.getDoneRangeVal();
  }

  public void setTurnMinDoneCycles(int cycles) {
    m_turnController.setMinDoneCycles(cycles);
  }

  /** @return Minimum turn for done cycle */
  public int getTurnMinDoneCycles() {
    return m_turnController.getMinDoneCycles();
  }

  /** @return Turn desired value. */
  public double getTurnDesValue() {
    return m_turnController.getDesiredVal();
  }

  public void setDriveMinDoneCycles(int cycles) {
    m_driveController.setMinDoneCycles(cycles);
  }

  /** @return minimum cycles done for drive */
  public int getDriveMinDoneCycles() {
    return m_driveController.getMinDoneCycles();
  }

  /**
   * @param angle
   * @return turns positive angles to negative and negative to positive
   */
  private double BoundAngleNeg180To180Degrees(double angle) {
    while (angle <= -180) angle += 360;
    while (angle > 180) angle -= 360;
    return angle;
  }

  /** @return true if you are close enough to the boiler to score */
  public boolean inShooterRange() {
    if (SmartDashboard.getNumber("BoilerRange", 0) >= 80
        && SmartDashboard.getNumber("BoilerRange", 0) <= 120) {
      return true;
    } else return false;
  }

  @Override
  public void updateSmartDashboard() {}

  @Override
  public void zeroSensors() {
    resetAll();
  }

  @Override
  public void registerEnabledLoops(Looper enabledLooper) {}

  public void updatePrefs() {
    PreferencesParser prefs = PreferencesParser.getInstance();
    double driveP = prefs.tryGetDouble("DriveP", 0);
    double driveI = prefs.tryGetDouble("DriveI", 0);
    double driveD = prefs.tryGetDouble("DriveD", 0);
    double driveEps = prefs.tryGetDouble("DriveEps", 0);
    m_driveController = new SimPID(driveP, driveI, driveD, driveEps);

    double turnP = prefs.tryGetDouble("DriveTurnP", 0);
    double turnI = prefs.tryGetDouble("DriveTurnI", 0);
    double turnD = prefs.tryGetDouble("DriveTurnD", 0);
    double turnEps = prefs.tryGetDouble("DriveTurnEps", 0);
    m_turnController = new SimPID(turnP, turnI, turnD, turnEps);
    m_turnController.setMaxOutput(1.0);
    m_turnController.setDoneRange(prefs.tryGetDouble("DriveTurnDoneRange", 1));
    m_turnController.setMinDoneCycles(1);
    m_turnController.setWrapAround(0, 360);

    double straightP = prefs.tryGetDouble("DriveStraightP", 0);
    double straightI = prefs.tryGetDouble("DriveStraightI", 0);
    double straightD = prefs.tryGetDouble("DriveStraightD", 0);
    double straightEps = prefs.tryGetDouble("DriveStraightEps", 0);
    m_straightController = new SimPID(straightP, straightI, straightD, straightEps);
  }

  @Override
  public void writeToLog() {}

  @Override
  public void reset() {}

  @Override
  public void setUpTrackables() {}
}
