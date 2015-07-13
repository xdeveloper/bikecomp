package ua.com.abakumov.bikecomp.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.NoSubscriberEvent;
import ua.com.abakumov.bikecomp.event.NewElapsedTime;
import ua.com.abakumov.bikecomp.event.SessionPauseResume;
import ua.com.abakumov.bikecomp.event.SessionStart;
import ua.com.abakumov.bikecomp.event.SessionStop;
import ua.com.abakumov.bikecomp.event.gps.NewDistance;
import ua.com.abakumov.bikecomp.event.gps.NewLocation;

/**
 * <Class Name and Purpose>
 * <p/>
 * Created by Oleksandr Abakumov on 7/13/15.
 */
public class InfoService extends Service {

    private static final long SECOND = 1000;

    private EventBus eventBus;

    private int elapsedTime;

    private float distance;

    private Handler handler = new Handler();

    private boolean paused;

    private ElapsedTimeFragmentTask timerTask;


    // ----------- System --------------------------------------------------------------------------

    @Override
    public void onCreate() {
        super.onCreate();
        eventBus = EventBus.getDefault();

        timerTask = new ElapsedTimeFragmentTask();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        eventBus.register(this);
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    public void onDestroy() {
        eventBus.unregister(this);

        super.onDestroy();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }


    // ----------- Events handling -----------------------------------------------------------------

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStart event) {
        elapsedTime = 0;
        distance = 0;
        paused = false;

        setupAndLaunchTimer();
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStop event) {
        paused = false;

        handler.removeCallbacks(timerTask);
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionPauseResume event) {
        if (paused) {
            // Resume
            paused = false;
            handler.removeCallbacks(timerTask);
            setupAndLaunchTimer();
        } else {
            paused = true;
            handler.removeCallbacks(timerTask);
        }
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(NewLocation event) {
        if (paused) {
            // ignore
            return;
        }

        // correct ?
        distance += event.getMpsSpeed();

        eventBus.post(new NewDistance(distance));
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(NoSubscriberEvent event) {
        // ignore
    }



    // ----------- Utilities -----------------------------------------------------------------------

    private void setupAndLaunchTimer() {
        handler.postDelayed(timerTask, SECOND);
    }

    private class ElapsedTimeFragmentTask implements Runnable {
        @Override
        public void run() {
            elapsedTime++;
            eventBus.post(new NewElapsedTime(elapsedTime));

            handler.postDelayed(timerTask, SECOND);
        }
    }


}
