package org.usfirst.frc.team4488.robot.app.paths;

import java.util.ArrayList;
import org.usfirst.frc.team4488.lib.app.control.Path;
import org.usfirst.frc.team4488.lib.app.math.RigidTransform2d;
import org.usfirst.frc.team4488.lib.app.math.Rotation2d;
import org.usfirst.frc.team4488.lib.app.math.Translation2d;
import org.usfirst.frc.team4488.robot.app.paths.PathBuilder.Waypoint;

public class StartOfSameTrenchToControlPanel implements PathContainer {

  @Override
  public Path buildPath() {
    ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();
    sWaypoints.add(new Waypoint(426, 30, 0, 60));
    sWaypoints.add(new Waypoint(328, 30, 0, 60));

    return PathBuilder.buildPathFromWaypoints(sWaypoints);
  }

  @Override
  public RigidTransform2d getStartPose() {
    return new RigidTransform2d(new Translation2d(426, 30), Rotation2d.fromDegrees(0.0));
  }

  @Override
  public boolean isReversed() {
    return true;
  }
  // WAYPOINT_DATA:[{"position":{"x":426,"y":30},"speed":60,"radius":0,"comment":""},{"position":{"x":328,"y":30},"speed":60,"radius":0,"comment":""}]
  // IS_REVERSED: true
  // FILE_NAME: StartOfSameTrenchToControlPanel
}
