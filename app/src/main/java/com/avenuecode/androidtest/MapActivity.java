package com.avenuecode.androidtest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Wally7 on 17-04-2015.
 */
public class MapActivity extends FragmentActivity{

    Context context = this;
    private GoogleMap map;

    @Override
    public void onCreate(Bundle onSavedInstanceState) {

        super.onCreate(onSavedInstanceState);
        setContentView(R.layout.activity_map);

        // Get location and coordinate from another activity

        Bundle bnd = getIntent().getExtras();
        int position = bnd.getInt("position");
        ArrayList<String> locations = bnd.getStringArrayList("locations");
        ArrayList<String> coordinates = bnd.getStringArrayList("coordinates");

        List<Marker> markers = new ArrayList<Marker>();

        String mainLocation = "";
        LatLng mainCoord = new LatLng(0, 0);

        // Get map using fragment from v4 support

        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map = fm.getMap();

        if (map != null) {

            setResult(RESULT_OK);

            try {
                // Iterate through locations and create markers

                for (int i = 0; i < locations.size(); i++) {

                    String location = locations.get(i);
                    JSONObject coordinate = new JSONObject(coordinates.get(i));
                    Double lat = coordinate.getDouble("lat");
                    Double lng = coordinate.getDouble("lng");
                    LatLng coord = new LatLng(lat, lng);
                    Marker marker;

                    // Set the principal location if selected

                    if (position == i) {
                        mainLocation = location;
                        mainCoord = coord;
                        marker = map.addMarker(new MarkerOptions()
                                .position(mainCoord)
                                .title(mainLocation)
                                .snippet(lat + " , " + lng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    } else {
                        marker = map.addMarker(new MarkerOptions()
                                .position(coord)
                                .title(location)
                                .snippet(lat + " , " + lng));
                    }

                    // Add to markers array

                    markers.add(marker);

                }

                // Move the camera to selected location

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(mainCoord, 1));
                map.animateCamera(CameraUpdateFactory.zoomTo(4), 2000, null);


            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }



}
