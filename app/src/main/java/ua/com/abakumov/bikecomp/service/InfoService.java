package ua.com.abakumov.bikecomp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.NoSubscriberEvent;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
import ua.com.abakumov.bikecomp.util.helper.UIHelper;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.util.Log.v;
import static ua.com.abakumov.bikecomp.util.Constants.TAG;
import static ua.com.abakumov.bikecomp.util.helper.EventBusHelper.post;
import static ua.com.abakumov.bikecomp.util.helper.LogHelper.information;
import static ua.com.abakumov.bikecomp.util.helper.LogHelper.verbose;

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

    private int elapsedSecounds;

    private boolean runningService;

    private boolean paused = true;

    private boolean stopped = true;

    private LocationManager locationManager;

    private LocationListener locationListener;

    private LocationHolder locationHolder;

    private ScheduledThreadPoolExecutor executor;


    // ----------- System --------------------------------------------------------------------------

    @Override
    public void onCreate() {
        super.onCreate();
        information("Creating ...");

        runningService = false;
        EventBus.getDefault().register(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationHolder = new LocationHolder();
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (!paused) {
                    locationHolder.updateLocation(location);

                    post(new NewDistance(locationHolder.getDistance()));
                }
                post(new NewSpeed(location.getSpeed()));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                switch (status) {
                    case LocationProvider.AVAILABLE:
                        post(new Available());
                        break;

                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        post(new TemporaryUnavailable());
                        break;

                    case LocationProvider.OUT_OF_SERVICE:
                        post(new OutOfService());
                        break;

                    default:
                        verbose("Unknown status");
                        break;
                }
            }

            @Override
            public void onProviderEnabled(String provider) {
                post(new Enabled());
            }

            @Override
            public void onProviderDisabled(String provider) {
                post(new Disabled());
            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        information("InfoService on start");
        runningService = true;
        elapsedSecounds = 0;
        locationManager.requestLocationUpdates(
                GPS_PROVIDER,
                MS_IN_SECOND,
                MINIMAL_DISTANCE_IN_METERS,
                locationListener);
        UIHelper.startInForeground(this);
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void onDestroy() {
        information("Destroying ...");
        locationManager.removeUpdates(locationListener);
        EventBus.getDefault().unregister(this);
        stopTimer();
        runningService = false;
        super.onDestroy();
    }


    // ----------- Custom methods ------------------------------------------------------------------

    /**
     * Get distance so far
     *
     * @return distance
     */
    public float getDistance() {
        return locationHolder.getDistance();
    }

    /**
     * Get elapsed secounds
     *
     * @return secounds
     */
    public int getElapsedSecounds() {
        return elapsedSecounds;
    }

    /**
     * Session start date
     *
     * @return date
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Session is running now
     *
     * @return boolean
     */
    public boolean isSessionRunning() {
        return !stopped;
    }

    /**
     * Session is stopped now
     *
     * @return boolean
     */
    public boolean isSessionStopped() {
        return stopped;
    }

    /**
     * Session is on pause
     *
     * @return boolean
     */
    public boolean isSessionPaused() {
        return paused;
    }

    /**
     * Service is running (not session but only service)
     *
     * @return boolean
     */
    public boolean isServiceRunning() {
        return this.runningService;
    }


    // ----------- Events handling -----------------------------------------------------------------

    @SuppressWarnings(value = "unused")
    @Subscribe
    public void onEvent(SessionStart event) {
        information("Session start");

        elapsedSecounds = 0;
        locationHolder.reset();
        startDate = new Date();
        paused = false;
        stopped = false;

        startTimer();
    }

    @SuppressWarnings(value = "unused")
    @Subscribe
    public void onEvent(SessionStop event) {
        Log.i(TAG, "Session stop");

        paused = true;
        stopped = true;

        stopTimer();
    }

    @SuppressWarnings(value = "unused")
    @Subscribe
    public void onEvent(SessionPauseResume event) {
        Log.i(TAG, "Session pause / resume");

        if (paused) {
            // Resume
            v(TAG, "(Paused). Is going to resume");

            paused = false;
            startTimer();
        } else {
            v(TAG, "(Running). Is going to pause");

            paused = true;
            stopTimer();
        }
    }

    @SuppressWarnings(value = "unused")
    @Subscribe
    public void onEvent(NoSubscriberEvent event) {
        // Ignore
    }

    // ----------- Utilities -----------------------------------------------------------------------


    private void startTimer() {
        executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleAtFixedRate(() -> {
            elapsedSecounds++;
            post(new NewElapsedSecounds(elapsedSecounds));
        }, SECOND, SECOND, TimeUnit.SECONDS);
    }

    private void stopTimer() {
        if (executor == null) {
            return;
        }

        executor.shutdown();
    }
}