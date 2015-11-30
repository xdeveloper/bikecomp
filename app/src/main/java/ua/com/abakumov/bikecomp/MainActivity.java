package ua.com.abakumov.bikecomp;


import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
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
import android.util.Log;
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
import ua.com.abakumov.bikecomp.fragment.SessionStopFragment;
import ua.com.abakumov.bikecomp.service.InfoService;
import ua.com.abakumov.bikecomp.util.FullscreenThemeDecider;
import ua.com.abakumov.bikecomp.util.ThemeDecider;
import ua.com.abakumov.bikecomp.util.Utils;

import static ua.com.abakumov.bikecomp.util.Constants.TAG;
import static ua.com.abakumov.bikecomp.util.UIUtils.SETTINGS_BACKLIGHT_STRATEGY_KEY;
import static ua.com.abakumov.bikecomp.util.UIUtils.SETTINGS_THEME_KEY;
import static ua.com.abakumov.bikecomp.util.UIUtils.goHome;
import static ua.com.abakumov.bikecomp.util.UIUtils.goReportScreen;
import static ua.com.abakumov.bikecomp.util.UIUtils.hideNotification;
import static ua.com.abakumov.bikecomp.util.UIUtils.setupTheme;
import static ua.com.abakumov.bikecomp.util.UIUtils.showNotification;
import static ua.com.abakumov.bikecomp.util.Utils.showShortToast;
import static ua.com.abakumov.bikecomp.util.Utils.showToast;


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
        if (infoService != null) infoService.runLoudly();

        setupBacklightStrategy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "++++++++++++++++++ Main screen has been shown ++++++++++++++++++");
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        infoService.runQuietly();

        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopServices();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Go to History
        if (id == R.id.action_history) {
            startActivity(new Intent(this, HistoryActivity.class));
        }

        // Go to Settings
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        // Quit application
        if (id == R.id.action_quit) {
            stopServices();
            System.exit(0);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Log.v(TAG, "onBackPressed");
        if (sessionIsRunning) {
            Log.v(TAG, "Session is still running. Go main screen");
            goHome(this);
        } else {
            Log.v(TAG, "Session is stopped. Exit application");
            finish();
        }
    }


    // ----------- Events handling -----------------------------------------------------------------

    @SuppressWarnings(value = "unused")
    public void onEvent(Enabled event) {
        showToast(R.string.enabled_gps_provider, getApplicationContext());
    }


    @SuppressWarnings(value = "unused")
    public void onEvent(Disabled event) {
        showToast(R.string.disabled_gps_provider, getApplicationContext());
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStart event) {
        this.sessionIsRunning = true;

        showShortToast(R.string.session_started, getApplicationContext());

        showNotification(this);
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

        showShortToast(R.string.session_stopped, getApplicationContext());

        hideNotification(this);

        Date startDate = infoService.getStartDate();
        float distance = infoService.getDistance();
        float elapsedTime = infoService.getElapsedTimeTicks();
        double averageSpeed = Utils.metersPerSecoundToKilometersPerHour(distance / elapsedTime);
        int averagePace = (int) (distance / elapsedTime);

        // Go to report screen
        goReportScreen(
                this,
                new Ride("New ride", startDate, new Date(), infoService.getElapsedTimeTicks(), averageSpeed,
                        averagePace, infoService.getDistance()));
    }

    // ----------- Utilities -----------------------------------------------------------------------

    private void startServices() {
        bindService(new Intent(this, InfoService.class), new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                infoService = ((InfoService.LocalBinder) iBinder).getService();
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
        String val = PreferenceManager.getDefaultSharedPreferences(this).getString("displaySettingsBacklightStrategyKey", "ALWAYS_ON_NORMAL");

        switch (val) {
            case "ALWAYS_ON_MAXIMUM":
                acquireWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK);
                break;
            case "ALWAYS_ON_NORMAL":
                acquireWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK);
                break;
            case "SYSTEM_SETTING":
                // Release lock if it was acquired earlier
                if (wakeLock != null && wakeLock.isHeld()) {
                    wakeLock.release();
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown backlight strategy");
        }
    }

    private void acquireWakeLock(int levelAndFlags) {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(levelAndFlags, TAG);
        wakeLock.acquire();
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

}