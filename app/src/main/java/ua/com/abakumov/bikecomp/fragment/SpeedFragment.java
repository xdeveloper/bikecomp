package ua.com.abakumov.bikecomp.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ua.com.abakumov.bikecomp.Actions;
import ua.com.abakumov.bikecomp.Constants;
import ua.com.abakumov.bikecomp.R;

import static ua.com.abakumov.bikecomp.Actions.BROADCAST_ACTION;
import static ua.com.abakumov.bikecomp.Utils.formatSpeed;
import static ua.com.abakumov.bikecomp.Utils.metersPerSecoundToKilometersPerHour;

/**
 * Fragment that shows speed on the screen
 * <p/>
 * Created by oabakumov on 26.06.2015.
 */
public class SpeedFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.speed_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);

        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String parcelName = intent.getStringExtra(Actions.PARCEL_NAME);
                if (parcelName.equals(Actions.GPS_LOCATION_CHANGED)) {
                    Location location = intent.getParcelableExtra(Actions.GPS_LOCATION_CHANGED_DATA);
                    makeUseOfNewLocation(location);
                }
            }
        }, intentFilter);
    }

    private void makeUseOfNewLocation(Location location) {
        if (location == null) {
            Log.e(Constants.BIKECOMP_TAG, "Location is null");
            return;
        }

        Log.v(Constants.BIKECOMP_TAG, "Location received:" + String.valueOf(location.getSpeed()));

        TextView speedTextView = (TextView) getActivity().findViewById(R.id.speedTextView);
        speedTextView.setText(formatSpeed(metersPerSecoundToKilometersPerHour(location.getSpeed())));
    }

}
