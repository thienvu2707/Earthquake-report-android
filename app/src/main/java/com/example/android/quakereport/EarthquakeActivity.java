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

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity {

    //public static final String LOG_TAG = EarthquakeActivity.class.getName();
    /**
     * Adapter for list of earthquakes
     */
    private EarthquakeAdapter mAdapter;

    private static final String USGS_REQUEST_URL = "http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=5&limit=10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        //start AsyncTask to fetch data
        earthquakeAsyncTask task = new earthquakeAsyncTask();
        task.execute(USGS_REQUEST_URL);
    }

    /**
     * AsyncTask to request http in the background
     */
    private class earthquakeAsyncTask extends AsyncTask<String, Void, List<EarthquakeWord>> {
        /**
         * this method to run in background thread and perform network request
         *
         * @param urls
         * @return
         */
        @Override
        protected List<EarthquakeWord> doInBackground(String... urls) {
            //check if the url null or not
            if (urls.length < 1 || urls[0] == null)
                return null;
            //perform HTTP request and then JSON processed the response
            List<EarthquakeWord> resultEarthquake = QueryUtils.fetchEarthquakeData(urls[0]);

            return resultEarthquake;
        }

        /**
         * use on post execute to update to UI
         */
        @Override
        protected void onPostExecute(List<EarthquakeWord> resultEarthquake) {
            //clear adapter of previous data
            mAdapter.clear();

            //check if the result null or not
            if (resultEarthquake != null && !resultEarthquake.isEmpty())
                mAdapter.addAll(resultEarthquake);

            //update information displayed to the user

        }
    }
}
