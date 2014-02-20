package uk.co.puddle.photoframe;

import java.util.List;

import uk.co.puddle.photoframe.photos.PhotoEntry;
import uk.co.puddle.photoframe.photos.PhotoOrder;
import uk.co.puddle.photoframe.photos.PhotoReader;
import uk.co.puddle.photoframe.prefs.MyPrefs;
import uk.co.puddle.photoframe.R;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoActivity extends Activity {
    
    private List<PhotoEntry> images;
    private PhotoOrder photoOrder = PhotoOrder.RANDOM;
    private int currentPhoto = -1; // means that PhotoOrder.SEQUENTIAL will move to zero first time through
    
    private Handler handler;
    private Runnable tickerRunnable = null;
    
    private long tickerTimeout = 5 * 1000; // default here is 5 secs, but overridden by a setting
    
    private LocalBroadcastManager lbm;
    private BroadcastReceiver br;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make this use the whole screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_view_photo);
        
        handler = new Handler();
        
        Bundle receiveBundle = this.getIntent().getExtras();
        int count = receiveBundle.getInt("count");
        Log.d(Logging.TAG, "PhotoActivity; starting show photos; count: " + count);
        String photoData = receiveBundle.getString("photo_data");
        Log.d(Logging.TAG, "PhotoActivity; photoData: " + photoData);
        //String thumb = receiveBundle.getString("photo_thumb");
        //Log.i(SleepLogging.TAG, "PhotoActivity; thumb: " + thumb);
        
        String delaySecs = MyPrefs.getStringPrefFromSettings(this, MyPrefs.PREF_DELAY_SECS, "10");
        Log.d(Logging.TAG, "PhotoActivity; delaySecs: " + delaySecs);
        tickerTimeout = Integer.parseInt(delaySecs) * 1000;
        
//        boolean showRandom = SleepPrefs.getBooleanPrefFromSettings(this, SleepPrefs.PREF_DISPLAY_RANDOM, true);
//        photoOrder = showRandom ? PhotoOrder.RANDOM : PhotoOrder.SEQUENTIAL;
        String orderValue = MyPrefs.getStringPrefFromSettings(this, MyPrefs.PREF_DISPLAY_ORDER, "");
        photoOrder = PhotoOrder.fromValue(orderValue);
    }
    
    
    @Override
    protected void onStart() {
        Log.i(Logging.TAG, "PhotoActivity; onStart...");
        super.onStart();

        refreshPhotos();
        nextPhotoNumber();
        showPhoto(currentPhoto);

        startIntervalTimer();
    }

    @Override
    protected void onRestart() {
        Log.d(Logging.TAG, "PhotoActivity; onRestart...");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        // Now in the foreground
        Log.i(Logging.TAG, "PhotoActivity; onResume...");
        super.onResume();
        setupBroadcastReceiver();
    }

    @Override
    protected void onPause() {
        // No longer in the foreground
        Log.i(Logging.TAG, "PhotoActivity; onPause...");
        cleardownBroadcastReceiver();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(Logging.TAG, "PhotoActivity; onStop...");
        super.onStop();
        stopIntervalTimer();
    }

    @Override
    protected void onDestroy() {
        Log.d(Logging.TAG, "PhotoActivity; onDestroy...");
        super.onDestroy();
    }

    private void refreshPhotos() {
        images = new PhotoReader().list(this);
        currentPhoto = -1; // means that PhotoOrder.SEQUENTIAL will move to zero first time through
    }
    
    private void showPhoto(int num) {
        PhotoEntry photoEntry = images.get(num);
        Log.i(Logging.TAG, "PhotoActivity; num: " + num + "; photoEntry: " + photoEntry);
        
        ImageView imgView = (ImageView)findViewById(R.id.imageView);
        
//        Bitmap bitmap = BitmapFactory.decodeFile(photoEntry.getData());
        Bitmap bitmap = getBitmapForView(imgView, photoEntry);
        imgView.setImageBitmap(bitmap);

        TextView myImageViewText = (TextView)findViewById(R.id.myImageViewText);
        String text = getPhotoText(photoEntry, num, images.size());
//        text = text + " " + width + " x " + height;
        myImageViewText.setText(text);
    }
    
    private Bitmap getBitmapForView(ImageView imgView, PhotoEntry photoEntry) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int width  = metrics.widthPixels;

        // Info about how to load large bitmaps here:
        // http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
        
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoEntry.getData(), options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, width, height);
        Log.d(Logging.TAG, "getBitmapForView; width: " + width + "; height: " + height + "; sampleSize: " + options.inSampleSize);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(photoEntry.getData(), options);
    }
    
    // Info about how to load large bitmaps here:
    // http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private String getPhotoText(PhotoEntry image, int num, int count) {
        StringBuilder sb = new StringBuilder();
        sb.append(num+1).append('/').append(count);
        sb.append(", ");
        sb.append(image.getFormattedDate());
        if (image.getBucketName() != null && !image.getBucketName().isEmpty()) {
            sb.append(", ");
            sb.append(image.getBucketName());
        }
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
                MyAction action = MyAction.fromActionName(paramIntent.getAction());
                Log.i(Logging.TAG, "PhotoActivity; onReceive: " + action);
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
        intentFilter.addAction(MyAction.WAKE_UP_SCREEN.getActionName());
        intentFilter.addAction(MyAction.SNOOZE_SCREEN.getActionName());
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
        Log.d(Logging.TAG, "PhotoActivity; createTickerRunnable; created: " + tickerRunnable);
    }
    
    private void stopIntervalTimer() {
        if (tickerRunnable != null) {
            Log.i(Logging.TAG, "PhotoActivity; stopIntervalTimer; removing tickerRunnable");
            handler.removeCallbacks(tickerRunnable);
        }
        tickerRunnable = null;
    }

    private void nextPhoto() {
        nextPhotoNumber();
        showPhoto(currentPhoto);
        startIntervalTimer();
    }

    private void nextPhotoNumber() {
        switch (photoOrder) {
        case SEQUENTIAL:
            currentPhoto++;
            if (currentPhoto >= images.size()) {
                currentPhoto = 0;
            }
            break;
        case RANDOM:
            double r = Math.random();
            currentPhoto = (int)Math.floor(images.size() * r);
            break;
        }
    }
}
