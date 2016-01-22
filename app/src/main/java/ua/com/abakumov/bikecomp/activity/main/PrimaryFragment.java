package ua.com.abakumov.bikecomp.activity.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.fragment.indicators.AverageSpeedFragment;
import ua.com.abakumov.bikecomp.fragment.indicators.DistanceFragment;
import ua.com.abakumov.bikecomp.fragment.indicators.SpeedFragment;

/**
 * <Class Name and Purpose>
 * <p>
 * Created by Oleksandr Abakumov on 7/29/15.
 */
public class PrimaryFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_primary, container, false);


        Fragment speedFragment = new SpeedFragment();
        Fragment averageSpeedFragment = new AverageSpeedFragment();
        Fragment distanceFragment = new DistanceFragment();

        getFragmentManager().beginTransaction()
                .add(R.id.primaryArea, speedFragment)
                .add(R.id.primaryArea, averageSpeedFragment)
                .add(R.id.primaryArea, distanceFragment)
                .commit();

        return inflate;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
