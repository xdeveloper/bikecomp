package ua.com.abakumov.bikecomp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;

import de.greenrobot.event.EventBus;
import ua.com.abakumov.bikecomp.data.Ride;
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

import static ua.com.abakumov.bikecomp.Utils.showShortToast;
import static ua.com.abakumov.bikecomp.Utils.showToast;


/**
 * Main activity
 * <p>
 * Created by Oleksandr Abakumov on 6/28/15.
 */
public class MainActivity extends Activity {

    private InfoService infoService;


    // ----------- System --------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addFragments();
        startServices();
    }

    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
        if (infoService != null) infoService.runQuietly(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(Constants.BIKECOMP_TAG, "++++++++++++++++++ Main screen has been shown ++++++++++++++++++");
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        infoService.runQuietly(true);

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

        float distance = infoService.getDistance();
        float elapsedTime = infoService.getElapsedTime();
        double averageSpeed = Utils.metersPerSecoundToKilometersPerHour(distance / elapsedTime);
        int averagePace = 0; // todo


        Intent intent = new Intent(MainActivity.this, ReportActivity.class);
        intent.putExtra(Ride.class.getCanonicalName(),
                new Ride("New ride", new Date(), infoService.getElapsedTime(), averageSpeed,
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
        // Add some fragments
        Fragment speedFragment = new SpeedFragment();
        Fragment averageSpeedFragment = new AverageSpeedFragment();
        Fragment clockFragment = new ClockFragment();
        Fragment elapsedTimeFragment = new ElapsedTimeFragment();
        Fragment hrFragment = new HeartRateFragment();
        Fragment distanceFragment = new DistanceFragment();

        getFragmentManager().beginTransaction()
                .add(R.id.mainArea, speedFragment)
                .add(R.id.mainArea, averageSpeedFragment)
                .add(R.id.mainArea, clockFragment)
                .add(R.id.mainArea, elapsedTimeFragment)
                .add(R.id.mainArea, distanceFragment)
                .commit();
    }

}
