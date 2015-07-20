package ua.com.abakumov.bikecomp.fragment;

import android.os.Bundle;

import ua.com.abakumov.bikecomp.R;

/**
 * <Class Name and Purpose>
 * <p>
 * Created by Oleksandr Abakumov on 7/20/15.
 */
public class PreferenceFragment extends android.preference.PreferenceFragment {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
