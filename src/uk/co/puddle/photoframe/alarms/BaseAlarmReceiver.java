package uk.co.puddle.photoframe.alarms;

import java.util.Calendar;

import uk.co.puddle.photoframe.MyAction;
import uk.co.puddle.photoframe.Logging;
import uk.co.puddle.photoframe.service.SleepIntentService;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast Intent 
 * and then starts the IntentService {@code SampleSchedulingService} to do some work.
 */
public abstract class BaseAlarmReceiver extends WakefulBroadcastReceiver {
    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;
    
    private final String alarmName;
    
    private static int countExistingAlarms = 0;
    
    protected BaseAlarmReceiver(String alarmName) {
        this.alarmName = alarmName;
    }
  
    public String getAlarmName() {
        return alarmName;
    }

    @Override
    public void onReceive(Context context, Intent intent) {   
        Log.d(Logging.TAG, "BaseAlarmReceiver; onReceive; " + getAlarmName() + " ...");
        // BEGIN_INCLUDE(alarm_onreceive)
        /* 
         * If your receiver intent includes extras that need to be passed along to the
         * service, use setComponent() to indicate that the service should handle the
         * receiver's intent. For example:
         * 
         * ComponentName comp = new ComponentName(context.getPackageName(), 
         *      MyService.class.getName());
         *
         * // This intent passed in this call will include the wake lock extra as well as 
         * // the receiver intent contents.
         * startWakefulService(context, (intent.setComponent(comp)));
         * 
         * In this example, we simply create a new intent to deliver to the service.
         * This intent holds an extra identifying the wake lock.
         */
        //Intent service = new Intent(context, SleepIntentService.class);
        
        // Start the service, keeping the device awake while it is launching.
        //startWakefulService(context, service);
        
        // This, effectively, passes the intent through, but now assigned to a specific class
        ComponentName comp = new ComponentName(context.getPackageName(), SleepIntentService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        
        doOnTrigger(context);
        
        // END_INCLUDE(alarm_onreceive)
    }
    
    protected void doOnTrigger(Context context) {
        // do nothing by default.
    }

    // BEGIN_INCLUDE(set_alarm)
    /**
     * Sets a repeating alarm that runs once a day at approximately 8:30 a.m. When the
     * alarm fires, the app broadcasts an Intent to this WakefulBroadcastReceiver.
     * @param context
     */
    public void startAlarm(Context context) {
        
        Log.d(Logging.TAG, "startAlarm: " + getAlarmName() + " ...");
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, this.getClass());
        intent.setAction(getActionToTrigger().getActionName());
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        setAlarmTriggerTime(context);
        countExistingAlarms++;
        
        if (countExistingAlarms == 1) {
            turnOnAlarmsAtBoot(context);
        }
    }
    // END_INCLUDE(set_alarm)

    protected PendingIntent getAlarmIntent() {
        return alarmIntent;
    }
    protected AlarmManager getAlarmMgr() {
        return alarmMgr;
    }
    
    /**
     * Get the action to be passed through to the service when the alarm triggers.
     * @return name of the action.
     */
    protected abstract MyAction getActionToTrigger();

    /**
     * Set the alarm wake up.
     * @param context Context
     */
    protected abstract void setAlarmTriggerTime(Context context);
    
    protected void setAlarmTriggerInterval(int delay) {
        Log.i(Logging.TAG, "BaseAlarmReceiver; " + getAlarmName() + "; setAlarm; delay: " + delay + " secs ...");
        getAlarmMgr().set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 
                SystemClock.elapsedRealtime() +
                delay*1000, getAlarmIntent());
    }

    protected void setAlarmTriggerDaily(int hours, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        long now = System.currentTimeMillis();
        long then = calendar.getTimeInMillis();
        if (now > then) {
            // the trigger time is earlier in the day than 'now', so we must mean tomorrow
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
            Log.i(Logging.TAG, "BaseAlarmReceiver; " + getAlarmName() + "; setAlarm; hours:mins: " + hours + ":" + minutes + " TOMORROW!");
        }

        // Set the alarm to fire at the specified time, according to the device's
        // clock, and to repeat once a day.
        Log.i(Logging.TAG, "BaseAlarmReceiver; " + getAlarmName() + "; setAlarm; hours:mins: " + hours + ":" + minutes);
        getAlarmMgr().setRepeating(AlarmManager.RTC_WAKEUP,  
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, getAlarmIntent());
    }

    /**
     * Cancels the alarm.
     * @param context
     */
    // BEGIN_INCLUDE(cancel_alarm)
    public void cancelAlarm(Context context) {
        // If the alarm has been set, cancel it.
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
            Log.i(Logging.TAG, "BaseAlarmReceiver; cancelAlarm: " + getAlarmName() + " ...");
            countExistingAlarms--;
        }
        
//        if (pairedAlarm != null) {
//            pairedAlarm.cancelAlarm(context);
//        }
//        
        if (countExistingAlarms == 0) {
            turnOffAlarmsAtBoot(context);
        }
    }
    // END_INCLUDE(cancel_alarm)

    private void turnOnAlarmsAtBoot(Context context) {
        BootReceiver.turnOnAlarmsAtBoot(context);
    }
    

    private void turnOffAlarmsAtBoot(Context context) {
        BootReceiver.turnOffAlarmsAtBoot(context);
    }
}
