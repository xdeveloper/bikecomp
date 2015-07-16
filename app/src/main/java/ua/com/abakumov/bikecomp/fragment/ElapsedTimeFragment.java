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

/*
 * Created by oabakumov on 26.06.2015.
 */
public class ElapsedTimeFragment extends Fragment {

    private EventBus eventBus;

    // ----------- System --------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_elapsedtime, container, false);
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
    public void onEvent(NewElapsedTime event) {
        updateUi(event.getElapsedTime());
    }


    // ----------- Utilities -----------------------------------------------------------------------

    private void updateUi(final int elapsedTime) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView elapsedTimeTextView = (TextView) getActivity().findViewById(R.id.elapsedTimeTextView);
                elapsedTimeTextView.setText(Utils.formatElapsedTime(elapsedTime));
            }
        });


    }

}
