package ua.com.abakumov.bikecomp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.greenrobot.event.EventBus;
import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.event.Event;
import ua.com.abakumov.bikecomp.event.SessionStartEvent;
import ua.com.abakumov.bikecomp.event.SessionStopEvent;

/**
 * Shows average speed
 * <p/>
 * Created by oabakumov on 26.06.2015.
 */
public class AverageSpeedFragment extends Fragment {

    private boolean sessionStarted;

    private EventBus eventBus;

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


    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStartEvent event) {
        sessionStarted = true;
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStopEvent event) {
        sessionStarted = false;
    }

}
