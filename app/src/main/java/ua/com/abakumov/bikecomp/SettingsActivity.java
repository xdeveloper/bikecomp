package ua.com.abakumov.bikecomp;

import android.content.SharedPreferences;
import android.preference.PreferenceActivity;

import java.util.List;

import ua.com.abakumov.bikecomp.fragment.PreferenceFragment;
import ua.com.abakumov.bikecomp.util.Constants;


/**
 * Settings activity
 * <p>
 * Created by Oleksandr Abakumov on 6/28/15.
 */
public class SettingsActivity extends PreferenceActivity {


    // ----------- System --------------------------------------------------------------------------

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preferences_headers, target);

        SharedPreferences prefs = this.getSharedPreferences(Constants.SETTINGS_NAME, 0);

        prefs.registerOnSharedPreferenceChangeListener((prefs1, key) -> {
            int flag = 1;
        });
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName);
    }


}
