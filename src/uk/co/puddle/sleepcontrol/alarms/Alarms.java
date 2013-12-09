package uk.co.puddle.sleepcontrol.alarms;

import java.util.Calendar;

import uk.co.puddle.sleepcontrol.R;
import uk.co.puddle.sleepcontrol.RunningMode;
import uk.co.puddle.sleepcontrol.SleepAction;
import uk.co.puddle.sleepcontrol.SleepLogging;
import uk.co.puddle.sleepcontrol.SleepNotifications;
import uk.co.puddle.sleepcontrol.SleepPrefs;
import uk.co.puddle.sleepcontrol.service.SleepIntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class Alarms {
    
    private static WakeAlarmReceiver   wakeAlarm   = new WakeAlarmReceiver();
    private static SnoozeAlarmReceiver snoozeAlarm = new SnoozeAlarmReceiver();
    private static SleepNotifications  notifications = new SleepNotifications();

    public static WakeAlarmReceiver getWakeAlarm() {
        return wakeAlarm;
    }

    public static SnoozeAlarmReceiver getSnoozeAlarm() {
        return snoozeAlarm;
    }

    public static void startAlarmsInCurrentMode(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(SleepPrefs.PREFS_NAME, Context.MODE_PRIVATE);
        RunningMode currentRunningMode = RunningMode.fromStorageValue(sharedPref.getInt(context.getString(R.string.prefs_current_running_mode), RunningMode.STOPPED.getStorageValue()));
        Log.i(SleepLogging.TAG, "Alarms; startAlarmsInCurrentMode: currentRunningMode: " + currentRunningMode);
        startAlarms(context, currentRunningMode);
    }
    
    public static void startAlarms(Context context, RunningMode runningMode) {
        SharedPreferences sharedPref = context.getSharedPreferences(SleepPrefs.PREFS_NAME, Context.MODE_PRIVATE);
        RunningMode currentRunningMode = RunningMode.fromStorageValue(sharedPref.getInt(context.getString(R.string.prefs_current_running_mode), RunningMode.STOPPED.getStorageValue()));
        if (currentRunningMode != RunningMode.STOPPED) {
            stopAlarms0(context);
        }
        Log.d(SleepLogging.TAG, "Alarms; startAlarms; setting running mode to: " + runningMode + " ...");
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(context.getString(R.string.prefs_current_running_mode), runningMode.getStorageValue());
        editor.commit();
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
        Log.i(SleepLogging.TAG, "Alarms; started all; running mode now: " + runningMode);
    }

    public static void stopAlarms(Context context) {
        stopAlarms0(context);
        notifications.clearNotification(context);
    }
    
    private static void stopAlarms0(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(SleepPrefs.PREFS_NAME, Context.MODE_PRIVATE);
        RunningMode currentRunningMode = RunningMode.fromStorageValue(sharedPref.getInt(context.getString(R.string.prefs_current_running_mode), RunningMode.STOPPED.getStorageValue()));
        if (currentRunningMode != RunningMode.STOPPED) {
            wakeAlarm.cancelAlarm(context);
            snoozeAlarm.cancelAlarm(context);
            releaseLocks(context);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(context.getString(R.string.prefs_current_running_mode), RunningMode.STOPPED.getStorageValue());
            editor.commit();
            Log.i(SleepLogging.TAG, "Alarms; cleared all; running mode now: " + RunningMode.STOPPED);
        }
    }
    
    private static boolean checkForInitialWake(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(SleepPrefs.PREFS_NAME, Context.MODE_PRIVATE);
        int snHours   = sharedPref.getInt(context.getString(R.string.prefs_start_sleep_time_hours), 18);
        int snMinutes = sharedPref.getInt(context.getString(R.string.prefs_start_sleep_time_minutes), 0);

        int wkHours   = sharedPref.getInt(context.getString(R.string.prefs_end_sleep_time_hours), 18);
        int wkMinutes = sharedPref.getInt(context.getString(R.string.prefs_end_sleep_time_minutes), 0);
        
        Calendar snCalendar = getCalendar(snHours, snMinutes);
        Calendar wkCalendar = getCalendar(wkHours, wkMinutes);

        long now    =   System.currentTimeMillis();
        long wake   = wkCalendar.getTimeInMillis();
        long snooze = snCalendar.getTimeInMillis();
        
        Log.d(SleepLogging.TAG, "Alarms; checkForInitialWake; now: " + now + "; wake: " + wake + "; snooze: " + snooze);
        Log.d(SleepLogging.TAG, "Alarms; checkForInitialWake; (now >= wake): " + (now >= wake)
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
        Log.i(SleepLogging.TAG, "Alarms; startInWakeMode; sending action: " + SleepAction.INIT_WAKE_UP);
        Intent intent = new Intent(SleepAction.INIT_WAKE_UP.getActionName(),null,context,SleepIntentService.class); 
        context.startService(intent);
    }
    private static void releaseLocks(Context context) {
        Log.d(SleepLogging.TAG, "Alarms; sending action: " + SleepAction.RELEASE_LOCKS);
        Intent intent = new Intent(SleepAction.RELEASE_LOCKS.getActionName(),null,context,SleepIntentService.class); 
        context.startService(intent);
    }
}
