package ua.com.abakumov.bikecomp.util.helper;

import android.preference.PreferenceManager;

/**
 * Helper for preferences
 * Created by Oleksandr_Abakumov on 1/13/2016.
 */
public class PreferencesHelper {
    /**
     * Load preference
     *
     * @param key        key to load by
     * @param defaultVal def value
     * @param context    context
     * @return string value
     */
    public static String getPreferenceByKey(String key, String defaultVal, android.content.Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultVal);
    }
}
