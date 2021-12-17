package org.usfirst.frc.team4488.robot.app.paths;

import java.util.ArrayList;
import org.usfirst.frc.team4488.lib.app.control.Path;
import org.usfirst.frc.team4488.lib.app.math.RigidTransform2d;
import org.usfirst.frc.team4488.lib.app.math.Rotation2d;
import org.usfirst.frc.team4488.lib.app.math.Translation2d;
import org.usfirst.frc.team4488.robot.app.paths.PathBuilder.Waypoint;

public class Straight implements PathContainer {

  @Override
  public Path buildPath() {
    ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();
    sWaypoints.add(new Waypoint(0, 0, 0, 0));
    sWaypoints.add(new Waypoint(96, 0, 15, 30));
    sWaypoints.add(new Waypoint(96, 96, 0, 30));

    return PathBuilder.buildPathFromWaypoints(sWaypoints);
  }

  @Override
  public RigidTransform2d getStartPose() {
    return new RigidTransform2d(new Translation2d(0, 0), Rotation2d.fromDegrees(0.0));
  }

  @Override
  public boolean isReversed() {
    return false;
  }
  // WAYPOINT_DATA:[{"position":{"x":0,"y":0},"speed":0,"radius":0,"comment":""},{"position":{"x":96,"y":0},"speed":30,"radius":15,"comment":""},{"position":{"x":96,"y":96},"speed":30,"radius":0,"comment":""}]
  // IS_REVERSED: false
  // FILE_NAME: Straight
}
