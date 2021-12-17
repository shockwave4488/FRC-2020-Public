package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.drive.SwerveDrive;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

public class DefaultSwerveDrive extends CommandBase {
  private final SwerveDrive swerve;
  private final double speedMultiplier;
  private final double rotationMultiplier;
  private final DoubleSupplier driveX;
  private final DoubleSupplier driveY;
  private final DoubleSupplier rot;
  private final BooleanSupplier fieldRelativeInput;
  private boolean fieldRelative = false; // Will change with driver's preference

  /**
   * Creates a new DefaultSwerveDrive.
   *
   * @param swerve The swerve subsystem this command will run on
   * @param speedMultiplier A speed multiplier for the driveX & driveY values (input value will be
   *     max speed in meters/sec)
   * @param rotationMultiplier A multiplier for the speed of the robot's rotation (input value will
   *     be max speed in radians/sec)
   * @param driveX The input for the swerve robot's speed on the x axis
   * @param driveY The input for the swerve robot's speed on the y axis
   * @param rot The input for how fast the robot should turn
   * @param fieldRelativeInput The input that indicates if the swerve robot should switch whether it
   *     drives in a field relative manner or robot relative manner
   */
  public DefaultSwerveDrive(
      SwerveDrive swerve,
      double speedMultiplier,
      double rotationMultiplier,
      DoubleSupplier driveX,
      DoubleSupplier driveY,
      DoubleSupplier rot,
      BooleanSupplier fieldRelativeInput) {
    this.swerve = swerve;
    this.speedMultiplier = speedMultiplier;
    this.rotationMultiplier = rotationMultiplier;
    this.driveX = driveX;
    this.driveY = driveY;
    this.rot = rot;
    this.fieldRelativeInput = fieldRelativeInput;
    addRequirements(swerve);
  }

  @Override
  public void execute() {
    fieldRelativeUpdate();
    /*
    The controller's y value effects the drive speed's x value (and vice versa) because the controller input is
    90 degrees off compared to the values SwerveDrive expects (particularly ChassisSpeeds in drive())
    */
    double driveXSpeed = driveX.getAsDouble() * speedMultiplier;
    double driveYSpeed = driveY.getAsDouble() * speedMultiplier;
    swerve.drive(driveXSpeed, driveYSpeed, rot.getAsDouble() * rotationMultiplier, fieldRelative);
  }

  /**
   * Checks if the value fieldRelativeInput provides has changed, which occurs if the driver has
   * pressed the button the BooleanSupplier is linked to. If the value has changed this class'
   * fieldRelative variable switches to let the drivers toggle between field relative or robot
   * relative operation.
   */
  private void fieldRelativeUpdate() {
    if (fieldRelativeInput.getAsBoolean()) {
      fieldRelative = !fieldRelative;
    }
  }
}
