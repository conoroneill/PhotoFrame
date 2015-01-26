package uk.co.puddle.photoframe.alarms;

import uk.co.puddle.photoframe.MyAction;
import uk.co.puddle.photoframe.Logging;
import uk.co.puddle.photoframe.prefs.MyPrefs;
import android.content.Context;
import android.util.Log;

public class SnoozeAlarmReceiver extends BaseAlarmReceiver {

    public SnoozeAlarmReceiver() {
        super("SNOOZE");
    }

    @Override
    protected MyAction getActionToTrigger() {
        return MyAction.SNOOZE_SCREEN;
    }

    @Override
    protected void setAlarmTriggerTime(Context context) {
        RunningMode runningMode = MyPrefs.getCurrentRunningMode(context);
        switch(runningMode) {
        case STOPPED:
            break;
            
        case INTERVALS:
            int delayBeforeSleep = MyPrefs.getIntPref(context, MyPrefs.PREF_DELAY_BEFORE_SLEEP, MyPrefs.DEFAULT_SNOOZE_INTERVAL);
            setAlarmTriggerInterval(delayBeforeSleep);
            break;
            
        case DAILY:
            int hours   = MyPrefs.getIntPref(context, MyPrefs.PREF_START_SLEEP_TIME_HOURS, 18);
            int minutes = MyPrefs.getIntPref(context, MyPrefs.PREF_START_SLEEP_TIME_MINS, 0);
            setAlarmTriggerDaily(hours, minutes);
            break;
        }
    }

    @Override
    protected void doOnTrigger(Context context) {
        RunningMode runningMode = MyPrefs.getCurrentRunningMode(context);
        if (runningMode == RunningMode.INTERVALS) {
            Log.d(Logging.TAG, "SnoozeAlarmReceiver; doOnTrigger; now starting Wake Alarm");
            Alarms.getWakeAlarm().startAlarm(context);
        }
    }
}
