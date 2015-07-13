package ua.com.abakumov.bikecomp.event;

/**
 * New elapsed time event
 * <p/>
 * Created by Oleksandr Abakumov on 7/8/15.
 */
public class NewElapsedTime implements Event {

    private int elapsedTime;

    public NewElapsedTime(int elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }
}
