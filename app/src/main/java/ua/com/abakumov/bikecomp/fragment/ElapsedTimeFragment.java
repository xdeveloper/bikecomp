package ua.com.abakumov.bikecomp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.event.NewElapsedSecounds;
import ua.com.abakumov.bikecomp.event.SessionStop;
import ua.com.abakumov.bikecomp.util.Utils;

/*
 * Created by oabakumov on 26.06.2015.
 */
public class ElapsedTimeFragment extends android.support.v4.app.Fragment {

    private EventBus eventBus;

    private long elapsedTime;

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
    public void onEvent(NewElapsedSecounds event) {
        elapsedTime = event.getElapsedSecounds();

        updateUi();
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStop event) {
        this.elapsedTime = 0;

        updateUi();
    }

    // ----------- Utilities -----------------------------------------------------------------------

    private void updateUi() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView elapsedTimeTextView = (TextView) getActivity().findViewById(R.id.elapsedTimeTextView);
                elapsedTimeTextView.setText(Utils.formatElapsedTime(elapsedTime));
            }
        });
    }
}