package ua.com.abakumov.bikecomp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.event.NewElapsedSecounds;
import ua.com.abakumov.bikecomp.event.SessionStart;
import ua.com.abakumov.bikecomp.event.SessionStop;
import ua.com.abakumov.bikecomp.event.gps.NewDistance;
import ua.com.abakumov.bikecomp.util.Utils;

import static android.util.Log.v;
import static java.lang.String.valueOf;
import static ua.com.abakumov.bikecomp.util.Constants.TAG;
import static ua.com.abakumov.bikecomp.util.Utils.formatSpeed;

/**
 * Shows average speed
 * <p>
 * Created by oabakumov on 26.06.2015.
 */
public class AverageSpeedFragment extends android.support.v4.app.Fragment {

    private EventBus eventBus;

    private long elapsedTime;

    private float distance;

    private double averageSpeed;


    // ----------- System --------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_average_speed, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        updateAverageSpeed();
    }

    @Override
    public void onStop() {
        eventBus.unregister(this);

        super.onStop();
    }


    // ----------- Events handling -----------------------------------------------------------------

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStart event) {
        this.averageSpeed = 0;

        updateUi();
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(NewElapsedSecounds event) {
        this.elapsedTime = event.getElapsedSecounds();

        v(TAG, "[ Av. Speed Fragment ] New elapsed time has been received, elapsed time (seconds) is " +
                valueOf(this.elapsedTime));

        updateAverageSpeed();
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(NewDistance event) {
        this.distance = event.getDistanceInMeters();

        v(TAG, "[ Av. Speed Fragment ] New distance has been received, distance (meters) is " +
                valueOf(this.distance));

        updateAverageSpeed();
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStop event) {
        this.distance = 0;
        this.elapsedTime = 0;

        updateAverageSpeed();
    }


    // ----------- Utilities -----------------------------------------------------------------------

    private void updateAverageSpeed() {
        reCalculateAverageSpeed();
        updateUi();
    }

    private void reCalculateAverageSpeed() {
        v(TAG, "[ Av. Speed Fragment ] Recalculate average speed ...");
        v(TAG, "[ Av. Speed Fragment ] Distance: " + distance + ", elapsed time: " + elapsedTime);

        // No elapsed time yet (prevent ArithmeticException exception "division by zero")
        if (this.elapsedTime == 0) {
            this.averageSpeed = 0;
        } else {
            this.averageSpeed = Utils.metersPerSecoundToKilometersPerHour(distance / elapsedTime);
        }

        v(TAG, "[ Av. Speed Fragment ] Average speed is " + valueOf(this.averageSpeed));
    }

    private void updateUi() {
        getActivity().runOnUiThread(() -> {
            ((TextView) getActivity().findViewById(R.id.averageSpeedTextView)).setText(formatSpeed(this.averageSpeed));
        });
    }
}