package ua.com.abakumov.bikecomp;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import ua.com.abakumov.bikecomp.fragment.AverageSpeedFragment;
import ua.com.abakumov.bikecomp.fragment.ButtonsFragment;
import ua.com.abakumov.bikecomp.fragment.ClockFragment;
import ua.com.abakumov.bikecomp.fragment.DistanceFragment;
import ua.com.abakumov.bikecomp.fragment.ElapsedTimeFragment;
import ua.com.abakumov.bikecomp.fragment.HeartRateFragment;
import ua.com.abakumov.bikecomp.fragment.SpeedFragment;
import ua.com.abakumov.bikecomp.service.GpsService;

import static ua.com.abakumov.bikecomp.Actions.BROADCAST_ACTION;
import static ua.com.abakumov.bikecomp.Actions.PARCEL_NAME;
import static ua.com.abakumov.bikecomp.Actions.SESSION_START;
import static ua.com.abakumov.bikecomp.Utils.showShortToast;
import static ua.com.abakumov.bikecomp.Utils.showToast;


public class MainActivity extends Activity {

    private Intent gpsServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gpsServiceIntent = new Intent(this, GpsService.class);

        addFragments();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // GPS provider service
        startService(gpsServiceIntent);

        subscribeBroadcastEvents();
    }

    @Override
    protected void onStop() {
        super.onStop();

        stopService(gpsServiceIntent);
    }

    private void subscribeBroadcastEvents() {

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getStringExtra(PARCEL_NAME);

                if (action.equals(SESSION_START)) {
                    showShortToast(R.string.session_started, getApplicationContext());
                }

                if (action.equals(Actions.SESSION_STOP)) {
                    showShortToast(R.string.session_stopped, getApplicationContext());
                }

                // On - Off
                if (action.equals(Actions.GPS_PROVIDER_ENABLED)) {
                    showToast(R.string.enabled_gps_provider, getApplicationContext());
                }
                if (action.equals(Actions.GPS_PROVIDER_DISABLED)) {
                    showToast(R.string.disabled_gps_provider, getApplicationContext());
                }

            }
        }, new IntentFilter(BROADCAST_ACTION));
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.v(Constants.BIKECOMP_TAG, "Main activity resumed");
    }

    private void addFragments() {
        // Add some fragments
        Fragment speedFragment = new SpeedFragment();
        Fragment averageSpeedFragment = new AverageSpeedFragment();
        Fragment clockFragment = new ClockFragment();
        Fragment elapsedTimeFragment = new ElapsedTimeFragment();
        Fragment hrFragment = new HeartRateFragment();
        Fragment distanceFragment = new DistanceFragment();
        Fragment buttonsFragment = new ButtonsFragment();

        getFragmentManager().beginTransaction()
                .add(R.id.mainLayout, speedFragment)
                .add(R.id.mainLayout, averageSpeedFragment)
                //.add(R.id.mainLayout, clockFragment)
                .add(R.id.mainLayout, elapsedTimeFragment)
                .add(R.id.mainLayout, distanceFragment)
                .add(R.id.mainLayout, buttonsFragment)
                .show(buttonsFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
