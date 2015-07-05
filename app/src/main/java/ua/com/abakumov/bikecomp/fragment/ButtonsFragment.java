package ua.com.abakumov.bikecomp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.com.abakumov.bikecomp.R;

/**
 * <Class Name and Purpose>
 * <p/>
 * Created by Oleksandr Abakumov on 7/5/15.
 */
public class ButtonsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.buttons_fragment, container, false);
    }
}
