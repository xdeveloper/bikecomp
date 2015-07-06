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

import static ua.com.abakumov.bikecomp.Actions.BROADCAST_ACTION;
import static ua.com.abakumov.bikecomp.Actions.PARCEL_NAME;
import static ua.com.abakumov.bikecomp.Actions.SESSION_START;

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

        mediaPlayerStart = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.start);
        mediaPlayerStop = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.stop);

        getActivity().findViewById(R.id.buttonStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerStart.start();

                getActivity().sendBroadcast(new Intent(BROADCAST_ACTION).
                        putExtra(PARCEL_NAME, SESSION_START));
            }
        });

        getActivity().findViewById(R.id.buttonStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerStop.start();

                getActivity().sendBroadcast(new Intent(BROADCAST_ACTION)
                        .putExtra(PARCEL_NAME, Actions.SESSION_STOP));
            }
        });
    }
}
