package org.usfirst.frc.team4488.robot;

import static org.junit.Assert.*;

import org.junit.Test;

public class RobotMapTest {
  // Verify Pneumatics

  @Test
  public void testDriveShifter() {
    assertEquals(RobotMap.DriveGearShiftSolenoid, 0);
  }

  // Verify Talons
  @Test
  public void testRightDriveMaster() {
    assertEquals(RobotMap.DriveMotorRightM, 0);
  }

  @Test
  public void testRightDriveSlave1() {
    assertEquals(RobotMap.DriveMotorRightF1, 1);
  }

  @Test
  public void testRightDriveSlave2() {
    assertEquals(RobotMap.DriveMotorRightF2, 2);
  }

  @Test
  public void testLeftDriveMaster() {
    assertEquals(RobotMap.DriveMotorLeftM, 5);
  }

  @Test
  public void testLeftDriveSlave1() {
    assertEquals(RobotMap.DriveMotorLeftF1, 4);
  }

  @Test
  public void testLeftDriveSlave2() {
    assertEquals(RobotMap.DriveMotorLeftF2, 3);
  }
}
