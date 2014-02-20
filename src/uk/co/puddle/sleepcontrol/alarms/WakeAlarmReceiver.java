package uk.co.puddle.sleepcontrol.alarms;

import uk.co.puddle.sleepcontrol.SleepAction;
import uk.co.puddle.sleepcontrol.SleepLogging;
import uk.co.puddle.sleepcontrol.SleepPrefs;
import android.content.Context;
import android.util.Log;

public class WakeAlarmReceiver extends BaseAlarmReceiver {
    
    public WakeAlarmReceiver() {
        super("WAKE");
    }

    @Override
    protected SleepAction getActionToTrigger() {
        return SleepAction.WAKE_UP_SCREEN;
    }

    @Override
    protected void setAlarmTriggerTime(Context context) {
        RunningMode runningMode = SleepPrefs.getCurrentRunningMode(context);
        switch(runningMode) {
        case STOPPED:
            break;
            
        case INTERVALS:
            int delayBeforeWake = SleepPrefs.getIntPref(context, SleepPrefs.PREF_DELAY_BEFORE_WAKE, SleepPrefs.DEFAULT_WAKE_INTERVAL);
            setAlarmTriggerInterval(delayBeforeWake);
            break;

        case DAILY:
            int hours   = SleepPrefs.getIntPref(context, SleepPrefs.PREF_END_SLEEP_TIME_HOURS, 18);
            int minutes = SleepPrefs.getIntPref(context, SleepPrefs.PREF_END_SLEEP_TIME_MINS, 0);
            setAlarmTriggerDaily(hours, minutes);
            break;
        }
    }

    @Override
    protected void doOnTrigger(Context context) {
        RunningMode runningMode = SleepPrefs.getCurrentRunningMode(context);
        if (runningMode == RunningMode.INTERVALS) {
            Log.d(SleepLogging.TAG, "WakeAlarmReceiver; doOnTrigger; now starting Snooze Alarm");
            Alarms.getSnoozeAlarm().startAlarm(context);
        }
    }
    
}
