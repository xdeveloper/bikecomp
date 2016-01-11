package ua.com.abakumov.bikecomp.service.infoservice;

import android.location.Location;

/**
 * Location helper
 * <p>
 * Created by Oleksandr_Abakumov on 1/11/2016.
 */
public class LocationHolder {
    private Location previousLocation;
    private Location currentLocation;

    /**
     * New location available
     *
     * @param location location
     */
    public void newLocation(Location location) {
        if (previousLocation == null) {
            previousLocation = location;
            currentLocation = location;
        } else {
            previousLocation = currentLocation;
            currentLocation = location;
        }
    }

    /**
     * Get a distance between two latest locations
     *
     * @return distance in meters
     */
    public float latestDistance() {
        if (currentLocation == null || previousLocation == null) {
            return 0;
        } else if (currentLocation.equals(previousLocation)) {
            return 0;
        } else {
            return currentLocation.distanceTo(previousLocation);
        }
    }
}
