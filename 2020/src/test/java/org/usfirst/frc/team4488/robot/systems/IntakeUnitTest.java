package org.usfirst.frc.team4488.robot.systems;

import static org.junit.Assert.*;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.Solenoid;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.usfirst.frc.team4488.robot.RobotMap;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Intake.class})
@SuppressStaticInitializationFor("edu.wpi.first.wpilibj.smartdashboard.SmartDashboard")
public class IntakeUnitTest {

  @Mock WPI_VictorSPX mockIntake;
  @Mock Solenoid mockIntakePiston;
  @Mock WPI_VictorSPX mockHopperMotor;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    mockHopperMotor.setInverted(false);
    PowerMockito.whenNew(WPI_VictorSPX.class)
        .withArguments(RobotMap.FrontRollerMotor)
        .thenReturn(mockIntake);
    PowerMockito.whenNew(Solenoid.class).withAnyArguments().thenReturn(mockIntakePiston);
    PowerMockito.whenNew(WPI_VictorSPX.class)
        .withArguments(RobotMap.HopperMotor)
        .thenReturn(mockHopperMotor);
  }

  @Test
  public void testIntakeConstructor() throws Exception {
    Intake intake = new Intake();

    assertNotNull(intake);
  }

  @Test
  public void intakeStateTest() throws Exception {
    Intake intake = new Intake();
    int intakePower = intake.getIntakePower().power;
    assert intakePower == 0;
    intake.setIntakeOn();
    intakePower = intake.getIntakePower().power;
    assert intakePower > 0;
    intake.setIntakeReverse();
    intakePower = intake.getIntakePower().power;
    assert intakePower < 0;
    intake.setIntakeOff();
    intakePower = intake.getIntakePower().power;
    assert intakePower == 0;
  }

  @Test
  public void hopperOnTest() throws Exception {
    Intake intake = new Intake();
    intake.hopperOn();
    assert intake.hopperIsOn();
  }

  @Test
  public void hopperOffTest() throws Exception {
    Intake intake = new Intake();
    intake.hopperOff();
    assert !intake.hopperIsOn();
  }
}
