package com.example.voicerecognitiongooglemaps;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Boolean locationPermission = false;
    final private int LOCATION_REQUEST  = 1;
    private static final String TAG = "MapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getPermission();
    }

    private void loadMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Log.d(TAG, "load map called");
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        Toast.makeText(this, "Location loaded", Toast.LENGTH_SHORT).show();
    }

    private void getPermission(){
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            locationPermission = true;
            loadMap();
        }
        else {
            ActivityCompat.requestPermissions(this, permission, LOCATION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermission = false;
        Log.d(TAG, "request result called");
        switch(requestCode){
            case LOCATION_REQUEST:{
                if(grantResults.length > 0){
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
}