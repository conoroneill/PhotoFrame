package uk.co.puddle.sleepcontrol;

import java.util.List;

import uk.co.puddle.sleepcontrol.photos.PhotoEntry;
import uk.co.puddle.sleepcontrol.photos.PhotoReader;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoActivity extends Activity {
    
    private ImageView imgView;
    
    private List<PhotoEntry> images;
    private int currentPhoto = 0;
    
    private Handler handler;
    private Runnable tickerRunnable = null;
    
    private long tickerTimeout = 5 * 1000; // 5 secs
    
    private LocalBroadcastManager lbm;
    private BroadcastReceiver br;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);
//        View rootView = findViewById(R.layout.activity_view_photo);
//        rootView.setBackgroundColor(Color.BLACK);
        
        handler = new Handler();
        
        Bundle receiveBundle = this.getIntent().getExtras();
        int count = receiveBundle.getInt("count");
        Log.d(SleepLogging.TAG, "PhotoActivity; starting show photos; count: " + count);
        String photoData = receiveBundle.getString("photo_data");
        Log.d(SleepLogging.TAG, "PhotoActivity; photoData: " + photoData);
        //String thumb = receiveBundle.getString("photo_thumb");
        //Log.i(SleepLogging.TAG, "PhotoActivity; thumb: " + thumb);
    }
    
    
    @Override
    protected void onStart() {
        Log.i(SleepLogging.TAG, "PhotoActivity; onStart...");
        super.onStart();

        refreshPhotos();
        showPhoto(currentPhoto);

        startIntervalTimer();
    }

    @Override
    protected void onRestart() {
        Log.d(SleepLogging.TAG, "PhotoActivity; onRestart...");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        // Now in the foreground
        Log.i(SleepLogging.TAG, "PhotoActivity; onResume...");
        super.onResume();
        setupBroadcastReceiver();
    }

    @Override
    protected void onPause() {
        // No longer in the foreground
        Log.i(SleepLogging.TAG, "PhotoActivity; onPause...");
        cleardownBroadcastReceiver();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(SleepLogging.TAG, "PhotoActivity; onStop...");
        super.onStop();
        stopIntervalTimer();
    }

    @Override
    protected void onDestroy() {
        Log.d(SleepLogging.TAG, "PhotoActivity; onDestroy...");
        super.onDestroy();
    }

    private void refreshPhotos() {
        images = new PhotoReader().list(this);
    }
    
    private void showPhoto(int num) {
        PhotoEntry photoEntry = images.get(num);
        Log.i(SleepLogging.TAG, "PhotoActivity; num: " + num + "; photoEntry: " + photoEntry);
        
        imgView = (ImageView)findViewById(R.id.imageView);
        Bitmap bitmap = BitmapFactory.decodeFile(photoEntry.getData());
        imgView.setImageBitmap(bitmap);
        //imgView.setBackgroundColor(Color.BLACK);
        TextView myImageViewText = (TextView)findViewById(R.id.myImageViewText);
        myImageViewText.setText(getPhotoText(photoEntry, num, images.size()));
        //myImageViewText.setTextColor(Color.RED);
    }
    
    private String getPhotoText(PhotoEntry image, int num, int count) {
        StringBuilder sb = new StringBuilder();
        sb.append(num+1).append('/').append(count);
        sb.append(", ");
        sb.append(image.getFormattedDate());
        if (image.getName() != null && !image.getName().isEmpty()) {
            sb.append(", ");
            sb.append(image.getName());
        }
        return sb.toString();
    }
    
    private void setupBroadcastReceiver() {
        lbm = LocalBroadcastManager.getInstance(this);
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context paramContext, Intent paramIntent) {
                SleepAction action = SleepAction.fromActionName(paramIntent.getAction());
                Log.i(SleepLogging.TAG, "PhotoActivity; onReceive: " + action);
                switch (action) {
                case WAKE_UP_SCREEN:
                    if (tickerRunnable == null) { // we must be currently paused
                        startIntervalTimer();
                    }
                    break;
                case SNOOZE_SCREEN:
                    stopIntervalTimer();
                    break;
                default:
                    // unexpected
                    break;
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SleepAction.WAKE_UP_SCREEN.getActionName());
        intentFilter.addAction(SleepAction.SNOOZE_SCREEN.getActionName());
        lbm.registerReceiver(br, intentFilter);
    }
    
    private void cleardownBroadcastReceiver() {
        lbm.unregisterReceiver(br);
        br = null;
        lbm = null;
    }
    
    private void startIntervalTimer() {
        if (tickerRunnable == null) {
            createTickerRunnable();
        }
        handler.postDelayed(tickerRunnable, tickerTimeout);
    }

    private void createTickerRunnable() {
        tickerRunnable = new Runnable() {
            @Override
            public void run() {
                nextPhoto();
            }
        };
        Log.d(SleepLogging.TAG, "PhotoActivity; createTickerRunnable; created: " + tickerRunnable);
    }
    
    private void stopIntervalTimer() {
        if (tickerRunnable != null) {
            Log.i(SleepLogging.TAG, "PhotoActivity; stopIntervalTimer; removing tickerRunnable");
            handler.removeCallbacks(tickerRunnable);
        }
        tickerRunnable = null;
    }

    private void nextPhoto() {
        currentPhoto++;
        if (currentPhoto >= images.size()) {
            currentPhoto = 0;
        }
        showPhoto(currentPhoto);
        startIntervalTimer();
    }
}
