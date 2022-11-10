package org.usfirst.frc.team4488.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.ArrayList;
import org.usfirst.frc.team4488.lib.DoubleBindingException;
import org.usfirst.frc.team4488.lib.flowcontrol.EdgeTrigger;
import org.usfirst.frc.team4488.lib.operator.Controllers;
import org.usfirst.frc.team4488.lib.operator.Controllers.XboxButtons;
import org.usfirst.frc.team4488.robot.autonomous.actions.RunOnceAction;
import org.usfirst.frc.team4488.robot.routines.ArcToStation;
import org.usfirst.frc.team4488.robot.routines.Bindable;
import org.usfirst.frc.team4488.robot.routines.BindedRoutine;
import org.usfirst.frc.team4488.robot.routines.IntakeRoutine;
import org.usfirst.frc.team4488.robot.routines.Routine;
import org.usfirst.frc.team4488.robot.routines.SimpleShoot;

public class Bindings {

  public static final Controllers xbox = Controllers.getInstance();

  private static ArrayList<XboxButtons> xboxButtons = new ArrayList<XboxButtons>();

  public static void addBindings() {
    switch (RobotMap.robotName) {
      case BareRoboRIO:
        addBareRioBindings();
        break;
      case ProgrammingPlatform:
        addProgPlatformBindings();
        break;
      case SwervePlatform:
        addSwervePlatformBindings();
        break;
      case MockPlatform:
        addMockBindings();
        break;
      case Practice:
      case Competition:
        addCompBindings();
        break;
    }
  }

  public static void addBareRioBindings() {}

  public static void addProgPlatformBindings() {}

  public static void addSwervePlatformBindings() {}

  public static void addMockBindings() {
    bindRoutine(new ArcToStation(), XboxButtons.BPrim, false, false);
  }

  public static void addCompBindings() {
    // Only use one controller
    bindRoutine(new IntakeRoutine(), XboxButtons.RightTriggerPrim, true, true);
    bindRoutine(
        new SimpleShoot(() -> SmartDashboard.getNumber("Simple shoot RPM", 2000)),
        XboxButtons.XPrim,
        true,
        true);
  }

  public static void bindRunOnce(RunOnceAction routine, Bindable bind) {
    EdgeTrigger trigger = new EdgeTrigger(false);
    BindedRoutine binded =
        new BindedRoutine(routine, false, () -> trigger.getRisingUpdate(bind.get()), false);
    RobotMap.bindedRoutines.add(0, binded);
  }

  public static void bindRoutine(
      Routine routine, Bindable bind, boolean shouldHold, boolean interruptible) {
    BindedRoutine binded = new BindedRoutine(routine, shouldHold, bind, interruptible);
    RobotMap.bindedRoutines.add(binded);
  }

  public static void bindRunOnce(RunOnceAction routine, XboxButtons bind) {
    for (int i = 0; i < xboxButtons.size(); i++) {
      if (bind == xboxButtons.get(i)) {
        throw new DoubleBindingException(bind);
      }
    }
    xboxButtons.add(bind);
    bindRunOnce(routine, bind.bind);
  }

  public static void bindRoutine(
      Routine routine, XboxButtons bind, boolean shouldHold, boolean interruptible) {
    for (int i = 0; i < xboxButtons.size(); i++) {
      if (bind == xboxButtons.get(i)) {
        throw new DoubleBindingException(bind);
      }
    }
    xboxButtons.add(bind);
    bindRoutine(routine, bind.bind, shouldHold, interruptible);
  }
}
