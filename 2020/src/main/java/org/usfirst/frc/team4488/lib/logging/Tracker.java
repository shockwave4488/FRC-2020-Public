package org.usfirst.frc.team4488.lib.logging;

import edu.wpi.first.wpilibj.Timer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Tracker {

  private final Trackable target;
  private final int frequency;
  private final String name;

  private int lastWritten;
  private BufferedWriter file;

  public Tracker(Trackable target, String name, int frequency) {
    this.target = target;
    this.frequency = frequency;
    this.name = name;
  }

  public void createFile(String dir) {
    try {
      String path = dir + "/" + name + ".txt";
      file = new BufferedWriter(new FileWriter(path));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void closeWriter() {
    try {
      file.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void update(String timestamp) {
    if (file == null) {
      System.out.println("Writing to log before initializing it!");
      return;
    }

    if (getTimeMillis() - lastWritten > (1000 / frequency)) {
      lastWritten = getTimeMillis();

      try {
        file.write(timestamp + " " + target.get() + "\n");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void flush() {
    try {
      file.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private int getTimeMillis() {
    return (int) (Timer.getFPGATimestamp() * 1000);
  }
}
