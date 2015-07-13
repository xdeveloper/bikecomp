package ua.com.abakumov.bikecomp.fragment;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.greenrobot.event.EventBus;
import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.event.SessionPauseResume;
import ua.com.abakumov.bikecomp.event.SessionStart;
import ua.com.abakumov.bikecomp.event.SessionStop;

import static android.media.MediaPlayer.create;
import static ua.com.abakumov.bikecomp.R.id.buttonPause;
import static ua.com.abakumov.bikecomp.R.id.buttonStart;
import static ua.com.abakumov.bikecomp.R.id.buttonStop;
import static ua.com.abakumov.bikecomp.R.raw.start;
import static ua.com.abakumov.bikecomp.R.raw.stop;

/**
 * <Class Name and Purpose>
 * <p/>
 * Created by Oleksandr Abakumov on 7/5/15.
 */
public class ButtonsFragment extends Fragment {

    private MediaPlayer mediaPlayerStart;

    private MediaPlayer mediaPlayerStop;

    private MediaPlayer mediaPlayerPause;

    private EventBus eventBus;

    private boolean sessionStarted;

    @Override
    public void onSaveInstanceState(Bundle out) {
        // Save state
        out.putBoolean("SESS_ST", this.sessionStarted);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // Restore state
        this.sessionStarted = bundle != null && bundle.getBoolean("SESS_ST");

        mediaPlayerStart = create(getActivity().getApplicationContext(), start);
        mediaPlayerStop = create(getActivity().getApplicationContext(), stop);
        mediaPlayerPause = create(getActivity().getApplicationContext(), stop);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.buttons_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        getActivity().findViewById(buttonStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerStart.start();

                eventBus.post(new SessionStart());
            }
        });

        getActivity().findViewById(buttonStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerStop.start();

                eventBus.post(new SessionStop());
            }
        });

        getActivity().findViewById(buttonPause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerPause.start();

                eventBus.post(new SessionPauseResume());
            }
        });

        buttonsState();
    }

    @Override
    public void onStop() {
        eventBus.unregister(this);

        super.onStop();
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStart event) {
        this.sessionStarted = true;

        buttonsState();
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStop event) {
        this.sessionStarted = false;

        buttonsState();
    }

    private void buttonsState() {
        if (sessionStarted) {
            hide(getStartButton());
            show(getPauseButton());
            show(getStopButton());
        } else {
            show(getStartButton());
            hide(getPauseButton());
            hide(getStopButton());
        }
    }

    private Button getStartButton() {
        return (Button) getActivity().findViewById(R.id.buttonStart);
    }

    private Button getPauseButton() {
        return (Button) getActivity().findViewById(R.id.buttonPause);
    }

    private Button getStopButton() {
        return (Button) getActivity().findViewById(R.id.buttonStop);
    }

    private void hide(Button button) {
        button.setVisibility(View.GONE);
    }

    private void show(Button button) {
        button.setVisibility(View.VISIBLE);
    }
}