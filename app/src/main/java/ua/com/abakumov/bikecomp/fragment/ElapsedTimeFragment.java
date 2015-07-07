package ua.com.abakumov.bikecomp.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import ua.com.abakumov.bikecomp.Actions;
import ua.com.abakumov.bikecomp.R;

/*
 * Created by oabakumov on 26.06.2015.
 */
public class ElapsedTimeFragment extends Fragment {

    private static final long SECOUND = 1000;
    private Timer timer;
    private ElapsedTimeFragmentTask task;
    private boolean started;
    private int counter;

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

        task = new ElapsedTimeFragmentTask();

        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String stringExtra = intent.getStringExtra(Actions.PARCEL_NAME);

                // Start !
                if (stringExtra.equals(Actions.SESSION_START)) {

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


                // Stop !
                if (stringExtra.equals(Actions.SESSION_STOP)) {
                    started = false;

                    timer.cancel();
                }

            }
        }, new IntentFilter(Actions.BROADCAST_ACTION));
    }


}
