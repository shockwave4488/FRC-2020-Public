package org.usfirst.frc.team4488.lib.sensors;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Optional;
import org.usfirst.frc.team4488.lib.controlsystems.SetPointProfile;
import org.usfirst.frc.team4488.lib.flowcontrol.EdgeTrigger;

public class Limelight {

  private String name;

  private static final int DEFAULT_PIPE = 0;

  private NetworkTable table;
  private NetworkTableEntry xEntry, yEntry, areaEntry;
  private NetworkTableEntry hasTargetEntry, currentPipeEntry;
  private NetworkTableEntry ledControlEntry, pipeControlEntry;

  private Optional<DigitalOutput> extraLedDio;

  private SendableChooser<Boolean> forceLedSelector = new SendableChooser<Boolean>();
  private EdgeTrigger selectedLedOn = new EdgeTrigger(false);

  private boolean usingInterpolation;
  private final SetPointProfile interpTable;

  public enum LedControl {
    PipeControl(0),
    ForceOff(1),
    ForceOn(3),
    ForceBlink(2);

    public int val;

    private LedControl(int val) {
      this.val = val;
    }
  }

  public static class DistanceEstimationConstants {
    public double camHeight;
    public double targetHeight;
    public double camToNormalAngle;

    public DistanceEstimationConstants(
        double camHeight, double targetHeight, double camToNormalAngle) {
      this.camHeight = camHeight;
      this.targetHeight = targetHeight;
      this.camToNormalAngle = camToNormalAngle;
    }
  }

  private final DistanceEstimationConstants distEstConsts;

  public Limelight(String name) {
    this(name, new DistanceEstimationConstants(0, 0, 0));
  }

  public Limelight(String name, DistanceEstimationConstants distEstConsts) {
    this.name = name;
    this.distEstConsts = distEstConsts;
    this.interpTable = null;
    extraLedDio = Optional.empty();
    updateTables();
    setLed(LedControl.ForceOff);
    setPipeline(DEFAULT_PIPE);

    forceLedSelector.setDefaultOption(name + "-NormalLedControl", false);
    forceLedSelector.addOption(name + "-ForceLedOn", true);
    SmartDashboard.putData(forceLedSelector);
  }

  public Limelight(String name, SetPointProfile distEstConsts) {
    this.name = name;
    this.interpTable = distEstConsts;
    this.distEstConsts = null;
    usingInterpolation = true;
    extraLedDio = Optional.empty();
    updateTables();
    setLed(LedControl.ForceOff);
    setPipeline(DEFAULT_PIPE);

    forceLedSelector.setDefaultOption(name + "-NormalLedControl", false);
    forceLedSelector.addOption(name + "-ForceLedOn", true);
    SmartDashboard.putData(forceLedSelector);
  }

  public Limelight(String name, DistanceEstimationConstants distEstConsts, int secondLedDio) {
    this(name, distEstConsts);
    extraLedDio = Optional.of(new DigitalOutput(secondLedDio));
  }

  public Limelight(String name, SetPointProfile distEstConsts, int secondLedDio) {
    this(name, distEstConsts);
    extraLedDio = Optional.of(new DigitalOutput(secondLedDio));
  }

  public void updateTables() {
    table = NetworkTableInstance.getDefault().getTable(name);
    xEntry = table.getEntry("tx");
    yEntry = table.getEntry("ty");
    areaEntry = table.getEntry("ta");
    hasTargetEntry = table.getEntry("tv");
    currentPipeEntry = table.getEntry("getpipe");
    ledControlEntry = table.getEntry("ledMode");
    pipeControlEntry = table.getEntry("pipeline");
  }

  public boolean hasTarget() {
    return hasTargetEntry.getDouble(0.0) == 1.0;
  }

  public int getRunningPipeline() {
    return (int) currentPipeEntry.getDouble(0.0);
  }

  /**
   * Gets the horizontal angle difference (in degrees) between the center of the limelight's view
   * and where its detected target is on its view.
   *
   * @return Horizontal angle difference in degrees
   */
  public double getX() {
    return xEntry.getDouble(0.0);
  }

  /**
   * Gets the vertical angle difference (in degrees) between the center of the limelight's view and
   * where its detected target is on its view.
   *
   * @return Vertical angle difference in degrees
   */
  public double getY() {
    return yEntry.getDouble(0.0);
  }

  public double getArea() {
    return areaEntry.getDouble(0.0);
  }

  /**
   * Estimates the distance from the limelight to the power port
   *
   * @return Distance to power port in inches
   */
  public double getEstimatedDistance() {
    if (usingInterpolation) return interpTable.get(getArea());
    else
      return (distEstConsts.targetHeight - distEstConsts.camHeight)
          / Math.tan((distEstConsts.camToNormalAngle + getY()) * Math.PI / 180);
  }

  public void setLed(LedControl controlMode) {
    ledControlEntry.setNumber(controlMode.val);
    extraLedDio.ifPresent(out -> out.set(controlMode == LedControl.ForceOn));
  }

  public void setPipeline(int pipeline) {
    pipeControlEntry.setNumber(pipeline);
  }

  public void updateSmartDashboard() {
    SmartDashboard.putNumber(name + "-X", getX());
    SmartDashboard.putNumber(name + "-Y", getY());
    SmartDashboard.putNumber(name + "-Area", getArea());
    SmartDashboard.putNumber(name + "-EstimatedDistance", getEstimatedDistance());
    SmartDashboard.putNumber(name + "-Pipe", getRunningPipeline());

    if (forceLedSelector.getSelected().booleanValue()) {
      setLed(LedControl.ForceOn);
      selectedLedOn.update(true);
    } else if (selectedLedOn.getFallingUpdate(forceLedSelector.getSelected().booleanValue()))
      setLed(LedControl.ForceOff);
  }
}
