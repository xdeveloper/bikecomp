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

import ua.com.abakumov.bikecomp.Actions;
import ua.com.abakumov.bikecomp.Constants;

import static ua.com.abakumov.bikecomp.Actions.BROADCAST_ACTION;
import static ua.com.abakumov.bikecomp.Actions.GPS_PROVIDER_AVAILABLE;
import static ua.com.abakumov.bikecomp.Actions.GPS_PROVIDER_DISABLED;
import static ua.com.abakumov.bikecomp.Actions.GPS_PROVIDER_ENABLED;
import static ua.com.abakumov.bikecomp.Actions.GPS_PROVIDER_OUT_OF_SERVICE;
import static ua.com.abakumov.bikecomp.Actions.PARCEL_NAME;
import static ua.com.abakumov.bikecomp.Constants.BIKECOMP_TAG;

/**
 * <Class Name and Purpose>
 * <p/>
 * Created by Oleksandr Abakumov on 7/6/15.
 */
public class GpsService extends Service {

    public void onCreate() {
        super.onCreate();
        Log.v(BIKECOMP_TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(BIKECOMP_TAG, "onStartCommand");

        task();

        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
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
    private void task() {

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
                    case LocationProvider.AVAILABLE: {
                        sendBroadcast(new Intent(BROADCAST_ACTION)
                                .putExtra(PARCEL_NAME, GPS_PROVIDER_AVAILABLE));
                    }

                    break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE: {
                        sendBroadcast(new Intent(BROADCAST_ACTION)
                                .putExtra(PARCEL_NAME, Actions.GPS_PROVIDER_TEMPORARILY_UNAVAILABLE));
                    }

                    break;
                    case LocationProvider.OUT_OF_SERVICE: {
                        sendBroadcast(new Intent(BROADCAST_ACTION)
                                .putExtra(PARCEL_NAME, GPS_PROVIDER_OUT_OF_SERVICE));
                    }
                    break;
                    default:
                        Log.w(Constants.BIKECOMP_TAG, "Unknown status");
                        break;
                }
            }

            @Override
            public void onProviderEnabled(String provider) {
                sendBroadcast(new Intent(BROADCAST_ACTION)
                        .putExtra(PARCEL_NAME, GPS_PROVIDER_ENABLED));
            }

            @Override
            public void onProviderDisabled(String provider) {
                sendBroadcast(new Intent(BROADCAST_ACTION)
                        .putExtra(PARCEL_NAME, GPS_PROVIDER_DISABLED));
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);

    }
}
