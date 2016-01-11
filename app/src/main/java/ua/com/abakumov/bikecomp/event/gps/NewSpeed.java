package ua.com.abakumov.bikecomp.event.gps;

import ua.com.abakumov.bikecomp.event.Event;
import ua.com.abakumov.bikecomp.util.Utils;

/**
 * Event for speed
 * <p>
 * Created by Oleksandr Abakumov on 7/8/15.
 */
public class NewSpeed implements Event {

    // Meters per second
    private final float speed;

    public NewSpeed(float speed) {
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

    public double getKmphSpeed() {
        return Utils.metersPerSecoundToKilometersPerHour(speed);
    }
}