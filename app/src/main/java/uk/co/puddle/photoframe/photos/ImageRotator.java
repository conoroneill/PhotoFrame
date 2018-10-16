package uk.co.puddle.photoframe.photos;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.media.ExifInterface;
import android.util.Log;

import java.io.IOException;

import uk.co.puddle.photoframe.Logging;

/**
 * Based on:
 * https://teamtreehouse.com/community/how-to-rotate-images-to-the-correct-orientation-portrait-by-editing-the-exif-data-once-photo-has-been-taken
 */
public class ImageRotator {
    public static Bitmap rotateImageIfRequired(Bitmap img, PhotoEntry photoEntry) {

        try {
            return rotateImageIfRequired(img, photoEntry.getData());
        } catch (IOException e) {
            Log.d(Logging.TAG, "ImageRotator: exception: " + e);
            return img;
        }
    }

    private static Bitmap rotateImageIfRequired(Bitmap img, String path) throws IOException {

        ExifInterface ei = new ExifInterface(path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Log.d(Logging.TAG, "ImageRotator: orientation: " + orientation);

        int rotation = getRotationAngle(orientation);
        return rotation == 0 ? img : rotateImage(img, rotation);
    }

    private static int getRotationAngle(int orientation) {
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL: return 0;
            case ExifInterface.ORIENTATION_ROTATE_90: return 90;
            case ExifInterface.ORIENTATION_ROTATE_180: return 180;
            case ExifInterface.ORIENTATION_ROTATE_270: return 270;
            case ExifInterface.ORIENTATION_UNDEFINED: return 0;
            default:
                Log.w(Logging.TAG, "ImageRotator: orientation: " + orientation + " (not recognised)");
                return 0;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Log.d(Logging.TAG, "ImageRotator: rotating by: " + degree + " degrees ...");
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
    }
}
