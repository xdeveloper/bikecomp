package ua.com.abakumov.bikecomp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.Utils;
import ua.com.abakumov.bikecomp.event.NewElapsedTime;
import ua.com.abakumov.bikecomp.event.gps.NewDistance;

/**
 * Shows average speed
 * <p/>
 * Created by oabakumov on 26.06.2015.
 */
public class AverageSpeedFragment extends Fragment {

    private EventBus eventBus;

    private int elapsedTime;

    private float distance;

    private double averageSpeed;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.average_speed_fragment, container, false);
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

    @SuppressWarnings(value = "unused")
    public void onEvent(NewElapsedTime event) {
        this.elapsedTime = event.getElapsedTime();

        reCalculateAverageSpeed();
        updateUi();
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(NewDistance event) {
        this.distance = event.getDistanceInMeters();

        reCalculateAverageSpeed();
        updateUi();
    }

    private void reCalculateAverageSpeed() {
        // No elapsed time yet (prevent ArithmeticException exception "division by zero")
        if (this.elapsedTime == 0) {
            this.averageSpeed = 0;

            return;
        }

        this.averageSpeed = Utils.metersPerSecoundToKilometersPerHour(distance / elapsedTime);
    }

    private void updateUi() {
        ((TextView) getActivity().findViewById(R.id.averageSpeedTextView)).setText(Utils.formatSpeed(averageSpeed));
    }
}
