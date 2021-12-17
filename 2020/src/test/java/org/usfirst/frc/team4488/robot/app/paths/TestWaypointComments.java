package org.usfirst.frc.team4488.robot.app.paths;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import org.junit.Test;
import org.usfirst.frc.team4488.lib.app.control.Path;
import org.usfirst.frc.team4488.lib.waypointCommentParser;

public class TestWaypointComments {

  public <T extends PathContainer> void testClass(Class<T> cls) {
    PathContainer actualDrivePath;
    try {
      actualDrivePath = cls.getDeclaredConstructor().newInstance();
      Path actual = actualDrivePath.buildPath();
      assertNotNull(actualDrivePath);
      Path expected = waypointCommentParser.readWaypointComments(actualDrivePath.getClass());
      assertEquals(actual.toString(), expected.toString());
    } catch (InstantiationException
        | IllegalAccessException
        | NoSuchMethodException
        | InvocationTargetException e) {
      e.printStackTrace();
      throw new RuntimeException(e.toString());
    }
  }

  @Test
  public void Straight() {
    testClass(Straight.class);
  }
}
