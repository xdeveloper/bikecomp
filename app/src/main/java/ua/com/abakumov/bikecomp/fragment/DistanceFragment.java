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

/**
 * Shows distance on the screen
 * <p/>
 * Created by oabakumov on 26.06.2015.
 */
public class DistanceFragment extends Fragment {

    private EventBus eventBus;


    // ----------- System --------------------------------------------------------------------------

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


    @Override
    public void onStop() {
        eventBus.unregister(this);
        super.onStop();
    }


    // ----------- Events handling -----------------------------------------------------------------

    @SuppressWarnings(value = "unused")
    public void onEvent(NewDistance event) {
        uiUpdateDistance(event.getDistanceInMeters());
    }


    // ----------- Utilities -----------------------------------------------------------------------

    private void uiUpdateDistance(float distanceInMeters) {
        ((TextView) getActivity().findViewById(R.id.distanceTextView)).setText(Utils.formatDistance(distanceInMeters));
    }

}
