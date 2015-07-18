package ua.com.abakumov.bikecomp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ua.com.abakumov.bikecomp.domain.Ride;
import ua.com.abakumov.bikecomp.util.DBHelper;
import ua.com.abakumov.bikecomp.util.Utils;

import static java.lang.Double.valueOf;
import static ua.com.abakumov.bikecomp.util.Utils.formatDate;
import static ua.com.abakumov.bikecomp.util.Utils.formatDistance;
import static ua.com.abakumov.bikecomp.util.Utils.formatElapsedTime;
import static ua.com.abakumov.bikecomp.util.Utils.formatSpeed;
import static ua.com.abakumov.bikecomp.util.Utils.formatTime;


/**
 * Show rides statistics
 * <p>
 * Created by Oleksandr Abakumov on 7/17/15.
 */
public class HistoryActivity extends Activity {

    private DBHelper dbHelper;


    // ----------- System --------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dbHelper = new DBHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadHistory();
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
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Clear all rides
        if (R.id.menuClearAll == id) {
            clearHistory();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // ----------- Utilities -----------------------------------------------------------------------


    private void loadHistory() {
        List<Ride> rides = dbHelper.getAllRides();
        ListView listView = (ListView) findViewById(R.id.history_list);
        listView.setAdapter(new CustomArrayAdapter(this, R.layout.ride_list_item, rides));
    }

    private void clearHistory() {
        dbHelper.deleteAllRides();
        loadHistory();
    }

    private class CustomArrayAdapter extends ArrayAdapter<Ride> {
        private ArrayList<Ride> list;

        public CustomArrayAdapter(Context context, int textViewResourceId, List<Ride> rowDataList) {
            super(context, textViewResourceId, rowDataList);
            this.list = new ArrayList<>();
            this.list.addAll(rowDataList);
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            // Build view
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.ride_list_item, null);

            // Fill ui fields
            Ride ride = list.get(position);

            ((TextView) convertView.findViewById(R.id.ride_list_item_title)).setText(ride.getTitle());
            ((TextView) convertView.findViewById(R.id.ride_list_item_date)).setText(formatDate(ride.getDate()));
            ((TextView) convertView.findViewById(R.id.ride_list_item_time)).setText(formatTime(ride.getDate()));
            ((TextView) convertView.findViewById(R.id.ride_list_item_elapsedtime)).setText(formatElapsedTime(ride.getElapsedTime()));
            ((TextView) convertView.findViewById(R.id.ride_list_item_averagespeed)).setText(formatSpeed(ride.getAverageSpeed()));
            ((TextView) convertView.findViewById(R.id.ride_list_item_distance)).setText(formatDistance(ride.getDistance()));

            return convertView;
        }

    }

}