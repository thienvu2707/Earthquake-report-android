package com.example.android.quakereport;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by thienvu on 1/19/17.
 */

public class EarthquakeAdapter extends ArrayAdapter<EarthquakeWord> {

    /**
     * Constructor for Adapter
     */
    public EarthquakeAdapter(Activity context, ArrayList<EarthquakeWord> earthquakes) {
        super(context, 0, earthquakes);
    }

    /**
     * method used for split String locations of JSON
     */
    private static final String LOCATION_SEPARATOR = " of";

    /**
     * getView for the adapter
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //check if the existing View being used or  inflate
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.earthquake_items, parent, false);
        }

        /**
         * get items at the right position
         */
        EarthquakeWord currentWord = getItem(position);

        /**
         * Area of magnitude
         */
        //find textview magnitude in list
        TextView findMagnitude = (TextView) convertView.findViewById(R.id.magnitude);
        // formatted double into String
        String formattedDecimal = formatMag(currentWord.getmMagnitude());
        // show to the display
        findMagnitude.setText(formattedDecimal);

        //set background for magnitude color
        //fetch background from the textView
        GradientDrawable gradientDrawable = (GradientDrawable) findMagnitude.getBackground();

        //get the appropriate color for the magnitude level
        int magnitudeColor = getMagnitudeColor(currentWord.getmMagnitude());

        //set color for the background
        gradientDrawable.setColor(magnitudeColor);


        /**
         * Area of Locations
         */
        //get the origin location from JSON
        String originLocations = currentWord.getmLocation();

        //create primary and offset locations variable
        String primaryLocations;
        String offsetLocation;

        //check if locations have primary and offset locations by the word "of"
        if (originLocations.contains(LOCATION_SEPARATOR)) {
            //use array of String to split the String
            String[] parts = originLocations.split(LOCATION_SEPARATOR);
            //set the String of locations offset ex "10km of...."
            offsetLocation = parts[0] + LOCATION_SEPARATOR;
            //set the String of primary locations example "tokyo, japan"
            primaryLocations = parts[1];
        } else {
            //if there no "of" word then offset be default using the word "near the"
            offsetLocation = getContext().getString(R.string.near_the);
            //then the primary location will be origin location
            primaryLocations = originLocations;
        }

        //find the Text view of primary locations
        TextView findPrimaryLocations = (TextView) convertView.findViewById(R.id.primary_locations);
        //set Text to upload to the screen
        findPrimaryLocations.setText(primaryLocations);

        //find the text view of offset locations
        TextView findOffsetLocations = (TextView) convertView.findViewById(R.id.offset_locations);
        //set text to display on the screen
        findOffsetLocations.setText(offsetLocation);


        /**
         * Area of time and date
         */
        //create date object in millisecond
        Date dateObject = new Date(currentWord.getmTime());

        //find the id of date
        TextView findDate = (TextView) convertView.findViewById(R.id.date);

        //format date from long into String show to display to easy to read "Jul 07, 2016"
        String formattedDate = formatDate(dateObject);

        //display the date to the UI
        findDate.setText(formattedDate);

        //find the id of time
        TextView findTime = (TextView) convertView.findViewById(R.id.time);
        //format the time from long to String to show to display easy to read "2:29am"
        String formattedTime = formatTime(dateObject);
        //display the time to UI
        findTime.setText(formattedTime);

        return convertView;
    }

    /**
     * use switch statements to know which case to use which color for magnitude
     */
    private int getMagnitudeColor(double magnitude) {
        int magColorID;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magColorID = R.color.magnitude1;
                break;
            case 2:
                magColorID = R.color.magnitude2;
                break;
            case 3:
                magColorID = R.color.magnitude3;
                break;
            case 4:
                magColorID = R.color.magnitude4;
                break;
            case 5:
                magColorID = R.color.magnitude5;
                break;
            case 6:
                magColorID = R.color.magnitude6;
                break;
            case 7:
                magColorID = R.color.magnitude7;
                break;
            case 8:
                magColorID = R.color.magnitude8;
                break;
            case 9:
                magColorID = R.color.magnitude9;
                break;
            default:
                magColorID = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magColorID);
    }

    /**
     * return the formatted for the date from dateObject
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * return formatted of the time from dateObject
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormate = new SimpleDateFormat("hh:mm a");
        return timeFormate.format(dateObject);
    }

    /**
     * return format of magnitude to simple decimal
     */
    private String formatMag(double mag) {
        DecimalFormat magFormat = new DecimalFormat("0.0");
        return magFormat.format(mag);
    }

}
