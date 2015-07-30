package ua.com.abakumov.bikecomp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.com.abakumov.bikecomp.fragment.AverageSpeedFragment;
import ua.com.abakumov.bikecomp.fragment.DistanceFragment;
import ua.com.abakumov.bikecomp.fragment.SpeedFragment;

/**
 * <Class Name and Purpose>
 * <p>
 * Created by Oleksandr Abakumov on 7/29/15.
 */
public class PrimaryFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_primary, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        Fragment speedFragment = new SpeedFragment();
        Fragment averageSpeedFragment = new AverageSpeedFragment();
        Fragment distanceFragment = new DistanceFragment();

        getFragmentManager().beginTransaction()
                .add(R.id.primaryArea, speedFragment)
                .add(R.id.primaryArea, averageSpeedFragment)
                .add(R.id.primaryArea, distanceFragment)
                .commit();
    }
}
