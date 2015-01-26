package uk.co.puddle.photoframe.photos;

public enum PhotoOrder {
    SEQUENTIAL,
    RANDOM,
    ;
    
    public static PhotoOrder fromValue(String value) {
        for (PhotoOrder order : values()) {
            if (order.name().equals(value)) { return order; }
        }
        return null;
    }
}
