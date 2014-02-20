package uk.co.puddle.photoframe.prefs;

import uk.co.puddle.photoframe.alarms.RunningMode;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MyPrefs {
    
    public static final String PREFS_NAME = "PhotoFrame";
    
    public static final String PREF_TAB_POSITION = "tab.position";
    public static final String PREF_RUNNING_MODE = "running.mode";
    public static final String PREF_ENABLE_TIMED_SLEEP = "enable.timed.sleep";
    public static final String PREF_ENABLE_NOW_SLEEP = "enable.now.sleep";
    public static final String PREF_DELAY_BEFORE_SLEEP = "delay.before.sleep";
    public static final String PREF_DELAY_BEFORE_WAKE = "delay.before.wake";
    public static final String PREF_START_SLEEP_TIME_HOURS = "start.sleep.time.hours";
    public static final String PREF_START_SLEEP_TIME_MINS  = "start.sleep.time.mins";
    public static final String PREF_END_SLEEP_TIME_HOURS   = "end.sleep.time.hours";
    public static final String PREF_END_SLEEP_TIME_MINS    = "end.sleep.time.mins";
    
    // These are the preferences from the main 'Settings' screen
    public static final String PREF_DELAY_SECS = "pref.delay.secs";
    public static final String PREF_DISPLAY_ORDER = "pref.display.order";
    //public static final String PREF_DISPLAY_RANDOM = "pref.display.random";
    
    public static final int DEFAULT_WAKE_INTERVAL   = 20; // for 'now' timer; secs
    public static final int DEFAULT_SNOOZE_INTERVAL = 20; // for 'now' timer; secs
    
    public static RunningMode getCurrentRunningMode(Context context) {
        int runningMode = getIntPref(context, PREF_RUNNING_MODE, RunningMode.STOPPED.getStorageValue());
        RunningMode currentRunningMode = RunningMode.fromStorageValue(runningMode);
        return currentRunningMode;
    }

    public static int getIntPref(Context context, String prefName, int defaultValue) {
        SharedPreferences sharedPref = context.getSharedPreferences(MyPrefs.PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPref.getInt(prefName, defaultValue);
    }

    public static void setIntPref(Context context, String prefName, int value) {
        SharedPreferences sharedPref = context.getSharedPreferences(MyPrefs.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(prefName, value);
        editor.commit();
    }

    public static boolean getBooleanPref(Context context, String prefName, boolean defaultValue) {
        SharedPreferences sharedPref = context.getSharedPreferences(MyPrefs.PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(prefName, defaultValue);
    }

    public static void setBooleanPref(Context context, String prefName, boolean value) {
        SharedPreferences sharedPref = context.getSharedPreferences(MyPrefs.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(prefName, value);
        editor.commit();
    }

    public static String getStringPrefFromSettings(Context context, String prefName, String defaultValue) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(prefName, defaultValue);
    }

    public static boolean getBooleanPrefFromSettings(Context context, String prefName, boolean defaultValue) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(prefName, defaultValue);
    }

}
