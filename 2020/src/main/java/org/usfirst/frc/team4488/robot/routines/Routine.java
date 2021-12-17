package org.usfirst.frc.team4488.robot.routines;

import java.util.ArrayList;
import org.usfirst.frc.team4488.robot.autonomous.actions.Action;
import org.usfirst.frc.team4488.robot.systems.Subsystem;

public abstract class Routine implements Action {

  protected ArrayList<Subsystem> requiredSystems;

  protected Routine() {
    requiredSystems = new ArrayList<Subsystem>();
  }

  protected void requireSystem(Subsystem system) {
    requiredSystems.add(system);
  }

  protected void requireSystems(Subsystem... systems) {
    for (Subsystem system : systems) {
      requireSystem(system);
    }
  }

  public ArrayList<Subsystem> getRequiredSystems() {
    return requiredSystems;
  }

  public abstract void abort();
}
