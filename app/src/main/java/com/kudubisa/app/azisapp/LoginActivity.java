package com.kudubisa.app.azisapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.kudubisa.app.azisapp.model.LoginCredentials;
import com.kudubisa.app.azisapp.remote.MyHTTPRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by asus on 4/14/18.
 */

public class LoginActivity extends AppCompatActivity {
    private final static String LOGIN_PREFS = "login_prefs";
    private final static String EMAIL = "email";
    private final static String PASSWORD = "password";
    private  final static  String USER_RAW = "userRaw";

    /**
     * Facebook SignIn
     */

    private static final String FB_EMAIL = "email";
    private static final String FB_USER_POSTS = "user_posts";
    private static final String FB_AUTH_TYPE = "rerequest";

    /**
     * this email and password is for automatic login when user
     * successfully signup with google or facebook account.
     */
    private String mEmail;
    private String mPassword;

    Button btnLogin;
    Button btnSignup;
    Button btnGoogleSignIn;
    Button btnFbSignIn;
    EditText loginEmail;
    EditText loginPassword;
    SharedPreferences preferences;
    LoginCredentials loginCredentials;
    ProgressBar progressBar;
    View mView;
    private static final int GOOGLE_SIGN_IN = 1;
    private static final int FACEBOOK_SIGN_IN = 2;
    private Context context;

    private CallbackManager callbackManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = getApplicationContext();
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnGoogleSignIn = (Button) findViewById(R.id.btn_google_login);
        btnFbSignIn = (Button) findViewById(R.id.btn_facebook_login);
        loginEmail = (EditText) findViewById(R.id.login_email);
        loginPassword = (EditText) findViewById(R.id.login_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        btnLogin.setOnClickListener(onClickListener);
        btnSignup.setOnClickListener(onClickListener);
        btnGoogleSignIn.setOnClickListener(onClickListener);
        btnFbSignIn.setOnClickListener(onClickListener);
        mView = getWindow().getDecorView().findViewById(R.id.content_frame);
        loadPreferences();
        FacebookSdk.isInitialized();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_login:
                    String email = loginEmail.getText().toString();
                    String password = loginPassword.getText().toString();
                    loginCredentials.setEmail(email);
                    loginCredentials.setPassword(password);
                    loginCredentials.setRemember(false);
                    if (!(email.isEmpty() && password.isEmpty())) {
                        login(email, password);
                    }
                    break;
                case R.id.btn_signup:
                    Intent intent = new Intent(context, SignUpActivity.class);
                    context.startActivity(intent);
                    break;
                case R.id.btn_google_login:
                    googleSignIn();
                    break;
                case R.id.btn_facebook_login:
                    facebookSignIn();
                    break;
            }
        }
    };

    /**
     * It will load LOGIN SharedPreferences, if not exist it will create one
     */
    private void loadPreferences(){
        preferences = getSharedPreferences(LOGIN_PREFS, MODE_PRIVATE);
        String email = preferences.getString(EMAIL,"");
        String password = preferences.getString(PASSWORD, "");
        loginCredentials = new LoginCredentials();
        loginCredentials.setEmail(email);
        loginCredentials.setPassword(password);
        if (!(email.isEmpty() && password.isEmpty())) {
            login(email, password);
        }
    }

    private void modifyPreferences(String userRaw){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_RAW, userRaw);
        editor.putString(EMAIL, loginCredentials.getEmail());
        editor.putString(PASSWORD, loginCredentials.getPassword());
        editor.apply();
    }

    private void login(String email, String password){
        JSONObject loginJsonObject = new JSONObject();
        try{
            loginJsonObject.put("email", email);
            loginJsonObject.put("password", password);
        }catch(JSONException e){
            e.printStackTrace();
        }

        MyHTTPRequest myHTTPRequest = new MyHTTPRequest(getApplicationContext(), mView, "/android-user/login",
                "POST", loginJsonObject, httpResponse, progressBar);
        myHTTPRequest.execute();
    }

    MyHTTPRequest.HTTPResponse httpResponse = new MyHTTPRequest.HTTPResponse() {
        @Override
        public void response(String body, View view) {
            try {
                JSONObject jsonObject = new JSONObject(body);
                if (jsonObject.getBoolean("error")) {
                    Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                } else {
                    JSONObject userJson = new JSONObject(jsonObject.getString("user"));
                    String userJsonRaw = jsonObject.getString("user");
                    modifyPreferences(userJsonRaw);
                    ifLoginSuccess();
                    Toast.makeText(getApplicationContext(),"Welcome "+userJson.getString("first_name"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    };

    private void ifLoginSuccess(){
        Context context = LoginActivity.this;
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        LoginActivity.this.finish();
    }

    private void googleSignIn() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(context, gso);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account == null) {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
        } else {
            String email = account.getEmail();
            String password = account.getId()+account.getEmail();
            loginCredentials.setEmail(email);
            loginCredentials.setPassword(password);
            login(email, password);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            /**
             * Handle sing up or register to apo when google sign in is success
             */
            handleGoogleSignInResult(task);
        } else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            registerUserWithGoogleAccount(account);
        } catch (ApiException e) {
            e.printStackTrace();
            Toast.makeText(context, "Google SignIn Failed", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Register user using google account
     * @param account
     */
    private void registerUserWithGoogleAccount(GoogleSignInAccount account){
        String url = "/android-user/signup";
        JSONObject userSignup = new JSONObject();
        try {
            String name[] = account.getDisplayName().split(" ");
            String firstName = name[0];
            String lastName = "";
            if (name.length > 1) {
                lastName = name[1];
            }
            mEmail = account.getEmail();
            mPassword = account.getId()+account.getEmail();
            userSignup.put("gmail_id", account.getId());
            userSignup.put("first_name", firstName);
            userSignup.put("last_name", lastName);
            userSignup.put("email", mEmail);
            userSignup.put("password", mPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MyHTTPRequest myHTTPRequest = new MyHTTPRequest(context, mView, url,
                "POST", userSignup, new MyHTTPRequest.HTTPResponse() {
            @Override
            public void response(String body, View view) {
                try {
                    JSONObject bodyJson = new JSONObject(body);
                    if (bodyJson.getBoolean("error")) {
                        Toast.makeText(context, bodyJson.getString("message"), Toast.LENGTH_LONG).show();
                    } else {
                        loginCredentials.setEmail(mEmail);
                        loginCredentials.setPassword(mPassword);
                        login(mEmail, mPassword);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, progressBar);

        myHTTPRequest.execute();
    }

    private void facebookSignIn() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(FB_EMAIL, FB_USER_POSTS));
        LoginManager.getInstance().setAuthType(FB_AUTH_TYPE);
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Bundle params = new Bundle();
                params.putString("fields","picture, name, id, email, permissions");
                GraphRequest request = new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/me",
                        params,
                        HttpMethod.GET,
                        callback);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    GraphRequest.Callback callback = new GraphRequest.Callback() {
        @Override
        public void onCompleted(GraphResponse response) {
            JSONObject jsonObject = response.getJSONObject();

            String url = "/android-user/signup";
            JSONObject userSignup = new JSONObject();
            try {
                String name[] = jsonObject.getString("name").split(" ");
                String firstName = name[0];
                String lastName = "";
                if (name.length > 1) {
                    lastName = name[1];
                }
                mEmail = jsonObject.getString("email");
                mPassword = jsonObject.getString("id")+jsonObject.getString("email");
                userSignup.put("fb_id", jsonObject.getString("id"));
                userSignup.put("first_name", firstName);
                userSignup.put("last_name", lastName);
                userSignup.put("email", mEmail);
                userSignup.put("password", mPassword);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            MyHTTPRequest myHTTPRequest = new MyHTTPRequest(context, mView, url,
                    "POST", userSignup, new MyHTTPRequest.HTTPResponse() {
                @Override
                public void response(String body, View view) {
                    try {
                        JSONObject bodyJson = new JSONObject(body);
                        if (bodyJson.getBoolean("error")) {
                            Toast.makeText(context, bodyJson.getString("message"), Toast.LENGTH_LONG).show();
                        } else {
                            loginCredentials.setEmail(mEmail);
                            loginCredentials.setPassword(mPassword);
                            login(mEmail, mPassword);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, progressBar);

            myHTTPRequest.execute();
        }
    };

}
