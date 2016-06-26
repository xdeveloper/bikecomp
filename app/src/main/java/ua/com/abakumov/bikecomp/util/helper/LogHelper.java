package ua.com.abakumov.bikecomp.util.helper;

import android.util.Log;

import ua.com.abakumov.bikecomp.util.Constants;

/**
 * Logging helper
 * <p>
 * Created by Oleksandr_Abakumov on 1/11/2016.
 */
public class LogHelper {

    public static void verbose(String message) {
        Log.v(Constants.TAG, surround(message));
    }

    public static void information(String message) {
        Log.i(Constants.TAG, surround(message));
    }

    public static void warning(String message) {
        Log.w(Constants.TAG, surround(message));
    }

    private static String surround(String message) {
        return "______________________________ " + message + " ___________________________________";
    }
}
