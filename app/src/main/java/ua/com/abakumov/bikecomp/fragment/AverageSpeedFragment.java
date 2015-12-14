package ua.com.abakumov.bikecomp.fragment;

import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.event.NewElapsedSecounds;
import ua.com.abakumov.bikecomp.event.SessionStart;
import ua.com.abakumov.bikecomp.event.SessionStop;
import ua.com.abakumov.bikecomp.event.gps.NewDistance;
import ua.com.abakumov.bikecomp.util.Utils;

import static android.util.Log.v;
import static java.lang.String.valueOf;
import static ua.com.abakumov.bikecomp.util.Constants.TAG;
import static ua.com.abakumov.bikecomp.util.Utils.formatSpeed;

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
        this.averageSpeed = 0;

        updateUI();
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(NewElapsedSecounds event) {
        this.elapsedTime = event.getElapsedSecounds();

        v(TAG, "[ Av. Speed Fragment ] New elapsed time has been received, elapsed time (seconds) is " +
                valueOf(this.elapsedTime));

        updateAverageSpeed();
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(NewDistance event) {
        this.distance = event.getDistanceInMeters();

        v(TAG, "[ Av. Speed Fragment ] New distance has been received, distance (meters) is " +
                valueOf(this.distance));

        updateAverageSpeed();
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStop event) {
        this.distance = 0;
        this.elapsedTime = 0;

        updateAverageSpeed();
    }


    // ----------- Utilities -----------------------------------------------------------------------

    private void updateAverageSpeed() {
        reCalculateAverageSpeed();
        updateUI();
    }

    private void reCalculateAverageSpeed() {
        v(TAG, "[ Av. Speed Fragment ] Recalculate average speed ...");
        v(TAG, "[ Av. Speed Fragment ] Distance: " + distance + ", elapsed time: " + elapsedTime);

        // No elapsed time yet (prevent ArithmeticException exception "division by zero")
        if (this.elapsedTime == 0) {
            this.averageSpeed = 0;
        } else {
            this.averageSpeed = Utils.metersPerSecoundToKilometersPerHour(distance / elapsedTime);
        }

        v(TAG, "[ Av. Speed Fragment ] Average speed is " + valueOf(this.averageSpeed));
    }
}