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

import de.greenrobot.event.EventBus;
import ua.com.abakumov.bikecomp.Constants;
import ua.com.abakumov.bikecomp.event.gps.Available;
import ua.com.abakumov.bikecomp.event.gps.Disabled;
import ua.com.abakumov.bikecomp.event.gps.Enabled;
import ua.com.abakumov.bikecomp.event.gps.OutOfService;
import ua.com.abakumov.bikecomp.event.gps.NewLocation;
import ua.com.abakumov.bikecomp.event.gps.TemporaryUnavailable;

import static ua.com.abakumov.bikecomp.Constants.BIKECOMP_TAG;

/**
 * <Class Name and Purpose>
 * <p/>
 * Created by Oleksandr Abakumov on 7/6/15.
 */
public class GpsService extends Service {

    private EventBus eventBus;

    public void onCreate() {
        super.onCreate();
        Log.v(BIKECOMP_TAG, "onCreate");

        eventBus = EventBus.getDefault();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(BIKECOMP_TAG, "onStartCommand");

        doJob();

        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        eventBus.unregister(this);

        super.onDestroy();
        Log.d(BIKECOMP_TAG, "onDestroy");
    }

    public IBinder onBind(Intent intent) {
        Log.d(BIKECOMP_TAG, "onBind");
        return null;
    }

    /**
     * Work is here
     */
    private void doJob() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                eventBus.post(new NewLocation(location.getSpeed()));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                switch (status) {
                    case LocationProvider.AVAILABLE:
                        eventBus.post(new Available());
                        break;

                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        eventBus.post(new TemporaryUnavailable());
                        break;

                    case LocationProvider.OUT_OF_SERVICE:
                        eventBus.post(new OutOfService());
                        break;

                    default:
                        Log.w(Constants.BIKECOMP_TAG, "Unknown status");
                        break;
                }
            }

            @Override
            public void onProviderEnabled(String provider) {
                eventBus.post(new Enabled());
            }

            @Override
            public void onProviderDisabled(String provider) {
                eventBus.post(new Disabled());
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
    }
}
