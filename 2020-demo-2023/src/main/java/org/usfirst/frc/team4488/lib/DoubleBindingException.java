package org.usfirst.frc.team4488.lib;

import org.usfirst.frc.team4488.lib.operator.Controllers.XboxButtons;

public class DoubleBindingException extends RuntimeException {
  private static final long serialVersionUID = 974937391648466429L;

  public DoubleBindingException(XboxButtons bind) {
    super("Controller Button " + bind + " Is Double Binded!");
  }
}
