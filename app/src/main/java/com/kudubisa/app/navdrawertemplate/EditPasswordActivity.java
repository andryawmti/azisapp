package com.kudubisa.app.navdrawertemplate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kudubisa.app.navdrawertemplate.remote.MyHTTPRequest;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Max;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by asus on 4/15/18.
 */

public class EditPasswordActivity extends AppCompatActivity {

    private final static String LOGIN_PREFS = "login_prefs";
    private final static  String USER_RAW = "userRaw";
    private final static String PASSWORD = "password";
    private String updatePasswordUrl = "/api/user/";
    SharedPreferences preferences;

    @NotEmpty(message = "Password is required")
    EditText etCurrentPassword;

    @Password(message = "Your password is not valid")
    @NotEmpty(message = "Password is required")
    EditText etNewPassword;

    @ConfirmPassword()
    EditText etConfirmPassword;

    View view;
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Password");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView tvResetPassword = (TextView) findViewById(R.id.tvResetPassword);
        tvResetPassword.setOnClickListener(onClickListener);

        etCurrentPassword = (EditText) findViewById(R.id.etCurrentPassword);
        etNewPassword = (EditText) findViewById(R.id.etNewPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        final Validator validator = new Validator(this);
        validator.setValidationListener(validationListener);

        Button btnSave = (Button) findViewById(R.id.btnSave);
        view = btnSave;
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tvResetPassword:
                    Intent intent = new Intent(EditPasswordActivity.this, ResetPasswordActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

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
//        Toast.makeText(this, "Data is valid", Toast.LENGTH_SHORT).show();
        JSONObject user = getProfile();
        savePassword();
    }

    private String getUserRaw(){
        preferences = getSharedPreferences(LOGIN_PREFS, MODE_PRIVATE);
        return preferences.getString(USER_RAW,"");
    }

    private void setUserRaw(String userRaw){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PASSWORD, etNewPassword.getText().toString());
        editor.putString(USER_RAW, userRaw);
        editor.commit();
    }

    private JSONObject getProfile(){
        JSONObject user;
        try {
            user = new JSONObject(getUserRaw());
            updatePasswordUrl += user.getString("id")+"/password?api_token="+user.getString("api_token");
            return user;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void savePassword(){
        JSONObject userJson = new JSONObject();
        try {
            userJson.put("password", etNewPassword.getText().toString());

            MyHTTPRequest myHTTPRequest = new MyHTTPRequest(this, view, updatePasswordUrl,
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
                setUserRaw(jsonObject.getString("user"));
                Toast.makeText(EditPasswordActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
