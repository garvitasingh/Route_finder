package com.example.googlemap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class initial_location extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
//        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback{

    public static final String LOC="com.rideshare.googlemap.info";

    private GoogleMap map;
    private int REQUEST_CODE = 1;
    //entry point to the places api
    private PlacesClient placesClient;
    //entry pont to Fused Location Provider
    private FusedLocationProviderClient client;
//    private boolean permissionDenied = false;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private MarkerOptions place1, place2;
    private final LatLng defaultLocation=new LatLng(25.157464, 82.954531);
    private static final String TAG = initial_location.class.getSimpleName();
//    private static final LatLngBounds LAT_LNG_BOUNDS=new LatLngBounds(
//            new LatLng(-40, -168),new LatLng(71,136));
//    private ActivityMapsBinding binding;
    //widgets
    private AutoCompleteTextView mSearchText;
    private Polyline currentPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_initial_loc);

        mSearchText=findViewById(R.id.input_search_from);

////        Construct a PlaceClient
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);
////        Construct a FusedLocationProviderClient
        client = LocationServices.getFusedLocationProviderClient(initial_location.this);

//        AutocompleteSessionToken autocompleteSessionToken;
//        autocompleteSessionToken=AutocompleteSessionToken.newInstance();

        AutoCompleteAdapter mAdapter;
        mAdapter = new AutoCompleteAdapter(this, placesClient);
        mSearchText.setAdapter(mAdapter);
        //to find id inside fragment
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapi);
        mapFragment.getMapAsync(this);
        getLocationPermission();


//        binding = ActivityMapsBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
    }

    private void init(){
        Log.d(TAG,"init: initialize");


//        mAutoCompleteAdapter =new AutoCompleteAdapter(this,mGoogleApiClient, LAT_LNG_BOUNDS,null);

        //for searching location on pressing enter key
        mSearchText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN ||
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    //execute our method for searching
                    Toast.makeText(getApplicationContext(),"enter clicked",Toast.LENGTH_LONG).show();
                    geoLocate();
                    return true;
                }
                return false;
            }
        });
    }
public void search_button_from(View view){
        geoLocate();
    }
public void Confirmi(View view){
    String loc = new String(mSearchText.getText().toString());
    Intent chooseLocIntent=new Intent();
    chooseLocIntent.putExtra("result",loc);
    setResult(78,chooseLocIntent);
    initial_location.super.onBackPressed();
}
    private void geoLocate(){
        Log.d(TAG, "geoLocate: geolocating");
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder =new Geocoder(initial_location.this);
        List<Address> list =new ArrayList<>();
        try{
            list=geocoder.getFromLocationName(searchString,1);
        }catch(IOException e){
            Log.e(TAG,"geoLocate: IOException"+e.getMessage());
        }
        if(list.size()>0){
            Address address =list.get(0);
            Log.d(TAG,"geoLocate: found a location:"+address.toString());
//            Toast.makeText(this,address.toString(),Toast.LENGTH_LONG).show();
            LatLng searchedPlace=new LatLng(address.getLatitude(), address.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(),address.getLongitude()),16));
            MarkerOptions options=new MarkerOptions().position(searchedPlace).title(address.getAddressLine(0));
            map.addMarker(options);
        }

    }
//            @Override
//            public void onMapReady(GoogleMap googleMap) {
//                map = googleMap;
//
//                // Add a marker in Sydney and move the camera
//                LatLng sydney = new LatLng(-34, 151);
//                map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//                map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//            }

    @SuppressLint("MissingPermission")

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
//to show marker
//        LatLng myPlace=new LatLng(25.157464, 82.954531);
//        MarkerOptions markerOptions=new MarkerOptions().position(myPlace).title("My Palce");
//        map.addMarker(markerOptions);
//to show the map at marker
//        map.moveCamera(CameraUpdateFactory.newLatLng(myPlace));
// to zoom in at marker
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(myPlace,16f));

//        map.setOnMyLocationClickListener((GoogleMap.OnMyLocationClickListener) this);

        getLocationPermission();
        if(locationPermissionGranted){
//            Toast.makeText(this,"location permission granted",Toast.LENGTH_LONG).show();
            init();
        }
        statusCheck();
        map.setOnMyLocationButtonClickListener(this);
        //Turn on the My Location layer and the related control on the map
        updateLocationUI();
        //get the current location of the device and set the position of the map
        getDeviceLocation();
//        enableMyLocation();

    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Task<Location> locationResult = client.getLastLocation();
               locationResult.addOnCompleteListener(this, task -> {
                   if (task.isSuccessful()) {
                       lastKnownLocation = task.getResult();
                       if (lastKnownLocation != null) {
                           LatLng myPlace=new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
    MarkerOptions markerOptions=new MarkerOptions().position(myPlace).title("My Palce");
    map.addMarker(markerOptions);
                           map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 16));
                           Toast.makeText(initial_location.this,"your last location",Toast.LENGTH_LONG).show();
                       }
                   }else {
                       Log.d(TAG, "Current location is null. Using defaults.");
                       Log.e(TAG, "Exception:%s", task.getException());
                       map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 16));
                       map.getUiSettings().setMyLocationButtonEnabled(false);
                   }
               });
           }
       }catch(SecurityException e){
            Log.e("Exception:%s",e.getMessage(),e);
        }
    }
    private void getLocationPermission(){
        if (ContextCompat.checkSelfPermission(initial_location.this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
//            locationPermissionGranted=false;
            ActivityCompat.requestPermissions(initial_location.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
        updateLocationUI();
    }
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);

            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
//                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }
//            private void enableMyLocation() {
//        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
//            if(map!=null){
//                map.setMyLocationEnabled(true);
//            }
//        }else{
//            ActivityCompat.requestPermissions(MapsActivity.this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
//        }
//            }

            /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

            public void statusCheck() {
                final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();

                }
            }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
            @Override
            public boolean onMyLocationButtonClick() {
                if(isLocationEnabled(this)){
                Toast.makeText(this, "You are here", Toast.LENGTH_SHORT).show();}
                else{
//                    Toast.makeText(this,"ON YOUR LOCATION",Toast.LENGTH_LONG).show();
                    new AlertDialog.Builder(initial_location.this)
                            .setTitle("Error")
                            .setMessage("Make sure that your location is on")
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Whatever...
//                                    Toast.makeText(MapsActivity.this,"alert finished",Toast.LENGTH_LONG).show();
                                }
                            }).show();
                }
                return false;
            }

//            @Override
//            public void onMyLocationClick(@NonNull Location location) {
//                Toast.makeText(this,"Current Location:\n"+location,Toast.LENGTH_LONG).show();
//
//            }
public static Boolean isLocationEnabled(Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        // This is a new method provided in API 28
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isLocationEnabled();
    } else {
        // This was deprecated in API 28
        int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                Settings.Secure.LOCATION_MODE_OFF);
        return (mode != Settings.Secure.LOCATION_MODE_OFF);
    }
}

}