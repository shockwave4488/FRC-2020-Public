package org.usfirst.frc.team4488.robot.app.paths;

import java.util.ArrayList;
import org.usfirst.frc.team4488.lib.app.control.Path;
import org.usfirst.frc.team4488.lib.app.math.RigidTransform2d;
import org.usfirst.frc.team4488.lib.app.math.Rotation2d;
import org.usfirst.frc.team4488.lib.app.math.Translation2d;
import org.usfirst.frc.team4488.robot.app.paths.PathBuilder.Waypoint;

public class DiffControlPanelToCenterShot implements PathContainer {

  @Override
  public Path buildPath() {
    ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();
    sWaypoints.add(new Waypoint(393, 297, 0, 0));
    sWaypoints.add(new Waypoint(404, 250, 20, 60));
    sWaypoints.add(new Waypoint(440, 204, 20, 60));
    sWaypoints.add(new Waypoint(440, 164, 15, 60));
    sWaypoints.add(new Waypoint(460, 154, 0, 60));

    return PathBuilder.buildPathFromWaypoints(sWaypoints);
  }

  @Override
  public RigidTransform2d getStartPose() {
    return new RigidTransform2d(new Translation2d(393, 297), Rotation2d.fromDegrees(0.0));
  }

  @Override
  public boolean isReversed() {
    return false;
  }
  // WAYPOINT_DATA:[{"position":{"x":393,"y":297},"speed":0,"radius":0,"comment":""},{"position":{"x":404,"y":250},"speed":60,"radius":20,"comment":""},{"position":{"x":440,"y":204},"speed":60,"radius":20,"comment":""},{"position":{"x":440,"y":164},"speed":60,"radius":15,"comment":""},{"position":{"x":460,"y":154},"speed":60,"radius":0,"comment":""}]
  // IS_REVERSED: false
  // FILE_NAME: DiffControlPanelToCenterShot
}
