package uk.co.puddle.sleepcontrol;

import uk.co.puddle.sleepcontrol.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SleepNotifications {

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = R.string.local_service_started;

    private NotificationManager nm;
    
    public SleepNotifications() {
    }
    
    /**
     * Show a notification while this service is running.
     */
    public void showNotification(Context context) {
        if (nm != null) {
            return; //already done
        }
        Log.d(SleepLogging.TAG, "Showing notification...");
        nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = context.getText(R.string.local_service_started);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.face_sleep, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, SleepControlActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(context, context.getText(R.string.local_service_label),
                       text, contentIntent);

        // Send the notification.
        nm.notify(NOTIFICATION, notification);
        Log.i(SleepLogging.TAG, "Showing notification; done");
    }

    /**
     * Clear the notification.
     */
    public void clearNotification(Context context) {
        if (nm == null) {
            return; //already done
        }
        Log.d(SleepLogging.TAG, "Clearing notification...");
        nm.cancel(NOTIFICATION);
        nm = null;
        Log.i(SleepLogging.TAG, "Clearing notification; done");
    }
}
