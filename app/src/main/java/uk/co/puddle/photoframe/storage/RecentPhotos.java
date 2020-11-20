package uk.co.puddle.photoframe.storage;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import uk.co.puddle.photoframe.photos.PhotoEntry;

public class RecentPhotos {
    
    private static final int NUMBER_TO_KEEP = 5;
    
    private static class SingletonHolder {
        private static final RecentPhotos instance = new RecentPhotos();
    }
    
    private final List<PhotoEntry> photos = Collections.synchronizedList(new LinkedList<PhotoEntry>());
    
    private RecentPhotos() {
    }
    
    public static RecentPhotos getInstance() {
        return SingletonHolder.instance;
    }

    public void pushPhotoEntry(PhotoEntry photoEntry) {
        if (photoEntry.getName() == null) {
            return;
        }
        synchronized(photos) {
            int currentSize = photos.size();
            if (currentSize != 0) {
                PhotoEntry currentTail = photos.get(currentSize - 1);
                if (photoEntry == currentTail) {
                    return; // no point in pushing it twice
                }
                if (photoEntry.getData() != null && currentTail.getData() != null) {
                    if (photoEntry.getData().equals(currentTail.getData())) {
                        return;
                    }
                }
            }
            if (currentSize >= NUMBER_TO_KEEP) {
                photos.remove(0); // remove the head
            }
            photos.add(photoEntry); // add to the end of the tail
        }
    }
    
    public List<PhotoEntry> getRecentPhotos() {
        return new LinkedList<>(photos);
    }
}
