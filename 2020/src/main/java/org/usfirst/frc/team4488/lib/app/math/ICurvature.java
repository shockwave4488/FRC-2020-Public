package org.usfirst.frc.team4488.lib.app.math;

public interface ICurvature<S> extends State<S> {
  double getCurvature();

  double getDCurvatureDs();
}
