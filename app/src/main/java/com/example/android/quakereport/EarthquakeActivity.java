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
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<EarthquakeWord>> {

    //add log message
    private static final String LOG_TAG = EarthquakeActivity.class.getName();

    //Identify the loader id
    private static final int EARTHQUAKE_LOADER_ID = 1;

    /**
     * Adapter for list of earthquakes
     */
    private EarthquakeAdapter mAdapter;

    private static final String USGS_REQUEST_URL = "http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=5&limit=10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "show how onCreate work call.......");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        //finding listview
        ListView findListView = (ListView) findViewById(R.id.list);

        //create a new adapter that take empty list of earthquake as input
        mAdapter = new EarthquakeAdapter(this, new ArrayList<EarthquakeWord>());

        //set adapter on UI show it can show user interface
        findListView.setAdapter(mAdapter);

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

        //get reference to the LoaderManager, in order to interact with loaders
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        Log.i(LOG_TAG, "Show how initLoader working........");
        loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);


//        //start AsyncTask to fetch data
//        earthquakeAsyncTask task = new earthquakeAsyncTask();
//        task.execute(USGS_REQUEST_URL);
    }

    @Override
    public Loader<List<EarthquakeWord>> onCreateLoader(int id, Bundle bundle) {
        Log.i(LOG_TAG, "onCreateLoader how it work......");
        //create new loader for given url
        return new EarthquakeLoader(this, USGS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<EarthquakeWord>> loader, List<EarthquakeWord> earthquakeWords) {
        Log.i(LOG_TAG, "onLoadFinished how it work.........");
        //clear the adapter of the previous data
        mAdapter.clear();

        //check if the link is valid list then add them to adapter
        if (earthquakeWords != null && !earthquakeWords.isEmpty())
            mAdapter.addAll(earthquakeWords);

    }

    @Override
    public void onLoaderReset(Loader<List<EarthquakeWord>> loader) {
        Log.i(LOG_TAG, "onLoaderReset how it work............");
        //clear existing data to reset
        mAdapter.clear();
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
