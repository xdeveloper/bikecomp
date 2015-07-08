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
import ua.com.abakumov.bikecomp.event.Event;
import ua.com.abakumov.bikecomp.event.gps.LocationProviderLocationChangedEvent;
import ua.com.abakumov.bikecomp.event.SessionStartEvent;
import ua.com.abakumov.bikecomp.event.SessionStopEvent;

/**
 * Shows distance on the screen
 * <p/>
 * Created by oabakumov on 26.06.2015.
 */
public class DistanceFragment extends Fragment {

    private float distance;

    private boolean sessionStarted;

    private EventBus eventBus;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.distance_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }


    private void resetDistance() {
        distance = 0;
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStartEvent event) {
        sessionStarted = true;
        resetDistance();
        calcDistance(0);
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStopEvent event) {
        sessionStarted = false;
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(LocationProviderLocationChangedEvent event) {
        calcDistance(event.getMpsSpeed());
    }

    private void calcDistance(float speed) {
        distance += speed;

        ((TextView) getActivity().findViewById(R.id.distanceTextView)).setText(Utils.formatDistance(distance));
    }

    @Override
    public void onStop() {
        eventBus.unregister(this);

        super.onStop();
    }


}
