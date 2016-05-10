package ua.com.abakumov.bikecomp.fragment.indicators;

import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.event.gps.GpsTrouble;
import ua.com.abakumov.bikecomp.event.gps.NewSpeed;

import static java.util.concurrent.TimeUnit.SECONDS;
import static ua.com.abakumov.bikecomp.util.helper.Helper.formatSpeed;
import static ua.com.abakumov.bikecomp.util.helper.LogHelper.verbose;


/**
 * Fragment that shows speed on the screen
 * <p>
 * Created by oabakumov on 26.06.2015.
 */
public class SpeedFragment extends IndicatorFragment {

    // TODO: move this constant to the system preferences
    private static final int COUNT_DOWN_MAXIMUM_SECOUNDS = 3;

    // Kilometers per hour
    private double speed;

    private long lastSpeedEventTime;

    private ScheduledThreadPoolExecutor executor;

    @Override
    protected void afterStart() {
        super.afterStart();

        executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleAtFixedRate(() -> {
            verbose("It's time to check speed validity");

            // Every COUNT_DOWN_MAXIMUM_SECOUNDS I have to verify whether
            // last received speed is not too old to be shown on the screen
            // (for example we have lost GPS connection and latest received
            // speed isn't actual any longer.
            // So - I have to show zero on the screen in that case.

            Calendar calendarLastSpeedEvent = Calendar.getInstance();
            calendarLastSpeedEvent.setTimeInMillis(lastSpeedEventTime);
            calendarLastSpeedEvent.add(Calendar.SECOND, COUNT_DOWN_MAXIMUM_SECOUNDS);

            Calendar calendarNow = Calendar.getInstance();

            if (calendarLastSpeedEvent.before(calendarNow)) {
                resetSpeed();
            }

        }, 0, COUNT_DOWN_MAXIMUM_SECOUNDS, SECONDS);
    }

    @Override
    protected void beforeStop() {
        super.beforeStop();

        executor.shutdown();
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

    // ----------- Events handling -----------------------------------------------------------------

    @SuppressWarnings(value = "unused")
    @Subscribe
    public void onEvent(GpsTrouble event) {
        resetSpeed();
    }

    private void resetSpeed() {
        speed = 0;
        updateUI();
    }

    @SuppressWarnings(value = "unused")
    @Subscribe
    public void onEvent(NewSpeed event) {
        speed = event.getKmphSpeed();
        lastSpeedEventTime = event.getEventTime();

        updateUI();
    }
}
