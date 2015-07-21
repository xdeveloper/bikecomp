package ua.com.abakumov.bikecomp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Date;

import de.greenrobot.event.EventBus;
import ua.com.abakumov.bikecomp.event.Event;
import ua.com.abakumov.bikecomp.event.NewElapsedTime;
import ua.com.abakumov.bikecomp.event.SessionPauseResume;
import ua.com.abakumov.bikecomp.event.SessionStart;
import ua.com.abakumov.bikecomp.event.SessionStop;
import ua.com.abakumov.bikecomp.event.gps.Available;
import ua.com.abakumov.bikecomp.event.gps.Disabled;
import ua.com.abakumov.bikecomp.event.gps.Enabled;
import ua.com.abakumov.bikecomp.event.gps.NewDistance;
import ua.com.abakumov.bikecomp.event.gps.NewLocation;
import ua.com.abakumov.bikecomp.event.gps.OutOfService;
import ua.com.abakumov.bikecomp.event.gps.TemporaryUnavailable;

import static ua.com.abakumov.bikecomp.util.Constants.BIKECOMP_TAG;

/**
 * Listens location updates and publishes events
 * <p>
 * Created by Oleksandr Abakumov on 7/13/15.
 */
public class InfoService extends Service {

    private boolean runQuietly;

    private static final long SECOND = 1000;

    private Date startDate;

    /**
     * Elapsed time in seconds
     */
    private int elapsedTime;

    /**
     * Distance in meters
     */
    private float distance;

    private Handler handler = new Handler();

    private boolean paused;

    private ElapsedTimeFragmentTask timerTask;

    private LocationManager locationManager;

    private LocationListener locationListener;


    // ----------- System --------------------------------------------------------------------------

    @Override
    public void onCreate() {
        super.onCreate();

        EventBus.getDefault().register(this);

        timerTask = new ElapsedTimeFragmentTask();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                post(new NewLocation(location.getSpeed()));
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
                        Log.w(BIKECOMP_TAG, "Unknown status");
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

    public int onStartCommand(Intent intent, int flags, int startId) {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, SECOND, 1, locationListener);
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        locationManager.removeUpdates(locationListener);
        super.onDestroy();
    }

    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }


    // ----------- Custom methods ------------------------------------------------------------------

    public void runQuietly(boolean runQuietly) {
        this.runQuietly = runQuietly;
    }

    public float getDistance() {
        return distance;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public Date getStartDate() {
        return startDate;
    }

    // ----------- Events handling -----------------------------------------------------------------

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStart event) {
        elapsedTime = 0;
        distance = 0;
        startDate = new Date();

        paused = false;

        setupAndLaunchTimer();
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStop event) {
        paused = true;

        handler.removeCallbacks(timerTask);
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionPauseResume event) {
        if (paused) {
            // Resume
            paused = false;
            handler.removeCallbacks(timerTask);
            setupAndLaunchTimer();
        } else {
            paused = true;
            handler.removeCallbacks(timerTask);
        }
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(NewLocation event) {
        if (paused) {
            // ignore
            return;
        }

        // correct ?
        distance += event.getMpsSpeed();

        post(new NewDistance(distance));
    }


    // ----------- Utilities -----------------------------------------------------------------------


    private void setupAndLaunchTimer() {
        handler.postDelayed(timerTask, SECOND);
    }

    private class ElapsedTimeFragmentTask implements Runnable {
        @Override
        public void run() {
            elapsedTime++;
            post(new NewElapsedTime(elapsedTime));
            handler.postDelayed(timerTask, SECOND);
        }
    }

    public class LocalBinder extends Binder {
        public InfoService getService() {
            return InfoService.this;
        }
    }

    private void post(Event event) {
        if (!runQuietly) {
            EventBus.getDefault().post(event);
        }
    }
}