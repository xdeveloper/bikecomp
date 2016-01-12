package ua.com.abakumov.bikecomp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import java.util.List;

import ua.com.abakumov.bikecomp.fragment.PreferenceFragment;
import ua.com.abakumov.bikecomp.util.Constants;
import ua.com.abakumov.bikecomp.util.theme.ThemeDecider;
import ua.com.abakumov.bikecomp.util.theme.WithActionBarThemeDecider;


/**
 * Settings activity
 * <p>
 * Created by Oleksandr Abakumov on 6/28/15.
 */
public class SettingsActivity extends PreferenceActivity {

    private ThemeDecider themeDecider = new WithActionBarThemeDecider();

    // ----------- System --------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setupTheme(this, themeDecider);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preferences_headers, target);

        SharedPreferences prefs = this.getSharedPreferences(Constants.SETTINGS_NAME, 0);

        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                int flag = 1;
            }
        });
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName);
    }


}
