package org.usfirst.frc.team4488.robot.autonomous.modes;

import java.util.ArrayList;
import org.usfirst.frc.team4488.robot.app.paths.RushB;
import org.usfirst.frc.team4488.robot.autonomous.AutoModeBase;
import org.usfirst.frc.team4488.robot.autonomous.actions.Action;
import org.usfirst.frc.team4488.robot.autonomous.actions.DrivePathAction;
import org.usfirst.frc.team4488.robot.autonomous.actions.ParallelAction;
import org.usfirst.frc.team4488.robot.autonomous.actions.ResetPoseFromPathAction;
import org.usfirst.frc.team4488.robot.autonomous.actions.SeriesAction;
import org.usfirst.frc.team4488.robot.autonomous.actions.WaitForPathMarkerAction;
import org.usfirst.frc.team4488.robot.autonomous.actions.stopPathAction;
import org.usfirst.frc.team4488.robot.systems.SubsystemManager;
import org.usfirst.frc.team4488.robot.systems.drive.WestCoastDrive;

public class RushBMode extends AutoModeBase {

  public RushBMode() {
    requireSystem(SubsystemManager.getInstance().getDrive());
  }

  @Override
  protected void modeStart() {
    WestCoastDrive westCoast = (WestCoastDrive) SubsystemManager.getInstance().getDrive();

    addAction(new ResetPoseFromPathAction(new RushB()));

    DrivePathAction rushB = new DrivePathAction(new RushB(), westCoast);

    ArrayList<Action> cancelDrive = new ArrayList<Action>();
    cancelDrive.add(new WaitForPathMarkerAction(rushB.getAppController(), "stop"));
    cancelDrive.add(new stopPathAction(rushB));
    // addAction(rushB);

    ArrayList<Action> drive = new ArrayList<Action>();
    drive.add(new SeriesAction(cancelDrive));
    drive.add(rushB);

    ParallelAction killPathRoutine = new ParallelAction(drive);

    addAction(killPathRoutine);
  }

  @Override
  protected void modeUpdate() {}

  @Override
  protected void modeDone() {}

  @Override
  protected void modeAbort() {}

  @Override
  protected boolean modeIsFinished() {
    return true;
  }
}
