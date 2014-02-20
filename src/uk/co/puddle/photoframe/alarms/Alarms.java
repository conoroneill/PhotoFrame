package uk.co.puddle.photoframe.alarms;

import java.util.Calendar;

import uk.co.puddle.photoframe.MyAction;
import uk.co.puddle.photoframe.Logging;
import uk.co.puddle.photoframe.prefs.MyPrefs;
import uk.co.puddle.photoframe.service.MyNotifications;
import uk.co.puddle.photoframe.service.SleepIntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Alarms {
    
    private static WakeAlarmReceiver   wakeAlarm   = new WakeAlarmReceiver();
    private static SnoozeAlarmReceiver snoozeAlarm = new SnoozeAlarmReceiver();
    private static MyNotifications  notifications = new MyNotifications();

    public static WakeAlarmReceiver getWakeAlarm() {
        return wakeAlarm;
    }

    public static SnoozeAlarmReceiver getSnoozeAlarm() {
        return snoozeAlarm;
    }

    public static void startAlarmsInCurrentMode(Context context) {
        RunningMode currentRunningMode = MyPrefs.getCurrentRunningMode(context);
        Log.i(Logging.TAG, "Alarms; startAlarmsInCurrentMode: currentRunningMode: " + currentRunningMode);
        startAlarms(context, currentRunningMode);
    }
    
    public static void startAlarms(Context context, RunningMode runningMode) {
        RunningMode currentRunningMode = MyPrefs.getCurrentRunningMode(context);
        if (currentRunningMode != RunningMode.STOPPED) {
            stopAlarms0(context);
        }
        Log.d(Logging.TAG, "Alarms; startAlarms; setting running mode to: " + runningMode + " ...");
        MyPrefs.setIntPref(context, MyPrefs.PREF_RUNNING_MODE, runningMode.getStorageValue());
//        Log.d(SleepLogging.TAG, "Alarms; startAlarms; setting running mode to: " + runningMode + " ...");
        switch(runningMode) {
        case STOPPED:
            break;
        case INTERVALS:
            //wakeAlarm.startAlarm(context);
            snoozeAlarm.startAlarm(context);
            break;
        case DAILY:
            wakeAlarm.startAlarm(context);
            snoozeAlarm.startAlarm(context);
            if (checkForInitialWake(context)) {
                startInWakeMode(context);
            }
            break;
        }
        notifications.showNotification(context);
        Log.i(Logging.TAG, "Alarms; started all; running mode now: " + runningMode);
    }

    public static void stopAlarms(Context context) {
        stopAlarms0(context);
        notifications.clearNotification(context);
    }
    
    private static void stopAlarms0(Context context) {
        RunningMode currentRunningMode = MyPrefs.getCurrentRunningMode(context);
        if (currentRunningMode != RunningMode.STOPPED) {
            wakeAlarm.cancelAlarm(context);
            snoozeAlarm.cancelAlarm(context);
            releaseLocks(context);
            MyPrefs.setIntPref(context, MyPrefs.PREF_RUNNING_MODE, RunningMode.STOPPED.getStorageValue());
            Log.i(Logging.TAG, "Alarms; cleared all; running mode now: " + RunningMode.STOPPED);
        }
    }
    
    private static boolean checkForInitialWake(Context context) {
        int snHours   = MyPrefs.getIntPref(context, MyPrefs.PREF_START_SLEEP_TIME_HOURS, 18);
        int snMinutes = MyPrefs.getIntPref(context, MyPrefs.PREF_START_SLEEP_TIME_MINS, 0);

        int wkHours   = MyPrefs.getIntPref(context, MyPrefs.PREF_END_SLEEP_TIME_HOURS, 18);
        int wkMinutes = MyPrefs.getIntPref(context, MyPrefs.PREF_END_SLEEP_TIME_MINS, 0);
        
        Calendar snCalendar = getCalendar(snHours, snMinutes);
        Calendar wkCalendar = getCalendar(wkHours, wkMinutes);

        long now    =   System.currentTimeMillis();
        long wake   = wkCalendar.getTimeInMillis();
        long snooze = snCalendar.getTimeInMillis();
        
        Log.d(Logging.TAG, "Alarms; checkForInitialWake; now: " + now + "; wake: " + wake + "; snooze: " + snooze);
        Log.d(Logging.TAG, "Alarms; checkForInitialWake; (now >= wake): " + (now >= wake)
                + "; (now < snooze): "  + (now < snooze)
                + "; (snooze < wake): " + (snooze < wake)
                + "; (now < snooze): "  + (now < snooze));
        if (now >= wake) {
            // If we have passed today's wake up time, but we need to be away because we are _before_ the snooze time,
            // or if the snooze time is 'early tomorrow morning', then we need to start in 'wake' mode...
            return (now < snooze) || (snooze < wake);
        } else {
            // Or if we wake up late in evening, but snooze only a bit before that,
            // and we are currently earlier than the snooze time.
            return (snooze < wake) && (now < snooze);
        }
    }
    
    private static Calendar getCalendar(int hours, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
    
    private static void startInWakeMode(Context context) {
        Log.i(Logging.TAG, "Alarms; startInWakeMode; sending action: " + MyAction.INIT_WAKE_UP);
        Intent intent = new Intent(MyAction.INIT_WAKE_UP.getActionName(),null,context,SleepIntentService.class); 
        context.startService(intent);
    }
    private static void releaseLocks(Context context) {
        Log.d(Logging.TAG, "Alarms; sending action: " + MyAction.RELEASE_LOCKS);
        Intent intent = new Intent(MyAction.RELEASE_LOCKS.getActionName(),null,context,SleepIntentService.class); 
        context.startService(intent);
    }
}
