package com.kudubisa.app.navdrawertemplate;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kudubisa.app.navdrawertemplate.remote.Common;
import com.kudubisa.app.navdrawertemplate.remote.MyHTTPRequest;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by asus on 4/15/18.
 */

public class ResetPasswordActivity extends AppCompatActivity {
    Validator validator;

    @Email(message = "Your email address is not valid")
    @NotEmpty(message = "Email is required")
    private EditText etEmail;

    private Button btnSave;
    private ProgressBar progressBar;
    private View mView;
    private Common common;
    private Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Reset Password");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        etEmail = (EditText) findViewById(R.id.etEmail);
        btnSave = (Button) findViewById(R.id.btnSave);
        mView = btnSave;

        validator = new Validator(this);
        validator.setValidationListener(validationListener);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });
        common = new Common();
        context = this;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    Validator.ValidationListener validationListener = new Validator.ValidationListener() {
        @Override
        public void onValidationSucceeded() {
            ifFormValid();
        }

        @Override
        public void onValidationFailed(List<ValidationError> errors) {
            for (ValidationError error : errors) {
                View view = error.getView();
                String message = error.getCollatedErrorMessage(getApplicationContext());

                // Display error messages ;)
                if (view instanceof EditText) {
                    ((EditText) view).setError(message);
                }
            }
        }
    };

    private void ifFormValid(){
        resetPassword();
    }

    /**
     * Get Profile
     * @return JSONObject
     */
    private JSONObject getProfile(){
        JSONObject user;
        try {
            user = new JSONObject(common.getUserRaw(this));
            return user;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void resetPassword(){
        JSONObject user = getProfile();
        String url = "/user/reset-password";

        JSONObject userJson = new JSONObject();
        try {
            userJson.put("email", etEmail.getText().toString());
            MyHTTPRequest myHTTPRequest = new MyHTTPRequest(this, mView, url,
                    "POST", userJson, httpResponse, progressBar);
            myHTTPRequest.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    MyHTTPRequest.HTTPResponse httpResponse = new MyHTTPRequest.HTTPResponse() {
        @Override
        public void response(String body, View view) {
            try {
                JSONObject jsonObject = new JSONObject(body);
                Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
