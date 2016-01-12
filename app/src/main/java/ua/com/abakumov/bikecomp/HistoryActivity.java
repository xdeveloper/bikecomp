package ua.com.abakumov.bikecomp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ua.com.abakumov.bikecomp.domain.Ride;
import ua.com.abakumov.bikecomp.util.helper.DBHelper;
import ua.com.abakumov.bikecomp.util.theme.ThemeDecider;
import ua.com.abakumov.bikecomp.util.theme.WithActionBarThemeDecider;

import static ua.com.abakumov.bikecomp.util.helper.UIHelper.setupTheme;
import static ua.com.abakumov.bikecomp.util.helper.Helper.formatDate;
import static ua.com.abakumov.bikecomp.util.helper.Helper.formatDistance;
import static ua.com.abakumov.bikecomp.util.helper.Helper.formatElapsedTime;
import static ua.com.abakumov.bikecomp.util.helper.Helper.formatSpeed;
import static ua.com.abakumov.bikecomp.util.helper.Helper.formatTime;
import static ua.com.abakumov.bikecomp.util.helper.Helper.metersToKilometers;


/**
 * Show rides statistics
 * <p>
 * Created by Oleksandr Abakumov on 7/17/15.
 */
public class HistoryActivity extends AppCompatActivity {

    private ThemeDecider themeDecider = new WithActionBarThemeDecider();

    private DBHelper dbHelper;

    private List<Ride> rides;


    // ----------- System --------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setupTheme(this, themeDecider);

        setContentView(R.layout.activity_history);

        dbHelper = new DBHelper(this);

        registerForContextMenu(findViewById(R.id.history_list));
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.history_list) {
            menu.setHeaderTitle(R.string.actions);
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            String delete = getResources().getString(R.string.delete);
            menu.add(Menu.NONE, info.position, 0, delete);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        Ride ride = rides.get(position);
        dbHelper.deleteRide(ride);
        loadHistory();
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
        rides = dbHelper.getAllRides();
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

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // Build view
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.ride_list_item, null);

            // Fill ui fields
            Ride ride = list.get(position);

            ((TextView) convertView.findViewById(R.id.ride_list_item_title)).setText(ride.getTitle());
            ((TextView) convertView.findViewById(R.id.ride_list_item_startdate)).setText(formatDate(ride.getStartDate()));
            ((TextView) convertView.findViewById(R.id.ride_list_item_starttime)).setText(formatTime(ride.getStartDate()));
            ((TextView) convertView.findViewById(R.id.ride_list_item_finishdate)).setText(formatDate(ride.getFinishDate()));
            ((TextView) convertView.findViewById(R.id.ride_list_item_finishtime)).setText(formatTime(ride.getFinishDate()));
            ((TextView) convertView.findViewById(R.id.ride_list_item_elapsedtime)).setText(formatElapsedTime(ride.getElapsedTime()));
            ((TextView) convertView.findViewById(R.id.ride_list_item_averagespeed)).setText(formatSpeed(ride.getAverageSpeed()));
            ((TextView) convertView.findViewById(R.id.ride_list_item_distance)).setText(formatDistance(metersToKilometers(ride.getDistance())));

            return convertView;
        }

    }

}