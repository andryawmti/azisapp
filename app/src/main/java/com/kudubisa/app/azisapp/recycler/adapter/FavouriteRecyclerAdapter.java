package com.kudubisa.app.azisapp.recycler.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kudubisa.app.azisapp.DestinationDetailActivity;
import com.kudubisa.app.azisapp.R;
import com.kudubisa.app.azisapp.fragment.FavouriteFragment;
import com.kudubisa.app.azisapp.model.Destination;
import com.kudubisa.app.azisapp.remote.Common;
import com.kudubisa.app.azisapp.remote.MyHTTPRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by asus on 7/22/18.
 */

public class FavouriteRecyclerAdapter extends RecyclerView.Adapter<FavouriteRecyclerAdapter.ViewHolder> {

    private List<Destination> destinationList;
    private Context context;
    private Common common;
    private ProgressBar progressBar;
    private FavouriteItemClickedListener itemClickedListener;
    public FavouriteRecyclerAdapter(List<Destination> destinationList, Context context, FavouriteItemClickedListener listener) {
        this.destinationList = destinationList;
        this.context = context;
        common = new Common();
        this.itemClickedListener = listener;
    }

    public interface FavouriteItemClickedListener {
        void onItemClicked();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.destination_item_card, parent, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Destination destination = destinationList.get(position);
        holder.cardTitle.setText(destination.getTitle());
        holder.cardText.setText(destination.getDesc());
        if (destination.getFavourite().equals("1")) {
            holder.favouriteButton.setColorFilter(context.getResources().getColor(R.color.colorAccent));
        } else {
            holder.favouriteButton.setColorFilter(context.getResources().getColor(R.color.button_grey));
        }
        holder.favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFavourite(v, destination.getId());
            }
        });
        holder.locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DestinationDetailActivity.class);
                intent.putExtra("id", destination.getId());
                intent.putExtra("title", destination.getTitle());
                intent.putExtra("description", destination.getDesc());
                intent.putExtra("picture", destination.getImage());
                intent.putExtra("longitude", destination.getLongitude());
                intent.putExtra("latitude", destination.getLatitude());
                v.getContext().startActivity(intent);
            }
        });
        holder.visitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Visit Destination", Toast.LENGTH_SHORT).show();
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+destination.getLatitude()
                        +", "+destination.getLongitude()+"&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(mapIntent);
            }
        });
        if (!destination.getImage().equals("null")) {
            String picUrl = common.getFullUrl(destination.getImage());
            Glide.with(context).load(picUrl).into(holder.cardImage);
            Log.d("getImage", destination.getImage());
        } else {
            holder.cardImage.setImageDrawable(context.getDrawable(R.drawable.a));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DestinationDetailActivity.class);
                intent.putExtra("id", destination.getId());
                intent.putExtra("title", destination.getTitle());
                intent.putExtra("description", destination.getDesc());
                intent.putExtra("picture", destination.getImage());
                intent.putExtra("longitude", destination.getLongitude());
                intent.putExtra("latitude", destination.getLatitude());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return destinationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView cardImage;
        TextView cardTitle, cardText;
        Button visitButton;
        ImageButton locationButton, favouriteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            cardImage = (ImageView) itemView.findViewById(R.id.card_image);
            cardTitle = (TextView) itemView.findViewById(R.id.card_title);
            cardText = (TextView) itemView.findViewById(R.id.card_text);
            visitButton = (Button) itemView.findViewById(R.id.visit_button);
            locationButton = (ImageButton) itemView.findViewById(R.id.location_button);
            favouriteButton = (ImageButton) itemView.findViewById(R.id.favourite_button);
        }
    }

    private void addToFavourite(View view, String destinationId) {
        JSONObject params = new JSONObject();
        String api_token = "";
        try {
            JSONObject userJson = new JSONObject(common.getUserRaw(context));
            params.put("destination_id", destinationId);
            params.put("user_id", userJson.getString("id"));
            api_token = userJson.getString("api_token");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "/api/destination/add-to-favourite?api_token="+api_token;
        MyHTTPRequest myHTTPRequest = new MyHTTPRequest(context, view, url, "POST",params, httpResponse, progressBar);
        myHTTPRequest.execute();
    }

    MyHTTPRequest.HTTPResponse httpResponse = new MyHTTPRequest.HTTPResponse() {
        @Override
        public void response(String body, View view) {
            try {
                JSONObject result = new JSONObject(body);
                if (!result.getBoolean("error")) {
                    Toast.makeText(context, result.getString("message"), Toast.LENGTH_SHORT).show();
                    ImageButton favBtn = (ImageButton) view.findViewById(R.id.favourite_button);
                    if (result.getString("is_favourited").equals("1")) {
                        favBtn.setColorFilter(context.getResources().getColor(R.color.colorAccent));
                    } else {
                        favBtn.setColorFilter(context.getResources().getColor(R.color.button_grey));
                    }
                    itemClickedListener.onItemClicked();
                } else {
                    Toast.makeText(context, "Something was wrong dude sorry", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };
}
