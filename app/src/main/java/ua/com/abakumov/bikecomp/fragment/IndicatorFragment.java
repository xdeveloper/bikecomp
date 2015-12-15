package ua.com.abakumov.bikecomp.fragment;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import ua.com.abakumov.bikecomp.R;

/**
 * Common for all indicators
 * Created by Oleksandr_Abakumov on 12/14/2015.
 */
public abstract class IndicatorFragment extends android.support.v4.app.Fragment {
    private static final String MAIN_TEXT_TEXT_VIEW_FONT = "fonts/RopaSans-Regular.ttf";

    private EventBus eventBus;

    @LayoutRes
    protected abstract int getLayoutRid();

    @StringRes
    protected abstract int getIndicatorNameRid();

    @StringRes
    protected abstract int getMeasurementRid();

    @StringRes
    protected int getInitialMainTextRid() {
        return R.string.zero;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(getLayoutRid(), container, false);

        TextView mainTextView = (TextView) v.findViewById(R.id.indicatorMainTextView);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), MAIN_TEXT_TEXT_VIEW_FONT);
        mainTextView.setText(getInitialMainTextRid());
        mainTextView.setTypeface(typeface);

        ((TextView) v.findViewById(R.id.indicatorNameTextView)).setText(getIndicatorNameRid());
        ((TextView) v.findViewById(R.id.indicatorMeasurementTextView)).setText(getMeasurementRid());
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        afterStart();
    }

    protected void afterStart() {
        eventBus = EventBus.getDefault();
        eventBus.register(this);

        updateUI();
    }

    @Override
    public void onStop() {
        beforeStop();
        super.onStop();
    }

    protected void beforeStop() {
        eventBus.unregister(this);
    }

    protected void updateUI() {
        getActivity().runOnUiThread(() ->
                ((TextView) getActivity()
                        .findViewById(getRootId())
                        .findViewById(R.id.indicatorMainTextView))
                        .setText(getIndicatorText()));
    }

    protected abstract int getRootId();

    protected abstract String getIndicatorText();
}
