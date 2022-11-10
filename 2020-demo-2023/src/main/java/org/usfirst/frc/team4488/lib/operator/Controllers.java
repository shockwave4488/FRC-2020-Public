package org.usfirst.frc.team4488.lib.operator;

import edu.wpi.first.wpilibj.XboxController;
import org.usfirst.frc.team4488.robot.Constants;
import org.usfirst.frc.team4488.robot.routines.Bindable;

public class Controllers {
  public XboxController m_primary;
  public XboxController m_secondary;

  private static Controllers sInstance = null;

  /*
   * This is the Controllers class, which grabs raw booleans and number values
   * from an Xbox controller and turns them into more obviously named
   * functions, which then are assigned to control different functions of the
   * robot.
   */

  public enum SecondaryState {
    manual,
    auto;
  }

  public enum XboxButtons {
    APrim(() -> Controllers.getInstance().getA(Controllers.getInstance().m_primary)),
    BPrim(() -> Controllers.getInstance().getB(Controllers.getInstance().m_primary)),
    XPrim(() -> Controllers.getInstance().getX(Controllers.getInstance().m_primary)),
    YPrim(() -> Controllers.getInstance().getY(Controllers.getInstance().m_primary)),
    LeftBumperPrim(
        () -> Controllers.getInstance().getLeftBumper(Controllers.getInstance().m_primary)),
    RightBumperPrim(
        () -> Controllers.getInstance().getRightBumper(Controllers.getInstance().m_primary)),
    SelectPrim(() -> Controllers.getInstance().getSelect(Controllers.getInstance().m_primary)),
    StartPrim(() -> Controllers.getInstance().getStart(Controllers.getInstance().m_primary)),
    LeftStickPressPrim(
        () -> Controllers.getInstance().getLeftStickPress(Controllers.getInstance().m_primary)),
    RightStickPressPrim(
        () -> Controllers.getInstance().getRightStickPress(Controllers.getInstance().m_primary)),
    LeftTriggerPrim(
        () -> Controllers.getInstance().getLeftTrigger(Controllers.getInstance().m_primary)),
    RightTriggerPrim(
        () -> Controllers.getInstance().getRightTrigger(Controllers.getInstance().m_primary)),
    ASec(() -> Controllers.getInstance().getA(Controllers.getInstance().m_secondary)),
    BSec(() -> Controllers.getInstance().getB(Controllers.getInstance().m_secondary)),
    XSec(() -> Controllers.getInstance().getX(Controllers.getInstance().m_secondary)),
    YSec(() -> Controllers.getInstance().getY(Controllers.getInstance().m_secondary)),
    LeftBumperSec(
        () -> Controllers.getInstance().getLeftBumper(Controllers.getInstance().m_secondary)),
    RightBumperSec(
        () -> Controllers.getInstance().getRightBumper(Controllers.getInstance().m_secondary)),
    SelectSec(() -> Controllers.getInstance().getSelect(Controllers.getInstance().m_secondary)),
    StartSec(() -> Controllers.getInstance().getStart(Controllers.getInstance().m_secondary)),
    LeftStickPressSec(
        () -> Controllers.getInstance().getLeftStickPress(Controllers.getInstance().m_secondary)),
    RightStickPressSec(
        () -> Controllers.getInstance().getRightStickPress(Controllers.getInstance().m_secondary)),
    LeftTriggerSec(
        () -> Controllers.getInstance().getLeftTrigger(Controllers.getInstance().m_secondary)),
    RightTriggerSec(
        () -> Controllers.getInstance().getRightTrigger(Controllers.getInstance().m_secondary));
    public Bindable bind;

    private XboxButtons(Bindable bind) {
      this.bind = bind;
    }
  }

  public static synchronized Controllers getInstance() {
    if (sInstance == null) {
      sInstance = new Controllers();
    }

    return sInstance;
  }

  public Controllers() {
    m_primary = new XboxController(0);
    m_secondary = new XboxController(1);
  }

  // ----------------------------------------
  // -----------GENERAL FUNCTIONS------------
  // ----------------------------------------

  // This function gets the A button boolean from the controller.
  public boolean getA(XboxController controller) {
    return controller.getRawButton(1);
  }

  // This function gets the B button boolean from the controller.
  public boolean getB(XboxController controller) {
    return controller.getRawButton(2);
  }

  // This function gets the X button boolean from the controller.
  public boolean getX(XboxController controller) {
    return controller.getRawButton(3);
  }

  // This function gets the Y button boolean from the controller.
  public boolean getY(XboxController controller) {
    return controller.getRawButton(4);
  }

  // This function gets the Left Bumper boolean from the controller.
  public boolean getLeftBumper(XboxController controller) {
    return controller.getRawButton(5);
  }

  // This function gets the Right Bumper boolean from the controller.
  public boolean getRightBumper(XboxController controller) {
    return controller.getRawButton(6);
  }

  // This function gets the Select button boolean from the controller.
  public boolean getSelect(XboxController controller) {
    return controller.getRawButton(7);
  }

  // This function gets the Start button boolean from the controller.
  public boolean getStart(XboxController controller) {
    return controller.getRawButton(8);
  }

  // This function gets the Left Stick button boolean from the controller.
  public boolean getLeftStickPress(XboxController controller) {
    return controller.getRawButton(9);
  }

  // This function gets the Right Stick button boolean from the controller.
  public boolean getRightStickPress(XboxController controller) {
    return controller.getRawButton(10);
  }

  // Creates a numerical threshold for the Right Trigger to be considered
  // pressed, and returns a boolean based on the raw numerical value compared
  // to the threshold.
  public boolean getLeftTrigger(XboxController controller) {
    return controller.getRawAxis(2) > 0.75;
  }

  // Creates a numerical threshold for the Left Trigger to be considered
  // pressed, and returns a boolean based on the raw numerical value compared
  // to the threshold.
  public boolean getRightTrigger(XboxController controller) {
    return controller.getRawAxis(3) > 0.75;
  }

  // Returns the raw numerical value of the X-axis of the Right Stick, with
  // left returning negative values, and right returning positive values.
  public double getRightStickX(XboxController controller) {
    return controller.getRawAxis(4);
  }

  // Returns the raw numerical value of the Y-axis of the Right Stick, with
  // down returning negative values, and up returning positive values.
  public double getRightStickY(XboxController controller) {
    return controller.getRawAxis(5) * -1.0;
  }

  // Returns the raw numerical value of the X-axis of the Left Stick, with
  // left returning negative values, and right returning positive values.
  public double getLeftStickX(XboxController controller) {
    return controller.getRawAxis(0);
  }

  // Returns the raw numerical value of the Y-axis of the Left Stick, with
  // down returning negative values, and up returning positive values.
  public double getLeftStickY(XboxController controller) {
    return controller.getRawAxis(1) * -1.0;
  }

  // Returns the raw numerical value of the Directional Pad. Will return -1 if
  // not pressed.
  // Will return a value between 0 and 360 based on the location of where the
  // D Pad is being pressed.
  public double getDPad(XboxController controller) {
    return controller.getPOV(0);
  }

  public boolean getDPadPressed(XboxController controller) {
    return controller.getPOV(0) > -1;
  }

  public String getPrimaryControllerLogging() {
    String toReturn = "";
    toReturn += getA(m_primary) ? ",1" : ",0";
    toReturn += getB(m_primary) ? ",1" : ",0";
    toReturn += getX(m_primary) ? ",1" : ",0";
    toReturn += getY(m_primary) ? ",1" : ",0";
    toReturn += getLeftBumper(m_primary) ? ",1" : ",0";
    toReturn += getRightBumper(m_primary) ? ",1" : ",0";
    toReturn += getLeftTrigger(m_primary) ? ",1" : ",0";
    toReturn += getRightTrigger(m_primary) ? ",1" : ",0";
    toReturn += getLeftStickPress(m_primary) ? ",1" : ",0";
    toReturn += getRightStickPress(m_primary) ? ",1" : ",0";
    toReturn += getStart(m_primary) ? ",1" : ",0";
    toReturn += getSelect(m_primary) ? ",1" : ",0";
    toReturn += getLeftStickX(m_primary) + ",";
    toReturn += getLeftStickY(m_primary) + ",";
    toReturn += getRightStickX(m_primary) + ",";
    toReturn += getRightStickY(m_primary) + ",";
    toReturn += getDPad(m_primary);
    return toReturn;
  }

  public String getSecondaryControllerLogging() {
    String toReturn = "";
    toReturn += getA(m_secondary) ? ",1" : ",0";
    toReturn += getB(m_secondary) ? ",1" : ",0";
    toReturn += getX(m_secondary) ? ",1" : ",0";
    toReturn += getY(m_secondary) ? ",1" : ",0";
    toReturn += getLeftBumper(m_secondary) ? ",1" : ",0";
    toReturn += getRightBumper(m_secondary) ? ",1" : ",0";
    toReturn += getLeftTrigger(m_secondary) ? ",1" : ",0";
    toReturn += getRightTrigger(m_secondary) ? ",1" : ",0";
    toReturn += getLeftStickPress(m_secondary) ? ",1" : ",0";
    toReturn += getRightStickPress(m_secondary) ? ",1" : ",0";
    toReturn += getStart(m_secondary) ? ",1" : ",0";
    toReturn += getSelect(m_secondary) ? ",1" : ",0";
    toReturn += getLeftStickX(m_secondary) + ",";
    toReturn += getLeftStickY(m_secondary) + ",";
    toReturn += getRightStickX(m_secondary) + ",";
    toReturn += getRightStickY(m_secondary) + ",";
    toReturn += getDPad(m_secondary);
    return toReturn;
  }

  public double deadzone(double val) {
    return deadzone(val, Constants.defaultStickDeadzone);
  }

  public double deadzone(double val, double deadzone) {
    if (Math.abs(val) > Math.abs(deadzone)) {
      if (val > 0) {
        return (val - deadzone) / (1 - deadzone);
      } else {
        return (val + deadzone) / (1 - deadzone);
      }
    }

    return 0;
  }
}
