package com.kudubisa.app.azisapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by asus on 8/15/18.
 */

public class PickLatLongActivity extends AppCompatActivity implements
        GoogleMap.OnMarkerClickListener, OnMapReadyCallback, GoogleMap.OnMarkerDragListener,
        View.OnClickListener{

    private Context context;

    private static final LatLng KEBUMEN = new LatLng(-7.654714, 109.608289);


    private Marker mKebumen;

    private GoogleMap mMap;

    private Double latitude = -7.654714;
    private Double longitude = 109.608289;

    private Button btnPick;

    private String title;
    private String description;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_lat_long);
        context = getApplicationContext();
        btnPick = (Button) findViewById(R.id.btnPickLatLong);
        btnPick.setOnClickListener(this);
        try {
            Intent intent = getIntent();
            title = intent.getStringExtra("title");
            description = intent.getStringExtra("description");
        } catch (Exception e) {
            Log.d("picklocation error", e.getLocalizedMessage());
        }
        initGoogleMaps();
    }

    private void initGoogleMaps() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.location_maps);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mKebumen = mMap.addMarker(getMarkerOption(KEBUMEN, "Kebumen"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(KEBUMEN));
        mMap.setMinZoomPreference(10);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setOnMarkerDragListener(this);
        mKebumen.setTag(0);
    }

    private MarkerOptions getMarkerOption(LatLng location, String title) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);
        markerOptions.title(title);
        markerOptions.draggable(true);
        return markerOptions;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Toast.makeText(context, "Drag start", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        System.out.println("dragging");
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;
        String location = "Latitude: "+String.valueOf(latitude)+", Longitude: "+String.valueOf(longitude);
        Toast.makeText(context, location, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPickLatLong:
                Intent intent = new Intent(this, AddContributionActivity.class);
                intent.putExtra("latitude", String.valueOf(latitude));
                intent.putExtra("longitude", String.valueOf(longitude));
                intent.putExtra("title", title);
                intent.putExtra("description", description);
                this.finish();
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, AddContributionActivity.class);
        intent.putExtra("latitude", "");
        intent.putExtra("longitude", "");
        intent.putExtra("title", title);
        intent.putExtra("description", description);
        this.finish();
        startActivity(intent);
    }
}
