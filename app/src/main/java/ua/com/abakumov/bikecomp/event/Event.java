package ua.com.abakumov.bikecomp.event;

import java.util.Date;

/**
 * Ancestor for all events
 * <p>
 * Created by Oleksandr Abakumov on 7/8/15.
 */
public abstract class Event {
    private final long eventTime;

    public Event() {
        this.eventTime = new Date().getTime();
    }

    public long getEventTime() {
        return eventTime;
    }
}