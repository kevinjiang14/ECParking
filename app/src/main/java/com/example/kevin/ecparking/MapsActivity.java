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
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng currentlocation;
//    private Intent changesIntent;
    Point p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        changesIntent = new Intent(MapsActivity.this, SMSActivity.class);

//        Button change = (Button)findViewById(R.id.change);
//        change.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(changesIntent);
//            }
//        });
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
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_COARSE_LOCATION}, 77 );
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION}, 77 );
        }

        mMap = googleMap;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            currentlocation = new LatLng(location.getLatitude(), location.getLongitude());

                            // Add a marker in current location and move the camera
                            mMap.addMarker(new MarkerOptions().position(currentlocation).title("Marker in Columbia University"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentlocation, 15f));
//                            DrawLine();
                        }
                    }
                });


        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                Point point = getMidPointofLine(polyline);
                showPopup(MapsActivity.this, point);
            }
        });
        // Add a line
        Polyline polyline = mMap.addPolyline(new PolylineOptions().clickable(true).add(
                new LatLng(40.810443, -73.961922),
                new LatLng(40.809343, -73.959326)
        ).color(Color.GREEN));
    }

    private Point getMidPointofLine(Polyline polyline) {
        List<LatLng> coordinateList = polyline.getPoints();
        LatLng startCoord = coordinateList.get(0);
        LatLng endCoord = coordinateList.get(1);
        Point startPoint = mMap.getProjection().toScreenLocation(startCoord);
        Point endPoint = mMap.getProjection().toScreenLocation(endCoord);

        Point midPoint = new Point((startPoint.x + endPoint.x) / 2, (startPoint.y + endPoint.y) / 2);

//        return mMap.getProjection().fromScreenLocation(midPoint);
        return midPoint;
    }

    private void DrawLine() {
        AssetManager assetManager = this.getResources().getAssets();
        StreetsSize Size = new StreetsSize();
        Size.openFile(assetManager);
        Size.setSize();
        Size.closeFile();

        int numOfStreets = Size.getSize();

        Street nextStreetInfo = new Street();
        nextStreetInfo.openFile();
        Street street[] = new Street[numOfStreets];

        for(int i = 0; i < numOfStreets; i++) {
            street[i] = new Street();
            street[i].inLat = nextStreetInfo.getElement();
            street[i].inLon = nextStreetInfo.getElement();
            street[i].endLat = nextStreetInfo.getElement();
            street[i].endLon = nextStreetInfo.getElement();
            street[i].ava = nextStreetInfo.getElement();
            Polyline polyline = mMap.addPolyline(new PolylineOptions().clickable(true).add(
                    new LatLng(Double.parseDouble(nextStreetInfo.getElement()), Double.parseDouble(nextStreetInfo.getElement())),
                    new LatLng(Double.parseDouble(nextStreetInfo.getElement()), Double.parseDouble(nextStreetInfo.getElement()))
            ).color(Color.GREEN));
        }

        nextStreetInfo.closeFile();
    }

    // The method that displays the popup.
    private void showPopup(final Activity context, Point p) {
        int popupWidth = 800;
        int popupHeight = 600;

        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_layout, viewGroup);

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);

        // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
        int OFFSET_X = 30;
        int OFFSET_Y = 30;

        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

        // Getting a reference to Close button, and close the popup when clicked.
        Button close = (Button) layout.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
    }
}
