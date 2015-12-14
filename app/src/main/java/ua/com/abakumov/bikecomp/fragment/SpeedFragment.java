package ua.com.abakumov.bikecomp.fragment;

import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.event.gps.GpsTrouble;
import ua.com.abakumov.bikecomp.event.gps.NewLocation;

import static android.util.Log.v;
import static java.lang.String.valueOf;
import static ua.com.abakumov.bikecomp.util.Constants.TAG;
import static ua.com.abakumov.bikecomp.util.Utils.formatSpeed;


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
    public void onEvent(NewLocation event) {
        speed = event.getKmphSpeed();
        v(TAG, "[ Speed Fragment ] Location has been received, speed is " + valueOf(speed));
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
