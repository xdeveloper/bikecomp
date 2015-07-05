package ua.com.abakumov.bikecomp.fragment;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Formatter;

import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.Utils;

import static ua.com.abakumov.bikecomp.Utils.formatSpeed;
import static ua.com.abakumov.bikecomp.Utils.metersPerSecoundToKilometersPerHour;
import static ua.com.abakumov.bikecomp.Utils.showToast;

/**
 * <Class Name>
 * Created by oabakumov on 26.06.2015.
 */
public class SpeedFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.speed_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        calcSpeed();
    }

    private void calcSpeed() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
                showToast(R.string.catch_gps_signal, getActivity().getApplicationContext());
            }

            public void onProviderDisabled(String provider) {
                showToast(R.string.lost_gps_signal, getActivity().getApplicationContext());
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, locationListener);
    }

    private void makeUseOfNewLocation(Location location) {
        Log.v("Location received:", String.valueOf(location.getSpeed()));
        TextView speedTextView = (TextView) getActivity().findViewById(R.id.speedTextView);
        speedTextView.setText(formatSpeed(metersPerSecoundToKilometersPerHour(location.getSpeed())));
    }

}
