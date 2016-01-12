package ua.com.abakumov.bikecomp.service;

import android.location.Location;

/**
 * Location helper
 * <p>
 * Created by Oleksandr_Abakumov on 1/11/2016.
 */
public class LocationHolder {

    private Location previousLocation;

    private Location currentLocation;

    private float distance;


    /**
     * New location available
     *
     * @param location location
     */
    public void updateLocation(Location location) {
        if (previousLocation == null) {
            previousLocation = location;
            currentLocation = location;
        } else {
            previousLocation = currentLocation;
            currentLocation = location;
        }

        calculateDistance();
    }

    private void calculateDistance() {
        distance += currentLocation.distanceTo(previousLocation);
    }

    /**
     * Get overall distance
     *
     * @return distance in meters
     */
    public float getDistance() {
        return distance;
    }
}
