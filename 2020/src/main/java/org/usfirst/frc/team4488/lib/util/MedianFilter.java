package org.usfirst.frc.team4488.lib.util;

import java.util.ArrayList;
import java.util.Collections;

public class MedianFilter {

  private final int bufferSize;
  private final ArrayList<Double> buffer;

  public MedianFilter(int bufferSize) {
    this.bufferSize = bufferSize;
    buffer = new ArrayList<Double>();
  }

  public void clear() {
    buffer.clear();
  }

  public boolean isFull() {
    return buffer.size() == bufferSize;
  }

  public void update(double val) {
    if (isFull()) {
      for (int i = 0; i < bufferSize - 1; i++) {
        buffer.set(i, buffer.get(i + 1));
      }
      buffer.set(bufferSize - 1, val);
    } else {
      buffer.add(val);
    }
  }

  public double getMedian() {
    int size = buffer.size();
    if (size == 0) return 0;

    ArrayList<Double> sorted = new ArrayList<Double>();
    for (Double d : buffer) {
      sorted.add(d);
    }
    Collections.sort(sorted);

    if (size % 2 == 1) {
      return sorted.get((size - 1) / 2);
    } else {
      int firstMiddle = size / 2;
      int secondMiddle = firstMiddle - 1;

      return (sorted.get(firstMiddle) + sorted.get(secondMiddle)) / 2;
    }
  }

  public double updateAndGetMedian(double val) {
    update(val);
    return getMedian();
  }
}
