package uk.co.puddle.photoframe.photos;

public enum PhotoOrder {
    SEQUENTIAL,
    RANDOM,
    SEQUENTIAL_WITHIN_RANDOM_FOLDER,
    ;
    
    public static PhotoOrder fromValue(String value) {
        for (PhotoOrder order : values()) {
            if (order.name().equals(value)) { return order; }
        }
        return null;
    }
}
