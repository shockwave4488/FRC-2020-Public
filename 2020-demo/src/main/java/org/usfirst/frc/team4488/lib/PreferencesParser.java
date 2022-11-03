package org.usfirst.frc.team4488.lib;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.usfirst.frc.team4488.robot.Constants;

public class PreferencesParser {

  private FileReader fileReader;
  private JSONObject json;
  private JSONParser jsonParser;

  private static PreferencesParser sInstance = null;

  public static synchronized PreferencesParser getInstance() {
    if (sInstance == null) {
      sInstance = new PreferencesParser();
    }

    return sInstance;
  }

  public PreferencesParser() {
    jsonParser = new JSONParser();
    update();
  }

  public String getString(String key) throws PreferenceDoesNotExistException {
    update();
    if (keyExists(key)) {
      return (String) json.get(key);
    }

    System.out.println("Key " + key + " does not exist in preferences!");
    throw new PreferenceDoesNotExistException();
  }

  public String tryGetString(String key, String defaultString) {
    try {
      return getString(key);
    } catch (PreferenceDoesNotExistException e) {
      return defaultString;
    }
  }

  public boolean getBoolean(String key) throws PreferenceDoesNotExistException {
    update();
    if (keyExists(key)) {
      return Boolean.parseBoolean(json.get(key).toString());
    }

    System.out.println("Key " + key + " does not exist in preferences!");
    throw new PreferenceDoesNotExistException();
  }

  public int getInt(String key) throws PreferenceDoesNotExistException {
    update();
    if (keyExists(key)) {
      return (int) json.get(key);
    }

    System.out.println("Key " + key + " does not exist in preferences!");
    throw new PreferenceDoesNotExistException();
  }

  public int tryGetInt(String key, int defaultInt) {
    try {
      return getInt(key);
    } catch (PreferenceDoesNotExistException e) {
      return defaultInt;
    }
  }

  public double getDouble(String key) throws PreferenceDoesNotExistException {
    update();
    if (keyExists(key)) {
      return ((Number) json.get(key)).doubleValue();
    }

    System.out.println("Key " + key + " does not exist in preferences!");
    throw new PreferenceDoesNotExistException();
  }

  public double tryGetDouble(String key, double defaultDouble) {
    try {
      return getDouble(key);
    } catch (PreferenceDoesNotExistException e) {
      return defaultDouble;
    }
  }

  public boolean tryGetBoolean(String key, boolean defaultBoolean) {
    try {
      return getBoolean(key);
    } catch (PreferenceDoesNotExistException e) {
      return defaultBoolean;
    }
  }

  private boolean keyExists(String key) {
    if (json == null || json.get(key) == null) {
      return false;
    }

    return true;
  }

  private void update() {
    try {
      fileReader = new FileReader(Constants.prefsPath);

      Object obj = jsonParser.parse(fileReader);
      json = (JSONObject) obj;

      fileReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("Could not find preferences file at " + Constants.prefsPath);
    } catch (IOException e) {
      System.out.println("IOException in PreferencesParser");
    } catch (ParseException e) {
      e.printStackTrace();
    } catch (NullPointerException e) {
      System.out.println("NullPointerException in PreferencesParser");
    }
  }
}
