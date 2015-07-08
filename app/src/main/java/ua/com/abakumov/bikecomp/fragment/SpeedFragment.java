package ua.com.abakumov.bikecomp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import ua.com.abakumov.bikecomp.Constants;
import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.event.gps.LocationProviderAvailableEvent;
import ua.com.abakumov.bikecomp.event.gps.LocationProviderDisabledEvent;
import ua.com.abakumov.bikecomp.event.gps.LocationProviderEnabledEvent;
import ua.com.abakumov.bikecomp.event.gps.LocationProviderIsOutOfServiceEvent;
import ua.com.abakumov.bikecomp.event.gps.LocationProviderLocationChangedEvent;
import ua.com.abakumov.bikecomp.event.gps.LocationProviderTemporaryUnavailableEvent;

import static ua.com.abakumov.bikecomp.Utils.formatSpeed;


/**
 * Fragment that shows speed on the screen
 * <p/>
 * Created by oabakumov on 26.06.2015.
 */
public class SpeedFragment extends Fragment {

    private EventBus eventBus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.speed_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(LocationProviderEnabledEvent event) {
        gpsEnabled(true);
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(LocationProviderDisabledEvent event) {
        gpsEnabled(false);
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(LocationProviderAvailableEvent event) {
        gpsAvailable(true);
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(LocationProviderTemporaryUnavailableEvent event) {
        gpsAvailable(false);
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(LocationProviderIsOutOfServiceEvent event) {
        gpsAvailable(false);
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(LocationProviderLocationChangedEvent event) {
        double kmphSpeed = event.getKmphSpeed();
        Log.v(Constants.BIKECOMP_TAG, "Location received:" + String.valueOf(kmphSpeed));
        ((TextView) getActivity().findViewById(R.id.speedTextView)).setText(formatSpeed(kmphSpeed));
    }

    private void gpsEnabled(boolean enabled) {
        ImageView satellite = (ImageView) getActivity().findViewById(R.id.gpsSateliteImageView);
        satellite.setImageResource(R.drawable.gps_satellite);
        satellite.setImageAlpha(enabled ? 100 : 60);
    }

    private void gpsAvailable(boolean available) {
        ImageView satellite = (ImageView) getActivity().findViewById(R.id.gpsSateliteImageView);
        satellite.setImageResource(R.drawable.gps_satellite_green);
        satellite.setImageAlpha(available ? 100 : 60);
    }

    @Override
    public void onStop() {
        eventBus.unregister(this);

        super.onStop();
    }

}
