package ua.com.abakumov.bikecomp.activity.main;


import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.activity.history.HistoryActivity;
import ua.com.abakumov.bikecomp.activity.settings.SettingsActivity;
import ua.com.abakumov.bikecomp.domain.Ride;
import ua.com.abakumov.bikecomp.event.ReloadApplication;
import ua.com.abakumov.bikecomp.event.SessionPauseResume;
import ua.com.abakumov.bikecomp.event.SessionPaused;
import ua.com.abakumov.bikecomp.event.SessionRunning;
import ua.com.abakumov.bikecomp.event.SessionStart;
import ua.com.abakumov.bikecomp.event.SessionStop;
import ua.com.abakumov.bikecomp.event.SessionStopRequest;
import ua.com.abakumov.bikecomp.event.gps.Disabled;
import ua.com.abakumov.bikecomp.event.gps.Enabled;
import ua.com.abakumov.bikecomp.event.gps.GpsTrouble;
import ua.com.abakumov.bikecomp.event.gps.OutOfService;
import ua.com.abakumov.bikecomp.event.gps.TemporaryUnavailable;
import ua.com.abakumov.bikecomp.fragment.SessionStopFragment;
import ua.com.abakumov.bikecomp.service.InfoService;
import ua.com.abakumov.bikecomp.service.LocalBinder;
import ua.com.abakumov.bikecomp.util.helper.Helper;
import ua.com.abakumov.bikecomp.util.helper.PreferencesHelper;
import ua.com.abakumov.bikecomp.util.helper.UIHelper;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static ua.com.abakumov.bikecomp.R.id.action_history;
import static ua.com.abakumov.bikecomp.R.id.action_quit;
import static ua.com.abakumov.bikecomp.R.id.action_settings;
import static ua.com.abakumov.bikecomp.util.Constants.SETTINGS_ROTATE_SCREENS_FREQ_KEY;
import static ua.com.abakumov.bikecomp.util.Constants.SETTINGS_ROTATE_SCREENS_KEY;
import static ua.com.abakumov.bikecomp.util.Constants.SETTINGS_THEME_BY_CALENDAR_KEY;
import static ua.com.abakumov.bikecomp.util.Constants.SETTINGS_THEME_KEY;
import static ua.com.abakumov.bikecomp.util.helper.EventBusHelper.post;
import static ua.com.abakumov.bikecomp.util.helper.EventBusHelper.registerEventBus;
import static ua.com.abakumov.bikecomp.util.helper.EventBusHelper.unregisterEventBus;
import static ua.com.abakumov.bikecomp.util.helper.Helper.inDaylightTime;
import static ua.com.abakumov.bikecomp.util.helper.LogHelper.information;
import static ua.com.abakumov.bikecomp.util.helper.LogHelper.verbose;
import static ua.com.abakumov.bikecomp.util.helper.UIHelper.Theme.Day;
import static ua.com.abakumov.bikecomp.util.helper.UIHelper.Theme.Night;
import static ua.com.abakumov.bikecomp.util.helper.UIHelper.goReportScreen;
import static ua.com.abakumov.bikecomp.util.helper.UIHelper.hideNotification;
import static ua.com.abakumov.bikecomp.util.helper.UIHelper.restartActivity;
import static ua.com.abakumov.bikecomp.util.helper.UIHelper.setupBacklightStrategy;
import static ua.com.abakumov.bikecomp.util.helper.UIHelper.showToast;

/**
 * Main activity
 * <p>
 * Created by Oleksandr Abakumov on 6/28/15.
 */
public class MainActivity extends AppCompatActivity {

    private InfoService infoService;

    private ViewPager viewPager;

    private ScreenSlidePagerAdapter viewPagerAdapter;

    public static final String EXIT_INTENT = "exit";

    private ScheduledThreadPoolExecutor executor;

    private ScheduledThreadPoolExecutor executorChangeTheme;

    private PreferencesHelper preferencesHelper = new PreferencesHelper(this);

    // ----------- System --------------------------------------------------------------------------

    SharedPreferences.OnSharedPreferenceChangeListener listener = (sharedPreferences, key) -> {
        information("Settings have been changed");

        // Screens rotation settings change
        if (SETTINGS_ROTATE_SCREENS_KEY.equals(key) || SETTINGS_ROTATE_SCREENS_FREQ_KEY.equals(key)) {
            setupScreensRotation();
        }

        // Change Theme by Calendar
        if (SETTINGS_THEME_BY_CALENDAR_KEY.equals(key)) {
            if (preferencesHelper.get(SETTINGS_THEME_BY_CALENDAR_KEY, false)) {
                // By calendar
                setupChangeThemeByCalendar();

            } else {
                restartActivity(this);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().getBooleanExtra(EXIT_INTENT, false)) {
            quitApplication();
        }

        UIHelper.setupTheme(this);
        setupChangeThemeByCalendar();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        setCurrentScreenText(R.string.primaryScreen);
                        break;
                    case 1:
                        setCurrentScreenText(R.string.secondaryScreen);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        viewPager.setCurrentItem(0);
        setCurrentScreenText(R.string.primaryScreen);

        setupScreensRotation();

        registerEventBus(this);

        getDefaultSharedPreferences(getApplicationContext())
                .registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        setupBacklightStrategy(this.getWindow());
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectToService();
        verbose("Main screen has been shown");
    }

    @Override
    protected void onDestroy() {
        if (infoService != null) {
            if (infoService.isSessionStopped()) {
                quitApplication();
            }
        }

        if (executor != null) {
            executor.shutdown();
        }

        getDefaultSharedPreferences(getApplicationContext()).unregisterOnSharedPreferenceChangeListener(listener);

        unregisterEventBus(this);

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Go to History
            case action_history:
                startActivity(new Intent(this, HistoryActivity.class));
                break;

            // Go to Settings
            case action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            // Quit application
            case action_quit:
                quitApplication();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    // ----------- Events handling -----------------------------------------------------------------

    @SuppressWarnings(value = "unused")
    @Subscribe
    public void onEvent(Enabled event) {
        showToast(R.string.enabled_gps_provider, this);
    }

    @SuppressWarnings(value = "unused")
    @Subscribe
    public void onEvent(GpsTrouble event) {
        if (event instanceof Disabled) {
            showToast(R.string.disabled_gps_provider, this);
        } else if (event instanceof OutOfService) {
            showToast(R.string.status_out_of_service, this);
        } else if (event instanceof TemporaryUnavailable) {
            showToast(R.string.status_temporary_unavailable, this);
        }
    }

    @SuppressWarnings(value = "unused")
    @Subscribe
    public void onEvent(SessionStart event) {
        showToast(R.string.session_started, this);
    }

    @SuppressWarnings(value = "unused")
    @Subscribe
    public void onEvent(SessionPauseResume event) {
        showToast(R.string.session_paused_resumed, this);
    }

    @SuppressWarnings(value = "unused")
    @Subscribe
    public void onEvent(SessionStopRequest event) {
        SessionStopFragment sessionStopFragment = new SessionStopFragment();
        final FragmentTransaction fTransaction = getFragmentManager().beginTransaction();
        fTransaction.addToBackStack(null);
        sessionStopFragment.show(fTransaction, "dialog");
    }

    @SuppressWarnings(value = "unused")
    @Subscribe
    public void onEvent(SessionStop event) {
        Date startDate = infoService.getStartDate();
        float distance = infoService.getDistance();
        float elapsedSecounds = infoService.getElapsedSecounds();
        double averageSpeed = Helper.metersPerSecoundToKilometersPerHour(distance / elapsedSecounds);
        int averagePace = (int) (distance / elapsedSecounds);

        // Go to report screen
        goReportScreen(this,
                new Ride("Ride " + new Date(),
                        startDate,
                        new Date(),
                        infoService.getElapsedSecounds(),
                        averageSpeed,
                        averagePace,
                        infoService.getDistance()));
    }

    @SuppressWarnings(value = "unused")
    @Subscribe
    public void onEvent(ReloadApplication event) {
        restartActivity(this);
    }

    // ----------- Utilities -----------------------------------------------------------------------

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private Map<Integer, Fragment> map;

        public ScreenSlidePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);

            map = new HashMap<>();
            map.put(0, new PrimaryFragment());
            map.put(1, new SecondaryFragment());
        }

        @Override
        public Fragment getItem(int position) {
            return map.get(position);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    private void connectToService() {
        bindService(new Intent(this, InfoService.class), new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                infoService = ((LocalBinder) iBinder).getService();

                if (!infoService.isServiceRunning()) {
                    infoService.startService(new Intent(MainActivity.this, InfoService.class));
                    return;
                }

                // Running
                if (infoService.isSessionRunning() && !infoService.isSessionPaused()) {
                    post(new SessionRunning());
                }

                // Running
                if (infoService.isSessionRunning() && infoService.isSessionPaused()) {
                    post(new SessionPaused());
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                infoService = null;

            }
        }, BIND_AUTO_CREATE);
    }

    private void stopService() {
        stopService(new Intent(MainActivity.this, InfoService.class));
    }

    private void setCurrentScreenText(int rid) {
        runOnUiThread(() -> ((TextView) findViewById(R.id.currentScreenText)).setText(rid));
    }

    private void setupScreensRotation() {
        boolean rotateScreens = preferencesHelper.get(SETTINGS_ROTATE_SCREENS_KEY, true);
        int freq = preferencesHelper.get(SETTINGS_ROTATE_SCREENS_FREQ_KEY, 5);

        if (rotateScreens) {
            if (executor != null) {
                executor.shutdown();
            }
            executor = new ScheduledThreadPoolExecutor(1);
            executor.scheduleAtFixedRate(() -> {
                int currentItem = viewPager.getCurrentItem();
                viewPager.setCurrentItem(currentItem == 0 ? 1 : 0);
            }, freq, freq, TimeUnit.SECONDS);
        } else {
            if (executor != null) {
                executor.shutdown();
            }
            viewPager.setCurrentItem(0);
        }
    }

    private void setupChangeThemeByCalendar() {
        if (preferencesHelper.get(SETTINGS_THEME_BY_CALENDAR_KEY, true)) {
            executorChangeTheme = new ScheduledThreadPoolExecutor(1);
            executorChangeTheme.scheduleAtFixedRate(() -> {
                // Change theme
                String currentTheme = preferencesHelper.get(SETTINGS_THEME_KEY, Day.name());

                if (inDaylightTime()) {
                    if (Night.name().equals(currentTheme)) {
                        switchThemeTo(Day);
                    }
                } else {
                    if (Day.name().equals(currentTheme)) {
                        switchThemeTo(Night);
                    }
                }
            }, 0, 5, TimeUnit.MINUTES);
        } else {
            if (executorChangeTheme != null) {
                executorChangeTheme.shutdown();
            }
        }
    }

    private void switchThemeTo(UIHelper.Theme to) {
        preferencesHelper.setPreferenceByKey(SETTINGS_THEME_KEY, to.name());
        runOnUiThread(() -> showToast(R.string.theme_has_been_changed, this));
        restartActivity(this);
    }

    /**
     * Stop everything and quit
     */
    private void quitApplication() {
        hideNotification(this);
        stopService();
        finish();
    }
}