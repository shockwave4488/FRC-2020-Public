package org.usfirst.frc.team4488.robot.autonomous;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team4488.lib.logging.Logging;
import org.usfirst.frc.team4488.robot.autonomous.modes.NothingMode;
import org.usfirst.frc.team4488.robot.autonomous.modes.ShootAndGrabTrenchMode;
import org.usfirst.frc.team4488.robot.autonomous.modes.StealDiffTrenchAndShootMode;

/*
 * Adds radio buttons to the SmartDashboard interface that allow the user to select starting position
 * and desired type of auto routine. Decision logic is used to combine these choices with the
 * Field Management Data (FMS) to select the type of auto routine you want to run.
 */
public class AutoModeSelector {

  private static SendableChooser<AutoModeBase> autoSelector;

  /*
   * returns the auto mode that you will run for your autonomous session
   */
  public static AutoModeBase getSelectedAutoMode() {
    AutoModeBase selected = autoSelector.getSelected();
    displayAutoModeDetails(selected);
    return autoSelector.getSelected();
  }

  /*
   * displays radio buttons on SmartDashboard. See determineAutoModeToUse() for details
   */
  public static void init() {
    autoSelector = new SendableChooser<AutoModeBase>();
    autoSelector.setDefaultOption("Same Trench", new ShootAndGrabTrenchMode());
    autoSelector.addOption("Steal Diff Trench", new StealDiffTrenchAndShootMode());
    autoSelector.addOption("Nothing", new NothingMode());
    SmartDashboard.putData(autoSelector);
  }

  /**
   * used to display the auto mode details, what starting side we selected, and what actual auto
   * mode was selected.
   */
  private static void displayAutoModeDetails(AutoModeBase selectedAutoMode) {
    String name = selectedAutoMode.getClass().getSimpleName();
    Logging.getInstance()
        .writeToLogFormatted("AutoModeSelector", "Auto Mode \"" + name + "\" Selected");
  }
}
