package ua.com.abakumov.bikecomp.util.helper;

import android.content.Context;
import android.location.LocationManager;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static android.content.Context.LOCATION_SERVICE;
import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static ua.com.abakumov.bikecomp.util.helper.LogHelper.information;

/**
 * Useful utilities
 * <p>
 * Created by Oleksandr Abakumov on 7/4/15.
 */
public final class Helper {
    private static NumberFormat ZERO_FORMAT = new DecimalFormat("0.0");

    private static NumberFormat USUAL_FORMAT = new DecimalFormat("##.0");

    private static NumberFormat DISTANCE_FORMAT = new DecimalFormat("##.#");

    /**
     * Format speed parameter (with locale-dependent decimal separator)
     * <p>
     * 0        ->  0.0
     * 0.32     ->  0.3
     * 0.37     ->  0.4
     * 1        ->  1.0
     * 10.45    ->  10.5
     * 10.42    ->  10.4
     * 20       ->  20.0
     *
     * @param speed speed
     * @return formatted string
     */
    public static String formatSpeed(double speed) {
        return (speed >= 0 && speed < 1 ? ZERO_FORMAT.format(speed) : USUAL_FORMAT.format(speed));
    }

    public static String formatDistance(float distance) {
        return DISTANCE_FORMAT.format(distance);
    }

    public static String formatElapsedTime(long seconds) {
        if (seconds < 60) {
            return lessThanMinute(seconds);
        }

        if (seconds >= 60 && seconds < 3600) {
            return lessThanHour(seconds);
        }

        // seconds >= 3600)
        return hourAndMore(seconds);
    }

    /**
     * Formatted date
     *
     * @param date date
     * @return formatted date
     */
    public static String formatDate(Date date) {
        return DateFormat.getDateInstance().format(date);
    }

    /**
     * Format date as time
     *
     * @param date date
     * @return formatted time
     */
    public static String formatTime(Date date) {
        return DateFormat.getTimeInstance().format(date);
    }

    /**
     * M/s to Km/ph
     *
     * @param speed in m/s
     * @return km/ph
     */
    public static double metersPerSecoundToKilometersPerHour(float speed) {
        return 3.6 * speed;
    }

    public static float metersToKilometers(float distanceInMeters) {
        return distanceInMeters / 1000;
    }

    private static String hourAndMore(long seconds) {
        // H
        long hours = TimeUnit.SECONDS.toHours(seconds);

        // M
        seconds = seconds - TimeUnit.HOURS.toSeconds(hours);
        long minutes = TimeUnit.SECONDS.toMinutes(seconds);

        // S
        seconds = seconds - TimeUnit.MINUTES.toSeconds(minutes);

        return normalizeNumber(hours) + ":" + normalizeNumber(minutes) + ":" + normalizeNumber(seconds);
    }

    private static String lessThanHour(long secounds) {
        long minutes = TimeUnit.SECONDS.toMinutes(secounds);
        secounds = secounds - TimeUnit.MINUTES.toSeconds(minutes);
        return minutes + ":" + normalizeNumber(secounds);
    }

    private static String lessThanMinute(long seconds) {
        return "0:" + normalizeNumber(seconds);
    }

    private static String normalizeNumber(long number) {
        return number < 10 ? "0" + number : String.valueOf(number);
    }

    public static Date timeToDate(long milliseconds) {
        Date date = new Date();
        date.setTime(milliseconds);
        return date;
    }

    public static boolean isItDaylightTimeNow(Context context, int timeOffsetInMinutes) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        // Try to detect by network, gps...
        android.location.Location lastKnownLocation = locationManager.getLastKnownLocation(NETWORK_PROVIDER);
        if (lastKnownLocation == null) {
            lastKnownLocation = locationManager.getLastKnownLocation(GPS_PROVIDER);
        }

        boolean usedLastKnown = false;
        Location location;

        if (lastKnownLocation == null) {
            location = new Location("50.27", "30.31"); // Kyiv by default
        } else {
            usedLastKnown = true;
            location = new Location(
                    String.valueOf(lastKnownLocation.getLatitude()),
                    String.valueOf(lastKnownLocation.getLongitude())
            );
        }


        Calendar now = Calendar.getInstance();
        TimeZone timeZone = now.getTimeZone();

        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, timeZone);
        Calendar officialSunrise = calculator.getOfficialSunriseCalendarForDate(now);
        String officialSunriseStr = calculator.getOfficialSunriseForDate(now);
        Calendar officialSunSet = calculator.getOfficialSunsetCalendarForDate(now);
        String officialSunSetStr = calculator.getOfficialSunsetForDate(now);

        now.add(Calendar.MINUTE, timeOffsetInMinutes);

        boolean daylightTime = now.after(officialSunrise) && now.before(officialSunSet);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        String messageToLog = "Now is - " + sdf.format(now.getTime()) +
                ", timezone - " + timeZone.getID() +
                ", sunrise - " + officialSunriseStr +
                ", sunset - " + officialSunSetStr +
                ", daylight time  - " + daylightTime +
                ", used last known location  - " + usedLastKnown;

        information(messageToLog);

        return daylightTime;
    }

    public static void shutdownExecutors(ExecutorService... executors) {
        for (ExecutorService executor : executors) {
            if (executor != null) {
                executor.shutdown();
            }
        }
    }
}