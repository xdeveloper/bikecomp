package ua.com.abakumov.bikecomp.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.function.FunctionalInterface;

import ua.com.abakumov.bikecomp.MainActivity;
import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.ReportActivity;
import ua.com.abakumov.bikecomp.domain.Ride;

/**
 * UI specific utilities
 * <p>
 * Created by Oleksandr Abakumov on 7/21/15.
 */
public class UIUtils {
    public static final String SETTINGS_THEME_KEY = "displaySettingsThemeKey";
    public static final String SETTINGS_BACKLIGHT_STRATEGY_KEY = "displaySettingsBacklightStrategyKey";


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
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public enum Theme {
        Day, Night
    }

    /**
     * Setup theme based on preferences or calendar
     *
     * @param context context
     * @param clazz   clazz
     */
    public static void setupTheme(Context context, Class<?> clazz, ThemeDecider decider) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String themeName = defaultSharedPreferences.getString(SETTINGS_THEME_KEY, UIUtils.Theme.Day.name());

        int resid;
        if (UIUtils.Theme.Day.name().equals(themeName)) {
            resid = decider.dailyTheme();
        } else if (UIUtils.Theme.Night.name().equals(themeName)) {
            resid = decider.nightlyTheme();
        } else {
            resid = decider.dailyTheme();
        }

        context.setTheme(resid);

        if (clazz != null) {
            Intent intent = new Intent(context, clazz);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        }
    }

    public static void setupTheme(Context context, ThemeDecider decider) {
        setupTheme(context, null, decider);
    }

    /**
     * Goes to android "home" screen
     *
     * @param context context
     */
    public static void goHome(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
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

    /**
     * Show notification
     *
     * @param context context
     */
    public static void showNotification(Context context) {
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

        Notification notification =
                new Notification.Builder(context)
                        .setOngoing(true)
                        .setSmallIcon(android.R.drawable.star_big_on)
                        .setContentTitle(context.getResources().getString(R.string.session_active))
                        .setContentText(context.getResources().getString(R.string.session_active_text))
                        .setContentIntent(pendingIntent)
                        .build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    /**
     * Hide notification panel
     *
     * @param context context
     */
    public static void hideNotification(Context context) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
    }

}