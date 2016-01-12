package ua.com.abakumov.bikecomp.fragment.indicators;

import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.event.gps.GpsTrouble;
import ua.com.abakumov.bikecomp.event.gps.NewSpeed;

import static ua.com.abakumov.bikecomp.util.helper.Helper.formatSpeed;


/**
 * Fragment that shows speed on the screen
 * <p>
 * Created by oabakumov on 26.06.2015.
 */
public class SpeedFragment extends IndicatorFragment {

    // Kilometers per hour
    private double speed;

    // ----------- Events handling -----------------------------------------------------------------

    @SuppressWarnings(value = "unused")
    public void onEvent(GpsTrouble event) {
        speed = 0;
        updateUI();
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(NewSpeed event) {
        speed = event.getKmphSpeed();
        updateUI();
    }

    @Override
    protected int getLayoutRid() {
        return R.layout.fragment_speed;
    }

    @Override
    protected int getRootId() {
        return R.id.speed_fragment_id;
    }

    @Override
    protected int getIndicatorNameRid() {
        return R.string.speed;
    }

    @Override
    protected int getMeasurementRid() {
        return R.string.kmh;
    }

    @Override
    public String getIndicatorText() {
        return formatSpeed(speed);
    }
}
