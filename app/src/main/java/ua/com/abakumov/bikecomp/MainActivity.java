package ua.com.abakumov.bikecomp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import ua.com.abakumov.bikecomp.fragment.AverageSpeedFragment;
import ua.com.abakumov.bikecomp.fragment.ClockFragment;
import ua.com.abakumov.bikecomp.fragment.ElapsedTimeFragment;
import ua.com.abakumov.bikecomp.fragment.HeartRateFragment;
import ua.com.abakumov.bikecomp.fragment.SpeedFragment;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addFragments();
    }

    private void addFragments() {
        // Add some fragments
        Fragment speedFragment = new SpeedFragment();
        Fragment averageSpeedFragment = new AverageSpeedFragment();
        Fragment clockFragment = new ClockFragment();
        Fragment elapsedTimeFragment = new ElapsedTimeFragment();
        Fragment hrFragment = new HeartRateFragment();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.mainLayout, speedFragment)
                .add(R.id.mainLayout, averageSpeedFragment)
                .add(R.id.mainLayout, clockFragment)
                .add(R.id.mainLayout, elapsedTimeFragment)
                .add(R.id.mainLayout, hrFragment)
                .show(speedFragment)
                .show(averageSpeedFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
