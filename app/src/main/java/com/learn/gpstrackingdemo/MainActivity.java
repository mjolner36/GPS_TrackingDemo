package com.learn.gpstrackingdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Parcelable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSION_FINE_LOCATION = 99;
    TextView textView_value_lat, textView_value_long, textView_value_altitude, textView_value_accuarary,
            textView_value_speed, textView_value_address, textView_off, textView_sensor,textView_value_waypoint;
    Switch switch_location_updates, switch_gps;
    Button button_show_waypoint_list,button_new_waypoint,button_show_map;

    //Google`s API for location services
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;

    List<Location> saveLocation;



    LocationRequest locationRequest;
    LocationCallback locationCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView_off = findViewById(R.id.textView_off);
        textView_value_lat = findViewById(R.id.textView_value_lat);
        textView_value_accuarary = findViewById(R.id.textView_value_accurary);
        textView_value_long = findViewById(R.id.textView_value_long);
        textView_value_altitude = findViewById(R.id.textView_value_altitude);
        textView_value_speed = findViewById(R.id.textView_value_speed);
        textView_value_address = findViewById(R.id.textView_value_address);
        textView_sensor = findViewById(R.id.textView_sensor);
        textView_value_waypoint = findViewById(R.id.textView_value_waypoint);
        //even that triggered wherever the update interval is net
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                //save location
                Location location = locationResult.getLastLocation();
                updateUIValue(location);
            }
        };
        switch_location_updates = findViewById(R.id.switch_location_updates);
        switch_gps = findViewById(R.id.switch_gps);
        button_show_waypoint_list = findViewById(R.id.button_show_waypoint_list);
        button_new_waypoint = findViewById(R.id.button_new_waypoint);
        button_show_map = findViewById(R.id.button_show_map);
        //set all properties for LocationRequest
        locationRequest = LocationRequest.create()
                //how often does the default location check occur?
                .setInterval(1000 * DEFAULT_UPDATE_INTERVAL)
                // how often does the location check occur when set to the most frequent update?
                .setFastestInterval(1000 * FAST_UPDATE_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        updateGPS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) updateGPS();
                else {
                    Toast.makeText(this, "This app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show();
                    finish();
                }

        }
    }

    public void onClickGps(View view) {
        if (switch_gps.isChecked()) {
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            textView_sensor.setText("Using GPS sensor");
        } else {
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            textView_sensor.setText("Using Towers + WiFi");
        }
    }

    public void onClickShowMap(View view){
        Intent i = new Intent(MainActivity.this,MapsActivity.class);
        startActivity(i);
    }

    private void updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    currentLocation = location;
                    updateUIValue(location);
                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }
    }

    public void onClickNewWaypoint(View view){
        //add new waypoint
        MyApplication myApplication = (MyApplication) getApplicationContext();
        saveLocation = myApplication.getMyLocations();
        saveLocation.add(currentLocation);

    }

    public void onClickShowWaypointList(View view){
        Intent i = new Intent(MainActivity.this,ShowSavedLocationList.class);
        startActivity(i);
    }

    private void updateUIValue(Location location) {
        //update all text view objects with new location
        textView_value_lat.setText(String.format("%.3f", location.getLatitude()));
        textView_value_long.setText(String.format("%.3f", location.getLongitude()));

        if (location.hasAccuracy())
            textView_value_accuarary.setText(String.format("%.3f", location.getAccuracy()));
        else textView_value_accuarary.setText("Not available");

        if (location.hasSpeed())
            textView_value_speed.setText(String.format("%.3f", location.getSpeed()));
        else textView_value_speed.setText("Not available");
        if (location.hasAltitude())
            textView_value_altitude.setText(String.format("%.3f", location.getAltitude()));
        else textView_value_altitude.setText("Not available");

        Geocoder geocoder = new Geocoder(MainActivity.this);
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            textView_value_address.setText(addresses.get(0).getAddressLine(0));
        }
        catch (Exception e){
            textView_value_address.setText("Unable to get street address");
        }
        MyApplication myApplication = (MyApplication) getApplicationContext();
        saveLocation = myApplication.getMyLocations();
        textView_value_waypoint.setText(Integer.toString(saveLocation.size()));

    }


    private void stopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
        textView_off.setText("Location is NOT being tracking");
        textView_value_lat.setText("NOT tracking location");
        textView_value_long.setText("NOT tracking location");
        textView_value_accuarary.setText("NOT tracking location");
        textView_value_speed.setText("NOT tracking location");
        textView_value_altitude.setText("NOT tracking location");
        textView_value_address.setText("NOT tracking location");

    }

    private void startLocationUpdate() {
        textView_off.setText("Location is being tracked");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        }
        updateGPS();
    }


    public void onClickUpdate(View view) {
        if (switch_location_updates.isChecked()) {
            //turn on location tracking
            startLocationUpdate();
        } else {
            //turn off location tracking
            stopLocationUpdate();
        }
    }

}