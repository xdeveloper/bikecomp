package ua.com.abakumov.bikecomp.fragment;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ua.com.abakumov.bikecomp.R;

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
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    private void makeUseOfNewLocation(Location location) {
        float speed = location.getSpeed();

        TextView speedTextView = (TextView) getActivity().findViewById(R.id.speedTextView);

        speedTextView.setText(String.valueOf(speed));
    }

}
