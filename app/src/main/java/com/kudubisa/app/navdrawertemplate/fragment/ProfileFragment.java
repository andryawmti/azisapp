package com.kudubisa.app.navdrawertemplate.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kudubisa.app.navdrawertemplate.AccountSettingsActivity;
import com.kudubisa.app.navdrawertemplate.EditProfileActivity;
import com.kudubisa.app.navdrawertemplate.R;
import com.kudubisa.app.navdrawertemplate.remote.Common;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by asus on 4/14/18.
 */

public class ProfileFragment extends Fragment{
    public ProfileFragment() {
    }

    private TextView tvProfileName, tvFullName, tvEmail, tvAddress, tvBirthDate, tvPregnancy, tvBloodType, tvWeight;
    private ImageView profilePhoto;
    private Common common;

    private String userRaw;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profilePhoto = (ImageView) view.findViewById(R.id.profile_picture);
        tvProfileName = (TextView) view.findViewById(R.id.profile_name);
        tvFullName = (TextView) view.findViewById(R.id.tv_full_name);
        tvEmail = (TextView) view.findViewById(R.id.tv_email);
        tvAddress = (TextView) view.findViewById(R.id.tv_address);
        tvBirthDate = (TextView) view.findViewById(R.id.tv_birth_date);
        tvPregnancy = (TextView) view.findViewById(R.id.tv_pregnancy);
        tvBloodType = (TextView) view.findViewById(R.id.tv_blood);
        tvWeight = (TextView) view.findViewById(R.id.tv_weight);

        JSONObject user;
        common = new Common();
        userRaw = common.getUserRaw(getContext());
        try {
            user = new JSONObject(userRaw);
            setFieldValues(user);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        FloatingActionButton fab_account_settings = (FloatingActionButton) view.findViewById(R.id.fab_edit_profile);
        fab_account_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, EditProfileActivity.class);
                context.startActivity(intent);
            }
        });
        return view;
    }

    private void setFieldValues(JSONObject userJson){
        try {
            String fullName = userJson.getString("first_name")+" "+userJson.getString("last_name");
            String photoUrl = common.getFullUrl(userJson.getString("photo"));
            Glide.with(getContext()).load(photoUrl).into(profilePhoto);
            tvProfileName.setText(userJson.getString("first_name"));
            tvFullName.setText(fullName);
            tvEmail.setText(userJson.getString("email"));
            tvAddress.setText(userJson.getString("address"));
            tvBirthDate.setText(userJson.getString("birth_date"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
