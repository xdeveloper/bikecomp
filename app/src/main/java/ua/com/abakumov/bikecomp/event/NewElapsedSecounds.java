package ua.com.abakumov.bikecomp.event;

/**
 * New elapsed time event
 * <p/>
 * Created by Oleksandr Abakumov on 7/8/15.
 */
public class NewElapsedSecounds extends Event {

    // In secounds
    private long elapsedSecounds;

    public NewElapsedSecounds(long elapsedSecounds) {
        this.elapsedSecounds = elapsedSecounds;
    }

    public long getElapsedSecounds() {
        return elapsedSecounds;
    }

    @Override
    public String toString() {
        return "NewElapsedSecounds{" +
                "elapsedSecounds=" + elapsedSecounds +
                '}';
    }
}