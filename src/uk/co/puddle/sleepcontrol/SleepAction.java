package uk.co.puddle.sleepcontrol;

public enum SleepAction {
    
    /** This is used when we _start_ the clocks in the middle of the wake-up period */
    INIT_WAKE_UP  ("uk.co.puddle.sleepcontrol.SLEEP_INIT_WAKE_UP_ACTION"),
    
    /** Normal wake up alarm */
    WAKE_UP_SCREEN("uk.co.puddle.sleepcontrol.SLEEP_WAKE_UP_SCREEN_ACTION"),
    
    /** Normal snooze alarm */
    SNOOZE_SCREEN ("uk.co.puddle.sleepcontrol.SLEEP_SNOOZE_SCREEN_ACTION"),

    /** Explicitly releasing all locks, eg cancelling the timers */
    RELEASE_LOCKS ("uk.co.puddle.sleepcontrol.SLEEP_RELEASE_LOCKS"),
    
    /** dummy action to document why we are releasing, ie because we've detected RUNMODE is STOPPED */
    RELEASE_COS_RUNMODE_STOPPED("uk.co.puddle.sleepcontrol.SLEEP_RELEASE_COS_RUNMODE_STOPPED"), // 

    /** dummy action to document why we are releasing, ie because we're about to re-acquire a lock, so must not 'leak' this one */
    RELEASE_COS_ABOUT_TO_ACQUIRE("uk.co.puddle.sleepcontrol.SLEEP_RELEASE_COS_ABOUT_TO_ACQUIRE"),
    
    /** Failed to understand the action string - use this enum value */
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
