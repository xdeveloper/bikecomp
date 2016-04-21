package ua.com.abakumov.bikecomp.util.helper;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.function.FunctionalInterface;

import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.activity.main.MainActivity;
import ua.com.abakumov.bikecomp.activity.report.ReportActivity;
import ua.com.abakumov.bikecomp.domain.Ride;
import ua.com.abakumov.bikecomp.util.Constants;

import static android.content.Context.NOTIFICATION_SERVICE;
import static ua.com.abakumov.bikecomp.util.Constants.NOTIFICATION_TAG;
import static ua.com.abakumov.bikecomp.util.Constants.SCREEN_KEEP_ON;
import static ua.com.abakumov.bikecomp.util.Constants.SCREEN_MIDDLE;
import static ua.com.abakumov.bikecomp.util.Constants.SCREEN_SYSTEM_DEFAULT;
import static ua.com.abakumov.bikecomp.util.Constants.SETTINGS_BACKLIGHT_STRATEGY_KEY;
import static ua.com.abakumov.bikecomp.util.Constants.SETTINGS_THEME_KEY;
import static ua.com.abakumov.bikecomp.util.helper.LogHelper.verbose;
import static ua.com.abakumov.bikecomp.util.helper.LogHelper.warning;
import static ua.com.abakumov.bikecomp.util.helper.PreferencesHelper.getPreferenceByKey;
import static ua.com.abakumov.bikecomp.util.helper.ScreenHelper.BrightnessLevel.AUTO;
import static ua.com.abakumov.bikecomp.util.helper.ScreenHelper.BrightnessLevel.MAX;
import static ua.com.abakumov.bikecomp.util.helper.ScreenHelper.BrightnessLevel.MIDDLE;
import static ua.com.abakumov.bikecomp.util.helper.ScreenHelper.ScreenLock.ALWAYS_ON;
import static ua.com.abakumov.bikecomp.util.helper.ScreenHelper.ScreenLock.SYS_DEFAULT;
import static ua.com.abakumov.bikecomp.util.helper.ScreenHelper.setBrightness;
import static ua.com.abakumov.bikecomp.util.helper.ScreenHelper.setScreenLock;

/**
 * UI specific utilities
 * <p>
 * Created by Oleksandr Abakumov on 7/21/15.
 */
public class UIHelper {

    @FunctionalInterface
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
        toast.setGravity(Gravity.FILL_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public enum Theme {
        Day, Night
    }

    /**
     * Setup theme by application settings
     * <p>
     * NOTE: remember - invoke this before super.onCreate();
     *
     * @param context context
     */
    public static void setupTheme(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String themeName = defaultSharedPreferences.getString(SETTINGS_THEME_KEY, UIHelper.Theme.Day.name());

        if (Theme.Day.name().equals(themeName)) {
            context.setTheme(R.style.BikeCompTheme_Light);
        } else if (Theme.Night.name().equals(themeName)) {
            context.setTheme(R.style.BikeCompTheme_Dark);
        }
    }

    /**
     * Go to Report screen (screen after ride is completed)
     *
     * @param context context
     * @param ride    ride object
     */
    public static void goReportScreen(Context context, Ride ride) {
        Intent intent = new Intent(context, ReportActivity.class);
        intent.putExtra(Ride.class.getCanonicalName(), ride);
        context.startActivity(intent);
    }

    public static Notification.Builder buildNotificationBuilder(Context context) {
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.np_bike_big);

        Notification.Builder builder = new Notification.Builder(context)
                .setOngoing(true)
                .setSmallIcon(R.drawable.np_bike)
                .setLargeIcon(largeIcon)
                .setContentTitle(context.getResources().getString(R.string.session_active))
                .setContentText(context.getResources().getString(R.string.session_active_text));

        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

        builder.setContentIntent(pendingIntent);
        return builder;

    }

    /**
     * Hide notification panel
     *
     * @param context context
     */
    public static void hideNotification(Context context) {
        ((NotificationManager) context.getSystemService(NOTIFICATION_SERVICE)).cancelAll();
    }

    public static void startInForeground(Service service) {
        service.startForeground(NOTIFICATION_TAG, buildNotificationBuilder(service).build());
    }

    public static void setupBacklightStrategy(Window window) {
        final String backlightSetting =
                getPreferenceByKey(
                        SETTINGS_BACKLIGHT_STRATEGY_KEY,
                        SCREEN_SYSTEM_DEFAULT,
                        window.getContext());

        switch (backlightSetting) {
            case SCREEN_KEEP_ON:
                verbose("Maximum brightness always on");
                setScreenLock(ALWAYS_ON, window);
                setBrightness(MAX, window);
                break;

            case SCREEN_MIDDLE:
                verbose("Middle brightness always on");
                setScreenLock(ALWAYS_ON, window);
                setBrightness(MIDDLE, window);
                break;

            case SCREEN_SYSTEM_DEFAULT:
                verbose("Middle brightness system default");
                setScreenLock(SYS_DEFAULT, window);
                setBrightness(AUTO, window);
                break;

            default:
                warning("Unknown value: " + backlightSetting);
        }
    }

    public static void restartActivity(Activity activity) {
        activity.finish();
        activity.startActivity(activity.getIntent());
    }

}