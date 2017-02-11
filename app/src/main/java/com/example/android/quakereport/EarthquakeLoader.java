package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by thienvu on 2/11/17.
 */

public class EarthquakeLoader extends AsyncTaskLoader<List<EarthquakeWord>> {
    //Tag a log message
    private static final String LOG_TAG = EarthquakeLoader.class.getName();

    private String mUrl;

    /**
     * Constructor of EarthquakeLoader
     * @param context
     * @param url
     */
    public EarthquakeLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    /**
     * method to trigger load in background of AsyncTaskLoader
     */
    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "onStartLoading when it work.......");
        forceLoad();
    }

    @Override
    public List<EarthquakeWord> loadInBackground() {
        Log.i(LOG_TAG, "loadInBackground and when it work.........");
        //check if url null or not
        if (mUrl == null)
        return null;

        //perform a network request, parse the response
        List<EarthquakeWord> earthquakes = QueryUtils.fetchEarthquakeData(mUrl);
        Log.i(LOG_TAG, "When and how fetchData should have work..........");
        return earthquakes;
    }
}
