package ua.com.abakumov.bikecomp.fragment.indicators;

import org.greenrobot.eventbus.Subscribe;

import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.event.SessionStop;
import ua.com.abakumov.bikecomp.event.gps.NewDistance;

import static ua.com.abakumov.bikecomp.util.helper.Helper.formatDistance;
import static ua.com.abakumov.bikecomp.util.helper.Helper.metersToKilometers;

/**
 * Shows distance on the screen
 * <p>
 * Created by oabakumov on 26.06.2015.
 */
public class DistanceFragment extends IndicatorFragment {

    //  Meters
    private float distance;

    // ----------- Events handling -----------------------------------------------------------------

    @SuppressWarnings(value = "unused")
    @Subscribe
    public void onEvent(NewDistance event) {
        distance = event.getDistance();

        updateUI();
    }

    @SuppressWarnings(value = "unused")
    @Subscribe
    public void onEvent(SessionStop event) {
        distance = 0;

        updateUI();
    }


    // ----------- Utilities -----------------------------------------------------------------------

    @Override
    protected int getLayoutRid() {
        return R.layout.fragment_distance;
    }

    @Override
    protected int getIndicatorNameRid() {
        return R.string.distance;
    }

    @Override
    protected int getMeasurementRid() {
        return R.string.km;
    }


    @Override
    protected int getRootId() {
        return R.id.distance_fragment_id;
    }

    @Override
    protected String getIndicatorText() {
        return formatDistance(metersToKilometers(distance));
    }

}
