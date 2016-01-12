package ua.com.abakumov.bikecomp.fragment.indicators;

import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.event.NewElapsedSecounds;
import ua.com.abakumov.bikecomp.event.SessionStart;
import ua.com.abakumov.bikecomp.event.SessionStop;
import ua.com.abakumov.bikecomp.event.gps.NewDistance;
import ua.com.abakumov.bikecomp.util.helper.Helper;

import static android.util.Log.v;
import static java.lang.String.valueOf;
import static ua.com.abakumov.bikecomp.util.helper.LogHelper.verbose;
import static ua.com.abakumov.bikecomp.util.helper.Helper.formatSpeed;

/**
 * Shows average speed
 * <p>
 * Created by oabakumov on 26.06.2015.
 */
public class AverageSpeedFragment extends IndicatorFragment {

    private long elapsedTime;

    private float distance;

    private double averageSpeed;


    // ----------- System --------------------------------------------------------------------------

    @Override
    protected int getLayoutRid() {
        return R.layout.fragment_average_speed;
    }

    @Override
    protected int getIndicatorNameRid() {
        return R.string.average_speed;
    }

    @Override
    protected int getMeasurementRid() {
        return R.string.kmh;
    }


    @Override
    protected int getRootId() {
        return R.id.avs_fragment_id;
    }

    @Override
    protected String getIndicatorText() {
        return formatSpeed(this.averageSpeed);
    }


    // ----------- Events handling -----------------------------------------------------------------

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStart event) {
        resetAll();
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(NewElapsedSecounds event) {
        this.elapsedTime = event.getElapsedSecounds();

        updateAverageSpeed();
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(NewDistance event) {
        this.distance = event.getDistance();

        verbose("New distance has been received " + valueOf(this.distance));

        updateAverageSpeed();
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStop event) {
        resetAll();
    }


    // ----------- Utilities -----------------------------------------------------------------------

    private void resetAll() {
        this.averageSpeed = 0;
        this.distance = 0;
        this.elapsedTime = 0;

        updateUI();
    }

    private void updateAverageSpeed() {
        reCalculateAverageSpeed();
        updateUI();
    }

    private void reCalculateAverageSpeed() {
        verbose("Recalculate average speed");
        verbose("Distance: " + distance + ", elapsed time: " + elapsedTime);

        if (this.elapsedTime == 0 || this.distance == 0) {
            this.averageSpeed = 0;
        } else {
            this.averageSpeed = Helper.metersPerSecoundToKilometersPerHour(distance / elapsedTime);
        }

        verbose("Average speed is " + valueOf(this.averageSpeed));
    }
}