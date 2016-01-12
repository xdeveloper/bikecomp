package ua.com.abakumov.bikecomp.event.gps;

import ua.com.abakumov.bikecomp.util.helper.Helper;
import ua.com.abakumov.bikecomp.event.Event;

/**
 * New location object from GPS module event
 * <p/>
 * Created by Oleksandr Abakumov on 7/8/15.
 */
public class NewLocation implements Event {

    private float metersPerSecoundSpeed;

    /**
     * Constructor with meters per secound parameter
     *
     * @param metersPerSecoundSpeed mps
     */
    public NewLocation(float metersPerSecoundSpeed) {
        this.metersPerSecoundSpeed = metersPerSecoundSpeed;
    }

    /**
     * Speed in meters per secound
     *
     * @return speed
     */
    public float getMpsSpeed() {
        return metersPerSecoundSpeed;
    }

    /**
     * Speed in kilometers per hour
     *
     * @return speed
     */
    public double getKmphSpeed() {
        return Helper.metersPerSecoundToKilometersPerHour(metersPerSecoundSpeed);
    }

}
