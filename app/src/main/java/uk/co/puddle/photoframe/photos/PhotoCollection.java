package uk.co.puddle.photoframe.photos;

import android.content.Context;
import android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.puddle.photoframe.Logging;
import uk.co.puddle.photoframe.storage.Storage;

public class PhotoCollection {

    private Context context;
    private List<PhotoEntry> images;
    private PhotoOrder photoOrder = PhotoOrder.RANDOM;
    private int currentPhoto = -1; // means that PhotoOrder.SEQUENTIAL will move to zero first time through
    private Map<String, Integer> firstImageInBucket;
    private Map<String, Integer> lastImageInBucket;
    private String currentBucketId = null;

    public PhotoCollection(Context context, PhotoOrder photoOrder) {
        this.context = context;
        this.photoOrder = photoOrder;

        //currentPhoto = -1; // means that PhotoOrder.SEQUENTIAL will move to zero first time through
        currentPhoto = new Storage().getIntProperty(context, Storage.CURRENT_SEQ_KEY, -1);

        images = new PhotoReader().list(context);
        clipSequenceToRange();

        Log.v(Logging.TAG, "PhotoCollection: now sorting " + images.size() + " entries...");
        Collections.sort(images, new PhotoEntry.PhotoEntryFolderAndDateComparator());

        firstImageInBucket = new HashMap<>();
        lastImageInBucket = new HashMap<>();
        for (int imageNumber = 0; imageNumber < images.size(); imageNumber++) {
            PhotoEntry image = images.get(imageNumber);
            String bucketId = image.getBucketId();
            Integer first = firstImageInBucket.get(bucketId);
            if (first == null) {
                firstImageInBucket.put(bucketId, imageNumber);
            }
            lastImageInBucket.put(bucketId, imageNumber);
        }
        currentBucketId = null;
        Log.i(Logging.TAG, "PhotoCollection; found a total of "
                + images.size() + " images"
                + " in " + firstImageInBucket.size() + " folders (buckets)"
        );

    }

    public PhotoEntry getPhotoEntry(int num) {
        PhotoEntry photoEntry;
        if (num < images.size()) {
            photoEntry = images.get(num);
        } else {
            photoEntry = new PhotoEntry();
        }
        return photoEntry;
    }

    public int getCount() {
        return images.size();
    }

    public int getCurrentPhoto() {
        return currentPhoto;
    }

    private void clipSequenceToRange() {
        if (currentPhoto < 0 || currentPhoto >= images.size()) {
            currentPhoto = 0;
        }
    }

    public void nextPhotoNumber() {
        switch (photoOrder) {
            case SEQUENTIAL:
                currentPhoto++;
                clipSequenceToRange();
                new Storage().saveIntProperty(context, Storage.CURRENT_SEQ_KEY, currentPhoto);
                break;
            case RANDOM:
                double r = Math.random();
                currentPhoto = (int)Math.floor(images.size() * r);
                break;
            case SEQUENTIAL_WITHIN_RANDOM_FOLDER:
                if ((currentBucketId == null) || (currentPhoto == lastImageInBucket.get(currentBucketId))) {
                    double r2 = Math.random();
                    int photoWithinBucket = (int)Math.floor(images.size() * r2);
                    PhotoEntry image = images.get(photoWithinBucket);
                    currentBucketId = image.getBucketId();
                    Log.d(Logging.TAG, "PhotoCollection; SEQUENTIAL_WITHIN_RANDOM_FOLDER"
                            + "; chose item " + photoWithinBucket + "/" + images.size()
                            + "; BucketId: " + currentBucketId + "; BucketName: " + image.getBucketName()
                    );
                    currentPhoto = firstImageInBucket.get(currentBucketId);
                    int last = lastImageInBucket.get(currentBucketId);
                    Log.d(Logging.TAG, "PhotoCollection; SEQUENTIAL_WITHIN_RANDOM_FOLDER"
                            + "; first: " + currentPhoto + "; last: " + last
                            + " (" + (last - currentPhoto + 1) + " in folder)");
                } else {
                    currentPhoto++;
                    Log.d(Logging.TAG, "PhotoCollection; SEQUENTIAL_WITHIN_RANDOM_FOLDER"
                            + "; next: " + currentPhoto + "; last: " + lastImageInBucket.get(currentBucketId));
                    clipSequenceToRange();
                }
                new Storage().saveIntProperty(context, Storage.CURRENT_SEQ_KEY, currentPhoto);
                break;
        }
    }
}
