package ua.com.abakumov.bikecomp.util;

import android.util.Log;

/**
 * Logging helper
 * <p>
 * Created by Oleksandr_Abakumov on 1/11/2016.
 */
public class LogUtils {

    public static final void verbose(String message) {
        Log.v(Constants.TAG, surround(message));
    }

    public static void information(String message) {
        Log.i(Constants.TAG, surround(message));
    }

    private static String surround(String message) {
        return "**********************" + message + "**********************";
    }
}
