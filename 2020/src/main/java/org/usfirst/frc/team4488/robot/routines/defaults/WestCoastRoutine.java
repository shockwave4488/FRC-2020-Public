package org.usfirst.frc.team4488.robot.routines.defaults;

import org.usfirst.frc.team4488.lib.operator.Controllers;
import org.usfirst.frc.team4488.robot.systems.drive.Drive;

public class WestCoastRoutine extends DefaultRoutine {

  private Drive drive = Drive.getInstance();
  private Controllers xbox = Controllers.getInstance();

  public WestCoastRoutine() {
    requireSystem(Drive.getInstance());
  }

  public void start() {}

  public void update() {
    double forward = xbox.deadzone(xbox.getLeftStickY(xbox.m_primary));
    double strafe = xbox.deadzone(xbox.getLeftStickX(xbox.m_primary));
    double turn = xbox.deadzone(xbox.getRightStickX(xbox.m_primary));
    drive.controllerUpdate(strafe, forward, turn);
  }

  public void abort() {
    drive.controllerUpdate(0, 0, 0);
  }
}
