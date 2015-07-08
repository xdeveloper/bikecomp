package ua.com.abakumov.bikecomp.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.com.abakumov.bikecomp.Actions;
import ua.com.abakumov.bikecomp.R;

import static ua.com.abakumov.bikecomp.Actions.BROADCAST_ACTION;
import static ua.com.abakumov.bikecomp.Actions.PARCEL_NAME;
import static ua.com.abakumov.bikecomp.Actions.SESSION_START;
import static ua.com.abakumov.bikecomp.Actions.SESSION_STOP;

/**
 * Shows average speed
 * <p/>
 * Created by oabakumov on 26.06.2015.
 */
public class AverageSpeedFragment extends Fragment {

    private boolean sessionStarted;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.average_speed_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String parcelName = intent.getStringExtra(PARCEL_NAME);

                if (parcelName.equals(SESSION_START)) {
                    sessionStarted = true;
                    return;
                }

                if (parcelName.equals(SESSION_STOP)) {
                    sessionStarted = false;
                    return;
                }

                if (parcelName.equals(Actions.GPS_LOCATION_CHANGED)) {
                    Location location = intent.getParcelableExtra(Actions.GPS_LOCATION_CHANGED_DATA);

                    //calculateAverageSpeed();
                }
            }
        }, new IntentFilter(BROADCAST_ACTION));
    }

    // TODO: later!
    private void calculateAverageSpeed(Location location) {
        float speed = location.getSpeed();


    }


}
