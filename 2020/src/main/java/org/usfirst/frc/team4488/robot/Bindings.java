package org.usfirst.frc.team4488.robot;

import java.util.ArrayList;
import org.usfirst.frc.team4488.lib.DoubleBindingException;
import org.usfirst.frc.team4488.lib.flowcontrol.EdgeTrigger;
import org.usfirst.frc.team4488.lib.operator.ButtonBox;
import org.usfirst.frc.team4488.lib.operator.Controllers;
import org.usfirst.frc.team4488.lib.operator.Controllers.XboxButtons;
import org.usfirst.frc.team4488.robot.autonomous.actions.IntakeInAction;
import org.usfirst.frc.team4488.robot.autonomous.actions.RunOnceAction;
import org.usfirst.frc.team4488.robot.autonomous.actions.SpinShooterAction;
import org.usfirst.frc.team4488.robot.routines.AlignDriveWithCamera;
import org.usfirst.frc.team4488.robot.routines.ArcToStation;
import org.usfirst.frc.team4488.robot.routines.Bindable;
import org.usfirst.frc.team4488.robot.routines.BindedRoutine;
import org.usfirst.frc.team4488.robot.routines.ControlPanelSpinnerRoutine;
import org.usfirst.frc.team4488.robot.routines.IntakeRoutine;
import org.usfirst.frc.team4488.robot.routines.LineUpAndShoot;
import org.usfirst.frc.team4488.robot.routines.OuttakeRoutine;
import org.usfirst.frc.team4488.robot.routines.PurgeCells;
import org.usfirst.frc.team4488.robot.routines.Routine;
import org.usfirst.frc.team4488.robot.routines.SoloClimbRoutine;

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
    /* Secondary Bindings */
    // bindRoutine(new ControlPanelRotation(), XboxButtons.ASec, false, false);
    // bindRoutine(new ControlPanelColor(), XboxButtons.BSec, false, false);
    bindRoutine(new IntakeRoutine(), XboxButtons.RightTriggerSec, true, true);
    bindRoutine(new OuttakeRoutine(), XboxButtons.RightBumperSec, true, false);
    // bindRoutine(new LineUpAndShoot(true), XboxButtons.RightBumperSec, false, false);
    // bindRoutine(new LineUpAndShoot(false), XboxButtons.LeftBumperSec, false, false);
    bindRunOnce(new IntakeInAction(), XboxButtons.LeftTriggerSec);
    bindRoutine(new PurgeCells(), XboxButtons.YSec, true, false);

    /* Primary Bindings */
    // bindRoutine(new TestShootingRoutine(), XboxButtons.BPrim, false, false);
    // bindRoutine(new LineUpAndShoot(true), XboxButtons.RightBumperPrim, false, false);
    bindRoutine(new LineUpAndShoot(false, false), XboxButtons.LeftTriggerPrim, false, false);
    // bindRoutine(new ShootingRoutine(false), XboxButtons.RightTriggerPrim, false, false);
    bindRoutine(new LineUpAndShoot(false, false, 4700, false), XboxButtons.APrim, false, false);
    bindRoutine(new AlignDriveWithCamera(false), XboxButtons.YPrim, false, false);
    bindRunOnce(new SpinShooterAction(4700), () -> ButtonBox.getInstance().button14());
    bindRunOnce(new SpinShooterAction(4200), () -> ButtonBox.getInstance().button15());
    bindRunOnce(new SpinShooterAction(0), () -> ButtonBox.getInstance().button16());
    // bindRoutine(new ArcToStation(), XboxButtons.APrim, false, false);

    bindRoutine(
        new ControlPanelSpinnerRoutine(), () -> ButtonBox.getInstance().button10(), true, false);

    /* Other Bindings */
    bindRoutine(
        new SoloClimbRoutine(),
        () -> xbox.getDPadPressed(xbox.m_primary) && xbox.getDPadPressed(xbox.m_secondary),
        false,
        false);
  }

  public static void bindRunOnce(RunOnceAction routine, Bindable bind) {
    EdgeTrigger trigger = new EdgeTrigger(false);
    BindedRoutine binded =
        new BindedRoutine(routine, false, () -> trigger.getRisingUpdate(bind.get()), false);
    RobotMap.bindedRoutines.add(0, binded);
  }

  public static void bindRoutine(
      Routine routine, Bindable bind, boolean shouldHold, boolean interruptable) {
    BindedRoutine binded = new BindedRoutine(routine, shouldHold, bind, interruptable);
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
      Routine routine, XboxButtons bind, boolean shouldHold, boolean interruptable) {
    for (int i = 0; i < xboxButtons.size(); i++) {
      if (bind == xboxButtons.get(i)) {
        throw new DoubleBindingException(bind);
      }
    }
    xboxButtons.add(bind);
    bindRoutine(routine, bind.bind, shouldHold, interruptable);
  }
}
