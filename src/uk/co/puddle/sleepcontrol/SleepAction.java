package uk.co.puddle.sleepcontrol;

public enum SleepAction {
    
    WAKE_UP_SCREEN("uk.co.puddle.sleepcontrol.SLEEP_WAKE_UP_SCREEN_ACTION"),
    SNOOZE_SCREEN ("uk.co.puddle.sleepcontrol.SLEEP_SNOOZE_SCREEN_ACTION"),
    RELEASE_LOCKS ("uk.co.puddle.sleepcontrol.SLEEP_RELEASE_LOCKS"),
    UNKNOWN       ("uk.co.puddle.sleepcontrol.SLEEP_UNKNOWN"),
    ;

    private final String actionName;
    
    private SleepAction(String actionName) {
        this.actionName = actionName;
    }
    
    public String getActionName() {
        return actionName;
    }

    public static SleepAction fromActionName(String actionName) {
        for (SleepAction a : values()) {
            if (a.getActionName().equals(actionName)) { return a; }
        }
        return SleepAction.UNKNOWN;
    }
}
