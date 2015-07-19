package ua.com.abakumov.bikecomp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import ua.com.abakumov.bikecomp.domain.Ride;
import ua.com.abakumov.bikecomp.util.DBHelper;

import static ua.com.abakumov.bikecomp.util.Utils.formatDate;
import static ua.com.abakumov.bikecomp.util.Utils.formatElapsedTime;
import static ua.com.abakumov.bikecomp.util.Utils.formatSpeed;
import static ua.com.abakumov.bikecomp.util.Utils.formatTime;


/**
 * Report activity
 * <p>
 * Created by Oleksandr Abakumov on 7/16/15.
 */
public class ReportActivity extends Activity {

    private Ride ride;


    // ----------- System --------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ride = getIntent().getParcelableExtra(Ride.class.getCanonicalName());

        saveSession(ride);

        setContentView(R.layout.activity_report);
    }

    @Override
    protected void onStart() {
        super.onStart();

        setText(R.id.report_title, ride.getTitle());
        setText(R.id.report_startdate, formatDate(ride.getStartDate()));
        setText(R.id.report_starttime, formatTime(ride.getStartDate()));
        setText(R.id.report_finishdate, formatDate(ride.getFinishDate()));
        setText(R.id.report_finishtime, formatTime(ride.getFinishDate()));
        setText(R.id.report_elapsedtime, formatElapsedTime(ride.getElapsedTime()));
        setText(R.id.report_average_speed, formatSpeed(ride.getAverageSpeed()));
        setText(R.id.report_average_pace, formatElapsedTime(ride.getAveragePace()));
        setText(R.id.report_distance, formatSpeed(ride.getDistance()));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_statistics) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // ----------- Utilities -----------------------------------------------------------------------


    private void setText(int id, String title) {
        TextView view = (TextView) findViewById(id);
        view.setText(title);
    }

    private void saveSession(Ride ride) {
        DBHelper dbHelper = new DBHelper(this);
        dbHelper.save(ride);
    }

}
