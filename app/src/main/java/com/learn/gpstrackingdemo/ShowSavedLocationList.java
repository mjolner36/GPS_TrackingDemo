package com.learn.gpstrackingdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShowSavedLocationList extends AppCompatActivity {


    ListView lv_saveLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_saved_location_list);
        ArrayList<String> saveAddressees = new ArrayList();
        MyApplication myApplication = (MyApplication)getApplicationContext();
        List<Location> saveLocations = myApplication.getMyLocations();

        for (Location location: saveLocations){
            Geocoder geocoder = new Geocoder(ShowSavedLocationList.this);
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                saveAddressees.add(addresses.get(0).getAddressLine(0));
            }
            catch (Exception e){
                saveAddressees.add("Unable to get street address");
            }
        }

        lv_saveLocation = findViewById(R.id.lv_wayPoints);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,saveAddressees);
        lv_saveLocation.setAdapter(adapter);
    }
}