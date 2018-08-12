package com.kudubisa.app.azisapp.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.kudubisa.app.azisapp.MainActivity;
import com.kudubisa.app.azisapp.R;

/**
 * Created by asus on 8/12/18.
 */

public class HomeFragment extends Fragment {
    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).hideFab();((MainActivity) getActivity()).setActionbarTitle("Home");
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ImageButton imgBtnAllSpot = (ImageButton) view.findViewById(R.id.imgBtnAllSpot);
        ImageButton imgBtnMySpot = (ImageButton) view.findViewById(R.id.imgBtnMySpot);
        ImageButton imgBtnContribute = (ImageButton) view.findViewById(R.id.imgBtnContribute);
        ImageButton imgBtnProfile = (ImageButton) view.findViewById(R.id.imgBtnProfile);
        ImageButton imgBtnAbout = (ImageButton) view.findViewById(R.id.imgBtnAbout);
        ImageButton imgBtnHelp = (ImageButton) view.findViewById(R.id.imgBtnHelp);
        imgBtnAllSpot.setOnClickListener(onClickListener);
        imgBtnMySpot.setOnClickListener(onClickListener);
        imgBtnContribute.setOnClickListener(onClickListener);
        imgBtnProfile.setOnClickListener(onClickListener);
        imgBtnAbout.setOnClickListener(onClickListener);
        imgBtnHelp.setOnClickListener(onClickListener);
        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Fragment fragment;
            switch (v.getId()) {
                case R.id.imgBtnAllSpot:
                    fragment = new DestinationFragment();
                    loadFragment(fragment);
                    break;

                case R.id.imgBtnMySpot:
                    fragment = new FavouriteFragment();
                    loadFragment(fragment);
                    break;

                case R.id.imgBtnContribute:
                    fragment = new ContributionFragment();
                    loadFragment(fragment);
                    break;

                case R.id.imgBtnProfile:
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    break;

                case R.id.imgBtnAbout:
                    fragment = new AboutFragment();
                    loadFragment(fragment);
                    break;

                case R.id.imgBtnHelp:
                    fragment = new HelpFragment();
                    loadFragment(fragment);
                    break;
            }
            ((MainActivity) getActivity()).hideFab();
        }
    };

    private void loadFragment(Fragment fragment){
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack("tag");
        ft.commit();
    }


}
