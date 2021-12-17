package org.usfirst.frc.team4488.robot.autonomous.modes;

import org.usfirst.frc.team4488.robot.app.paths.Straight;
import org.usfirst.frc.team4488.robot.autonomous.AutoModeBase;
import org.usfirst.frc.team4488.robot.autonomous.actions.DrivePathAction;
import org.usfirst.frc.team4488.robot.systems.SubsystemManager;
import org.usfirst.frc.team4488.robot.systems.drive.DriveBase;
import org.usfirst.frc.team4488.robot.systems.drive.WestCoastDrive;

public class StraightMode extends AutoModeBase {

  private DriveBase drive = SubsystemManager.getInstance().getDrive();

  public StraightMode() {
    requireSystem(drive);
  }

  @Override
  protected void modeStart() {
    // Unsafe cast is intentional, APP shouldnt be run with non west coast drives
    WestCoastDrive westCoast = (WestCoastDrive) drive;

    DrivePathAction action = new DrivePathAction(new Straight(), westCoast);
    addAction(action);
  }

  protected void modeUpdate() {}

  protected void modeDone() {}

  protected void modeAbort() {}

  protected boolean modeIsFinished() {
    return true;
  }
}
