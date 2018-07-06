package com.kudubisa.app.azisapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by asus on 7/5/18.
 */

public class DestinationDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_destination);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(intent.getStringExtra("title"));
        TextView description = (TextView) findViewById(R.id.description);
        description.setText(intent.getStringExtra("description"));
        ImageView placePicture = (ImageView) findViewById(R.id.place_picture);
        //Glide.with(getApplicationContext()).load(intent.getStringExtra("picture")).into(placePicture);
        placePicture.setImageDrawable(getDrawable(R.drawable.a));
        Double latitude = Double.parseDouble(intent.getStringExtra("latitude"));
        Double longitude = Double.parseDouble(intent.getStringExtra("longitude"));
        initLocatinMap(latitude, longitude, intent.getStringExtra("title"));
    }

    private void initLocatinMap(final Double latitude, final Double longitude, final String title) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.location_maps);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng location = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(location).title(title));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                googleMap.setMinZoomPreference(15);
            }
        });
    }
}
