package org.usfirst.frc.team4488.lib.sensors;

import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;

public class ColorSensor {

  private final I2C.Port i2cPort = I2C.Port.kOnboard;
  private final ColorSensorV3 m_colorSensor = new ColorSensorV3(i2cPort);
  private final ColorMatch m_colorMatcher = new ColorMatch();
  public static ColorSensor instance = null;
  public String colorString;

  public enum ColorType {
    RED,
    GREEN,
    BLUE,
    YELLOW,
    UNKNOWN
  }

  private ColorType returnColor;

  final Color kBlueTarget = ColorMatch.makeColor(0.15, 0.43, 0.40);
  final Color kGreenTarget = ColorMatch.makeColor(0.20, 0.54, 0.25);
  final Color kRedTarget = ColorMatch.makeColor(0.45, 0.37, 0.17);
  final Color kYellowTarget = ColorMatch.makeColor(0.33, 0.53, 0.13);

  ColorSensor() {
    m_colorMatcher.addColorMatch(kBlueTarget);
    m_colorMatcher.addColorMatch(kGreenTarget);
    m_colorMatcher.addColorMatch(kRedTarget);
    m_colorMatcher.addColorMatch(kYellowTarget);
  }

  public static synchronized ColorSensor getInstance() {
    if (instance == null) instance = new ColorSensor();
    return instance;
  }

  /**
   * gets red RGB value of color sensor
   *
   * @return red RGB value 0 to 1
   */
  public double getRed() {
    return m_colorSensor.getColor().red;
  }

  /**
   * gets blue RGB value of color sensor
   *
   * @return blue RGB value 0 to 1
   */
  public double getBlue() {
    return m_colorSensor.getColor().blue;
  }

  /**
   * gets green RGB value of color sensor
   *
   * @return green RGB value 0 to 1
   */
  public double getGreen() {
    return m_colorSensor.getColor().green;
  }

  /**
   * gets what color the color sensor sees
   *
   * @return returns enum of color red, blue, yellow, green or unknown
   */
  public ColorType currentColor() {
    Color detectedColor = m_colorSensor.getColor();
    ColorMatchResult match = m_colorMatcher.matchClosestColor(detectedColor);
    if (match.color == kBlueTarget) {
      returnColor = ColorType.BLUE;
    } else if (match.color == kRedTarget) {
      returnColor = ColorType.RED;
    } else if (match.color == kGreenTarget) {
      returnColor = ColorType.GREEN;
    } else if (match.color == kYellowTarget) {
      returnColor = ColorType.YELLOW;
    } else {
      returnColor = ColorType.UNKNOWN;
    }
    if (match.confidence < 0.95) {
      returnColor = ColorType.UNKNOWN;
    }
    return returnColor;
  }

  /**
   * gets what color the color sensor sees
   *
   * @return returns string of color red, blue, yellow, green or unknown
   */
  public String colorString() {
    if (currentColor() == ColorType.RED) {
      return "R";
    } else if (currentColor() == ColorType.GREEN) {
      return "G";
    } else if (currentColor() == ColorType.BLUE) {
      return "B";
    } else if (currentColor() == ColorType.YELLOW) {
      return "Y";
    } else {
      return "UNKNOWN";
    }
  }
}
