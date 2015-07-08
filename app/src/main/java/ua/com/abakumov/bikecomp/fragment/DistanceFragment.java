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
import ua.com.abakumov.bikecomp.event.gps.NewDistance;
import ua.com.abakumov.bikecomp.event.gps.NewLocation;
import ua.com.abakumov.bikecomp.event.SessionStart;
import ua.com.abakumov.bikecomp.event.SessionStop;

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

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStart event) {
        sessionStarted = true;
        resetDistance();
        calcDistance(0);
        uiUpdateDistance();
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStop event) {
        sessionStarted = false;
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(NewLocation event) {
        calcDistance(event.getMpsSpeed());
        uiUpdateDistance();

        eventBus.post(new NewDistance(distance));
    }

    @Override
    public void onStop() {
        eventBus.unregister(this);

        super.onStop();
    }

    private void calcDistance(float speed) {
        distance += speed;
    }

    private void resetDistance() {
        distance = 0;
    }

    private void uiUpdateDistance() {
        ((TextView) getActivity().findViewById(R.id.distanceTextView)).setText(Utils.formatDistance(distance));
    }


}
