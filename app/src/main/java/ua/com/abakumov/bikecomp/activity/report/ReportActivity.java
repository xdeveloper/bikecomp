package ua.com.abakumov.bikecomp.activity.report;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.activity.main.MainActivity;
import ua.com.abakumov.bikecomp.domain.Ride;
import ua.com.abakumov.bikecomp.util.helper.DBHelper;
import ua.com.abakumov.bikecomp.util.helper.UIHelper;

import static ua.com.abakumov.bikecomp.util.Constants.UA_COM_ABAKUMOV_BIKECOMP_ACTION_HISTORY_MAIN;
import static ua.com.abakumov.bikecomp.util.helper.Helper.formatDate;
import static ua.com.abakumov.bikecomp.util.helper.Helper.formatElapsedTime;
import static ua.com.abakumov.bikecomp.util.helper.Helper.formatSpeed;
import static ua.com.abakumov.bikecomp.util.helper.Helper.formatTime;
import static ua.com.abakumov.bikecomp.util.helper.LogHelper.warning;


/**
 * Report activity
 * <p>
 * Created by Oleksandr Abakumov on 7/16/15.
 */
public class ReportActivity extends AppCompatActivity {

    private Ride ride;


    // ----------- System --------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UIHelper.setupTheme(this);

        super.onCreate(savedInstanceState);

        ride = getIntent().getParcelableExtra(Ride.class.getCanonicalName());

        setContentView(R.layout.activity_report);

        // Share button
/*        findViewById(R.id.activityReportShareButton).setOnClickListener(v -> {
            loadRide();
        });*/

        // Exit application button
        findViewById(R.id.activityReportExitButton).setOnClickListener(v -> {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_exitapp);
            dialog.findViewById(R.id.ok_button_dlg).setOnClickListener(v1 -> {
                dialog.dismiss();
                quitApplication();
            });
            dialog.findViewById(R.id.cancel_button_dlg).setOnClickListener(v2 -> dialog.dismiss());
            dialog.show();
        });
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

        saveRide();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;

            case R.id.action_statistics:
                startActivity(new Intent(UA_COM_ABAKUMOV_BIKECOMP_ACTION_HISTORY_MAIN));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // ----------- Utilities -----------------------------------------------------------------------

    private void setText(int id, String title) {
        TextView view = (TextView) findViewById(id);
        view.setText(title);
    }


    private void saveRide() {
        new Thread(() -> {
            if (ride == null) {
                warning("No ride object to save");
                return;
            }

            DBHelper dbHelper = new DBHelper(this);
            dbHelper.save(ride);
        }).start();
    }

    private void quitApplication() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
        mainIntent.putExtra(MainActivity.EXIT_INTENT, true);
        startActivity(mainIntent);
    }
}
