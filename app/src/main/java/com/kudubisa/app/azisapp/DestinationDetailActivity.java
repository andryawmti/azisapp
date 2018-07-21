package com.kudubisa.app.azisapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kudubisa.app.azisapp.remote.Common;

/**
 * Created by asus on 7/5/18.
 */

public class DestinationDetailActivity extends AppCompatActivity {
    private Common common;
    private Context context;
    private Intent intent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_destination);
        common = new Common();
        context = getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();

        CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(intent.getStringExtra("title"));
        TextView description = (TextView) findViewById(R.id.description);
        description.setText(intent.getStringExtra("description"));
        ImageView placePicture = (ImageView) findViewById(R.id.place_picture);

        /**
         * Load destination picture
         */
        loadDestinationPicture(placePicture);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                getSupportFragmentManager().popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadDestinationPicture(ImageView placePicture) {
        if (!intent.getStringExtra("picture").equals("null")) {
            String picUrl = common.getFullUrl(intent.getStringExtra("picture"));
            Glide.with(getApplicationContext()).load(picUrl).into(placePicture);
        }
    }
}
