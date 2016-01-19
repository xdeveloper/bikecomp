package ua.com.abakumov.bikecomp;


import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;
import ua.com.abakumov.bikecomp.domain.Ride;
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
import ua.com.abakumov.bikecomp.util.theme.FullscreenThemeDecider;
import ua.com.abakumov.bikecomp.util.theme.ThemeDecider;

import static ua.com.abakumov.bikecomp.PreferencesHelper.getPreferenceByKey;
import static ua.com.abakumov.bikecomp.R.id.action_history;
import static ua.com.abakumov.bikecomp.R.id.action_quit;
import static ua.com.abakumov.bikecomp.R.id.action_settings;
import static ua.com.abakumov.bikecomp.util.Constants.SCREEN_KEEP_ON;
import static ua.com.abakumov.bikecomp.util.Constants.SCREEN_MIDDLE;
import static ua.com.abakumov.bikecomp.util.Constants.SCREEN_SYSTEM_DEFAULT;
import static ua.com.abakumov.bikecomp.util.Constants.SETTINGS_BACKLIGHT_STRATEGY_KEY;
import static ua.com.abakumov.bikecomp.util.helper.LogHelper.*;
import static ua.com.abakumov.bikecomp.util.helper.ScreenHelper.*;
import static ua.com.abakumov.bikecomp.util.helper.ScreenHelper.BrightnessLevel.MAX;
import static ua.com.abakumov.bikecomp.util.helper.ScreenHelper.BrightnessLevel.MIDDLE;
import static ua.com.abakumov.bikecomp.util.helper.ScreenHelper.BrightnessLevel.AUTO;
import static ua.com.abakumov.bikecomp.util.helper.ScreenHelper.ScreenLock.ALWAYS_ON;
import static ua.com.abakumov.bikecomp.util.helper.ScreenHelper.ScreenLock.SYS_DEFAULT;
import static ua.com.abakumov.bikecomp.util.helper.UIHelper.*;


/**
 * Main activity
 * <p>
 * Created by Oleksandr Abakumov on 6/28/15.
 */
public class MainActivity extends FragmentActivity {

    private boolean sessionIsRunning;

    private InfoService infoService;

    private ViewPager viewPager;

    private ScreenSlidePagerAdapter viewPagerAdapter;

    private PowerManager.WakeLock wakeLock;

    private ThemeDecider themeDecider = new FullscreenThemeDecider();

    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (SETTINGS_BACKLIGHT_STRATEGY_KEY.equals(key)) {
                setupBacklightStrategy();
            } else if (SETTINGS_THEME_KEY.equals(key)) {
                setupTheme(MainActivity.this, MainActivity.class, themeDecider);
            }
        }
    };


    // ----------- System --------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
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

        setCurrentScreenText(R.string.primaryScreen);
        startServices();
    }

    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);

        setupBacklightStrategy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        verbose("Main screen has been shown");
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopServices();

        hideNotification(this);
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

    @Override
    public void onBackPressed() {
        verbose("Back pressed");

        if (sessionIsRunning) {
            verbose("Session is still running. Go main screen");
            goHome(this);
        } else {
            verbose("Session is stopped. Exit application");
            finish();
        }
    }

    // ----------- Events handling -----------------------------------------------------------------


    @SuppressWarnings(value = "unused")
    public void onEvent(Enabled event) {
        showToast(R.string.enabled_gps_provider, this);
    }

    @SuppressWarnings(value = "unused")
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
    public void onEvent(SessionStart event) {
        this.sessionIsRunning = true;

        showToast(R.string.session_started, this);
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStopRequest event) {
        SessionStopFragment sessionStopFragment = new SessionStopFragment();
        final FragmentTransaction fTransaction = getFragmentManager().beginTransaction();
        fTransaction.addToBackStack(null);
        sessionStopFragment.show(fTransaction, "dialog");
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStop event) {
        this.sessionIsRunning = false;

        showToast(R.string.session_stopped, this);

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

    // ----------- Utilities -----------------------------------------------------------------------

    private void startServices() {
        bindService(new Intent(this, InfoService.class), new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                infoService = ((LocalBinder) iBinder).getService();
                infoService.startService(new Intent(MainActivity.this, InfoService.class));
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                infoService = null;

            }
        }, BIND_AUTO_CREATE);
    }

    private void stopServices() {
        stopService(new Intent(MainActivity.this, InfoService.class));
    }

    private void setupBacklightStrategy() {
        final String backlightSetting =
                getPreferenceByKey(SETTINGS_BACKLIGHT_STRATEGY_KEY, SCREEN_SYSTEM_DEFAULT, this);

        switch (backlightSetting) {
            case SCREEN_KEEP_ON:
                verbose("Maximum brightness always on");
                setScreenLock(ALWAYS_ON, getWindow());
                setBrightness(MAX, getWindow());
                break;

            case SCREEN_MIDDLE:
                verbose("Middle brightness always on");
                setScreenLock(ALWAYS_ON, getWindow());
                setBrightness(MIDDLE, getWindow());
                break;

            case SCREEN_SYSTEM_DEFAULT:
                verbose("Middle brightness system default");
                setScreenLock(SYS_DEFAULT, getWindow());
                setBrightness(AUTO, getWindow());
                break;

            default:
                warning("Unknown value: " + backlightSetting);
        }
    }

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

    private void setCurrentScreenText(int rid) {
        runOnUiThread(() -> ((TextView) findViewById(R.id.currentScreenText)).setText(rid));
    }

    private void quitApplication() {
        hideNotification(this);
        stopServices();
        finish();
    }
}