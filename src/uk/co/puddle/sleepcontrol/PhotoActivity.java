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
        Log.d(SleepLogging.TAG, "PhotoActivity; onStart...");
        super.onStart();

        refreshPhotos();
        showPhoto(currentPhoto);

        if (tickerRunnable == null) {
            createTickerRunnable();
        }
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
        Log.d(SleepLogging.TAG, "PhotoActivity; onResume...");
        super.onResume();
        setupBroadcastReceiver();
    }

    @Override
    protected void onPause() {
        // No longer in the foreground
        Log.d(SleepLogging.TAG, "PhotoActivity; onPause...");
        cleardownBroadcastReceiver();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(SleepLogging.TAG, "PhotoActivity; onStop...");
        super.onStop();
        if (tickerRunnable != null) {
            Log.i(SleepLogging.TAG, "PhotoActivity; onStop; removing tickerRunnable");
            handler.removeCallbacks(tickerRunnable);
        }
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
    }
    
    private void setupBroadcastReceiver() {
        lbm = LocalBroadcastManager.getInstance(this);
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context paramContext, Intent paramIntent) {
                SleepAction action = SleepAction.fromActionName(paramIntent.getAction());
                Log.i(SleepLogging.TAG, "PhotoActivity; onReceive: " + action);
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
    
    private void nextPhoto() {
        currentPhoto++;
        if (currentPhoto >= images.size()) {
            currentPhoto = 0;
        }
        showPhoto(currentPhoto);
        startIntervalTimer();
    }
}
