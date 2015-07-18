package ua.com.abakumov.bikecomp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ua.com.abakumov.bikecomp.data.Ride;

/**
 * DAO/Helper (sqlite)
 * <p>
 * Created by Oleksandr Abakumov on 7/18/15.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String BIKECOMP_TABLE = "bikecomp_table";

    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_DATE = "date";
    private static final String COL_ELAPSEDTIME = "elapsedtime";
    private static final String COL_AV_SPEED = "av_speed";
    private static final String COL_AV_PACE = "av_pace";
    private static final String COL_DISTANCE = "distance";

    public DBHelper(Context context) {
        super(context, "bikecomp-database.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(Constants.BIKECOMP_TAG, "Create database");

        sqLiteDatabase.execSQL("CREATE TABLE " + BIKECOMP_TABLE + " ("
                + COL_ID + " integer primary key autoincrement,"
                + COL_TITLE + " text,"
                + COL_DATE + " numeric,"
                + COL_ELAPSEDTIME + " integer,"
                + COL_AV_SPEED + " real,"
                + COL_AV_PACE + " integer,"
                + COL_DISTANCE + " integer"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(Constants.BIKECOMP_TAG, "Upgrade database");
    }


    /**
     * Read all rides
     *
     * @return list of rides
     */
    public List<Ride> getAllRides() {
        Log.d(Constants.BIKECOMP_TAG, "Get all rides");

        List<Ride> list = new ArrayList<>();

        Cursor cursor = getReadableDatabase().query(BIKECOMP_TABLE, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {

            int titleIndex = cursor.getColumnIndex(COL_TITLE);
            int dateIndex = cursor.getColumnIndex(COL_DATE);
            int elapsedTimeIndex = cursor.getColumnIndex(COL_ELAPSEDTIME);
            int avTimeIndex = cursor.getColumnIndex(COL_AV_SPEED);
            int avPaceIndex = cursor.getColumnIndex(COL_AV_PACE);
            int distanceIndex = cursor.getColumnIndex(COL_DISTANCE);

            do {
                String title = cursor.getString(titleIndex);
                Date date = new Date();
                date.setTime(cursor.getInt(dateIndex));
                int elapsedTime = cursor.getInt(elapsedTimeIndex);
                double avSpeed = cursor.getDouble(avTimeIndex);
                int avPace = cursor.getInt(avPaceIndex);
                float distance = cursor.getFloat(distanceIndex);

                list.add(new Ride(title, date, elapsedTime, avSpeed, avPace, distance));
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
        Log.d(Constants.BIKECOMP_TAG, "Saving ride");

        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransaction();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TITLE, ride.getTitle());
        contentValues.put(COL_DATE, ride.getDate().getTime());
        contentValues.put(COL_ELAPSEDTIME, ride.getElapsedTime());
        contentValues.put(COL_AV_SPEED, ride.getAverageSpeed());
        contentValues.put(COL_AV_PACE, ride.getAveragePace());
        contentValues.put(COL_DISTANCE, ride.getDistance());

        writableDatabase.insert(BIKECOMP_TABLE, null, contentValues);
        writableDatabase.setTransactionSuccessful();
        writableDatabase.endTransaction();
        writableDatabase.close();
        this.close();
    }

}