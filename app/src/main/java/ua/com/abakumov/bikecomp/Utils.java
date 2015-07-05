package ua.com.abakumov.bikecomp;

import android.content.Context;
import android.widget.Toast;

import java.text.DecimalFormat;

/**
 * Useful utilities
 * <p/>
 * Created by Oleksandr Abakumov on 7/4/15.
 */
public final class Utils {
    private static DecimalFormat ZERO_FORMAT = new DecimalFormat("0.0");

    private static DecimalFormat USUAL_FORMAT = new DecimalFormat("##.0");

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
}
