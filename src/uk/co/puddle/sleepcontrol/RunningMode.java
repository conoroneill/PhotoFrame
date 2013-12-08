package uk.co.puddle.sleepcontrol;

public enum RunningMode {
    
    STOPPED  (0),
    INTERVALS(1),
    DAILY    (2),
    ;

    private final int storageValue;
    
    private RunningMode(int storageValue) {
        this.storageValue = storageValue;
    }
    
    public int getStorageValue() {
        return storageValue;
    }
    
    public static RunningMode fromStorageValue(int storageValue) {
        for (RunningMode r : values()) {
            if (r.getStorageValue() == storageValue) { return r; }
        }
        return RunningMode.STOPPED;
    }
}
