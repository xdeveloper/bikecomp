package ua.com.abakumov.bikecomp.event.gps;

import ua.com.abakumov.bikecomp.event.Event;

/**
 * Event for distance
 * <p/>
 * Created by Oleksandr Abakumov on 7/8/15.
 */
public class NewDistance implements Event {
    private float distance;

    public NewDistance(float distance) {
        this.distance = distance;
    }

    public float getDistance() {
        return distance;
    }
}