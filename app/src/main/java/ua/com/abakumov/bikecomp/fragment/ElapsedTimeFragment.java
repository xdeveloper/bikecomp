package ua.com.abakumov.bikecomp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.Utils;
import ua.com.abakumov.bikecomp.event.NewElapsedTime;
import ua.com.abakumov.bikecomp.event.SessionPauseResume;
import ua.com.abakumov.bikecomp.event.SessionStart;
import ua.com.abakumov.bikecomp.event.SessionStop;

/*
 * Created by oabakumov on 26.06.2015.
 */
public class ElapsedTimeFragment extends Fragment {

    private static final long SECOUND = 1000;

    private Timer timer;

    private ElapsedTimeFragmentTask task;

    private boolean paused = false;

    private int counter;


    private EventBus eventBus;
    private class ElapsedTimeFragmentTask extends TimerTask {
        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    counter++;
                    updateUi();

                    eventBus.post(new NewElapsedTime(counter));
                }
            });
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.elapsedtime_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        task = new ElapsedTimeFragmentTask();
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStart event) {
        counter = 0;
        updateUi();
        setupAndLaunchTimer();
    }


    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStop event) {
        timer.cancel();
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionPauseResume event) {

        if (paused) {
            // Resume
            paused = false;
            timer.cancel();
            setupAndLaunchTimer();

        } else {
            paused = true;
            timer.cancel();
        }
    }

    @Override
    public void onStop() {
        eventBus.unregister(this);

        super.onStop();
    }

    private void updateUi() {
        TextView elapsedTimeTextView = (TextView) getActivity().findViewById(R.id.elapsedTimeTextView);
        elapsedTimeTextView.setText(Utils.formatElapsedTime(counter));
    }

    private void setupAndLaunchTimer() {
        timer = new Timer();
        task = new ElapsedTimeFragmentTask();
        timer.schedule(task, SECOUND, SECOUND);
    }
}
