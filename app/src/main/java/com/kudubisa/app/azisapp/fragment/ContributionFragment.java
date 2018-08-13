package com.kudubisa.app.azisapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kudubisa.app.azisapp.AddContributionActivity;
import com.kudubisa.app.azisapp.MainActivity;
import com.kudubisa.app.azisapp.R;
import com.kudubisa.app.azisapp.model.Destination;
import com.kudubisa.app.azisapp.recycler.adapter.ContributionRecyclerAdapter;
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

public class ContributionFragment extends Fragment{
    private View view;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private List<Destination> destinationList;
    private FloatingActionButton fabAddContribution;
    private Common common;
    private Context context;

    public ContributionFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contribution, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setActionbarTitle("Contribution");
        context = getContext();
        common = new Common();
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) view.findViewById(R.id.contribution_recycler_view);

        fabAddContribution = (FloatingActionButton) mainActivity.findViewById(R.id.fab);
        fabAddContribution.setVisibility(View.VISIBLE);

        fabAddContribution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddContributionActivity.class);
                context.startActivity(intent);
            }
        });

        initDestinationRecyclerView(view);
        return view;
    }

    private void initDestinationRecyclerView(View view) {

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

        String URL = "/api/destination/get-contribution/"+userId+"?api_token="+apiToken;
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
                JSONArray dataDestination = response.optJSONArray("contribution_list");
                destinationList = new ArrayList<>();
                if (dataDestination.length() > 0) {
                    for (int i = 0; i < dataDestination.length(); i++) {
                        JSONObject destJson = dataDestination.optJSONObject(i);
                        Destination destination = new Destination();
                        destination.setId(destJson.getString("id"));
                        destination.setTitle(destJson.getString("title"));
                        destination.setDesc(destJson.getString("description"));
                        destination.setLatitude(destJson.getString("latitude"));
                        destination.setLongitude(destJson.getString("longitude"));
                        destination.setImage(destJson.getString("picture"));
                        destination.setStatus(destJson.getString("status"));
                        destinationList.add(destination);
                    }

                    ContributionRecyclerAdapter contributionRecyclerAdapter = new ContributionRecyclerAdapter(destinationList, getContext());
                    recyclerView.setAdapter(contributionRecyclerAdapter);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        initDestinationRecyclerView(view);
    }
}
