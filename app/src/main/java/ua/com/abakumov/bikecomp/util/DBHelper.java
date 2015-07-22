package ua.com.abakumov.bikecomp.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ua.com.abakumov.bikecomp.domain.Ride;

import static ua.com.abakumov.bikecomp.util.Constants.TAG;
import static ua.com.abakumov.bikecomp.util.Utils.timeToDate;

/**
 * DAO/Helper (sqlite)
 * <p>
 * Created by Oleksandr Abakumov on 7/18/15.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String BIKECOMP_TABLE = "bikecomp_table";

    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_START_DATE = "start_date";
    private static final String COL_FINISH_DATE = "finish_date";
    private static final String COL_ELAPSED_TIME = "elapsed_time";
    private static final String COL_AV_SPEED = "average_speed";
    private static final String COL_AV_PACE = "average_pace";
    private static final String COL_DISTANCE = "distance";

    public DBHelper(Context context) {
        super(context, "bikecomp-database.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "Create database");

        sqLiteDatabase.execSQL("CREATE TABLE " + BIKECOMP_TABLE + " ("
                + COL_ID + " integer primary key autoincrement,"
                + COL_TITLE + " text,"
                + COL_START_DATE + " numeric,"
                + COL_FINISH_DATE + " numeric,"
                + COL_ELAPSED_TIME + " integer,"
                + COL_AV_SPEED + " real,"
                + COL_AV_PACE + " integer,"
                + COL_DISTANCE + " integer"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrade database");
    }

    /**
     * Read all rides
     *
     * @return list of rides
     */
    public List<Ride> getAllRides() {
        Log.d(TAG, "Get all rides");

        List<Ride> list = new ArrayList<>();

        Cursor cursor = getReadableDatabase().query(BIKECOMP_TABLE, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {

            int idIndex = cursor.getColumnIndex(COL_ID);
            int titleIndex = cursor.getColumnIndex(COL_TITLE);
            int startDateIndex = cursor.getColumnIndex(COL_START_DATE);
            int finishDateIndex = cursor.getColumnIndex(COL_FINISH_DATE);
            int elapsedTimeIndex = cursor.getColumnIndex(COL_ELAPSED_TIME);
            int avTimeIndex = cursor.getColumnIndex(COL_AV_SPEED);
            int avPaceIndex = cursor.getColumnIndex(COL_AV_PACE);
            int distanceIndex = cursor.getColumnIndex(COL_DISTANCE);

            do {
                int id = cursor.getInt(idIndex);
                String title = cursor.getString(titleIndex);
                Date startDate = timeToDate(cursor.getLong(startDateIndex));
                Date finishDate = timeToDate(cursor.getLong(finishDateIndex));
                int elapsedTime = cursor.getInt(elapsedTimeIndex);
                double avSpeed = cursor.getDouble(avTimeIndex);
                int avPace = cursor.getInt(avPaceIndex);
                float distance = cursor.getFloat(distanceIndex);

                list.add(new Ride(id, title, startDate, finishDate, elapsedTime, avSpeed, avPace, distance));
            }
            while (cursor.moveToNext());
        }

        cursor.close();

        return list;
    }

    /**
     * Save ride
     *
     * @param ride ride
     */
    public void save(Ride ride) {
        Log.d(TAG, "Saving ride");

        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransaction();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TITLE, ride.getTitle());
        contentValues.put(COL_START_DATE, ride.getStartDate().getTime());
        contentValues.put(COL_FINISH_DATE, ride.getFinishDate().getTime());
        contentValues.put(COL_ELAPSED_TIME, ride.getElapsedTime());
        contentValues.put(COL_AV_SPEED, ride.getAverageSpeed());
        contentValues.put(COL_AV_PACE, ride.getAveragePace());
        contentValues.put(COL_DISTANCE, ride.getDistance());

        writableDatabase.insert(BIKECOMP_TABLE, null, contentValues);
        writableDatabase.setTransactionSuccessful();
        writableDatabase.endTransaction();
        writableDatabase.close();
        this.close();
    }

    /**
     * Delete all rides
     */
    public void deleteAllRides() {
        Log.d(TAG, "Delete all rides");

        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransaction();
        writableDatabase.delete(BIKECOMP_TABLE, null, null);
        writableDatabase.setTransactionSuccessful();
        writableDatabase.endTransaction();
        writableDatabase.close();
        close();
    }

    /**
     * Delete ride
     * <p>
     * (ride must have id field filled (i.e. ride must be saved before (and has id assigned by db) )
     *
     * @param ride ride
     */
    public void deleteRide(Ride ride) {
        Log.d(TAG, "Delete ride");

        Integer id = ride.getId();

        if (id == null) {
            throw new IllegalArgumentException(
                    "Ride without id (maybe this ride hasn't been saved to the database)");
        }

        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransaction();
        writableDatabase.delete(BIKECOMP_TABLE, "id = ?", new String[]{id.toString()});
        writableDatabase.setTransactionSuccessful();
        writableDatabase.endTransaction();
        writableDatabase.close();
        close();
    }
}