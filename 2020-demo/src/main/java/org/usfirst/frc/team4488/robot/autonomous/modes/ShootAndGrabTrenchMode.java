package org.usfirst.frc.team4488.robot.autonomous.modes;

import java.util.ArrayList;
import org.usfirst.frc.team4488.robot.app.paths.PortStartToStartOfSameTrench;
import org.usfirst.frc.team4488.robot.app.paths.StartOfSameTrenchToControlPanel;
import org.usfirst.frc.team4488.robot.autonomous.AutoModeBase;
import org.usfirst.frc.team4488.robot.autonomous.actions.Action;
import org.usfirst.frc.team4488.robot.autonomous.actions.DrivePathAction;
import org.usfirst.frc.team4488.robot.autonomous.actions.IntakeOut;
import org.usfirst.frc.team4488.robot.autonomous.actions.NonBlockingIndex;
import org.usfirst.frc.team4488.robot.autonomous.actions.NonBlockingIntake;
import org.usfirst.frc.team4488.robot.autonomous.actions.ParallelAction;
import org.usfirst.frc.team4488.robot.autonomous.actions.ResetPoseFromPathAction;
import org.usfirst.frc.team4488.robot.autonomous.actions.SpinShooterAction;
import org.usfirst.frc.team4488.robot.routines.LineUpAndShoot;
import org.usfirst.frc.team4488.robot.systems.Indexer;
import org.usfirst.frc.team4488.robot.systems.Intake;
import org.usfirst.frc.team4488.robot.systems.LimelightManager;
import org.usfirst.frc.team4488.robot.systems.Shooter;
import org.usfirst.frc.team4488.robot.systems.SubsystemManager;
import org.usfirst.frc.team4488.robot.systems.drive.DriveBase;
import org.usfirst.frc.team4488.robot.systems.drive.WestCoastDrive;

public class ShootAndGrabTrenchMode extends AutoModeBase {

  // private static final double INTAKE_TIME = 5.0;
  // private static final double FAKE_SHOOT_TIME = 5;
  private static final double FIRST_PRESPIN_RPMS = 4350;
  private static final double SECOND_PRESPIN_RPMS = 4400;

  private DriveBase drive = SubsystemManager.getInstance().getDrive();
  private Shooter shooter = Shooter.getInstance();
  private LimelightManager llManager = LimelightManager.getInstance();
  private Intake intake = Intake.getInstance();
  private Indexer indexer = Indexer.getInstance();

  public ShootAndGrabTrenchMode() {
    requireSystem(drive);
    requireSystem(shooter);
    requireSystem(llManager);
    requireSystem(intake);
    requireSystem(indexer);
  }

  @Override
  protected void modeStart() {
    WestCoastDrive westCoast = (WestCoastDrive) drive;

    addAction(new ResetPoseFromPathAction(new PortStartToStartOfSameTrench()));
    addAction(new SpinShooterAction(FIRST_PRESPIN_RPMS));

    // Back up to the start of the trench and shoot
    DrivePathAction driveToTrench =
        new DrivePathAction(new PortStartToStartOfSameTrench(), westCoast);
    addAction(driveToTrench);

    IntakeOut intakeOut = new IntakeOut();
    addAction(intakeOut);

    addAction(new LineUpAndShoot(false, true, 2.5, FIRST_PRESPIN_RPMS));

    addAction(new SpinShooterAction(SECOND_PRESPIN_RPMS));

    // Back up to the end of the trench while intaking
    DrivePathAction drive = new DrivePathAction(new StartOfSameTrenchToControlPanel(), westCoast);
    NonBlockingIndex index = new NonBlockingIndex();
    NonBlockingIntake intake = new NonBlockingIntake();

    ArrayList<Action> intakeAndIndexAndDriveList = new ArrayList<Action>();
    intakeAndIndexAndDriveList.add(intake);
    intakeAndIndexAndDriveList.add(index);
    intakeAndIndexAndDriveList.add(drive);
    ParallelAction intakeAndIndexAndDrive = new ParallelAction(intakeAndIndexAndDriveList);

    addAction(intakeAndIndexAndDrive);

    // Shoot balls
    ArrayList<Action> shootAndIntakeList = new ArrayList<Action>();
    shootAndIntakeList.add(new LineUpAndShoot(false, true, 2.5, SECOND_PRESPIN_RPMS));
    shootAndIntakeList.add(intake);
    ParallelAction shootAndIntake = new ParallelAction(shootAndIntakeList);
    addAction(shootAndIntake);
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
