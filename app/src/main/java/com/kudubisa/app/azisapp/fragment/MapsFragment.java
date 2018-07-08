package com.kudubisa.app.azisapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kudubisa.app.azisapp.R;

/**
 * Created by asus on 4/23/18.
 */

public class MapsFragment extends Fragment {

    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        context = getContext();

        MapFragment mapFragment = (MapFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.maps);

        mapFragment.getMapAsync(onMapReadyCallback);

        return view;
    }

    OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng yogyakarta = new LatLng(7.7956, 110.3695);
            googleMap.addMarker(new MarkerOptions().position(yogyakarta).title("Di Sini Cuy"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(yogyakarta));
        }
    };
}
