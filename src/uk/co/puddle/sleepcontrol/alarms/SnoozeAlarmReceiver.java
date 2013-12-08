package uk.co.puddle.sleepcontrol.alarms;

import uk.co.puddle.sleepcontrol.R;
import uk.co.puddle.sleepcontrol.RunningMode;
import uk.co.puddle.sleepcontrol.SleepAction;
import uk.co.puddle.sleepcontrol.SleepLogging;
import uk.co.puddle.sleepcontrol.SleepPrefs;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SnoozeAlarmReceiver extends BaseAlarmReceiver {

    public SnoozeAlarmReceiver() {
        super("SNOOZE");
    }

    @Override
    protected SleepAction getActionToTrigger() {
        return SleepAction.SNOOZE_SCREEN;
    }

    @Override
    protected void setAlarmTriggerTime(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(SleepPrefs.PREFS_NAME, Context.MODE_PRIVATE);
        RunningMode runningMode = SleepPrefs.getCurrentRunningMode(context);
        switch(runningMode) {
        case STOPPED:
            break;
            
        case INTERVALS:
            int delayBeforeSleep = sharedPref.getInt(context.getString(R.string.prefs_delay_before_sleep_secs), SleepPrefs.DEFAULT_SNOOZE_INTERVAL);
            setAlarmTriggerInterval(delayBeforeSleep);
            break;
            
        case DAILY:
            int hours   = sharedPref.getInt(context.getString(R.string.prefs_start_sleep_time_hours), 18);
            int minutes = sharedPref.getInt(context.getString(R.string.prefs_start_sleep_time_minutes), 0);
            setAlarmTriggerDaily(hours, minutes);
            break;
        }
    }

    @Override
    protected void doOnTrigger(Context context) {
        RunningMode runningMode = SleepPrefs.getCurrentRunningMode(context);
        if (runningMode == RunningMode.INTERVALS) {
            Log.d(SleepLogging.TAG, "SnoozeAlarmReceiver; doOnTrigger; now starting Wake Alarm");
            Alarms.getWakeAlarm().startAlarm(context);
        }
    }
}
