package uk.co.puddle.photoframe.photos;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class PhotoEntry {
    
    private String name; // the filename
    private String data; // the full filename
    private String date;
    private String bucketName;
    private String bucketId;
    private String thumb;
    private String textOnScreen;
    
    public String getName() {
        return name;
    }
    public PhotoEntry withName(String name) {
        this.name = name;
        return this;
    }
    public String getData() {
        return data;
    }
    public PhotoEntry withData(String data) {
        this.data = data;
        return this;
    }
    public String getDate() {
        return date;
    }
    public PhotoEntry withDate(String date) {
        this.date = date;
        return this;
    }
    public String getBucketName() {
        return bucketName;
    }
    public PhotoEntry withBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }
    public String getBucketId() {
        return bucketId;
    }
    public PhotoEntry withBucketId(String bucketId) {
        this.bucketId = bucketId;
        return this;
    }
    public String getThumb() {
        return thumb;
    }
    public PhotoEntry withThumb(String thumb) {
        this.thumb = thumb;
        return this;
    }
    
    public String getTextOnScreen() {
        return textOnScreen;
    }
    public void setTextOnScreen(String textOnScreen) {
        this.textOnScreen = textOnScreen;
    }
    
    public String getFormattedDate() {
        if (date == null || date.isEmpty()) {
            return "";
        }
        Long ldate = Long.parseLong(date);
        Date d = new Date(ldate);
        //DateFormat df = SimpleDateFormat.getDateInstance();
        DateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String result = df.format(d);
        return result;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Ph:");
        sb.append("F:").append(getData());
        if (getName() != null) { sb.append(",N:").append(getName()); }
        if (getDate() != null) { sb.append(",D:").append(getDate()); }
        if (getBucketId() != null) { sb.append(",I:").append(getBucketId()); }
        if (getBucketName() != null) { sb.append(",B:").append(getBucketName()); }
        if (getThumb() != null) { sb.append(",T:").append(getThumb()); }
        sb.append("]");
        return sb.toString();
    }

    public static class PhotoEntryFolderAndDateComparator implements Comparator<PhotoEntry> {
        public int compare(@NonNull PhotoEntry p1, @NonNull PhotoEntry p2) {
            // Sort by folderName (BucketName), then BucketId in case of duplicates,
            // and then by date within that bucket
            int bucketNameResult = p1.getBucketName().compareTo(p2.getBucketName());
            if (bucketNameResult != 0) return bucketNameResult;
            int bucketIdResult = p1.getBucketId().compareTo(p2.getBucketId());
            if (bucketIdResult != 0) return bucketIdResult;
            return p1.getDate().compareTo(p2.getDate());
        }
    }
}
