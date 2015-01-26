package uk.co.puddle.photoframe.alarms;

import uk.co.puddle.photoframe.Logging;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * This BroadcastReceiver automatically (re)starts the alarm when the device is
 * rebooted. This receiver is set to be disabled (android:enabled="false") in the
 * application's manifest file. When the user sets the alarm, the receiver is enabled.
 * When the user cancels the alarm, the receiver is disabled, so that rebooting the
 * device will not trigger this receiver.
 */
// BEGIN_INCLUDE(autostart)
public class BootReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Alarms.startAlarmsInCurrentMode(context);
        }
    }

    public static void turnOnAlarmsAtBoot(Context context) {

        Log.i(Logging.TAG, "BootReceiver; turnOnAlarmsAtBoot");
        // Enable {@code SampleBootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
    

    public static void turnOffAlarmsAtBoot(Context context) {
        Log.i(Logging.TAG, "BootReceiver; turnOffAlarmsAtBoot");

        // Disable {@code SampleBootReceiver} so that it doesn't automatically restart the 
        // alarm when the device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
//END_INCLUDE(autostart)