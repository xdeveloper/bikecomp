package ua.com.abakumov.bikecomp.activity.report;

import android.Manifest;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.activity.history.HistoryActivity;
import ua.com.abakumov.bikecomp.activity.main.MainActivity;
import ua.com.abakumov.bikecomp.domain.Ride;
import ua.com.abakumov.bikecomp.util.helper.DBHelper;
import ua.com.abakumov.bikecomp.util.helper.UIHelper;

import static ua.com.abakumov.bikecomp.util.helper.Helper.formatDate;
import static ua.com.abakumov.bikecomp.util.helper.Helper.formatElapsedTime;
import static ua.com.abakumov.bikecomp.util.helper.Helper.formatSpeed;
import static ua.com.abakumov.bikecomp.util.helper.Helper.formatTime;
import static ua.com.abakumov.bikecomp.util.helper.LogHelper.information;
import static ua.com.abakumov.bikecomp.util.helper.LogHelper.warning;

import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Report activity
 * <p>
 * Created by Oleksandr Abakumov on 7/16/15.
 */
public class ReportActivity extends AppCompatActivity {

    private static final int SOME_FUCKING_MAGIC_NUMBER = 42;

    private Ride ride;

    private GoogleApiClient googleApiClient = null;

    // ----------- System --------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UIHelper.setupTheme(this);

        super.onCreate(savedInstanceState);

        ride = getIntent().getParcelableExtra(Ride.class.getCanonicalName());

        buildGoogleFitnessClient();

        // When permissions are revoked the app is restarted so onCreate is sufficient to check for
        // permissions core to the Activity's functionality.
        //if (!checkPermissions()) {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS},
                SOME_FUCKING_MAGIC_NUMBER);

        setContentView(R.layout.activity_report);

        // Share button
        findViewById(R.id.activityReportShareButton).setOnClickListener(v -> {
            loadRide();
        });

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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        // This ensures that if the user denies the permissions then uses Settings to re-enable
        // them, the app will start working.
        buildGoogleFitnessClient();

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

    private void loadRide() {
        new Thread(() -> {
            Calendar cal = Calendar.getInstance();
            Date now = new Date();
            cal.setTime(now);
            long endTime = cal.getTimeInMillis();
            cal.add(Calendar.WEEK_OF_YEAR, -1);
            long startTime = cal.getTimeInMillis();

            java.text.DateFormat dateFormat = SimpleDateFormat.getDateInstance();

            DataReadRequest readRequest = new DataReadRequest.Builder()
                    // The data request can specify multiple data types to return, effectively
                    // combining multiple data queries into one call.
                    // In this example, it's very unlikely that the request is for several hundred
                    // datapoints each consisting of a few steps and a timestamp.  The more likely
                    // scenario is wanting to see how many steps were walked per day, for 7 days.
                    .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                    // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                    // bucketByTime allows for a time span, whereas bucketBySession would allow
                    // bucketing by "sessions", which would need to be defined in code.
                    .bucketByTime(1, TimeUnit.DAYS)
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                    .build();

            DataReadResult dataReadResult =
                    Fitness.HistoryApi.readData(googleApiClient, readRequest).await();

            List<DataSet> dataSets = dataReadResult.getDataSets();
            for (DataSet ds : dataSets) {
                dumpDataSet(ds);
            }
        }).start();


    }

    private void dumpDataSet(DataSet ds) {
        information("Data returned for Data type: " + ds.getDataType().getName());
        java.text.DateFormat dateFormat = SimpleDateFormat.getDateInstance();

        for (DataPoint dp : ds.getDataPoints()) {
            information("Data point:");
            information("\tType: " + dp.getDataType().getName());
            information("\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            information("\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for (Field field : dp.getDataType().getFields()) {
                information("\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
            }
        }

    }

    private void saveRide() {
        new Thread(() -> {
            if (ride == null) {
                warning("No ride object to save");
                return;
            }

            DBHelper dbHelper = new DBHelper(this);
            dbHelper.save(ride);


            // Set a start and end time for our data, using a start time of 1 hour before this moment.
            Calendar cal = Calendar.getInstance();
            Date now = new Date();
            cal.setTime(now);
            long endTime = cal.getTimeInMillis();
            cal.add(Calendar.HOUR_OF_DAY, -1);
            long startTime = cal.getTimeInMillis();

            // Create a data source
            DataSource dataSource = new DataSource.Builder()
                    .setAppPackageName(this)
                    .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                    .setStreamName("" + " - step count")
                    .setType(DataSource.TYPE_RAW)
                    .build();

            // Create a data set
            int stepCountDelta = 950;
            DataSet dataSet = DataSet.create(dataSource);
            // For each data point, specify a start time, end time, and the data value -- in this case,
            // the number of new steps.
            DataPoint dataPoint = dataSet.createDataPoint()
                    .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
            dataPoint.getValue(Field.FIELD_STEPS).setInt(stepCountDelta);
            dataSet.add(dataPoint);


            information("Saving the ride into the Google");

            Status st = Fitness.HistoryApi.insertData(googleApiClient, dataSet).await(4, TimeUnit.SECONDS);

            information(st.toString());
        }).start();
    }

    private void quitApplication() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
        mainIntent.putExtra(MainActivity.EXIT_INTENT, true);
        startActivity(mainIntent);
    }

    private void buildGoogleFitnessClient() {
        if (googleApiClient == null && checkPermissions()) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Fitness.HISTORY_API)
                    .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                    .addOnConnectionFailedListener((l) -> {
                        warning("Connection failed");
                    })
                    .addConnectionCallbacks(
                            new GoogleApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(Bundle bundle) {
                                    information("Connected!!!");
                                    // Now you can make calls to the Fitness APIs.
                                    saveRide();
                                }

                                @Override
                                public void onConnectionSuspended(int i) {
                                    // If your connection to the sensor gets lost at some point,
                                    // you'll be able to determine the reason and react to it here.
                                    if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                        information("Connection lost.  Cause: Network Lost.");
                                    } else if (i
                                            == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                        information(
                                                "Connection lost.  Reason: Service Disconnected");
                                    }
                                }
                            }
                    )
                    .enableAutoManage(this, 0, result -> {
                        information("!");
                    })
                    .build();
        }

        googleApiClient.connect();

    }

    private boolean checkPermissions() {

        return true;
    }
}
