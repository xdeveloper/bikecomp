package ua.com.abakumov;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;
import ua.com.abakumov.fragment.AverageSpeedFragment;
import ua.com.abakumov.fragment.SpeedFragment;

public class MainActivity extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        // Add some fragments
        Fragment speedFragment = new SpeedFragment();
        Fragment averageSpeedFragment = new AverageSpeedFragment();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.mainLayout, speedFragment)
                .add(R.id.mainLayout, averageSpeedFragment)
                .show(speedFragment)
                .show(averageSpeedFragment)
                .commit();
    }
}
