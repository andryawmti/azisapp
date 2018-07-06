package com.kudubisa.app.azisapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.kudubisa.app.azisapp.R;
import com.kudubisa.app.azisapp.model.Destination;
import com.kudubisa.app.azisapp.recycler.adapter.DestinationRecyclerAdapter;
import com.kudubisa.app.azisapp.remote.Common;
import com.kudubisa.app.azisapp.remote.MyHTTPRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 7/3/18.
 */

public class DestinationFragment extends Fragment {
    List<Destination> destinationList;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    Common common = new Common();

    public DestinationFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_destination, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        recyclerView = (RecyclerView) view.findViewById(R.id.destination_recycler_view);
        initDestinationRecyclerView(view);
        return view;
    }

    private void initDestinationRecyclerView(View view) {

        JSONObject user = null;
        String apiToken = "";
        try {
            user = new JSONObject(common.getUserRaw(getContext()));
            apiToken = user.getString("api_token");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String URL = "/api/destination?api_token="+apiToken;
        Log.d("URL",URL);
        JSONObject jsonObject = new JSONObject();
        MyHTTPRequest httpRequest = new MyHTTPRequest(getContext(), view, URL, "GET",
                jsonObject, httpResponse, progressBar);
        httpRequest.execute();
    }

    MyHTTPRequest.HTTPResponse httpResponse = new MyHTTPRequest.HTTPResponse() {
        @Override
        public void response(String body, View view) {
            try {
                JSONObject response = new JSONObject(body);
                JSONArray dataDestination = response.optJSONArray("destination_list");
                destinationList = new ArrayList<>();
                for (int i = 0; i < dataDestination.length(); i++) {
                    JSONObject destJson = dataDestination.optJSONObject(i);
                    Destination destination = new Destination();
                    destination.setId(destJson.getString("id"));
                    destination.setTitle(destJson.getString("title"));
                    destination.setDesc(destJson.getString("description"));
                    destination.setLatitude(destJson.getString("latitude"));
                    destination.setLongitude(destJson.getString("longitude"));
                    destination.setImage(destJson.getString("picture"));
                    destinationList.add(destination);
                }

                DestinationRecyclerAdapter destinationRecyclerAdapter = new DestinationRecyclerAdapter(destinationList, getContext());
                recyclerView.setAdapter(destinationRecyclerAdapter);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
}
