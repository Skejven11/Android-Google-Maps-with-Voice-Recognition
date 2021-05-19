package com.example.voicerecognitiongooglemaps;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Boolean locationPermission = false;
    final private int LOCATION_REQUEST_CODE = 1;
    private static final String TAG = "MapActivity";
    private FusedLocationProviderClient userLocation;
    private EditText searchField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getPermission();
        loadInputField();
    }

    private void loadInputField() {
        searchField = (EditText) findViewById(R.id.input_search);
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    findPlace();
                }
                return false;
            }
        });
    }

    private void loadMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void findPlace() {
        String search = searchField.getText().toString();

        Geocoder geocoder = new Geocoder(this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(search, 1);
        }catch (IOException e){
            Log.e(TAG, e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);
            moveToPoint(new LatLng(address.getLatitude(),address.getLongitude()),12f, address.getAddressLine(0));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (locationPermission) {
            getLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }
        Toast.makeText(this, "Location loaded", Toast.LENGTH_SHORT).show();
    }

    private void getPermission(){
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            locationPermission = true;
            loadMap();
        }
        else {
            ActivityCompat.requestPermissions(this, permission, LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermission = false;
        switch(requestCode){
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length != 0){
                    for(int i = 0; i < grantResults.length; i++){
                        Log.d(TAG, String.valueOf(grantResults[i]));
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            locationPermission = false;
                            Toast.makeText(this,"Permission not granted", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    locationPermission = true;
                    loadMap();
                }
            }
        }
    }

    private void getLocation() {
        userLocation = LocationServices.getFusedLocationProviderClient(this);

        try {
            userLocation.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        moveToPoint(new LatLng(location.getLatitude(), location.getLongitude()),12f, "Me");
                    }
                    else {
                        Toast.makeText(MapsActivity.this,"Can't load user location, enable localisation feature in your device", Toast.LENGTH_SHORT).show();
                    }
            }});
            }
        catch (SecurityException e){
            Log.e(TAG, "Exception: " + e.getMessage() );
        }
    }

    private void moveToPoint(LatLng latLng, float zoom, String name) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (name !="Me") {
            MarkerOptions options = new MarkerOptions().position(latLng).title(name);
            mMap.addMarker(options);
        }
    }
}