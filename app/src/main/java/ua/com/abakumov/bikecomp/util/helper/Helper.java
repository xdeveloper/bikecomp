package ua.com.abakumov.bikecomp.util.helper;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

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

    public static boolean inDaylightTime() {
        return TimeZone.getDefault().inDaylightTime(new Date());
    }
}