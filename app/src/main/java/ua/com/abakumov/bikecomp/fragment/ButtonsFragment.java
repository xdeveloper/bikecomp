package ua.com.abakumov.bikecomp.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.com.abakumov.bikecomp.Actions;
import ua.com.abakumov.bikecomp.R;

import static android.media.MediaPlayer.create;
import static ua.com.abakumov.bikecomp.Actions.BROADCAST_ACTION;
import static ua.com.abakumov.bikecomp.Actions.PARCEL_NAME;
import static ua.com.abakumov.bikecomp.Actions.SESSION_START;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.buttons_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        mediaPlayerStart = create(getActivity().getApplicationContext(), start);
        mediaPlayerStop = create(getActivity().getApplicationContext(), stop);

        getActivity().findViewById(buttonStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerStart.start();

                getActivity().sendBroadcast(new Intent(BROADCAST_ACTION).
                        putExtra(PARCEL_NAME, SESSION_START));
            }
        });

        getActivity().findViewById(buttonStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerStop.start();

                getActivity().sendBroadcast(new Intent(BROADCAST_ACTION)
                        .putExtra(PARCEL_NAME, Actions.SESSION_STOP));
            }
        });
    }
}
