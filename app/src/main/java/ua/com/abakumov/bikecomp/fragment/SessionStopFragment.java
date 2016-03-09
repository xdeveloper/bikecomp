package ua.com.abakumov.bikecomp.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import ua.com.abakumov.bikecomp.R;
import ua.com.abakumov.bikecomp.event.SessionStop;

import static java.util.concurrent.TimeUnit.SECONDS;
import static ua.com.abakumov.bikecomp.util.Constants.SETTINGS_HOW_LONG_DIALOG_TO_WAIT_KEY;
import static ua.com.abakumov.bikecomp.util.helper.EventBusHelper.post;

/**
 * Confirm dialog.
 * Confirm the stopping of current riding session.
 * <p>
 * Created by Oleksandr Abakumov on 7/16/15.
 */
public class SessionStopFragment extends DialogFragment {
    private ScheduledThreadPoolExecutor executor;
    private long howLongDialogToWait = 5;

    // ----------- System --------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        howLongDialogToWait = PreferenceManager.getDefaultSharedPreferences(
                this.getActivity()).getLong(SETTINGS_HOW_LONG_DIALOG_TO_WAIT_KEY, 5);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session_stop, container, false);
        view.findViewById(R.id.buttonSessionContinue).setOnClickListener(view1 -> dismissAndStop(false));
        view.findViewById(R.id.buttonSessionFinish).setOnClickListener(view1 -> dismissAndStop(true));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        executor = new ScheduledThreadPoolExecutor(1);
        executor.schedule(this::dismiss, howLongDialogToWait, SECONDS);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void dismissAndStop(boolean stop) {
        executor.shutdown();
        dismiss();
        if (stop) {
            post(new SessionStop());
        }
    }


}