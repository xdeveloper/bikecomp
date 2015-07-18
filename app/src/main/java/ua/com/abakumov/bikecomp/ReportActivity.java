package ua.com.abakumov.bikecomp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

import ua.com.abakumov.bikecomp.data.SessionData;

import static ua.com.abakumov.bikecomp.Utils.formatDate;
import static ua.com.abakumov.bikecomp.Utils.formatElapsedTime;
import static ua.com.abakumov.bikecomp.Utils.formatSpeed;
import static ua.com.abakumov.bikecomp.Utils.formatTime;


/**
 * Report activity
 * <p>
 * Created by Oleksandr Abakumov on 7/16/15.
 */
public class ReportActivity extends Activity {

    private SessionData sessionData;


    // ----------- System --------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionData = getIntent().getParcelableExtra(SessionData.class.getCanonicalName());

        saveSession(sessionData);

        setContentView(R.layout.activity_report);
    }

    @Override
    protected void onStart() {
        super.onStart();

        setText(R.id.report_title, sessionData.getTitle());
        setText(R.id.report_date, formatDate(sessionData.getDate()));
        setText(R.id.report_finished_at, formatTime(sessionData.getDate()));
        setText(R.id.report_elapsedtime, formatElapsedTime(sessionData.getElapsedTime()));
        setText(R.id.report_average_speed, formatSpeed(sessionData.getAverageSpeed()));
        setText(R.id.report_average_pace, formatElapsedTime(sessionData.getAveragePace()));
        setText(R.id.report_distance, formatSpeed(sessionData.getDistance()));
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
            Intent intent = new Intent(this, StatisticsActivity.class);
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

    private void saveSession(SessionData sessionData) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", sessionData.getTitle());
        writableDatabase.insert("bikecomp_table", null, contentValues);

      /*  private final String title;
        private final Date date;
        private final int elapsedTime;
        private final double averageSpeed;
        private final int averagePace;
        private final float distance;*/


    }

    private class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, "bikecomp-database", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            Log.d(Constants.BIKECOMP_TAG, "Create database");

            sqLiteDatabase.execSQL("create table bikecomp_table ("
                    + "id integer primary key autoincrement,"
                    + "title text"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

}
