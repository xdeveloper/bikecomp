package ua.com.abakumov.bikecomp.event.gps;

import ua.com.abakumov.bikecomp.Utils;

/**
 * <Class Name and Purpose>
 * <p/>
 * Created by Oleksandr Abakumov on 7/8/15.
 */
public class LocationProviderLocationChangedEvent {

    private float metersPerSecoundSpeed;

    /**
     * Constructor with meters per secound parameter
     *
     * @param metersPerSecoundSpeed mps
     */
    public LocationProviderLocationChangedEvent(float metersPerSecoundSpeed) {
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
        return Utils.metersPerSecoundToKilometersPerHour(metersPerSecoundSpeed);
    }

}
