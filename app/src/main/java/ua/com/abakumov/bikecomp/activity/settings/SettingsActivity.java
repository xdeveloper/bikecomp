package ua.com.abakumov.bikecomp.activity.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import java.util.List;

import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.event.ReloadApplication;
import ua.com.abakumov.bikecomp.fragment.PreferenceFragment;
import ua.com.abakumov.bikecomp.util.helper.EventBusHelper;
import ua.com.abakumov.bikecomp.util.helper.LogHelper;
import ua.com.abakumov.bikecomp.util.helper.UIHelper;

import static ua.com.abakumov.bikecomp.util.helper.UIHelper.SETTINGS_THEME_KEY;
import static ua.com.abakumov.bikecomp.util.helper.UIHelper.restartActivity;


/**
 * Settings activity
 * <p>
 * Created by Oleksandr Abakumov on 6/28/15.
 */
public class SettingsActivity extends PreferenceActivity {

    SharedPreferences.OnSharedPreferenceChangeListener listener = (sharedPreferences, key) -> {
        switch (key) {
            case SETTINGS_THEME_KEY:
                LogHelper.information("Changed application theme.Apply new theme.");
                EventBusHelper.post(new ReloadApplication());
                restartActivity(this);
                break;
        }
    };

    // ----------- System --------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        UIHelper.setupTheme(this);

        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).registerOnSharedPreferenceChangeListener(listener);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).unregisterOnSharedPreferenceChangeListener(listener);
        super.onDestroy();
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preferences_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName);
    }


}
