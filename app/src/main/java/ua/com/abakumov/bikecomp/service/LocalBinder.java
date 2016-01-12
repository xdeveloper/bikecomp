package ua.com.abakumov.bikecomp.service;

import android.os.Binder;

/**
 * Binder
 * Created by Oleksandr_Abakumov on 1/11/2016.
 */
public class LocalBinder extends Binder {
    private InfoService infoService;

    public LocalBinder(InfoService infoService) {
        this.infoService = infoService;
    }

    public InfoService getService() {
        return infoService;
    }
}
