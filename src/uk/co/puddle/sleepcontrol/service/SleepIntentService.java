package uk.co.puddle.sleepcontrol.service;

import uk.co.puddle.sleepcontrol.RunningMode;
import uk.co.puddle.sleepcontrol.SleepAction;
import uk.co.puddle.sleepcontrol.SleepLogging;
import uk.co.puddle.sleepcontrol.SleepPrefs;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

public class SleepIntentService extends IntentService {
    
    private static PowerManager.WakeLock wl = null;

    /** 
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public SleepIntentService() {
        super("SleepIntentService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.i(SleepLogging.TAG, "SleepIntentService; onStartCommand");
        //Toast.makeText(this, "SleepIntentService starting", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent,flags,startId);
    }
    
    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        SleepAction sleepAction = SleepAction.fromActionName(action);
        //Log.i(SleepLogging.TAG, "Inside sleep thread; action: " + action);
        switch(sleepAction) {
        case WAKE_UP_SCREEN:
            doWakeUpScreen(intent);
            break;
        case SNOOZE_SCREEN:
            doSnoozeScreen(intent);
            break;
        case RELEASE_LOCKS:
            releaseLocks();
            break;
        case UNKNOWN:
            break;
        }
        //Log.i(SleepLogging.TAG, "Exiting sleep thread");
    }
    
    protected void doWakeUpScreen(Intent intent) {
        Log.d(SleepLogging.TAG, "doWakeUpScreen");
        RunningMode runningMode = SleepPrefs.getCurrentRunningMode(this);
        if (runningMode == RunningMode.STOPPED) {
            // Belt and braces; if we are in the middle of something when we shut this down, then release all
            releaseLocks();
            return;
        }
        //Toast.makeText(this, "SleepIntentService WAKE", Toast.LENGTH_SHORT).show();
        //Log.i(SleepLogging.TAG, "Exiting doAlarmOn");
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (wl != null) {
            // belt and braces - should not be needed - make sure we release any old locks!
            releaseLocks();
        }
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(
                  PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                  ,
                SleepLogging.TAG);
        wl.acquire();
        Log.d(SleepLogging.TAG, "doWakeUpScreen; acquired lock: " + wl.toString());
        Log.i(SleepLogging.TAG, "doWakeUpScreen; acquired lock");
        
        //pm.wakeUp(SystemClock.uptimeMillis());
    }

    protected void doSnoozeScreen(Intent intent) {
        Log.d(SleepLogging.TAG, "doSnoozeScreen");
        RunningMode runningMode = SleepPrefs.getCurrentRunningMode(this);
        if (runningMode == RunningMode.STOPPED) {
            // Belt and braces; if we are in the middle of something when we shut this down, then release all
            releaseLocks();
            return;
        }
        if (wl != null) {
            Log.d(SleepLogging.TAG, "doSnoozeScreen; releasing lock: " + wl.toString() + " ...");
            wl.release();
            Log.i(SleepLogging.TAG, "doSnoozeScreen; released lock");
            wl = null;
        }
        
        //PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //pm.goToSleep(SystemClock.uptimeMillis());
        
        //Toast.makeText(this, "SleepIntentService SNOOZE", Toast.LENGTH_SHORT).show();
        //Log.i(SleepLogging.TAG, "Exiting doAlarmOff");
    }
    
    private void releaseLocks() {
        if (wl != null) {
            Log.i(SleepLogging.TAG, "releaseLocks; releasing: " + wl.toString() + " ...");
            wl.release();
            wl = null;
        } else {
            Log.i(SleepLogging.TAG, "releaseLocks; not currently held");
        }
    }

}
