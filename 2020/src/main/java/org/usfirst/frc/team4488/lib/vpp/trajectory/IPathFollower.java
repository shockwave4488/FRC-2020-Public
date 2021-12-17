package org.usfirst.frc.team4488.lib.vpp.trajectory;

import org.usfirst.frc.team4488.lib.app.math.Pose2d;
import org.usfirst.frc.team4488.lib.app.math.Twist2d;

public interface IPathFollower {
  public Twist2d steer(Pose2d current_pose);

  public boolean isDone();
}
