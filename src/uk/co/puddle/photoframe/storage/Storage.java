package uk.co.puddle.photoframe.storage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import uk.co.puddle.photoframe.Logging;

import android.content.Context;
import android.util.Log;

public class Storage {
    
    public static final String CURRENT_SEQ_KEY = "current.sequence";
    
    private static final String FILENAME = "StoredProperties.properties";

    public void saveProperty(Context context, String key, String value) {
        try {
            Log.d(Logging.TAG, "Storage; saveProperty; key: " + key + "; value: " + value);
            Properties properties = load(context);
            properties.put(key, value);
            save(context, properties);
        } catch (IOException e) {
            Log.w(Logging.TAG, "Storage; saveProperty; failed with: " + e.toString(), e);
        }
    }
    public void saveIntProperty(Context context, String key, int value) {
        saveProperty(context, key, Integer.toString(value));
    }
    
    public String getProperty(Context context, String key, String defaultValue) {
        String value;
        Log.d(Logging.TAG, "Storage; getProperty; key: " + key + "; defaultValue: " + defaultValue);
        try {
            Properties properties = load(context);
            value = properties.getProperty(key, defaultValue);
        } catch (IOException e) {
            Log.w(Logging.TAG, "Storage; getProperty; failed with: " + e.toString(), e);
            value = defaultValue;
        }
        Log.d(Logging.TAG, "Storage; getProperty; key: " + key + "; returning value: " + value);
        return value;
    }

    public int getIntProperty(Context context, String key, int defaultValue) {
        String value = getProperty(context, key, Integer.toString(defaultValue));
        if (value == null) {
            return defaultValue;
        } else {
            return Integer.parseInt(value);
        }
    }
    
    private Properties load(Context context) throws IOException {
        Properties properties = new Properties();
        FileInputStream in = null;
        try {
            in = context.openFileInput(FILENAME);
            properties.load(in);
        } catch (FileNotFoundException e) {
            Log.i(Logging.TAG, "Storage; load; failed with: " + e.toString() + "; assuming first time through, continue anyway");
        } finally {
            if (in != null) { in.close(); }
        }
        return properties;
    }

    private void save(Context context, Properties properties) throws IOException {
        FileOutputStream out = null;
        try {
            out = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            properties.store(out, "");
        } finally {
            if (out != null) { out.close(); }
        }
    }
}
