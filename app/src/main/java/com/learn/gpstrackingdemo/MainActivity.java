package com.learn.gpstrackingdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.security.Permission;

public class MainActivity extends AppCompatActivity {
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSION_FINE_LOCATION = 99;
    TextView textView_value_lat, textView_value_long, textView_value_altitude, textView_value_accuarary,
            textView_value_speed,textView_value_address,textView_off,textView_sensor;
    Switch switch_location_updates,switch_gps;

    //Google`s API for location services
    FusedLocationProviderClient fusedLocationProviderClient;

    LocationRequest locationRequest;

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

        switch_location_updates = findViewById(R.id.switch_location_updates);
        switch_gps = findViewById(R.id.switch_gps);

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
        switch (requestCode){
            case PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) updateGPS();
                else {
                    Toast.makeText(this,"This app requires permission to be granted in order to work properly",Toast.LENGTH_SHORT).show();
                    finish();
                }

        }
    }

    public void onClickGps (View view){
        if (switch_gps.isChecked()){
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            textView_sensor.setText("Using GPS sensor");
        }
        else {
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            textView_sensor.setText("Using Towers + WiFi");
        }
    }
    private void updateGPS(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateUIValue(location);
                }
            });
        }
        else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }
    }

    private void updateUIValue(Location location) {
        //update all text view objects with new location
        textView_value_lat.setText(String.format("%.3f",location.getLatitude()));
        textView_value_long.setText(String.format("%.3f",location.getLongitude()));
        textView_value_accuarary.setText(String.format("%.3f",location.getAccuracy()));

        if (location.hasSpeed()) textView_value_speed.setText(String.format("%.3f",location.getSpeed()));
            else textView_value_speed.setText("Not available");
        if (location.hasAltitude()) textView_value_altitude.setText(String.format("%.3f",location.getAltitude()));
            else textView_value_altitude.setText("Not available");

    }

}