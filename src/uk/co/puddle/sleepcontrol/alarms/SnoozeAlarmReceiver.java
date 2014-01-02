package uk.co.puddle.sleepcontrol.alarms;

import uk.co.puddle.sleepcontrol.RunningMode;
import uk.co.puddle.sleepcontrol.SleepAction;
import uk.co.puddle.sleepcontrol.SleepLogging;
import uk.co.puddle.sleepcontrol.SleepPrefs;
import android.content.Context;
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
        RunningMode runningMode = SleepPrefs.getCurrentRunningMode(context);
        switch(runningMode) {
        case STOPPED:
            break;
            
        case INTERVALS:
            int delayBeforeSleep = SleepPrefs.getIntPref(context, SleepPrefs.PREF_DELAY_BEFORE_SLEEP, SleepPrefs.DEFAULT_SNOOZE_INTERVAL);
            setAlarmTriggerInterval(delayBeforeSleep);
            break;
            
        case DAILY:
            int hours   = SleepPrefs.getIntPref(context, SleepPrefs.PREF_START_SLEEP_TIME_HOURS, 18);
            int minutes = SleepPrefs.getIntPref(context, SleepPrefs.PREF_START_SLEEP_TIME_MINS, 0);
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
