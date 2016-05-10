package ua.com.abakumov.bikecomp.util.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Helper for preferences
 * Created by Oleksandr_Abakumov on 1/13/2016.
 */
public class PreferencesHelper {
    private final Context context;

    public PreferencesHelper(Context context) {
        this.context = context;
    }

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

    public boolean get(String k, boolean def) {
        SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(context);
        return pm.getBoolean(k, def);
    }

    public String get(String k, String def) {
        SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(context);
        return pm.getString(k, def);
    }

    public Integer get(String k, Integer def) {
        SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(context);
        return pm.getInt(k, def);
    }

    /**
     * Save preference
     *
     * @param key     key
     * @param val     value
     * @return result
     */
    public boolean setPreferenceByKey(String key, String val) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(key, val);
        return editor.commit();
    }


}
