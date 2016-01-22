package ua.com.abakumov.bikecomp.activity.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.fragment.indicators.ClockFragment;
import ua.com.abakumov.bikecomp.fragment.indicators.ElapsedTimeFragment;

/**
 * <Class Name and Purpose>
 * <p>
 * Created by Oleksandr Abakumov on 7/29/15.
 */
public class SecondaryFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_secondary, container, false);

        Fragment clockFragment = new ClockFragment();
        Fragment elapsedTimeFragment = new ElapsedTimeFragment();

        getFragmentManager().beginTransaction()
                .add(R.id.secondaryArea, clockFragment)
                .add(R.id.secondaryArea, elapsedTimeFragment)
                .commit();

        return inflate;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
