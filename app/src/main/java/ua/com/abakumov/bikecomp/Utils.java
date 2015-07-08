package ua.com.abakumov.bikecomp;

import android.content.Context;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Formatter;
import java.util.concurrent.TimeUnit;

/**
 * Useful utilities
 * <p/>
 * Created by Oleksandr Abakumov on 7/4/15.
 */
public final class Utils {
    private static NumberFormat ZERO_FORMAT = new DecimalFormat("0.0");

    private static NumberFormat USUAL_FORMAT = new DecimalFormat("##.0");

    private static NumberFormat DISTANCE_FORMAT = new DecimalFormat("##.#");

    /**
     * Format speed parameter (with locale-dependent decimal separator)
     * <p/>
     * 0 -> 0.0
     * 1 -> 1.0
     * 10.45 -> 10.5
     * 10.42 -> 10.4
     * 20 -> 20.0
     *
     * @param speed speed
     * @return formatted string
     */
    public static String formatSpeed(double speed) {
        return (speed == 0 ? ZERO_FORMAT.format(0) : USUAL_FORMAT.format(speed));
    }

    public static String formatDistance(float distance) {
        return DISTANCE_FORMAT.format(distance);
    }

    public static String formatElapsedTime(int seconds) {
        if (seconds < 60) {
            return lessThanMinute(seconds);
        }

        if (seconds >= 60 && seconds < 3600) {
            return lessThanHour(seconds);
        }

        // seconds >= 3600)
        return hourAndMore(seconds);
    }

    private static String hourAndMore(long seconds) {
        // H
        long hours = TimeUnit.SECONDS.toHours(seconds);

        // M
        seconds = seconds - TimeUnit.HOURS.toSeconds(hours);
        long minutes = TimeUnit.SECONDS.toMinutes(seconds);

        // S
        seconds = seconds - TimeUnit.MINUTES.toSeconds(minutes);

        return normalizeNumber(hours) + ":" + normalizeNumber(minutes) + normalizeNumber(seconds);
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

    /**
     * M/s to Km/ph
     *
     * @param speed in m/s
     * @return km/ph
     */
    public static double metersPerSecoundToKilometersPerHour(float speed) {
        return 3.6 * speed;
    }

    public static void showToast(int resId, Context context) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }

    public static void showShortToast(int resId, Context context) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }
}
