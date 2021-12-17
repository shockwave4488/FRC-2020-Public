package org.usfirst.frc.team4488.lib.logging;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.MatchType;
import edu.wpi.first.wpilibj.Timer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor({"edu.wpi.first.wpilibj.DriverStation"})
@PrepareForTest({Logging.class, Timer.class, DriverStation.class})
public class LoggingTest {
  String fullPath;
  String testDirectory = "/build/.testLogger";
  @Mock private DriverStation mockDriverStation;

  @Before
  public void setup() throws Exception {
    // Prepare mocks
    MockitoAnnotations.initMocks(this);
    PowerMockito.mockStatic(Timer.class);
    PowerMockito.mockStatic(DriverStation.class);
    when(Timer.getFPGATimestamp()).thenReturn(100.0);

    // Mocking DriverStation.getInstance() as well as the constructor since static initalization was
    // skipped
    PowerMockito.whenNew(DriverStation.class).withAnyArguments().thenReturn(mockDriverStation);
    when(DriverStation.getInstance()).thenReturn(mockDriverStation);
    when(mockDriverStation.getMatchType()).thenReturn(MatchType.Qualification);
    when(mockDriverStation.getMatchNumber()).thenReturn(10);

    // Prepare test path for creating the logger
    Path currentRelativePath = Paths.get("");
    fullPath = currentRelativePath.toAbsolutePath().toString();
    fullPath += testDirectory;
  }

  @Test
  public void testWriteToLog() {
    // Create and initialize the logger
    Logging logger = Logging.getInstance(fullPath);
    logger.initialize();

    // Write a couple of lines
    logger.writeRaw("test log print 1");
    logger.writeRaw("second log print");
    logger.flush();

    // Read in the results
    String actual = "";
    String expected = "test log print 1\nsecond log print\n";
    try {
      FileReader fr = new FileReader(logger.fullPath + "/main.txt");
      int i;
      while ((i = fr.read()) != -1) actual += (char) i;
      fr.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Make sure that the logs were actually written
    assertEquals(actual, expected);
  }

  @After
  public void cleanUp() {
    try {
      FileUtils.deleteDirectory(new File(fullPath));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
