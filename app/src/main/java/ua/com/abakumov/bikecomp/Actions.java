package ua.com.abakumov.bikecomp;

/**
 * Holds system-wide constants
 * <p/>
 * Created by Oleksandr Abakumov on 7/5/15.
 */
public class Actions {

    // Broadcast action
    public static final String BROADCAST_ACTION = "ua.com.abakumov.bikecomp.BROADCAST_ACTION";

    public static final String PARCEL_NAME = "ParcelName";

    // ---------------------------------- G P S ----------------------------------------------------
    // "GPS on"
    public static final String GPS_PROVIDER_ENABLED = "GPS_PROVIDER_ENABLED";

    // "GPS off"
    public static final String GPS_PROVIDER_DISABLED = "GPS_PROVIDER_DISABLED";

    // "Receives GPS signal"
    public static final String GPS_PROVIDER_AVAILABLE = "GPS_PROVIDER_AVAILABLE";

    // "Temporarily lost GPS signal"
    public static final String GPS_PROVIDER_TEMPORARILY_UNAVAILABLE = "GPS_PROVIDER_TEMPORARILY_UNAVAILABLE";

    // "Lost GPS signal"
    public static final String GPS_PROVIDER_OUT_OF_SERVICE = "GPS_PROVIDER_OUT_OF_SERVICE";

    // "New GPS location received"
    public static final String GPS_LOCATION_CHANGED = "GPS_LOCATION_CHANGED";
    public static final String GPS_LOCATION_CHANGED_DATA = "GPS_LOCATION_CHANGED_DATA";



    // -------------------------------- C O M M O N ------------------------------------------------


    public static final String SESSION_START = "SESSION_START";
    public static final String SESSION_STOP = "SESSION_STOP";


}
