package com.example.kevin.ecparking;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private int REQUEST_CODE = 77;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng currentlocation;
    private Intent updateIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }

        mMap = googleMap;

        // Get last known location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            currentlocation = new LatLng(location.getLatitude(), location.getLongitude());

                            // Move camera to current location
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentlocation, 15f));
                        }
                    }
                });

        // Polylines onClickListeners
        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                Point point = getMidPointofLine(polyline);
                showPopup(MapsActivity.this, point, polyline);
            }
        });

        // Read data file
        AssetManager assetManager = this.getResources().getAssets();
        try{
            InputStream streetDB = assetManager.open("streetDB.txt");
            readFile(streetDB);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    // Read from InputStream
    private void readFile(InputStream streetDB) {
        try{
            InputStreamReader reader = new InputStreamReader(streetDB);
            BufferedReader br = new BufferedReader(reader);
            String[] data = br.readLine().split(" ");
            while(data != null) {
                if (data[4].equals("YES")) {
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().clickable(true).add(
                            new LatLng(Double.parseDouble(data[0]), Double.parseDouble(data[1])),
                            new LatLng(Double.parseDouble(data[2]), Double.parseDouble(data[3]))
                    ).color(Color.GREEN));
                } else if (data[4].equals("NO")) {
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().clickable(true).add(
                            new LatLng(Double.parseDouble(data[0]), Double.parseDouble(data[1])),
                            new LatLng(Double.parseDouble(data[2]), Double.parseDouble(data[3]))
                    ).color(Color.RED));
                }
                data = br.readLine().split(" ");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private Point getMidPointofLine(Polyline polyline) {
        List<LatLng> coordinateList = polyline.getPoints();
        LatLng startCoord = coordinateList.get(0);
        LatLng endCoord = coordinateList.get(1);
        Point startPoint = mMap.getProjection().toScreenLocation(startCoord);
        Point endPoint = mMap.getProjection().toScreenLocation(endCoord);

        Point midPoint = new Point((startPoint.x + endPoint.x) / 2, (startPoint.y + endPoint.y) / 2);

        return midPoint;
    }

    // The method that displays the popup.
    private void showPopup(final Activity context, Point p, Polyline polyline) {
        // Inflate the popup_layout.xml
        RelativeLayout viewGroup = (RelativeLayout) context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_layout, viewGroup);

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setFocusable(true);

        // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
        int OFFSET_X = -100;
        int OFFSET_Y = 30;

        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

        TextView parkable = (TextView)layout.findViewById(R.id.parkable);
        if((int)Color.GREEN == polyline.getColor()){
            parkable.setText("Yes");
        }
        else if((int)Color.RED == polyline.getColor()){
            parkable.setText("No");
        }



        // Getting a reference to Close button, and close the popup when clicked.
        Button close = (Button)layout.findViewById(R.id.cancel);
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
        // Update button functionality
        updateIntent = new Intent(MapsActivity.this, SMSActivity.class);
        Button update = (Button)layout.findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(updateIntent);
            }
        });
    }
}
