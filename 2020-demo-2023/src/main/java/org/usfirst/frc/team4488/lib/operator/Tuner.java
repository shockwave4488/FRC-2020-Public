package org.usfirst.frc.team4488.lib.operator;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.usfirst.frc.team4488.lib.PreferencesParser;

public class Tuner {

  private double period;
  private AtomicBoolean shouldRun = new AtomicBoolean(false);
  private Thread updateThread;
  // private TunerCallback callback;
  private Map<String, Double> vals = new HashMap<String, Double>();

  /**
   * Create a new Tuner. SmartDashboard values will be collected [frequency] times a second, and if
   * any are changed, they will be passed to the callback
   *
   * @param callback callback to call when a value is changed
   * @param frequency how many times to check SmartDashboard each second
   */
  public Tuner(TunerCallback callback, int frequency) {
    // this.callback = callback;

    period = 1 / (double) frequency;

    updateThread =
        new Thread(
            new Runnable() {
              public void run() {
                callback.update(vals);
                while (shouldRun.get()) {
                  Map<String, Double> newVals = getVals();
                  for (String key : newVals.keySet()) {
                    if (newVals.get(key).doubleValue() != vals.get(key).doubleValue()) {
                      callback.update(newVals);
                      break;
                    }
                  }
                  vals = newVals;

                  try {
                    Thread.sleep(Math.round(period * 1000));
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                }
              }
            });
  }

  /**
   * Add a new key to track, and put it on SmartDashboard
   *
   * @param key key to track
   * @param initialValue The initial value to put onto SmartDashboard
   */
  public void addValue(String key, double initialValue) {
    vals.put(key, initialValue);
    SmartDashboard.putNumber(key, initialValue);
  }

  /**
   * Add a value using the key's prefs value as the initial value
   *
   * @param key the key to track and get from prefs
   * @param defaultValue default value to use in case the pref doesnt exist
   */
  public void addValueFromPrefs(String key, double defaultValue) {
    double val = PreferencesParser.getInstance().tryGetDouble(key, defaultValue);
    addValue(key, val);
  }

  private Map<String, Double> getVals() {
    Map<String, Double> newVals = new HashMap<String, Double>();
    for (String key : vals.keySet()) {
      double val = SmartDashboard.getNumber(key, vals.get(key));
      newVals.put(key, val);
    }
    return newVals;
  }

  /** Start the thread the tuner runs on. The callback will be immediately called once */
  public void start() {
    shouldRun.set(true);
    updateThread.start();
  }

  /** Stop the tuner thread */
  public void stop() {
    shouldRun.set(false);
  }
}
