package ua.com.abakumov.bikecomp.fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import ua.com.abakumov.bikecomp.R;

/*
 * Created by oabakumov on 26.06.2015.
 */
public class ClockFragment extends IndicatorFragment {

    @SuppressWarnings("FieldCanBeLocal")
    private static int SECOUND = 1000;

    private Timer timer;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm.ss", Locale.getDefault());


    // ----------- System --------------------------------------------------------------------------
    protected void afterStart() {
        // Show time immediately
        updateUI();

        // Schedule periodical update
        timer = new Timer();
        timer.schedule(new ClockFragmentTimerTask(), SECOUND, SECOUND);
    }

    protected void beforeStop() {
        timer.cancel();
    }

    @Override
    protected int getRootId() {
        return R.id.clock_fragment_id;
    }

    @Override
    protected String getIndicatorText() {
        return simpleDateFormat.format(Calendar.getInstance().getTime());
    }


    @Override
    protected int getLayoutRid() {
        return R.layout.fragment_clock;
    }

    @Override
    protected int getIndicatorNameRid() {
        return R.string.clock;
    }

    @Override
    protected int getMeasurementRid() {
        return R.string.nothing;
    }

    // ----------- Utilities -----------------------------------------------------------------------

    private class ClockFragmentTimerTask extends TimerTask {
        @Override
        public void run() {
            getActivity().runOnUiThread(() -> updateUI());
        }
    }
}