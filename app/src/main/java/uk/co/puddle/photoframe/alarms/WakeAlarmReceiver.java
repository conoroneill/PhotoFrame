package uk.co.puddle.photoframe.alarms;

import uk.co.puddle.photoframe.MyAction;
import uk.co.puddle.photoframe.Logging;
import uk.co.puddle.photoframe.prefs.MyPrefs;
import android.content.Context;
import android.util.Log;

public class WakeAlarmReceiver extends BaseAlarmReceiver {
    
    public WakeAlarmReceiver() {
        super("WAKE");
    }

    @Override
    protected MyAction getActionToTrigger() {
        return MyAction.WAKE_UP_SCREEN;
    }

    @Override
    protected void setAlarmTriggerTime(Context context) {
        RunningMode runningMode = MyPrefs.getCurrentRunningMode(context);
        switch(runningMode) {
        case STOPPED:
            break;
            
        case INTERVALS:
            int delayBeforeWake = MyPrefs.getIntPref(context, MyPrefs.PREF_DELAY_BEFORE_WAKE, MyPrefs.DEFAULT_WAKE_INTERVAL);
            setAlarmTriggerInterval(delayBeforeWake);
            break;

        case DAILY:
            int hours   = MyPrefs.getIntPref(context, MyPrefs.PREF_END_SLEEP_TIME_HOURS, 18);
            int minutes = MyPrefs.getIntPref(context, MyPrefs.PREF_END_SLEEP_TIME_MINS, 0);
            setAlarmTriggerDaily(hours, minutes);
            break;
        }
    }

    @Override
    protected void doOnTrigger(Context context) {
        RunningMode runningMode = MyPrefs.getCurrentRunningMode(context);
        if (runningMode == RunningMode.INTERVALS) {
            Log.d(Logging.TAG, "WakeAlarmReceiver; doOnTrigger; now starting Snooze Alarm");
            Alarms.getSnoozeAlarm().startAlarm(context);
        }
    }
    
}
