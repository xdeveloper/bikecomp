package ua.com.abakumov.bikecomp.fragment.indicators;

import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.event.NewElapsedSecounds;
import ua.com.abakumov.bikecomp.event.SessionStop;

import static ua.com.abakumov.bikecomp.util.Utils.formatElapsedTime;

/*
 * Created by oabakumov on 26.06.2015.
 */
public class ElapsedTimeFragment extends IndicatorFragment {

    private long elapsedTime;

    // ----------- Events handling -----------------------------------------------------------------

    @SuppressWarnings(value = "unused")
    public void onEvent(NewElapsedSecounds event) {
        elapsedTime = event.getElapsedSecounds();

        updateUI();
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStop event) {
        this.elapsedTime = 0;

        updateUI();
    }


    @Override
    protected int getLayoutRid() {
        return R.layout.fragment_elapsedtime;
    }

    @Override
    protected int getIndicatorNameRid() {
        return R.string.elapsed_time;
    }

    @Override
    protected int getMeasurementRid() {
        return R.string.nothing;
    }

    @Override
    protected int getRootId() {
        return R.id.elapsed_time_fragment_id;
    }

    @Override
    protected String getIndicatorText() {
        return formatElapsedTime(elapsedTime);
    }
}