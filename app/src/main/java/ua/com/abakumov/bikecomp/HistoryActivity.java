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

import ua.com.abakumov.bikecomp.data.Ride;


/**
 * Show rides statistics
 * <p>
 * Created by Oleksandr Abakumov on 7/17/15.
 */
public class HistoryActivity extends Activity {


    // ----------- System --------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadListView();
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
        // todo
        return super.onOptionsItemSelected(item);
    }


    // ----------- Utilities -----------------------------------------------------------------------

    private void loadListView() {
        DBHelper dbHelper = new DBHelper(this);
        List<Ride> rides = dbHelper.getAllRides();
        ListView listView = (ListView) findViewById(R.id.history_list);
        listView.setAdapter(new CustomArrayAdapter(this, R.layout.ride_list_item, rides));
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
            LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.ride_list_item, null);

            // Fill ui fields
            Ride ride = list.get(position);

            ((TextView) convertView.findViewById(R.id.ride_list_item_title)).setText(ride.getTitle());
            ((TextView) convertView.findViewById(R.id.ride_list_item_date)).setText(Utils.formatDate(ride.getDate()));
            ((TextView) convertView.findViewById(R.id.ride_list_item_elapsedtime)).setText(Utils.formatElapsedTime(ride.getElapsedTime()));
            ((TextView) convertView.findViewById(R.id.ride_list_item_averagespeed)).setText(Double.valueOf(ride.getAverageSpeed()).toString());
            ((TextView) convertView.findViewById(R.id.ride_list_item_distance)).setText(Utils.formatSpeed(ride.getDistance()));

            return convertView;
        }


    }


}
