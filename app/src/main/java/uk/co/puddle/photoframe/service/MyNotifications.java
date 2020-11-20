package uk.co.puddle.photoframe.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import uk.co.puddle.photoframe.Logging;
import uk.co.puddle.photoframe.PhotoControlActivity;
import uk.co.puddle.photoframe.R;

public class MyNotifications {

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private final int NOTIFICATION = R.string.local_service_started;

    private NotificationManager nm;
    
    public MyNotifications() {
    }
    
    /**
     * Show a notification while this service is running.
     */
    public void showNotification(Context context) {
        if (nm != null) {
            return; //already done
        }
        Log.d(Logging.TAG, "Showing notification...");
        nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = context.getText(R.string.local_service_started);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, PhotoControlActivity.class), 0);

        // Set the icon, scrolling text and timestamp
        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(context)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.pictures_icon_128)
                .setTicker(text)
                .setContentTitle(context.getText(R.string.local_service_label))
                .setContentText(text)
                .setContentIntent(contentIntent)
                .build();

        // Send the notification.
        nm.notify(NOTIFICATION, notification);
        Log.i(Logging.TAG, "Showing notification; done");
    }

    /**
     * Clear the notification.
     */
    public void clearNotification(Context context) {
        if (nm == null) {
            return; //already done
        }
        Log.d(Logging.TAG, "Clearing notification...");
        nm.cancel(NOTIFICATION);
        nm = null;
        Log.i(Logging.TAG, "Clearing notification; done");
    }
}
