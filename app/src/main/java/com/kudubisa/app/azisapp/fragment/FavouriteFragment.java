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

import com.kudubisa.app.azisapp.MainActivity;
import com.kudubisa.app.azisapp.R;
import com.kudubisa.app.azisapp.model.Destination;
import com.kudubisa.app.azisapp.recycler.adapter.FavouriteRecyclerAdapter;
import com.kudubisa.app.azisapp.remote.Common;
import com.kudubisa.app.azisapp.remote.MyHTTPRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 7/22/18.
 */

public class FavouriteFragment extends Fragment{
    private View view;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private List<Destination> destinationList;
    Common common;
    public FavouriteFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setActionbarTitle("Favourite Spot");
        common = new Common();
        view = inflater.inflate(R.layout.fragment_favourite, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) view.findViewById(R.id.favourite_recycler_view);
        initDestinationRecyclerView(view);
        return view;
    }

    public void initDestinationRecyclerView(View view) {

        JSONObject user = null;
        String apiToken = "";
        String userId = "";
        try {
            user = new JSONObject(common.getUserRaw(getContext()));
            apiToken = user.getString("api_token");
            userId = user.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String URL = "/api/destination/get-favourite/"+userId+"?api_token="+apiToken;
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
                JSONArray dataDestination = response.optJSONArray("favourite_list");
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
                    destination.setFavourite(destJson.getString("favourite"));
                    destinationList.add(destination);
                }

                FavouriteRecyclerAdapter favouriteRecyclerAdapter =
                        new FavouriteRecyclerAdapter(destinationList, getActivity(), listener);
                recyclerView.setAdapter(favouriteRecyclerAdapter);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    FavouriteRecyclerAdapter.FavouriteItemClickedListener listener = new FavouriteRecyclerAdapter.FavouriteItemClickedListener() {
        @Override
        public void onItemClicked() {
            initDestinationRecyclerView(view);
        }
    };
}
