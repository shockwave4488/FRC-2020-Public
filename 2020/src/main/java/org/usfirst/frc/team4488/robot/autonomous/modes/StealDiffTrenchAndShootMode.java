package org.usfirst.frc.team4488.robot.autonomous.modes;

import java.util.ArrayList;
import org.usfirst.frc.team4488.robot.app.paths.CenterShotToFirstRailBall;
import org.usfirst.frc.team4488.robot.app.paths.DiffControlPanelToCenterShot;
import org.usfirst.frc.team4488.robot.app.paths.FarStartToDiffControlPanel;
import org.usfirst.frc.team4488.robot.app.paths.FirstRailBallToCenterShot;
import org.usfirst.frc.team4488.robot.autonomous.AutoModeBase;
import org.usfirst.frc.team4488.robot.autonomous.actions.Action;
import org.usfirst.frc.team4488.robot.autonomous.actions.DrivePathAction;
import org.usfirst.frc.team4488.robot.autonomous.actions.NonBlockingIndex;
import org.usfirst.frc.team4488.robot.autonomous.actions.NonBlockingIntake;
import org.usfirst.frc.team4488.robot.autonomous.actions.ParallelAction;
import org.usfirst.frc.team4488.robot.autonomous.actions.ResetPoseFromPathAction;
import org.usfirst.frc.team4488.robot.autonomous.actions.SeriesAction;
import org.usfirst.frc.team4488.robot.autonomous.actions.SpinShooterAction;
import org.usfirst.frc.team4488.robot.routines.LineUpAndShoot;
import org.usfirst.frc.team4488.robot.systems.Indexer;
import org.usfirst.frc.team4488.robot.systems.Intake;
import org.usfirst.frc.team4488.robot.systems.LimelightManager;
import org.usfirst.frc.team4488.robot.systems.Shooter;
import org.usfirst.frc.team4488.robot.systems.SubsystemManager;
import org.usfirst.frc.team4488.robot.systems.drive.DriveBase;
import org.usfirst.frc.team4488.robot.systems.drive.WestCoastDrive;

public class StealDiffTrenchAndShootMode extends AutoModeBase {

  private static final double INTAKE_TIME = 5.0;
  private static final int APPROXIMATE_ANGLE_TO_TARGET = 350;
  private static final double FAKE_SHOOT_TIME = 5;
  private static final double PRESPIN_RPMS = 4400;

  private DriveBase drive = SubsystemManager.getInstance().getDrive();
  private Shooter shooter = Shooter.getInstance();
  private LimelightManager llManager = LimelightManager.getInstance();
  private Intake intake = Intake.getInstance();
  private Indexer indexer = Indexer.getInstance();

  public StealDiffTrenchAndShootMode() {
    requireSystem(drive);
    requireSystem(shooter);
    requireSystem(llManager);
    requireSystem(intake);
    requireSystem(indexer);

    WestCoastDrive westCoast = (WestCoastDrive) drive;

    // Drive to control panel and intake
    ArrayList<Action> driveAndIntakeList = new ArrayList<Action>();
    driveAndIntakeList.add(new NonBlockingIntake());
    driveAndIntakeList.add(new NonBlockingIndex());

    ArrayList<Action> driveList = new ArrayList<Action>();
    driveList.add(new DrivePathAction(new FarStartToDiffControlPanel(), westCoast));
    driveList.add(new SpinShooterAction(PRESPIN_RPMS));
    driveList.add(new DrivePathAction(new DiffControlPanelToCenterShot(), westCoast));

    driveAndIntakeList.add(new SeriesAction(driveList));
    ParallelAction driveAndIntake = new ParallelAction(driveAndIntakeList);

    addAction(new ResetPoseFromPathAction(new FarStartToDiffControlPanel()));
    addAction(driveAndIntake);
    addAction(new LineUpAndShoot(false, true, 2.5, PRESPIN_RPMS));

    ArrayList<Action> driveList2 = new ArrayList<Action>();
    driveList2.add(new SpinShooterAction(PRESPIN_RPMS));
    driveList2.add(new DrivePathAction(new CenterShotToFirstRailBall(), westCoast));
    driveList2.add(new DrivePathAction(new FirstRailBallToCenterShot(), westCoast));

    /* In case we decide to try to do an 8-ball auto
    driveList2.add(new DrivePathAction(new CenterShotToOtherRailBalls(), westCoast));
    driveList2.add(new SpinShooterAction(PRESPIN_RPMS));
    driveList2.add(new DrivePathAction(new OtherRailBallsToCenterShot(), westCoast));
    */

    ArrayList<Action> driveAndIntakeList2 = new ArrayList<Action>();
    driveAndIntakeList2.add(new NonBlockingIntake());
    driveAndIntakeList2.add(new NonBlockingIndex());
    driveAndIntakeList2.add(new SeriesAction(driveList2));
    ParallelAction driveAndIntake2 = new ParallelAction(driveAndIntakeList2);

    addAction(driveAndIntake2);

    ArrayList<Action> shootAndIntakeList = new ArrayList<Action>();
    shootAndIntakeList.add(new NonBlockingIntake());
    shootAndIntakeList.add(new NonBlockingIndex());
    shootAndIntakeList.add(new LineUpAndShoot(false, true, 2.5, PRESPIN_RPMS));
    ParallelAction shootAndIntake = new ParallelAction(shootAndIntakeList);
    addAction(shootAndIntake);
  }

  @Override
  protected void modeStart() {}

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
