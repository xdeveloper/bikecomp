package ua.com.abakumov.bikecomp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import ua.com.abakumov.bikecomp.R;

/*
 * Created by oabakumov on 26.06.2015.
 */
public class ClockFragment extends Fragment {

    private Timer timer;

    private ClockFragmentTimerTask task;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());


    private class ClockFragmentTimerTask extends TimerTask {
        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView textView = (TextView) getActivity().findViewById(R.id.clockTextView);
                    textView.setText(simpleDateFormat.format(Calendar.getInstance().getTime()));
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        timer = new Timer();
        task = new ClockFragmentTimerTask();
        timer.schedule(task, 1000, 1000);
    }

    @Override
    public void onStop() {
        super.onStop();

        timer.cancel();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.clock_fragment, container, false);
    }

}
