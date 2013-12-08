package uk.co.puddle.sleepcontrol;

import android.content.Context;
import android.content.SharedPreferences;

public class SleepPrefs {
    
    public static final String PREFS_NAME = "SleepControl";
    
    public static final int DEFAULT_WAKE_INTERVAL = 20;
    public static final int DEFAULT_SNOOZE_INTERVAL = 20;
    
    public static RunningMode getCurrentRunningMode(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(SleepPrefs.PREFS_NAME, Context.MODE_PRIVATE);
        RunningMode currentRunningMode = RunningMode.fromStorageValue(sharedPref.getInt(context.getString(R.string.prefs_current_running_mode), RunningMode.STOPPED.getStorageValue()));
        return currentRunningMode;
    }
}
