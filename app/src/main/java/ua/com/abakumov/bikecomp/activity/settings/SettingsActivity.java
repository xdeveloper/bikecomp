package ua.com.abakumov.bikecomp.activity.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import java.util.List;

import de.greenrobot.event.NoSubscriberEvent;
import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.event.ReloadApplication;
import ua.com.abakumov.bikecomp.fragment.PreferenceFragment;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static ua.com.abakumov.bikecomp.util.helper.EventBusHelper.post;
import static ua.com.abakumov.bikecomp.util.helper.EventBusHelper.register;
import static ua.com.abakumov.bikecomp.util.helper.EventBusHelper.unregister;
import static ua.com.abakumov.bikecomp.util.helper.LogHelper.information;
import static ua.com.abakumov.bikecomp.util.helper.UIHelper.SETTINGS_THEME_KEY;
import static ua.com.abakumov.bikecomp.util.helper.UIHelper.restartActivity;
import static ua.com.abakumov.bikecomp.util.helper.UIHelper.setupTheme;


/**
 * Settings activity
 * <p>
 * Created by Oleksandr Abakumov on 6/28/15.
 */
public class SettingsActivity extends PreferenceActivity {

    SharedPreferences.OnSharedPreferenceChangeListener listener = (sharedPreferences, key) -> {
        switch (key) {
            case SETTINGS_THEME_KEY:
                information("Changed application theme.Apply new theme.");
                post(new ReloadApplication());
                restartActivity(this);
                break;
        }
    };

    // ----------- System --------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setupTheme(this);
        register(this);
        getDefaultSharedPreferences(getApplicationContext()).registerOnSharedPreferenceChangeListener(listener);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        getDefaultSharedPreferences(getApplicationContext()).unregisterOnSharedPreferenceChangeListener(listener);
        unregister(this);

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


    // ----------- Events handling -----------------------------------------------------------------

    @SuppressWarnings(value = "unused")
    public void onEvent(NoSubscriberEvent event) {
        // Just a stub
        information("Event came");
    }

}
