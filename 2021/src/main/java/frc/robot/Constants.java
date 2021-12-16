// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 *
 * <p>Further, this class should only be used for constants that are universal across all of our
 * robots, put robot specifc constants in the correct Constants class in the robotspecifics folder
 */
public final class Constants {

  public static final class DriveTrainConstants {
    // Drive Train
    public static final int LEFT_FRONT_PORT = 0;
    public static final int RIGHT_FRONT_PORT = 1;
    public static final int LEFT_BACK_PORT = 2;
    public static final int RIGHT_BACK_PORT = 3;

    public static final double DRIVE_TRAIN_SPEED = 0.5;
    public static final double SWERVE_DRIVE_SPEED = 1.5;
    public static final double SWERVE_ROTATION_SPEED = 4;
  }

  public static final class OIConstants {
    // Joysticks
    public static final int XBOX_LEFT_Y_AXIS = 1; // double check
    public static final int XBOX_LEFT_X_AXIS = 0; // double check
    public static final int DRIVER_CONTROLLER_PORT = 0; // double check
    public static final double DEFAULT_CONTROLLER_DEADZONE = 0.2;
  }

  public static final class ShooterConstants {
    public static final int MASTER_PORT = 0;
    public static final int FOLLOWER_PORT = 0;
    public static final double RAMP_RATE = 0.1;
  }
}
