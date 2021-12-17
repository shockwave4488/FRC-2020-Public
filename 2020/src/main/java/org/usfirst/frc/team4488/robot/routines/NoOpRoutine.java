package org.usfirst.frc.team4488.robot.routines;

import java.util.ArrayList;
import org.usfirst.frc.team4488.robot.routines.defaults.DefaultRoutine;
import org.usfirst.frc.team4488.robot.systems.Subsystem;

public class NoOpRoutine extends DefaultRoutine {

  public NoOpRoutine() {}

  public NoOpRoutine(Subsystem systemToHold) {
    requireSystem(systemToHold);
  }

  public NoOpRoutine(ArrayList<Subsystem> systemsToHold) {
    for (Subsystem system : systemsToHold) {
      requireSystem(system);
    }
  }

  public void start() {}

  public void update() {}

  public void abort() {};
}
