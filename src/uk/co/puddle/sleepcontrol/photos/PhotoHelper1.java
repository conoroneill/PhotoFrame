package uk.co.puddle.sleepcontrol.photos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.puddle.sleepcontrol.SleepLogging;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;

/**
 * See: http://stackoverflow.com/questions/4484158/list-all-camera-images-in-android
 */
public class PhotoHelper1 {
    
    public static final String CAMERA_IMAGE_BUCKET_NAME =
            Environment.getExternalStorageDirectory().toString()
            + "/DCIM/Camera";
    public static final String CAMERA_IMAGE_BUCKET_ID =
            getBucketId(CAMERA_IMAGE_BUCKET_NAME);

    public static final File PHOTO_IMAGE_BUCKET_NAME =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    public static final String PHOTO_IMAGE_BUCKET_ID =
            getBucketId(PHOTO_IMAGE_BUCKET_NAME.getAbsolutePath());
    
    private static boolean useAllImages = true;

    /**
     * Matches code in MediaProvider.computeBucketValues. Should be a common
     * function.
     */
    @SuppressLint("DefaultLocale")
    public static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }

    public static boolean isUseAllImages() {
        return useAllImages;
    }

    public static void setUseAllImages(boolean useAllImages) {
        PhotoHelper1.useAllImages = useAllImages;
    }

    public static List<PhotoEntry> getCameraImages(Context context) {
        List<PhotoEntry> images;
        if (isUseAllImages()) {
            images = getAllImages(context);
        } else {
            Log.d(SleepLogging.TAG, "PhotoHelper1: CAMERA_IMAGE_BUCKET_NAME: " + CAMERA_IMAGE_BUCKET_NAME);
            Log.d(SleepLogging.TAG, "PhotoHelper1: CAMERA_IMAGE_BUCKET_ID: " + CAMERA_IMAGE_BUCKET_ID);
            images = getCameraImages(context, CAMERA_IMAGE_BUCKET_ID);
            Log.i(SleepLogging.TAG, "PhotoReader: camera images: " + images.size());
            
            Log.d(SleepLogging.TAG, "PhotoHelper1: PHOTO_IMAGE_BUCKET_NAME: " + PHOTO_IMAGE_BUCKET_NAME);
            Log.d(SleepLogging.TAG, "PhotoHelper1: PHOTO_IMAGE_BUCKET_ID: " + PHOTO_IMAGE_BUCKET_ID);

            images.addAll(getCameraImages(context, PHOTO_IMAGE_BUCKET_ID));
            Log.i(SleepLogging.TAG, "PhotoReader: photo images: " + images.size());
        }
        Log.i(SleepLogging.TAG, "PhotoReader: images: " + images.size());
        for (PhotoEntry image : images) {
            Log.d(SleepLogging.TAG, "PhotoReader: image: " + image);
        }
        return images;
    }
    
    public static List<PhotoEntry> getCameraImages(Context context, String rootBucketId) {
        //Log.i(SleepLogging.TAG, "PhotoHelper1: rootBucketId: " + rootBucketId);
        final String[] projection = { MediaStore.Images.Media.DATA }; // which 'columns' we need
        final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
        final String[] selectionArgs = { rootBucketId };
        final Cursor cursor = context.getContentResolver().query(Images.Media.EXTERNAL_CONTENT_URI, 
                projection, 
                selection, // can be null, meaning all (http://stackoverflow.com/questions/3020478/android-list-all-images-available/3021018#3021018)
                selectionArgs, // can be null if selection is null
                null);
        List<PhotoEntry> result = new ArrayList<PhotoEntry>(cursor.getCount());
        if (cursor.moveToFirst()) {
            final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            do {
                final String data = cursor.getString(dataColumn);
                PhotoEntry photoEntry = new PhotoEntry().withData(data);
                result.add(photoEntry);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public static List<PhotoEntry> getAllImages(Context context) {
        Log.d(SleepLogging.TAG, "PhotoHelper1: getAllImages ...");
        final String[] projection = { MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.MINI_THUMB_MAGIC,
                }; // which 'columns' we need
        final Cursor cursor = context.getContentResolver().query(Images.Media.EXTERNAL_CONTENT_URI, 
                projection, 
                null, // can be null, meaning all (http://stackoverflow.com/questions/3020478/android-list-all-images-available/3021018#3021018)
                null, // can be null if selection is null
                null);
        List<PhotoEntry> result = new ArrayList<PhotoEntry>(cursor.getCount());
        if (cursor.moveToFirst()) {
            final int dataColumn       = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            final int displayColumn    = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            final int dateColumn       = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
            final int bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            final int bucketIdColumn   = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);
            final int thumbColumn      = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MINI_THUMB_MAGIC);
            do {
                final String data       = cursor.getString(dataColumn);
                final String display    = cursor.getString(displayColumn);
                final String date       = cursor.getString(dateColumn);
                final String bucketName = cursor.getString(bucketNameColumn);
                final String bucketId   = cursor.getString(bucketIdColumn);
                final String thumb     = cursor.getString(thumbColumn);
                Log.d(SleepLogging.TAG, "PhotoHelper1: name: " + display + "; date: " + date + "; data: " + data
                        + "; bucket: " + bucketName + "; id: " + bucketId + "; th: " + thumb);
                PhotoEntry photoEntry = new PhotoEntry();
                photoEntry.withName(display).withData(data).withDate(date);
                photoEntry.withBucketName(bucketName).withBucketId(bucketId).withThumb(thumb);
                result.add(photoEntry);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }
}
