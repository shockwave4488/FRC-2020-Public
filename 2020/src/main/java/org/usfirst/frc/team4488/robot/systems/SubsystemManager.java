package org.usfirst.frc.team4488.robot.systems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.ArrayList;
import java.util.HashSet;
import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.lib.loops.Looper;
import org.usfirst.frc.team4488.lib.operator.Controllers;
import org.usfirst.frc.team4488.robot.RobotInfo;
import org.usfirst.frc.team4488.robot.app.RobotStateEstimatorBase;
import org.usfirst.frc.team4488.robot.autonomous.AutoModeBase;
import org.usfirst.frc.team4488.robot.loops.RobotStateLoop;
import org.usfirst.frc.team4488.robot.routines.BindedRoutine;
import org.usfirst.frc.team4488.robot.systems.drive.DriveBase;

// import org.usfirst.frc.team4488.robot.systems.LEDController.Color;

/** Used to reset, start, stop, and update all subsystems at once */
public class SubsystemManager {

  private final Controllers xbox = Controllers.getInstance();
  private final ArrayList<Subsystem> mAllSubsystems;
  private ArrayList<BindedRoutine> enabledRoutines;

  private HashSet<BindedRoutine> runningRoutines;

  private boolean inRoutine = false;
  private final DriveBase drive;

  private boolean firstRun = true;

  private static SubsystemManager sInstance;

  public static SubsystemManager createInstance(RobotInfo robotInfo) {
    ArrayList<Subsystem> systems = robotInfo.otherSystems;
    systems.add((Subsystem) robotInfo.drive);
    sInstance = new SubsystemManager(robotInfo.drive, systems);
    return sInstance;
  }

  public static synchronized SubsystemManager getInstance() {
    return sInstance;
  }

  public SubsystemManager(DriveBase drive, ArrayList<Subsystem> allSubsystems) {
    mAllSubsystems = allSubsystems;
    this.drive = drive;
    runningRoutines = new HashSet<BindedRoutine>();
    enabledRoutines = new ArrayList<BindedRoutine>();
  }

  public void addRoutines(ArrayList<BindedRoutine> routines) {
    enabledRoutines = routines;
  }

  public void updateSmartDashboard() {
    SmartDashboard.putNumber("num routines", runningRoutines.size());

    mAllSubsystems.forEach((s) -> s.updateSmartDashboard());
  }

  public void writeToLog() {
    mAllSubsystems.forEach((s) -> s.writeToLog());
  }

  public void stop() {
    runningRoutines.forEach(routine -> routine.abort());
    runningRoutines.clear();

    mAllSubsystems.forEach((s) -> s.stop());
  }

  public void zeroSensors() {
    mAllSubsystems.forEach((s) -> s.zeroSensors());
  }

  public void registerEnabledLoops(Looper enabledLooper) {
    mAllSubsystems.forEach((s) -> s.registerEnabledLoops(enabledLooper));
  }

  public void updatePrefs() {
    mAllSubsystems.forEach((s) -> s.updatePrefs());
  }

  public void reset() {
    mAllSubsystems.forEach((s) -> s.reset());
  }

  public void setUpTrackables() {
    Logging logger = Logging.getInstance();
    logger.addStringTrackable(() -> getCurrentRoutineName(), "RunningRoutine", 10, "");

    RobotStateEstimatorBase estimator = RobotStateLoop.getInstance().getEstimator();

    logger.addTrackable(
        () -> estimator.getLatestFieldToVehicle().getValue().getRotation().getDegrees(),
        "PoseTheta",
        5);

    logger.addTrackable(
        () -> estimator.getLatestFieldToVehicle().getValue().getTranslation().x(), "PoseX", 5);
    logger.addTrackable(
        () -> estimator.getLatestFieldToVehicle().getValue().getTranslation().y(), "PoseY", 5);
    mAllSubsystems.forEach((s) -> s.setUpTrackables());
  }

  public void controllerUpdates() {

    // Check for abort
    if (wantsAbort()) {
      Logging.getInstance().writeToLogFormatted(this, "All", "All", "Aborting routines");
      runningRoutines.forEach(routine -> routine.abort());
      runningRoutines.clear();
    }

    // Check all routines bound to buttons
    for (BindedRoutine boundRoutine : enabledRoutines) {
      if (boundRoutine.wantsStart()) tryStart(boundRoutine);
    }

    // If any subsystems are free, run their default routines
    for (Subsystem system : mAllSubsystems) {
      if (getConflictingRoutine(system) == null) {
        tryStart(new BindedRoutine(system.getDefaultRoutine(), false, () -> false, true));
      }
    }

    // Update all routines
    for (BindedRoutine runningRoutine : runningRoutines) {
      handleRoutine(runningRoutine);
    }

    runningRoutines.removeIf(routine -> routine.isFinished() || !routine.wantsRun());
  }

  private void handleRoutine(BindedRoutine routine) {
    routine.update();

    if (!routine.wantsRun()) {
      routine.abort();
    }

    if (routine.isFinished()) {
      routine.done();
    }
  }

  public void startAutoRoutine(AutoModeBase routine) {
    BindedRoutine bindedAuto = new BindedRoutine(routine, false, () -> false, false);
    tryStart(bindedAuto);
  }

  public void endAutoRoutine() {
    for (BindedRoutine routine : runningRoutines) {
      if (routine.isAuto()) routine.abort();
    }

    runningRoutines.removeIf(routine -> routine.isAuto());
  }

  private void tryStart(BindedRoutine routine) {
    if (runningRoutines.contains(routine)) return;

    HashSet<BindedRoutine> conflictingRoutines = getConflictingRoutines(routine);
    for (BindedRoutine conflictingRoutine : conflictingRoutines) {
      if (!conflictingRoutine.isInterruptable()) {
        return;
      }
    }

    if (!routine.isRunOnce()) {
      for (BindedRoutine conflictingRoutine : conflictingRoutines) {
        interruptRoutine(conflictingRoutine);
        runningRoutines.remove(conflictingRoutine);
      }
    }

    startRoutine(routine);
  }

  private BindedRoutine getConflictingRoutine(Subsystem system) {
    for (BindedRoutine routine : runningRoutines) {
      if (routine.getRequiredSystems().contains(system)) {
        return routine;
      }
    }

    return null;
  }

  private HashSet<BindedRoutine> getConflictingRoutines(BindedRoutine routine) {
    HashSet<BindedRoutine> conflictingRoutines = new HashSet<BindedRoutine>();
    for (Subsystem system : routine.getRequiredSystems()) {
      BindedRoutine conflicting = getConflictingRoutine(system);
      if (conflicting != null) {
        conflictingRoutines.add(conflicting);
      }
    }

    return conflictingRoutines;
  }

  private void interruptRoutine(BindedRoutine routine) {
    routine.abort();
  }

  private void startRoutine(BindedRoutine routine) {
    if (routine.isRunOnce()) {
      routine.start();
      return;
    }

    routine.start();
    runningRoutines.add(routine);
  }

  private boolean wantsAbort() {
    return xbox.getX(xbox.m_primary) || xbox.getX(xbox.m_secondary);
  }

  public String getCurrentRoutineName() {
    ArrayList<String> names = new ArrayList<String>();
    runningRoutines.forEach(routine -> names.add(routine.getRoutineName()));

    return String.join(",", names);
  }

  public DriveBase getDrive() {
    return drive;
  }
}
