package ua.com.abakumov.bikecomp.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ua.com.abakumov.bikecomp.MainActivity;

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
        String theme = defaultSharedPreferences.getString(SETTINGS_THEME_KEY, Theme.Day.name());
        int id = Theme.byThemeName(theme);
        context.setTheme(id);

        if (clazz != null) {
            context.startActivity(new Intent(context, clazz));
        }
    }

    public static void setupTheme(Context context) {
        setupTheme(context, null);
    }


}
