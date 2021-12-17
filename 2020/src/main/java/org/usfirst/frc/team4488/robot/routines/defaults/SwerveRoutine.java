package org.usfirst.frc.team4488.robot.routines.defaults;

import org.usfirst.frc.team4488.lib.operator.Controllers;
import org.usfirst.frc.team4488.robot.systems.drive.SwerveDrive;

public class SwerveRoutine extends DefaultRoutine {

  private SwerveDrive swerve = SwerveDrive.getInstance();
  private Controllers xbox = Controllers.getInstance();

  public SwerveRoutine() {
    requireSystem(SwerveDrive.getInstance());
  }

  public void start() {}

  public void update() {
    double forward = xbox.deadzone(xbox.getLeftStickY(xbox.m_primary));
    double strafe = xbox.deadzone(xbox.getLeftStickX(xbox.m_primary));
    double turn = xbox.deadzone(xbox.getRightStickX(xbox.m_primary));
    swerve.controllerUpdate(strafe, forward, turn);
  }

  public void abort() {}
}
