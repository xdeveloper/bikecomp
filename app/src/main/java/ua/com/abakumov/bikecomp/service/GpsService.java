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
import ua.com.abakumov.bikecomp.Actions;
import ua.com.abakumov.bikecomp.Constants;
import ua.com.abakumov.bikecomp.event.gps.LocationProviderAvailableEvent;
import ua.com.abakumov.bikecomp.event.gps.LocationProviderDisabledEvent;
import ua.com.abakumov.bikecomp.event.gps.LocationProviderEnabledEvent;
import ua.com.abakumov.bikecomp.event.gps.LocationProviderIsOutOfServiceEvent;
import ua.com.abakumov.bikecomp.event.gps.LocationProviderTemporaryUnavailableEvent;

import static ua.com.abakumov.bikecomp.Actions.BROADCAST_ACTION;
import static ua.com.abakumov.bikecomp.Actions.PARCEL_NAME;
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
                sendBroadcast(new Intent(BROADCAST_ACTION)
                        .putExtra(PARCEL_NAME, Actions.GPS_LOCATION_CHANGED)
                        .putExtra(Actions.GPS_LOCATION_CHANGED_DATA, location));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                switch (status) {
                    case LocationProvider.AVAILABLE:
                        eventBus.post(new LocationProviderAvailableEvent());
                        break;

                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        eventBus.post(new LocationProviderTemporaryUnavailableEvent());
                        break;

                    case LocationProvider.OUT_OF_SERVICE:
                        eventBus.post(new LocationProviderIsOutOfServiceEvent());
                        break;

                    default:
                        Log.w(Constants.BIKECOMP_TAG, "Unknown status");
                        break;
                }
            }

            @Override
            public void onProviderEnabled(String provider) {
                eventBus.post(new LocationProviderEnabledEvent());
            }

            @Override
            public void onProviderDisabled(String provider) {
                eventBus.post(new LocationProviderDisabledEvent());
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
    }
}
