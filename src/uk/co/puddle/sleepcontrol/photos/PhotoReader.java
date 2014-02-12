package uk.co.puddle.sleepcontrol.photos;

import java.io.File;
import java.util.List;

import uk.co.puddle.sleepcontrol.SleepLogging;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class PhotoReader {
    
    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
            Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    
    public List<PhotoEntry> list(Context context) {
        boolean readable = isExternalStorageReadable();
        Log.i(SleepLogging.TAG, "PhotoReader: readable: " + readable);
        if (!readable) {
//            return;
        }
        File dir = Environment.getExternalStorageDirectory();
        Log.i(SleepLogging.TAG, "PhotoReader: externalDirName: dir: " + dir);
        listSubDir(Environment.DIRECTORY_PICTURES);
        listSubDir(Environment.DIRECTORY_MUSIC);
        listSubDir(Environment.DIRECTORY_DCIM);
        
        List<PhotoEntry> images = PhotoHelper1.getCameraImages(context);
        return images;
    }

    private void listSubDir(String subDirName) {
        File dir = Environment.getExternalStoragePublicDirectory(subDirName);
        Log.i(SleepLogging.TAG, "PhotoReader: subDirName: " + subDirName + "; dir: " + dir);
        File[] files = dir.listFiles();
        if (files == null) {
            Log.i(SleepLogging.TAG, "PhotoReader: listFiles returned null");
        } else {
            for (File file : files) {
                Log.i(SleepLogging.TAG, "PhotoReader: file: " + file);
            }
        }
    }

}
