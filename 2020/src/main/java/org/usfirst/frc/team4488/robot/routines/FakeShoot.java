package org.usfirst.frc.team4488.robot.routines;

public class FakeShoot extends SeriesRoutine {

  public FakeShoot() {
    appendRoutine(new AlignDriveWithCamera(true));
    appendRoutine(new PurgeCells(2));
  }

  @Override
  protected void constantStart() {}

  @Override
  protected void constantUpdate() {}

  @Override
  protected void constantDone() {}

  @Override
  protected void constantAbort() {}

  @Override
  protected boolean constantIsFinished() {
    return true;
  }
}
