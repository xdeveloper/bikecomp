package ua.com.abakumov.bikecomp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import ua.com.abakumov.bikecomp.util.Constants;
import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.event.gps.Available;
import ua.com.abakumov.bikecomp.event.gps.Disabled;
import ua.com.abakumov.bikecomp.event.gps.Enabled;
import ua.com.abakumov.bikecomp.event.gps.OutOfService;
import ua.com.abakumov.bikecomp.event.gps.NewLocation;
import ua.com.abakumov.bikecomp.event.gps.TemporaryUnavailable;

import static ua.com.abakumov.bikecomp.util.Utils.formatSpeed;


/**
 * Fragment that shows speed on the screen
 * <p/>
 * Created by oabakumov on 26.06.2015.
 */
public class SpeedFragment extends Fragment {

    private EventBus eventBus;


    // ----------- System --------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_speed, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }

    @Override
    public void onStop() {
        eventBus.unregister(this);
        super.onStop();
    }


    // ----------- Events handling -----------------------------------------------------------------

    @SuppressWarnings(value = "unused")
    public void onEvent(Enabled event) {
        gpsEnabled(true);
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(Disabled event) {
        gpsEnabled(false);
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(Available event) {
        gpsAvailable(true);
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(TemporaryUnavailable event) {
        gpsAvailable(false);
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(OutOfService event) {
        gpsAvailable(false);
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(NewLocation event) {
        double kmphSpeed = event.getKmphSpeed();
        Log.v(Constants.BIKECOMP_TAG, "Location received:" + String.valueOf(kmphSpeed));
        ((TextView) getActivity().findViewById(R.id.speedTextView)).setText(formatSpeed(kmphSpeed));
    }


    // ----------- Utilities -----------------------------------------------------------------------
    private void gpsEnabled(boolean enabled) {
        /*ImageView satellite = (ImageView) getActivity().findViewById(R.id.gpsSateliteImageView);
        satellite.setImageResource(R.drawable.gps_satellite);
        satellite.setImageAlpha(enabled ? 100 : 60);*/
    }

    private void gpsAvailable(boolean available) {
        /*ImageView satellite = (ImageView) getActivity().findViewById(R.id.gpsSateliteImageView);
        satellite.setImageResource(R.drawable.gps_satellite_green);
        satellite.setImageAlpha(available ? 100 : 60);*/
    }

}
