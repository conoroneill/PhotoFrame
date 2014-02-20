package uk.co.puddle.photoframe.service;

import uk.co.puddle.photoframe.MyAction;
import uk.co.puddle.photoframe.Logging;
import uk.co.puddle.photoframe.alarms.RunningMode;
import uk.co.puddle.photoframe.prefs.MyPrefs;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
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
        MyAction sleepAction = MyAction.fromActionName(action);
        //Log.i(SleepLogging.TAG, "Inside sleep thread; action: " + action);
        switch(sleepAction) {
        case WAKE_UP_SCREEN:
        case INIT_WAKE_UP:
            doWakeUpScreen(intent, sleepAction);
            break;
        case SNOOZE_SCREEN:
            doSnoozeScreen(intent, sleepAction);
            break;
        case RELEASE_LOCKS:
            releaseLocks(sleepAction);
            break;
        case UNKNOWN:
        case RELEASE_COS_RUNMODE_STOPPED:
        case RELEASE_COS_ABOUT_TO_ACQUIRE:
            Log.w(Logging.TAG, "Received unexpected action: " + sleepAction + "(" + action + ")");
            break;
        }
        //Log.i(SleepLogging.TAG, "Exiting sleep thread");
    }
    
    protected void doWakeUpScreen(Intent intent, MyAction triggeredByAction) {
        Log.d(Logging.TAG, "doWakeUpScreen (" + triggeredByAction + ")");
        RunningMode runningMode = MyPrefs.getCurrentRunningMode(this);
        if (runningMode == RunningMode.STOPPED) {
            // Belt and braces; if we are in the middle of something when we shut this down, then release all
            releaseLocks(MyAction.RELEASE_COS_RUNMODE_STOPPED);
            return;
        }
        //Toast.makeText(this, "SleepIntentService WAKE", Toast.LENGTH_SHORT).show();
        //Log.i(SleepLogging.TAG, "Exiting doAlarmOn");
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (wl != null) {
            // belt and braces - should not be needed - make sure we release any old locks!
            releaseLocks(MyAction.RELEASE_COS_ABOUT_TO_ACQUIRE);
        }
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(
                  PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                  ,
                Logging.TAG);
        wl.acquire();
        Log.d(Logging.TAG, "doWakeUpScreen; acquired lock: " + wl.toString() + ", due to: " + triggeredByAction);
        Log.i(Logging.TAG, "doWakeUpScreen; acquired lock (" + triggeredByAction + ")");
        
        Log.i(Logging.TAG, "doWakeUpScreen; sending broadcast intent: " + MyAction.WAKE_UP_SCREEN);
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        Intent wakeIntent = new Intent(MyAction.WAKE_UP_SCREEN.getActionName());
        lbm.sendBroadcast(wakeIntent);
        
        //pm.wakeUp(SystemClock.uptimeMillis());
    }

    protected void doSnoozeScreen(Intent intent, MyAction triggeredByAction) {
        Log.d(Logging.TAG, "doSnoozeScreen (" + triggeredByAction + ")");
        RunningMode runningMode = MyPrefs.getCurrentRunningMode(this);
        if (runningMode == RunningMode.STOPPED) {
            // Belt and braces; if we are in the middle of something when we shut this down, then release all
            releaseLocks(MyAction.RELEASE_COS_RUNMODE_STOPPED);
            return;
        }
        if (wl != null) {
            Log.d(Logging.TAG, "doSnoozeScreen; releasing lock: " + wl.toString() + ", due to: " + triggeredByAction + " ...");
            wl.release();
            Log.i(Logging.TAG, "doSnoozeScreen; released lock (" + triggeredByAction + ")");
            wl = null;
        }
        
        //PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //pm.goToSleep(SystemClock.uptimeMillis());
        
        Log.i(Logging.TAG, "doSnoozeScreen; sending broadcast intent: " + MyAction.SNOOZE_SCREEN);
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        Intent wakeIntent = new Intent(MyAction.SNOOZE_SCREEN.getActionName());
        lbm.sendBroadcast(wakeIntent);

        //Toast.makeText(this, "SleepIntentService SNOOZE", Toast.LENGTH_SHORT).show();
        //Log.i(SleepLogging.TAG, "Exiting doAlarmOff");
    }
    
    private void releaseLocks(MyAction triggeredByAction) {
        if (wl != null) {
            Log.i(Logging.TAG, "releaseLocks; releasing: " + wl.toString() + ", due to: " + triggeredByAction + " ...");
            wl.release();
            wl = null;
        } else {
            Log.i(Logging.TAG, "releaseLocks; not currently held");
        }
    }

}
