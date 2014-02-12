package uk.co.puddle.sleepcontrol.photos;

public class PhotoEntry {
    
    private String name; // the filename
    private String data; // the full filename
    private String date;
    private String bucketName;
    private String bucketId;
    private String thumb;
    
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

}
