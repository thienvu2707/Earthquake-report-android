/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<EarthquakeWord>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    //add log message
    private static final String LOG_TAG = EarthquakeActivity.class.getName();

    //Identify the loader id
    private static final int EARTHQUAKE_LOADER_ID = 1;

    /**
     * Adapter for list of earthquakes
     */
    private EarthquakeAdapter mAdapter;

    /**
     * Variable for the TextView
     */
    private TextView mEmptyStateTextView;

    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "show how onCreate work call.......");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        //finding listview
        ListView findListView = (ListView) findViewById(R.id.list);

        //find Empty TextView
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        findListView.setEmptyView(mEmptyStateTextView);

        //create a new adapter that take empty list of earthquake as input
        mAdapter = new EarthquakeAdapter(this, new ArrayList<EarthquakeWord>());

        //set adapter on UI show it can show user interface
        findListView.setAdapter(mAdapter);

        //Obtain the reference to the Shared Preferences file for the app
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //And register to notified system changed
        preferences.registerOnSharedPreferenceChangeListener(this);

        findListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //find the current earthquake that user clicked on
                EarthquakeWord currentEarthquake = mAdapter.getItem(position);

                //convert URL to URI
                Uri earthquakeUri = Uri.parse(currentEarthquake.getmURL());

                //create a new intent to view uri
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                //send intent to launch to browser
                startActivity(websiteIntent);
            }
        });

        //Get reference of Connectivity manager to check the state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //get detail on current active network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //if there a connection info then we fetch the data
        if (networkInfo != null && networkInfo.isConnected()) {
            //get reference to the LoaderManager, in order to interact with loaders
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            Log.i(LOG_TAG, "Show how initLoader working........");

            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        } else {
            //display error
            //hide indicator after fetching data
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            //Update error message without connection
            mEmptyStateTextView.setText(R.string.no_internet);
        }

//        //start AsyncTask to fetch data
//        earthquakeAsyncTask task = new earthquakeAsyncTask();
//        task.execute(USGS_REQUEST_URL);
    }

    @Override
    public Loader<List<EarthquakeWord>> onCreateLoader(int id, Bundle bundle) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPreferences.getString(getString(R.string.setting_min_magnitude_key), getString(R.string.setting_min_magnitude_default));

        String orderBy = sharedPreferences.getString(getString(R.string.setting_order_by_key),
                getString(R.string.setting_order_by_default));

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        Log.i(LOG_TAG, "onCreateLoader how it work......");
        //create new loader for given url
        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<EarthquakeWord>> loader, List<EarthquakeWord> earthquakeWords) {
        //hide loading bar when the app load is finished
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        Log.i(LOG_TAG, "onLoadFinished how it work.........");
        //clear the adapter of the previous data
        mAdapter.clear();

        //check if the link is valid list then add them to adapter
        if (earthquakeWords != null && !earthquakeWords.isEmpty())
            mAdapter.addAll(earthquakeWords);
        else {
            //set Text for empty state if earthquake == null
            mEmptyStateTextView.setText(R.string.no_earthquake);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<EarthquakeWord>> loader) {
        Log.i(LOG_TAG, "onLoaderReset how it work............");
        //clear existing data to reset
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intentSettings = new Intent(this, SettingsActivity.class);
            startActivity(intentSettings);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals(R.string.setting_min_magnitude_key) ||
                key.equals(R.string.setting_order_by_key))
        {
            //clear the ListView as a new query will kicked off
            mAdapter.clear();

            //Hide the empty state
            mEmptyStateTextView.setVisibility(View.GONE);

            //Show the progress bar
            View progressBar = findViewById(R.id.loading_indicator);
            progressBar.setVisibility(View.VISIBLE);

            //Restart the loader to requery the USGS
            getLoaderManager().restartLoader(EARTHQUAKE_LOADER_ID, null, this);
        }

    }

    //    /**
//     * AsyncTask to request http in the background
//     */
//    private class earthquakeAsyncTask extends AsyncTask<String, Void, List<EarthquakeWord>> {
//        /**
//         * this method to run in background thread and perform network request
//         *
//         * @param urls
//         * @return
//         */
//        @Override
//        protected List<EarthquakeWord> doInBackground(String... urls) {
//            //check if the url null or not
//            if (urls.length < 1 || urls[0] == null)
//                return null;
//            //perform HTTP request and then JSON processed the response
//            List<EarthquakeWord> resultEarthquake = QueryUtils.fetchEarthquakeData(urls[0]);
//
//            return resultEarthquake;
//        }
//
//        /**
//         * use on post execute to update to UI
//         */
//        @Override
//        protected void onPostExecute(List<EarthquakeWord> resultEarthquake) {
//            //clear adapter of previous data
//            mAdapter.clear();
//
//            //check if the result null or not
//            if (resultEarthquake != null && !resultEarthquake.isEmpty())
//                mAdapter.addAll(resultEarthquake);
//        }
//    }
}
