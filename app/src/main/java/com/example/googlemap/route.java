package com.example.googlemap;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//        import android.support.annotation.Nullable;
//        import com.googlemap.mapsdirection.directionhelpers.FetchURL;
//        import com.googlemap.mapsdirection.directionhelpers.TaskLoadedCallback;



public class route extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback{
    private GoogleMap mMap;
    private MarkerOptions place1, place2;
    private Polyline currentPolyline;
    private static final String TAG=route.class.getSimpleName();
    private String pickUp,destination;


//    private Handler mHandler ;
//    private int mInterval = 3000; // after the coordinate is found, refresh every 3 seconds.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            pickUp=bundle.getString("pickUp");
            destination=bundle.getString("destination");
        }
//        new FetchURL(route.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
//        place1 = new MarkerOptions().position(new LatLng(27.658143, 85.3199503)).title("Location 1");
//        place2 = new MarkerOptions().position(new LatLng(27.667491, 85.3208583)).title("Location 2");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapr);
        mapFragment.getMapAsync(this);
//        Handler handler2 = new Handler();
//        handler2.postDelayed(new Runnable() {
//            public void run() {
//                mHandler = new Handler();
//                startRepeatingTask();
//            }
//        }, 1000);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("mylog", "Added Markers");
        Geocoder geocoder=new Geocoder(route.this);
        List<Address> list1=new ArrayList<>();
        List<Address> list2=new ArrayList<>();
        try{
            list1=geocoder.getFromLocationName(pickUp,1);
            list2=geocoder.getFromLocationName(destination,1);
        }catch(IOException e){
            Log.e(TAG,"geoLocate: IOException"+e.getMessage());
        }
        if(list1.size()>0 && list2.size()>0){
            Address PickUp_Add=list1.get(0);
            Address Dest_Add=list2.get(0);
            LatLng pickplace=new LatLng(PickUp_Add.getLatitude(),PickUp_Add.getLongitude());
            LatLng destplace=new LatLng(Dest_Add.getLatitude(),Dest_Add.getLongitude());
//            place1 = new MarkerOptions().position(new LatLng(27.658143, 85.3199503)).title("Location 1");
            place1=new MarkerOptions().position(new LatLng(PickUp_Add.getLatitude(),PickUp_Add.getLongitude())).title("Initial Location");
//            place2 = new MarkerOptions().position(new LatLng(27.667491, 85.3208583)).title("Location 2");
            place2=new MarkerOptions().position(new LatLng(Dest_Add.getLatitude(),Dest_Add.getLongitude())).title("Final Location");
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(PickUp_Add.getLatitude(),PickUp_Add.getLongitude()),10));
            MarkerOptions options1=new MarkerOptions().position(pickplace).title(pickUp);
            MarkerOptions options2=new MarkerOptions().position(destplace).title(destination);
            mMap.addMarker(options1);
            mMap.addMarker(options2);
            //Route between two points
            new FetchURL(route.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
            //distance between two points in a Straight Line
            Double distance = SphericalUtil.computeDistanceBetween(place1.getPosition(), place2.getPosition());
            CameraPosition googlePlex = CameraPosition.builder()
                    .target(place1.getPosition())
                    .zoom(10)
                    .bearing(0)
                    //.tilt(45)
                    .build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 8000, null);
            Toast.makeText(route.this,"Distance"+distance,Toast.LENGTH_LONG).show();
            Log.d("Location", "Distance: " + distance );
        }
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values)  {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}
