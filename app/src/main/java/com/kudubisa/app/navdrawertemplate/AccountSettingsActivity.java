package com.kudubisa.app.navdrawertemplate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by asus on 4/14/18.
 */

public class AccountSettingsActivity extends AppCompatActivity {
    Context context = AccountSettingsActivity.this;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.account_settings_toolbar);
        toolbar.setTitle("Account Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RelativeLayout editProfile = (RelativeLayout) findViewById(R.id.rel_edit_profile);
        RelativeLayout editAlamat = (RelativeLayout) findViewById(R.id.rel_edit_address);
        RelativeLayout editEmail = (RelativeLayout) findViewById(R.id.rel_edit_email);
        RelativeLayout editPassword = (RelativeLayout) findViewById(R.id.rel_edit_password);
        editProfile.setOnClickListener(onClickListener);
        editAlamat.setOnClickListener(onClickListener);
        editEmail.setOnClickListener(onClickListener);
        editPassword.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            Intent intent;
            switch(id){
                case R.id.rel_edit_profile:
//                    Snackbar.make(view, "Hello", Snackbar.LENGTH_LONG).show();
                    intent = new Intent(context, EditProfileActivity.class);
                    context.startActivity(intent);
                    break;
                case R.id.rel_edit_address:
                    Snackbar.make(view, "Hello", Snackbar.LENGTH_LONG).show();
                    break;
                case R.id.rel_edit_email:
                    Snackbar.make(view, "Hello", Snackbar.LENGTH_LONG).show();
                    break;
                case R.id.rel_edit_password:
                    Snackbar.make(view, "Hello", Snackbar.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
