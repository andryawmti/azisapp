package com.kudubisa.app.azisapp.recycler.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kudubisa.app.azisapp.ContributionDetailActivity;
import com.kudubisa.app.azisapp.R;
import com.kudubisa.app.azisapp.model.Destination;
import com.kudubisa.app.azisapp.remote.Common;

import java.util.List;

/**
 * Created by asus on 7/22/18.
 */

public class ContributionRecyclerAdapter extends RecyclerView.Adapter<ContributionRecyclerAdapter.ViewHolder>{

    private List<Destination> destinationList;
    private Context context;
    private Common common;

    public ContributionRecyclerAdapter(List<Destination> destinationList, Context context) {
        this.destinationList = destinationList;
        this.context = context;
        common = new Common();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.destination_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Destination destination = destinationList.get(position);
        holder.title.setText(destination.getTitle());
        holder.description.setText(destination.getDesc());
        String picUrl = common.getFullUrl(destination.getImage());
        Glide.with(context).load(picUrl).into(holder.picture);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ContributionDetailActivity.class);
                intent.putExtra("id", destination.getId());
                intent.putExtra("title", destination.getTitle());
                intent.putExtra("description", destination.getDesc());
                intent.putExtra("picture", destination.getImage());
                intent.putExtra("longitude", destination.getLongitude());
                intent.putExtra("latitude", destination.getLatitude());
                intent.putExtra("status", destination.getStatus());
                v.getContext().startActivity(intent);
            }
        });
        holder.approvedStatus.setText(destination.getStatus());
    }

    @Override
    public int getItemCount() {
        return destinationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView picture;
        TextView title, description, approvedStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            picture = (ImageView) itemView.findViewById(R.id.picture);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            approvedStatus = (TextView) itemView.findViewById(R.id.approved_status);
        }
    }
}
