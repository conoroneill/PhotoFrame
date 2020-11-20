package uk.co.puddle.photoframe.photos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.puddle.photoframe.Logging;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class PhotoReader {
    
    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
    
    public List<PhotoEntry> list(Context context) {
        boolean readable = isExternalStorageReadable();
        if (!readable) {
            Log.i(Logging.TAG, "PhotoReader: readable: " + readable + "; no images");
            return new ArrayList<>();
        }
        File dir = Environment.getExternalStorageDirectory();
        Log.d(Logging.TAG, "PhotoReader: externalDirName: dir: " + dir);
        listSubDir(Environment.DIRECTORY_PICTURES);
        listSubDir(Environment.DIRECTORY_MUSIC);
        listSubDir(Environment.DIRECTORY_DCIM);

        return PhotoHelper1.getCameraImages(context);
    }

    private void listSubDir(String subDirName) {
        File dir = Environment.getExternalStoragePublicDirectory(subDirName);
        Log.d(Logging.TAG, "PhotoReader: subDirName: " + subDirName + "; dir: " + dir);
        File[] files = dir.listFiles();
        if (files == null) {
            Log.d(Logging.TAG, "PhotoReader: listFiles returned null");
        } else {
            for (File file : files) {
                Log.d(Logging.TAG, "PhotoReader: file: " + file);
            }
        }
    }

}
