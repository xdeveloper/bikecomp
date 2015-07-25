package ua.com.abakumov.bikecomp.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.greenrobot.event.EventBus;
import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.event.SessionStop;

/**
 * Confirm dialog.
 * Confirm the stopping of current riding session.
 * <p>
 * Created by Oleksandr Abakumov on 7/16/15.
 */
public class SessionStopFragment extends DialogFragment {


    // ----------- System --------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session_stop, container, false);
        view.findViewById(R.id.buttonSessionContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        view.findViewById(R.id.buttonSessionFinish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                EventBus.getDefault().post(new SessionStop());
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


}