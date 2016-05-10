package ua.com.abakumov.bikecomp.activity.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import org.greenrobot.eventbus.NoSubscriberEvent;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.event.ReloadApplication;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static ua.com.abakumov.bikecomp.util.Constants.SETTINGS_THEME_BY_CALENDAR_KEY;
import static ua.com.abakumov.bikecomp.util.Constants.SETTINGS_THEME_KEY;
import static ua.com.abakumov.bikecomp.util.helper.EventBusHelper.post;
import static ua.com.abakumov.bikecomp.util.helper.EventBusHelper.registerEventBus;
import static ua.com.abakumov.bikecomp.util.helper.EventBusHelper.unregisterEventBus;
import static ua.com.abakumov.bikecomp.util.helper.LogHelper.information;
import static ua.com.abakumov.bikecomp.util.helper.UIHelper.restartActivity;
import static ua.com.abakumov.bikecomp.util.helper.UIHelper.setupTheme;


/**
 * Settings activity
 * <p>
 * Created by Oleksandr Abakumov on 6/28/15.
 */
public class SettingsActivity extends PreferenceActivity {

    public static class GeneralPreferenceFragment extends PreferenceFragment {
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.general_preferences);
        }
    }

    public static class ScreenPreferenceFragment extends PreferenceFragment {
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.screen_preferences);
        }
    }

    public static class SoundPreferenceFragment extends PreferenceFragment {
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.sound_preferences);
        }
    }

    SharedPreferences.OnSharedPreferenceChangeListener listener = (sharedPreferences, key) -> {
        switch (key) {
            case SETTINGS_THEME_KEY:
                information("Changed application theme. Apply new theme.");
                post(new ReloadApplication());
                restartActivity(this);
                break;

            case SETTINGS_THEME_BY_CALENDAR_KEY:
                information("Changed application theme. Apply new theme.");
                post(new ReloadApplication());
                restartActivity(this);
                break;
        }
    };

    // ----------- System --------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setupTheme(this);
        registerEventBus(this);
        getDefaultSharedPreferences(getApplicationContext())
                .registerOnSharedPreferenceChangeListener(listener);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        getDefaultSharedPreferences(getApplicationContext()).unregisterOnSharedPreferenceChangeListener(listener);
        unregisterEventBus(this);

        super.onDestroy();
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preferences_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }


    // ----------- Events handling -----------------------------------------------------------------

    @SuppressWarnings(value = "unused")
    @Subscribe
    public void onEvent(NoSubscriberEvent event) {
        // Just a stub
        information("Event came");
    }

}
