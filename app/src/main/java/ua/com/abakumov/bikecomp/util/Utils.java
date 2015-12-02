package ua.com.abakumov.bikecomp.util;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import ua.com.abakumov.bikecomp.R;

/**
 * Useful utilities
 * <p>
 * Created by Oleksandr Abakumov on 7/4/15.
 */
public final class Utils {
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
     *
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

    private interface Callback {
        void callback(View view);
    }

    public static void showToast(int rid, Activity activity) {
        showToast(activity, (View v) -> {
            ((TextView) v.findViewById(R.id.new_toast_layout_text)).setText(rid);
        });
    }

    public static void showToast(Activity activity, String text) {
        showToast(activity, (View v) -> {
            ((TextView) v.findViewById(R.id.new_toast_layout_text)).setText(text);
        });
    }

    private static void showToast(Activity activity, Callback callback) {
        LayoutInflater inflater = activity.getLayoutInflater();

        View layout = inflater.inflate(
                R.layout.new_toast_layout,
                (ViewGroup) activity.findViewById(R.id.new_toast_layout_id));

        callback.callback(layout);

        Toast toast = new Toast(activity.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
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
}