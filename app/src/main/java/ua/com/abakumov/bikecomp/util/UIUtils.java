package ua.com.abakumov.bikecomp.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen;
import static android.R.style.Theme_Holo_NoActionBar_Fullscreen;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * UI specific utilities
 * <p>
 * Created by Oleksandr Abakumov on 7/21/15.
 */
public class UIUtils {
    public static final String SETTINGS_THEME_KEY = "displaySettingsThemeKey";
    public static final String SETTINGS_BACKLIGHT_STRATEGY_KEY = "displaySettingsBacklightStrategyKey";

    public enum Theme {
        Day(Theme_Holo_Light_NoActionBar_Fullscreen),
        Night(Theme_Holo_NoActionBar_Fullscreen);

        private final int themeResId;

        Theme(int themeResId) {
            this.themeResId = themeResId;
        }

        public static int byThemeName(String theme) {
            Theme t = valueOf(theme);
            return t.themeResId;
        }

    }

    /**
     * Setup theme based on preferences or calendar
     *
     * @param context context
     * @param clazz   clazz
     */
    public static void setupTheme(Context context, Class<?> clazz) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        context.setTheme(Theme.byThemeName(defaultSharedPreferences.getString(SETTINGS_THEME_KEY, Theme.Day.name())));

        if (clazz != null) {
            Intent intent = new Intent(context, clazz);
            intent.setFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static void setupTheme(Context context) {
        setupTheme(context, null);
    }


}
