package org.usfirst.frc.team4488.robot.app.paths;

import java.util.ArrayList;
import org.usfirst.frc.team4488.lib.app.control.Path;
import org.usfirst.frc.team4488.lib.app.math.RigidTransform2d;
import org.usfirst.frc.team4488.lib.app.math.Rotation2d;
import org.usfirst.frc.team4488.lib.app.math.Translation2d;
import org.usfirst.frc.team4488.robot.app.paths.PathBuilder.Waypoint;

public class CenterShotAlongRail implements PathContainer {

  @Override
  public Path buildPath() {
    ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();
    sWaypoints.add(new Waypoint(460, 154, 0, 0));
    sWaypoints.add(new Waypoint(435, 145, 0, 60));
    sWaypoints.add(new Waypoint(410, 145, 15, 60));
    sWaypoints.add(new Waypoint(400, 170, 15, 60));
    sWaypoints.add(new Waypoint(360, 160, 0, 60));

    return PathBuilder.buildPathFromWaypoints(sWaypoints);
  }

  @Override
  public RigidTransform2d getStartPose() {
    return new RigidTransform2d(new Translation2d(460, 154), Rotation2d.fromDegrees(0.0));
  }

  @Override
  public boolean isReversed() {
    return true;
  }
  // WAYPOINT_DATA:[{"position":{"x":460,"y":154},"speed":0,"radius":0,"comment":""},{"position":{"x":435,"y":145},"speed":60,"radius":0,"comment":""},{"position":{"x":410,"y":145},"speed":60,"radius":15,"comment":""},{"position":{"x":400,"y":170},"speed":60,"radius":15,"comment":""},{"position":{"x":360,"y":160},"speed":60,"radius":0,"comment":""}]
  // IS_REVERSED: true
  // FILE_NAME: CenterShotAlongRail
}
