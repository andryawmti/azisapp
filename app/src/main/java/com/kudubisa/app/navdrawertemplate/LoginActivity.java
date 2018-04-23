package com.kudubisa.app.navdrawertemplate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kudubisa.app.navdrawertemplate.model.LoginCredentials;
import com.kudubisa.app.navdrawertemplate.model.UserLogin;
import com.kudubisa.app.navdrawertemplate.remote.MyHTTPRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by asus on 4/14/18.
 */

public class LoginActivity extends AppCompatActivity {
    private final static String LOGIN_PREFS = "login_prefs";
    private final static String EMAIL = "email";
    private final static String PASSWORD = "password";
    private  final static  String USER_RAW = "userRaw";
    Button btnLogin;
    Button btnSignup;
    EditText loginEmail;
    EditText loginPassword;
    SharedPreferences preferences;
    LoginCredentials loginCredentials;
    UserLogin userLogin;
    ProgressBar progressBar;
    View mView;
    private Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = getApplicationContext();
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        loginEmail = (EditText) findViewById(R.id.login_email);
        loginPassword = (EditText) findViewById(R.id.login_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        btnLogin.setOnClickListener(onClickListener);
        btnSignup.setOnClickListener(onClickListener);
        mView = btnLogin;
        loadPreferences();
        login(mView);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_login:
                    loginCredentials.setEmail(loginEmail.getText().toString());
                    loginCredentials.setPassword(loginPassword.getText().toString());
                    loginCredentials.setRemember(false);
                    login(view); //it will do login check and return UserLogin model
                    break;
                case R.id.btn_signup:
                    Intent intent = new Intent(context, SignUpActivity.class);
                    context.startActivity(intent);
                    break;
            }
        }
    };

    /**
     * It will load LOGIN SharedPreferences, if not exist it will create one
     * @author Wahid K.R
     */
    private void loadPreferences(){
        preferences = getSharedPreferences(LOGIN_PREFS, MODE_PRIVATE);
        loginCredentials = new LoginCredentials();
        loginCredentials.setEmail(preferences.getString(EMAIL,""));
        loginCredentials.setPassword(preferences.getString(PASSWORD, ""));
    }

    private void modifyPreferences(String userRaw){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_RAW, userRaw);
        editor.putString(EMAIL, loginCredentials.getEmail());
        editor.putString(PASSWORD, loginCredentials.getPassword());
        editor.apply();
    }

    private void login(View view){
        JSONObject loginJsonObject = new JSONObject();
        try{
            loginJsonObject.put("email", loginCredentials.getEmail());
            loginJsonObject.put("password", loginCredentials.getPassword());
        }catch(JSONException e){
            e.printStackTrace();
        }

        MyHTTPRequest myHTTPRequest = new MyHTTPRequest(getApplicationContext(), view, "/user/login",
                "POST", loginJsonObject, httpResponse, progressBar);
        myHTTPRequest.execute();
    }

    MyHTTPRequest.HTTPResponse httpResponse = new MyHTTPRequest.HTTPResponse() {
        @Override
        public void response(String body, View view) {
            try {
                JSONObject jsonObject = new JSONObject(body);
                Toast.makeText(getApplicationContext(),"Welcome "+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                if (jsonObject.getBoolean("error")==false){
                    String userJsonRaw = jsonObject.getString("user");
//                    JSONObject userObejct = new JSONObject(userJsonRaw);
                    modifyPreferences(userJsonRaw);
                    ifLoginSuccess();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void ifLoginSuccess(){
        Context context = LoginActivity.this;
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        LoginActivity.this.finish();
    }
}
