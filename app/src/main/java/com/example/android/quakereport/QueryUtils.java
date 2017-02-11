package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thienvu on 1/24/17.
 */

public final class QueryUtils {

    /**
     * Tag for the log message
     */
    public static final String LOG_TAG = QueryUtils.class.getName();

    /**
     * return new URL object from the given String URL
     *
     * @param requestURL
     * @return
     */
    private static URL createUrl(String requestURL) {
        URL url = null;
        try {
            url = new URL(requestURL);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error creating URL", e);
        }
        return url;
    }

    /**
     * Make HTTP request from given URL and return response
     *
     * @return
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        //check if url is null or not
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            //1st open connection to http
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            //use method get to download from url
            urlConnection.setRequestMethod("GET");
            //establish the connection
            urlConnection.connect();

            //check if the response code is ok or not
            //then read the input stream and parse the response
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem cannot retrieve earthquake JSON result", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (urlConnection != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * convert input stream to a String which contain JSON response from server
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder outputFromString = new StringBuilder();
        //check if input stream is null or not
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                outputFromString.append(line);
                line = reader.readLine();
            }
        }
        return outputFromString.toString();
    }

    /**
     * Return a list of Earthquake using ArrayList and parsing JSON response
     */
    public static List<EarthquakeWord> extractEarthquakesFeatureFromJSON(String earthquakeJSON) {

        //check if JSON is empty or not
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }
        List<EarthquakeWord> earthquakes = new ArrayList<>();

        //parsing JSON here
        try {

            //create the root of JSON
            JSONObject root = new JSONObject(earthquakeJSON);

            //get JSON array "feature"
            JSONArray features = root.getJSONArray("features");

            //get all JSON object in an JSON array
            for (int i = 0; i < features.length(); i++) {
                //create JSON object to find it in array
                JSONObject jsonObject = features.getJSONObject(i);

                //get JSON object "properties" in each JSON object in the array
                JSONObject properties = jsonObject.getJSONObject("properties");

                //get attribute of each "properties" object
                double mag = properties.getDouble("mag");
                String place = properties.getString("place");
                long time = Long.parseLong(properties.optString("time"));
                //extract value url in json
                String url = properties.getString("url");

                //Create a new object with mag, location, time and url
                EarthquakeWord earthquake = new EarthquakeWord(mag, place, time, url);

                earthquakes.add(earthquake);
            }
        } catch (JSONException e) {
//            e.printStackTrace();
            Log.e("QueryUtils", "Check problem parsing the JSON to earthquake report", e);
        }
        return earthquakes;
    }

    /**
     * Query USGS dataset and return the list of earthquake
     *
     * @param requestURL
     * @return
     */
    public static List<EarthquakeWord> fetchEarthquakeData(String requestURL) {
        Log.i(LOG_TAG, "When fetch earthquake data is called...........");
        //create URL
        URL url = createUrl(requestURL);
        //HTTP request to URL and JSON to response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error input stream", e);
        }
        //extract relevant fields from the JSON response and create object
        List<EarthquakeWord> earthquake = extractEarthquakesFeatureFromJSON(jsonResponse);
        return earthquake;
    }
}
