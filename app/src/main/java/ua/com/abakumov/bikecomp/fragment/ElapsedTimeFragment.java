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
import ua.com.abakumov.bikecomp.event.Event;
import ua.com.abakumov.bikecomp.event.SessionStartEvent;
import ua.com.abakumov.bikecomp.event.SessionStopEvent;

/*
 * Created by oabakumov on 26.06.2015.
 */
public class ElapsedTimeFragment extends Fragment {

    private static final long SECOUND = 1000;

    private Timer timer;

    private ElapsedTimeFragmentTask task;

    private boolean started;

    private int counter;

    private EventBus eventBus;


    private class ElapsedTimeFragmentTask extends TimerTask {
        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    counter++;
                    updateElapsedTime();
                }
            });
        }
    }

    private void updateElapsedTime() {
        TextView elapsedTimeTextView = (TextView) getActivity().findViewById(R.id.elapsedTimeTextView);
        elapsedTimeTextView.setText(String.valueOf(counter));
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
    public void onEvent(SessionStartEvent event) {
        if (started) {
            return;
        }

        started = true;
        counter = 0;
        updateElapsedTime();

        timer = new Timer();
        task = new ElapsedTimeFragmentTask();
        timer.schedule(task, SECOUND, SECOUND);
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStopEvent event) {
        started = false;

        timer.cancel();
    }

    @Override
    public void onStop() {
        eventBus.unregister(this);

        super.onStop();
    }
}
