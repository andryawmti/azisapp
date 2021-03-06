package com.kudubisa.app.azisapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kudubisa.app.azisapp.fragment.AboutFragment;
import com.kudubisa.app.azisapp.fragment.ContributionFragment;
import com.kudubisa.app.azisapp.fragment.DestinationFragment;
import com.kudubisa.app.azisapp.fragment.FavouriteFragment;
import com.kudubisa.app.azisapp.fragment.HelpFragment;
import com.kudubisa.app.azisapp.fragment.HomeFragment;
import com.kudubisa.app.azisapp.fragment.MapsFragment;
import com.kudubisa.app.azisapp.fragment.ProfileFragment;
import com.kudubisa.app.azisapp.remote.Common;
import com.kudubisa.app.azisapp.remote.MyHTTPRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ProgressBar progressBar;
    View mView;
    SharedPreferences preferences;
    private final static String LOGIN_PREFS = "login_prefs";
    private final static String EMAIL = "email";
    private final static String PASSWORD = "password";
    Context context = MainActivity.this;
    private ImageView profilPiture;
    private TextView profileName;
    private Common common = new Common();
    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            VectorDrawableCompat indicator = VectorDrawableCompat.create(getResources(),
                    R.drawable.ic_menu_black_24dp, getTheme());
            indicator.setTint(ResourcesCompat.getColor(getResources(), R.color.white, getTheme()));
            supportActionBar.setHomeAsUpIndicator(indicator);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navView.getHeaderView(0);
        profilPiture = (ImageView) headerView.findViewById(R.id.navheader_profile);
        profileName = (TextView) headerView.findViewById(R.id.navheader_profile_name);
        setNavHeaderProfile();
        navView.setNavigationItemSelectedListener(navigationItemSelectedListener);
        loadFragment(new HomeFragment());
    }

    NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            item.setChecked(true);
            switch (item.getItemId()){
                case R.id.show_home:
                    fragment = new HomeFragment();
                    loadFragment(fragment);
                    break;
                case R.id.profile_settings:
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    break;
                case R.id.show_destination:
                    fragment = new DestinationFragment();
                    loadFragment(fragment);
                    break;
                case R.id.show_myspot:
                    fragment = new FavouriteFragment();
                    loadFragment(fragment);
                    break;
                case R.id.show_contribute:
                    fragment = new ContributionFragment();
                    loadFragment(fragment);
                    break;
                case R.id.show_about:
                    fragment = new AboutFragment();
                    loadFragment(fragment);
                    break;
                case R.id.show_help:
                    fragment = new HelpFragment();
                    loadFragment(fragment);
                    break;
                case R.id.logout:
                    logout(mView);
                    break;
            }
            drawerLayout.closeDrawers();
            hideFab();
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        int x = menu.size();
        Log.d("size:", String.valueOf(x));
        for (int i=0; i < x; i++) {
            Drawable icon = menu.getItem(i).getIcon();
            icon.mutate();
            icon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            return true;
        }else if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFragment(Fragment fragment){
        hideWelcome();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack("tag");
        ft.commit();
    }

    private void hideWelcome(){
        RelativeLayout relContenFrame = (RelativeLayout) findViewById(R.id.rel_content_frame);
        relContenFrame.setVisibility(View.GONE);
    }

    private void logout(View mView){
        String logoutURL = "/android-user/logout";
        JSONObject logoutObject = new JSONObject();
        MyHTTPRequest myHTTPRequest = new MyHTTPRequest(getApplicationContext(), mView,
                logoutURL, "POST", logoutObject, logoutResponse, progressBar);
        myHTTPRequest.execute();
    }

    MyHTTPRequest.HTTPResponse logoutResponse = new MyHTTPRequest.HTTPResponse() {
        @Override
        public void response(String body, View view) {
            try {
                JSONObject jsonObject = new JSONObject(body);
                if (jsonObject.getBoolean("error")) {
                    Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                } else {
                    modifyPreferences();
                    ifLogoutSuccess();
                    Toast.makeText(context, "You are logged out", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void ifLogoutSuccess(){
        Context context = MainActivity.this;
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        MainActivity.this.finish();

    }

    private void modifyPreferences(){
        preferences = getSharedPreferences(LOGIN_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(EMAIL, "");
        editor.putString(PASSWORD, "");
        editor.apply();
    }

    public void setActionbarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    public void setNavHeaderProfile() {
        try {
            JSONObject jsonObject = new JSONObject(common.getUserRaw(context));
            String photoPath = common.getFullUrl(jsonObject.getString("photo"));
            Log.d("photoPath", photoPath);
            Glide.with(context).load(photoPath).into(profilPiture);
            String fullName = jsonObject.getString("first_name") + " " + jsonObject.getString("last_name");
            profileName.setText(fullName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void hideFab(){
        fab.setVisibility(View.GONE);
    }
}
