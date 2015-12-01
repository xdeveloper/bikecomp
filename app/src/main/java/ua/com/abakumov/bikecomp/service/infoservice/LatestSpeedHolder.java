package ua.com.abakumov.bikecomp.service.infoservice;

import android.support.annotation.NonNull;
import android.util.Log;

import static ua.com.abakumov.bikecomp.util.Constants.TAG;

/**
 * Holds latest known speed
 * <p>
 * Created by Oleksandr_Abakumov on 11/30/2015.
 */
class LatestSpeedHolder {
    private float mpsSpeed;

    public void updateMpsSpeed(float mpsSpeed) {
        Log.d(TAG, "[LatestSpeedHolder] New speed is being updated " + prevSpeedText(mpsSpeed));

        this.mpsSpeed = mpsSpeed;
    }

    @NonNull
    private String prevSpeedText(float mpsSpeed) {
        if (mpsSpeed == this.mpsSpeed) {
            return "(nothing changed)";
        } else {
            return "(previous was - " + this.mpsSpeed + " meters per secound)";
        }
    }

    public float askForMpsSpeed() {
        return mpsSpeed;
    }

    public void resetSpeed() {
        this.mpsSpeed = 0;
    }
}
