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
import ua.com.abakumov.bikecomp.event.SessionStart;
import ua.com.abakumov.bikecomp.event.SessionStop;

import static android.media.MediaPlayer.create;
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

    private EventBus eventBus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.buttons_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        buttonsInitialState();

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        mediaPlayerStart = create(getActivity().getApplicationContext(), start);
        mediaPlayerStop = create(getActivity().getApplicationContext(), stop);

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
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStart event) {
        hide(getStartButton());
        show(getPauseButton());
        show(getStopButton());
    }

    @SuppressWarnings(value = "unused")
    public void onEvent(SessionStop event) {
        show(getStartButton());
        hide(getPauseButton());
        hide(getStopButton());
    }

    private void buttonsInitialState() {
        show(getStartButton());
        hide(getPauseButton());
        hide(getStopButton());
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