package ua.com.abakumov.bikecomp;

import android.app.Activity;
import android.app.Fragment;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import java.util.Date;

import de.greenrobot.event.EventBus;
import ua.com.abakumov.bikecomp.domain.Ride;
import ua.com.abakumov.bikecomp.event.SessionStart;
import ua.com.abakumov.bikecomp.event.SessionStop;
import ua.com.abakumov.bikecomp.event.SessionStopRequest;
import ua.com.abakumov.bikecomp.event.gps.Disabled;
import ua.com.abakumov.bikecomp.event.gps.Enabled;
import ua.com.abakumov.bikecomp.fragment.AverageSpeedFragment;
import ua.com.abakumov.bikecomp.fragment.ClockFragment;
import ua.com.abakumov.bikecomp.fragment.DistanceFragment;
import ua.com.abakumov.bikecomp.fragment.ElapsedTimeFragment;
import ua.com.abakumov.bikecomp.fragment.HeartRateFragment;
import ua.com.abakumov.bikecomp.fragment.SessionStopFragment;
import ua.com.abakumov.bikecomp.fragment.SpeedFragment;
import ua.com.abakumov.bikecomp.service.InfoService;
import ua.com.abakumov.bikecomp.util.Constants;
import ua.com.abakumov.bikecomp.util.Utils;

import static ua.com.abakumov.bikecomp.util.Constants.BIKECOMP_TAG;
import static ua.com.abakumov.bikecomp.util.Utils.showShortToast;
import static ua.com.abakumov.bikecomp.util.Utils.showToast;


/**
 * Main activity
 * <p>
 * Created by Oleksandr Abakumov on 6/28/15.
 */
public class MainActivity extends Activity {

    private InfoService infoService;

    private PowerManager.WakeLock wakeLock;

    private SharedPreferences defaultSharedPreferences;

    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = (sharedPreferences, key) -> {
        if ("displaySettingsBacklightStrategyKey".equals(key)) {
            setupBacklightStrategy();
        } else if ("displaySettingsThemeKey".equals(key)) {
            setupTheme();
        }

    };


    // ----------- System --------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addFragments();
        startServices();

        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
        if (infoService != null) infoService.runQuietly(false);

        setupBacklightStrategy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(BIKECOMP_TAG, "++++++++++++++++++ Main screen has been shown ++++++++++++++++++");
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        infoService.runQuietly(true);

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
        showShortToast(R.string.session_started, getApplicationContext());
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
        showShortToast(R.string.session_stopped, getApplicationContext());

        Date startDate = infoService.getStartDate();
        float distance = infoService.getDistance();
        float elapsedTime = infoService.getElapsedTime();
        double averageSpeed = Utils.metersPerSecoundToKilometersPerHour(distance / elapsedTime);
        int averagePace = (int) (distance / elapsedTime);

        Intent intent = new Intent(MainActivity.this, ReportActivity.class);
        intent.putExtra(Ride.class.getCanonicalName(),
                new Ride("New ride", startDate, new Date(), infoService.getElapsedTime(), averageSpeed,
                        averagePace, infoService.getDistance()));
        startActivity(intent);
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

    private void addFragments() {
        Fragment speedFragment = new SpeedFragment();
        Fragment averageSpeedFragment = new AverageSpeedFragment();
        Fragment clockFragment = new ClockFragment();
        Fragment elapsedTimeFragment = new ElapsedTimeFragment();
        Fragment hrFragment = new HeartRateFragment(); // todo
        Fragment distanceFragment = new DistanceFragment();

        getFragmentManager().beginTransaction()
                .add(R.id.mainArea, speedFragment)
                .add(R.id.mainArea, averageSpeedFragment)
                .add(R.id.mainArea, clockFragment)
                .add(R.id.mainArea, elapsedTimeFragment)
                .add(R.id.mainArea, distanceFragment)
                .commit();
    }

    private void setupBacklightStrategy() {
        String val = defaultSharedPreferences.getString("displaySettingsBacklightStrategyKey", "ALWAYS_ON_NORMAL");

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
        wakeLock = pm.newWakeLock(levelAndFlags, BIKECOMP_TAG);
        wakeLock.acquire();
    }

    private void setupTheme() {
        String val = defaultSharedPreferences.getString("displaySettingsThemeKey", "DAY");

        switch (val) {
            case "DAY":
                Log.d(BIKECOMP_TAG, "Set daily theme");
                break;
            case "NIGHT":
                Log.d(BIKECOMP_TAG, "Set nightly theme");
                break;
            default:
                throw new IllegalArgumentException("Unknown theme");
        }
    }

}
