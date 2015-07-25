package ua.com.abakumov.bikecomp.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ua.com.abakumov.bikecomp.MainActivity;
import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.ReportActivity;
import ua.com.abakumov.bikecomp.domain.Ride;

import static android.R.style.Theme;
import static android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen;
import static android.R.style.Theme_Holo_NoActionBar_Fullscreen;

/**
 * UI specific utilities
 * <p>
 * Created by Oleksandr Abakumov on 7/21/15.
 */
public class UIUtils {
    public static final String SETTINGS_THEME_KEY = "displaySettingsThemeKey";
    public static final String SETTINGS_BACKLIGHT_STRATEGY_KEY = "displaySettingsBacklightStrategyKey";

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

    public static void goReportScreen(Context context, Ride ride) {
        Intent intent = new Intent(context, ReportActivity.class);
        intent.putExtra(Ride.class.getCanonicalName(), ride);
        context.startActivity(intent);
    }

    public static void showNotification(Context context) {
        Intent intent = new Intent(context, MainActivity.class);

        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Notification notification =
                new Notification.Builder(context)
                        .setOngoing(true)
                        .setSmallIcon(android.R.drawable.btn_star)
                        .setContentTitle(context.getResources().getString(R.string.session_active))
                        .setContentText(context.getResources().getString(R.string.session_active_text))
                        .setContentIntent(pIntent)
                        .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    public static void hideNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

}