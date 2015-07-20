package ua.com.abakumov.bikecomp.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Represents ride domain class
 * <p>
 * Created by Oleksandr Abakumov on 7/16/15.
 */
public class Ride implements Parcelable {
    private Integer id;
    private final String title;
    private final Date startDate;
    private final Date finishDate;
    private final int elapsedTime;
    private final double averageSpeed;
    private final int averagePace;
    private final float distance;

    public Ride(String title, Date startDate, Date finishDate, int elapsedTime, double averageSpeed, int averagePace, float distance) {
        this.title = title;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.elapsedTime = elapsedTime;
        this.averageSpeed = averageSpeed;
        this.averagePace = averagePace;
        this.distance = distance;
    }

    public Ride(Integer id, String title, Date startDate, Date finishDate, int elapsedTime, double averageSpeed, int averagePace, float distance) {
        this(title, startDate, finishDate, elapsedTime, averageSpeed, averagePace, distance);
        this.id = id;
    }

    private Ride(Parcel parcel) {
        this.title = parcel.readString();
        this.startDate = timeToDate(parcel.readLong());
        this.finishDate = timeToDate(parcel.readLong());
        this.elapsedTime = parcel.readInt();
        this.averageSpeed = parcel.readDouble();
        this.averagePace = parcel.readInt();
        this.distance = parcel.readFloat();
    }

    private static Date timeToDate(long time) {
        Date date = new Date();
        date.setTime(time);
        return date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeLong(this.startDate.getTime());
        dest.writeLong(this.finishDate.getTime());
        dest.writeInt(this.elapsedTime);
        dest.writeDouble(this.averageSpeed);
        dest.writeInt(this.averagePace);
        dest.writeFloat(this.distance);
    }

    public static final Parcelable.Creator<Ride> CREATOR = new Parcelable.Creator<Ride>() {
        public Ride createFromParcel(Parcel in) {
            return new Ride(in);
        }

        public Ride[] newArray(int size) {
            return new Ride[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public int getAveragePace() {
        return averagePace;
    }

    public float getDistance() {
        return distance;
    }
}
