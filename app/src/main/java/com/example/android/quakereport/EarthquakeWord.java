package com.example.android.quakereport;

/**
 * Created by thienvu on 1/1/17.
 */

public class EarthquakeWord {
    //create variable for Earthquake
    //magnitude variable
    private double mMagnitude;

    //location variable
    private String mLocation;

    //time variable
    private long mTime;

    //url
    private String mURL;

    //Constructor for Earthquake
    public EarthquakeWord(double magnitude, String location, long time, String url) {
        mMagnitude = magnitude;
        mLocation = location;
        mTime = time;
        mURL = url;
    }

    public double getmMagnitude() {
        return mMagnitude;
    }

    public String getmLocation() {
        return mLocation;
    }

    public long getmTime() {
        return mTime;
    }

    public String getmURL() {
        return mURL;
    }
}
