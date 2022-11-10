package org.usfirst.frc.team4488.robot.systems;

import edu.wpi.first.wpilibj.PowerDistribution;
import org.usfirst.frc.team4488.robot.RobotMap;

public class SmartPDP extends PowerDistribution {

  public static SmartPDP sInstance = null;
  /** @return an instance of smart PDP */
  public static synchronized SmartPDP getInstance() {
    if (sInstance == null) {
      sInstance = new SmartPDP(RobotMap.PDP);
    }

    return sInstance;
  }

  public SmartPDP(int id) {
    super(id, ModuleType.kCTRE);
  }
}
