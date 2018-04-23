package com.kudubisa.app.navdrawertemplate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kudubisa.app.navdrawertemplate.fragment.ProfileFragment;
import com.kudubisa.app.navdrawertemplate.remote.MyHTTPRequest;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        navView.setNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            item.setChecked(true);
            switch (item.getItemId()){
                case R.id.profile_settings:
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    break;
                case R.id.logout:
                    logout(mView);
                    break;
            }
            drawerLayout.closeDrawers();
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
        ft.addToBackStack(null);
        ft.commit();
    }

    private void hideWelcome(){
        RelativeLayout relContenFrame = (RelativeLayout) findViewById(R.id.rel_content_frame);
        relContenFrame.setVisibility(View.GONE);
    }

    private void logout(View mView){
        String logoutURL = "/user/logout";
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
                Log.d("MainActivity", "response_executed");
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                if (jsonObject.getBoolean("error")==false) {
                    modifyPreferences();
                    ifLogoutSuccess();
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

}
