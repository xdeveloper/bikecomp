package ua.com.abakumov.bikecomp.util.helper;

import de.greenrobot.event.EventBus;
import ua.com.abakumov.bikecomp.event.Event;

/**
 * Helper for event bus
 * <p>
 * Created by Oleksandr_Abakumov on 1/12/2016.
 */
public class EventBusHelper {

    /**
     * Post method
     *
     * @param event event
     */
    public static void post(Event event) {
        EventBus.getDefault().post(event);
    }
}
