package ua.com.abakumov.bikecomp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Date;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.NoSubscriberEvent;
import ua.com.abakumov.bikecomp.event.NewElapsedSecounds;
import ua.com.abakumov.bikecomp.event.SessionPauseResume;
import ua.com.abakumov.bikecomp.event.SessionStart;
import ua.com.abakumov.bikecomp.event.SessionStop;
import ua.com.abakumov.bikecomp.event.gps.Available;
import ua.com.abakumov.bikecomp.event.gps.Disabled;
import ua.com.abakumov.bikecomp.event.gps.Enabled;
import ua.com.abakumov.bikecomp.event.gps.NewDistance;
import ua.com.abakumov.bikecomp.event.gps.NewSpeed;
import ua.com.abakumov.bikecomp.event.gps.OutOfService;
import ua.com.abakumov.bikecomp.event.gps.TemporaryUnavailable;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.util.Log.v;
import static ua.com.abakumov.bikecomp.util.Constants.TAG;
import static ua.com.abakumov.bikecomp.util.LogUtils.information;
import static ua.com.abakumov.bikecomp.util.LogUtils.verbose;

/**
 * Listens location updates and publishes different events
 * <p>
 * Created by Oleksandr Abakumov on 7/13/15.
 */
public class InfoService extends Service {

    private static final float MINIMAL_DISTANCE_IN_METERS = 1;

    private static final long MS_IN_SECOND = 1000;

    private static final long SECOND = 1;

    private Date startDate;

    /**
     * Elapsed time ticks
     */
    private int elapsedTimeTicks;

    /**
     * Distance in meters
     */
    private float distance;

    private Handler handler = new Handler();

    private boolean paused = true;

    private Runnable timerTask;

    private LocationManager locationManager;

    private LocationListener locationListener;

    private LocationHolder locationHolder;

    // ----------- System --------------------------------------------------------------------------

    @Override
    public void onCreate() {
        super.onCreate();

        EventBus.getDefault().register(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationHolder = new LocationHolder();

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationHolder.newLocation(location);

                EventBus.getDefault().post(new NewDistance(locationHolder.latestDistance()));
                EventBus.getDefault().post(new NewSpeed(location.getSpeed()));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                switch (status) {
                    case LocationProvider.AVAILABLE:
                        EventBus.getDefault().post(new Available());
                        break;

                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        EventBus.getDefault().post(new TemporaryUnavailable());
                        break;

                    case LocationProvider.OUT_OF_SERVICE:
                        EventBus.getDefault().post(new OutOfService());
                        break;

                    default:
                        verbose("Unknown status");
                        break;
                }
            }

            @Override
            public void onProviderEnabled(String provider) {
                EventBus.getDefault().post(new Enabled());
            }

            @Override
            public void onProviderDisabled(String provider) {
                EventBus.getDefault().post(new Disabled());
            }
        };

    }

    public IBinder onBind(Intent intent) {
        return new LocalBinder(this);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        information("InfoService on start");

        locationManager.requestLocationUpdates(
                GPS_PROVIDER,
                MS_IN_SECOND,
                MINIMAL_DISTANCE_IN_METERS,
                locationListener);

        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    public void onDestroy() {
        Log.i(TAG, "[InfoService] Destroying ...");

        EventBus.getDefault().unregister(this);
        locationManager.removeUpdates(locationListener);

        super.onDestroy();
    }


    // ----------- Custom methods ------------------------------------------------------------------

    public float getDistance() {
        return distance;
    }

    public int getElapsedTimeTicks() {
        return elapsedTimeTicks;
    }

    public Date getStartDate() {
        return startDate;
    }


    // ----------- Events handling -----------------------------------------------------------------

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStart event) {
        information("Session start");

        elapsedTimeTicks = 0;
        distance = 0;
        startDate = new Date();
        paused = false;

        setupAndLaunchTimer();
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStop event) {
        Log.i(TAG, "Session stop");

        paused = true;

        handler.removeCallbacks(timerTask);
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionPauseResume event) {
        Log.i(TAG, "Session pause / resume");

        if (paused) {
            // Resume
            v(TAG, "(Paused). Is going to resume");

            paused = false;
            handler.removeCallbacks(timerTask);
            setupAndLaunchTimer();
        } else {
            v(TAG, "(Running). Is going to pause");

            paused = true;
            handler.removeCallbacks(timerTask);
        }
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(NoSubscriberEvent event) {
        // Ignore
    }


    // ----------- Utilities -----------------------------------------------------------------------

    private void setupAndLaunchTimer() {
        timerTask = new Runnable() {
            private final long stepInSecounds = SECOND;

            @Override
            public void run() {
                elapsedTimeTicks++;
                EventBus.getDefault().post(new NewElapsedSecounds(elapsedTimeTicks * stepInSecounds));
                handler.postDelayed(timerTask, MS_IN_SECOND);
            }
        };
        handler.postDelayed(timerTask, MS_IN_SECOND);
    }
}