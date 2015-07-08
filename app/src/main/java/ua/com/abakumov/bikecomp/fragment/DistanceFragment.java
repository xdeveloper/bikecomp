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
import android.widget.TextView;

import ua.com.abakumov.bikecomp.Actions;
import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.Utils;

import static ua.com.abakumov.bikecomp.Actions.BROADCAST_ACTION;
import static ua.com.abakumov.bikecomp.Actions.PARCEL_NAME;
import static ua.com.abakumov.bikecomp.Actions.SESSION_START;
import static ua.com.abakumov.bikecomp.Actions.SESSION_STOP;

/**
 * Shows distance on the screen
 * <p/>
 * Created by oabakumov on 26.06.2015.
 */
public class DistanceFragment extends Fragment {

    private float distance;

    private boolean sessionStarted;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.distance_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);

        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String parcelName = intent.getStringExtra(Actions.PARCEL_NAME);

                if (parcelName.equals(SESSION_START)) {
                    sessionStarted = true;
                    resetDistance();
                    calcDistance(0);
                }

                if (parcelName.equals(SESSION_STOP)) {
                    sessionStarted = false;
                }

                if (parcelName.equals(Actions.GPS_LOCATION_CHANGED)) {
                    if (!sessionStarted) {
                        return;
                    }

                    Location location = intent.getParcelableExtra(Actions.GPS_LOCATION_CHANGED_DATA);
                    calcDistance(location.getSpeed());
                    return;
                }

            }
        }, intentFilter);
    }

    private void resetDistance() {
        distance = 0;
    }

    private void calcDistance(float speed) {
        distance += speed;

        ((TextView)getActivity().findViewById(R.id.distanceTextView)).setText(Utils.formatDistance(distance));
    }


}
