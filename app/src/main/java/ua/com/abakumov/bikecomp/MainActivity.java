package ua.com.abakumov.bikecomp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import de.greenrobot.event.EventBus;
import ua.com.abakumov.bikecomp.event.SessionStart;
import ua.com.abakumov.bikecomp.event.SessionStop;
import ua.com.abakumov.bikecomp.event.gps.Disabled;
import ua.com.abakumov.bikecomp.event.gps.Enabled;
import ua.com.abakumov.bikecomp.fragment.AverageSpeedFragment;
import ua.com.abakumov.bikecomp.fragment.ButtonsFragment;
import ua.com.abakumov.bikecomp.fragment.ClockFragment;
import ua.com.abakumov.bikecomp.fragment.DistanceFragment;
import ua.com.abakumov.bikecomp.fragment.ElapsedTimeFragment;
import ua.com.abakumov.bikecomp.fragment.HeartRateFragment;
import ua.com.abakumov.bikecomp.fragment.SpeedFragment;
import ua.com.abakumov.bikecomp.service.GpsService;

import static ua.com.abakumov.bikecomp.Utils.showShortToast;
import static ua.com.abakumov.bikecomp.Utils.showToast;


public class MainActivity extends Activity {

    private EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        addFragments();
    }

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
    public void onEvent(SessionStop event) {
        showShortToast(R.string.session_stopped, getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();

        startService(new Intent(this, GpsService.class));
    }

    @Override
    protected void onStop() {
        super.onStop();

        eventBus.unregister(this);

        stopService(new Intent(this, GpsService.class));
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
