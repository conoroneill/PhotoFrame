package uk.co.puddle.photoframe;

public enum MyAction {
    
    /** This is used when we _start_ the clocks in the middle of the wake-up period */
    INIT_WAKE_UP  ("uk.co.puddle.photoframe.SLEEP_INIT_WAKE_UP_ACTION"),
    
    /** Normal wake up alarm */
    WAKE_UP_SCREEN("uk.co.puddle.photoframe.SLEEP_WAKE_UP_SCREEN_ACTION"),
    
    /** Normal snooze alarm */
    SNOOZE_SCREEN ("uk.co.puddle.photoframe.SLEEP_SNOOZE_SCREEN_ACTION"),

    /** Alarm has woken, now tell the rest of the app to re-wake */
    WAKE_UP_OUR_COMPONENTS("uk.co.puddle.photoframe.SLEEP_WAKE_UP_OUR_COMPONENTS_ACTION"),
    
    /** Alarm has decided to snooze, so tell the rest of the app to snooze */
    SNOOZE_OUR_COMPONENTS ("uk.co.puddle.photoframe.SLEEP_SNOOZE_OUR_COMPONENTS_ACTION"),

    /** Explicitly releasing all locks, eg cancelling the timers */
    RELEASE_LOCKS ("uk.co.puddle.photoframe.SLEEP_RELEASE_LOCKS"),
    
    /** dummy action to document why we are releasing, ie because we've detected RUNMODE is STOPPED */
    RELEASE_COS_RUNMODE_STOPPED("uk.co.puddle.photoframe.SLEEP_RELEASE_COS_RUNMODE_STOPPED"), // 

    /** dummy action to document why we are releasing, ie because we're about to re-acquire a lock, so must not 'leak' this one */
    RELEASE_COS_ABOUT_TO_ACQUIRE("uk.co.puddle.photoframe.SLEEP_RELEASE_COS_ABOUT_TO_ACQUIRE"),
    
    /** Failed to understand the action string - use this enum value */
    UNKNOWN       ("uk.co.puddle.photoframe.SLEEP_UNKNOWN"),
    ;

    private final String actionName;
    
    private MyAction(String actionName) {
        this.actionName = actionName;
    }
    
    public String getActionName() {
        return actionName;
    }

    public static MyAction fromActionName(String actionName) {
        for (MyAction a : values()) {
            if (a.getActionName().equals(actionName)) { return a; }
        }
        return MyAction.UNKNOWN;
    }
}
