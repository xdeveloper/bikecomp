package ua.com.abakumov.bikecomp.activity.report;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.zzj;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * Report activity
 * <p>
 * Created by Oleksandr Abakumov on 7/16/15.
 */
public class ReportActivity extends AppCompatActivity {

    private static final String TAG = "dsdsd";
    private static final int SOME_FUCKING_MAGIC_NUMBER = 42;
    private Ride ride;

    private GoogleApiClient mClient = null;

    // ----------- System --------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UIHelper.setupTheme(this);

        super.onCreate(savedInstanceState);

        ride = getIntent().getParcelableExtra(Ride.class.getCanonicalName());

        /// ---------------- google fit -------------
        // This method sets up our custom logger, which will print all log messages to the device
        // screen, as well as to adb logcat.
        buildFitnessClient();

        // When permissions are revoked the app is restarted so onCreate is sufficient to check for
        // permissions core to the Activity's functionality.
        //if (!checkPermissions()) {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS},
                SOME_FUCKING_MAGIC_NUMBER);
        //}


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
        // This ensures that if the user denies the permissions then uses Settings to re-enable
        // them, the app will start working.
        buildFitnessClient();

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

        buildFitnessClient();


       /* // Set a start and end time for our data, using a start time of 1 hour before this moment.
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
                .setStreamName("TAG" + " - step count")
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


        // Then, invoke the History API to insert the data and await the result, which is
        // possible here because of the {@link AsyncTask}. Always include a timeout when calling
        // await() to prevent hanging that can occur from the service being shutdown because
        // of low memory or other conditions.
        // Log.i(TAG, "Inserting the dataset in the History API.");

        //
        Fitness.HistoryApi.insertData(mClient, dataSet).await(1, TimeUnit.MINUTES);*/



// Before querying the data, check to see if the insertion succeeded.


    }

    private void quitApplication() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
        mainIntent.putExtra(MainActivity.EXIT_INTENT, true);
        startActivity(mainIntent);
    }


    private void buildFitnessClient() {
        if (mClient == null && checkPermissions()) {
            mClient = new GoogleApiClient.Builder(this)
                    .addApi(Fitness.HISTORY_API)
                    .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                    .addConnectionCallbacks(
                            new GoogleApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(Bundle bundle) {
                                    Log.i(TAG, "Connected!!!");
                                    // Now you can make calls to the Fitness APIs.
                                    findFitnessDataSources();
                                }

                                @Override
                                public void onConnectionSuspended(int i) {
                                    // If your connection to the sensor gets lost at some point,
                                    // you'll be able to determine the reason and react to it here.
                                    if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                        Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                    } else if (i
                                            == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                        Log.i(TAG,
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

        mClient.connect();

    }

    private void findFitnessDataSources() {

    }

    private boolean checkPermissions() {

        return true;
    }
}
