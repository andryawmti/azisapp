package com.kudubisa.app.navdrawertemplate;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kudubisa.app.navdrawertemplate.fragment.DatePickerFragment;
import com.kudubisa.app.navdrawertemplate.model.LoginCredentials;
import com.kudubisa.app.navdrawertemplate.model.User;
import com.kudubisa.app.navdrawertemplate.remote.AndroidMultiPartEntity;
import com.kudubisa.app.navdrawertemplate.remote.Common;
import com.kudubisa.app.navdrawertemplate.remote.MyHTTPRequest;
import com.kudubisa.app.navdrawertemplate.remote.UploadToServer;
import com.mobsandgeeks.saripaar.DateFormats;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Min;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by asus on 4/15/18.
 */

public class EditProfileActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE=123;

    int PICK_IMAGE_REQUEST=1;
    Uri filePath=null;
    private Bitmap mImageBitmap;

    private final static String LOGIN_PREFS = "login_prefs";
    private final static String EMAIL = "email";
    private final static  String USER_RAW = "userRaw";

    SharedPreferences preferences;

    @NotEmpty(message = "First Name should not be empty")
    EditText etFirstName;

    @NotEmpty(message = "Last Name should not be empty")
    EditText etLastName;

    @NotEmpty(message = "Address should not be empty")
    EditText etAddress;

    @NotEmpty(message = "Weight should not be empty")
    EditText etWeight;

    @Email(message = "Email not valid")
    EditText etEmail;

    Validator validator;

    TextView tvBirthdate;
    TextView tvPregnancyStart;

    ProgressBar progressBar;

    ImageView fotoProfile;

    View view;

    Context context;

    Common common;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        context = getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        common = new Common();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        tvBirthdate = (TextView) findViewById(R.id.tvBirthdate);
        tvPregnancyStart = (TextView) findViewById(R.id.tvPregnancyStart);
        TextView tvEditFotoProfile = (TextView) findViewById(R.id.edit_foto_profile);
        fotoProfile = (ImageView) findViewById(R.id.foto_profile);

        RelativeLayout relEditAddress = (RelativeLayout) findViewById(R.id.rel_edit_address);
        RelativeLayout relEditEmail = (RelativeLayout) findViewById(R.id.rel_edit_email);
        RelativeLayout relEditPassword = (RelativeLayout) findViewById(R.id.rel_edit_password);
        RelativeLayout relEditBirthdate = (RelativeLayout) findViewById(R.id.relEditBirthDate);
        RelativeLayout relEditPregnancyStart = (RelativeLayout) findViewById(R.id.relEditPregnancyStart);

        etAddress = (EditText) findViewById(R.id.etAddress);
        etFirstName = (EditText) findViewById(R.id.etEditFirstName);
        etLastName = (EditText) findViewById(R.id.etEditLastName);
        etWeight = (EditText) findViewById(R.id.etWeight);
        etEmail = (EditText) findViewById(R.id.etEmail);

        relEditAddress.setOnClickListener(onClickListener);
        relEditEmail.setOnClickListener(onClickListener);
        relEditPassword.setOnClickListener(onClickListener);
        relEditBirthdate.setOnClickListener(onClickListener);
        relEditPregnancyStart.setOnClickListener(onClickListener);

        tvEditFotoProfile.setOnClickListener(onClickListener);

        Button btnSave = (Button) findViewById(R.id.btnSaveProfile);
        view = btnSave;
        btnSave.setOnClickListener(onClickListener);

        validator = new Validator(this);
        validator.setValidationListener(validationListener);

        setDataField();

    }

    private void setDataField(){
        JSONObject userJson = getProfile();
        try {
            tvBirthdate.setText(userJson.getString("birth_date"));
            tvPregnancyStart.setText(userJson.getString("pregnancy_start_at"));
            etFirstName.setText( userJson.getString("first_name"));
            etLastName.setText(userJson.getString("last_name"));
            etWeight.setText(String.valueOf(userJson.getInt("weight")));
            etAddress.setText(userJson.getString("address"));
            etEmail.setText(userJson.getString("email"));
            String photoUrl = common.getFullUrl(userJson.getString("photo"));
            Glide.with(context)
                    .load(photoUrl)
                    .into(fotoProfile);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressBar.setVisibility(View.GONE);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DatePickerFragment datePickerFragment;
            switch (view.getId()) {
                case R.id.relEditBirthDate:
                    datePickerFragment = DatePickerFragment.newInstance(R.id.tvBirthdate);
                    datePickerFragment.show(getFragmentManager(), "datePicker");
                    break;
                case R.id.relEditPregnancyStart:
                    datePickerFragment = DatePickerFragment.newInstance(R.id.tvPregnancyStart);
                    datePickerFragment.show(getFragmentManager(), "datePicker");
                    break;
                case R.id.edit_foto_profile:
                    requestStoragePermission();
                    showGalleryIntent();
                    break;
                case R.id.btnSaveProfile:
                    validator.validate();
                    break;
                case R.id.rel_edit_address:
                    showHideEditAddress(R.id.etAddress, R.id.ivEditAddress);
                    break;
                case R.id.rel_edit_email:
                    showHideEditAddress(R.id.etEmail, R.id.ivEditEmail);
                    break;
                case R.id.rel_edit_password:
                    Intent intent = new Intent(EditProfileActivity.this, EditPasswordActivity.class);
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

    private void showHideEditAddress(int editText, int imgView){
        final EditText edText = (EditText) findViewById(editText);
        ImageView imView = (ImageView) findViewById(imgView);
        int v = edText.getVisibility();
        if (v == View.GONE) {
            Log.d("fuck", "you");
            imView.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                    R.drawable.ic_keyboard_arrow_down_black_24dp, getTheme()));
            edText.setAlpha(0.0f);
            edText.animate()
                    .alpha(1.0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            edText.setVisibility(View.VISIBLE);
                        }
                    });

        }else{
//            Log.d("fuck", "her");
//            imView.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
//                    R.drawable.ic_chevron_right_black_24dp, getTheme()));
//            edText.setAlpha(1.0f);
//            edText.animate()
//                    .alpha(1.0f)
//                    .setDuration(300)
//                    .setListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationStart(Animator animation) {
//                            super.onAnimationStart(animation);
//                            edText.setVisibility(View.GONE);
//                        }
//                    });
        }
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
        saveProfile();
    }

    private String getUserRaw(){
        preferences = getSharedPreferences(LOGIN_PREFS, MODE_PRIVATE);
        return preferences.getString(USER_RAW,"");
    }

    private void setUserRaw(String userRaw){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(EMAIL, etEmail.getText().toString());
        editor.putString(USER_RAW, userRaw);
        editor.commit();
    }

    /**
     * Get Profile
     * @return JSONObject
     */
    private JSONObject getProfile(){
        JSONObject user;
        try {
            user = new JSONObject(getUserRaw());
            return user;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveProfile(){
        JSONObject user = getProfile();
        String apiToken = "";
        String id = "";
        try {
            apiToken = user.getString("api_token");
            id = user.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "/api/user/"+id+"?api_token="+apiToken;

        JSONObject userJson = new JSONObject();
        try {
            userJson.put("first_name", etFirstName.getText().toString());
            userJson.put("last_name", etLastName.getText().toString());
            userJson.put("address", etAddress.getText().toString());
            userJson.put("email", etEmail.getText().toString());
            userJson.put("birth_date", tvBirthdate.getText().toString());
            userJson.put("pregnancy_start", tvPregnancyStart.getText().toString());
            userJson.put("weight", etWeight.getText().toString());

            MyHTTPRequest myHTTPRequest = new MyHTTPRequest(this, view, url,
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
                Toast.makeText(EditProfileActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * Show the gallery intent, picking picture for profile
     */
    public void showGalleryIntent(){
        Intent mIntent = new Intent();
        mIntent.setType("image/*");
        mIntent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(mIntent,"Select Picture"),PICK_IMAGE_REQUEST);
    }

    /**
     * This is a callback, for showGalleryIntent().
     * It will set the fotoProfile ImageView with the new picture from gallery.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data !=null && data.getData() != null){
            filePath = data.getData();
            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),
                        data.getData());
                fotoProfile.setImageBitmap(mImageBitmap);
                try{
                    String realPath = common.getRealPathFromURI(filePath, context);
                    uploadFotoProfile(realPath);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("FilePathError", e.getLocalizedMessage()+", "+e.getMessage()+", caused by:"+e.getCause());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Upload Foto Profile to Server
     * @param mFilePath
     */
    private void uploadFotoProfile(String mFilePath){
        AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                new AndroidMultiPartEntity.ProgressListener() {

                    @Override
                    public void transferred(long num) {}
                });

        JSONObject userJson = getProfile();
        String apiToken = "";
        String id = "";
        try {
            apiToken = userJson.getString("api_token");
            id = userJson.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        File sourceFile = new File(mFilePath);
        // Adding file data to http body
        try {
            entity.addPart("image", new FileBody(sourceFile));
            entity.addPart("id", new StringBody(id));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = "/api/user/"+id+"/upload-photo?api_token="+apiToken;
        UploadToServer uploadToServer = new UploadToServer(progressBar, entity, new UploadToServer.ResultUpload() {
            @Override
            public void resultUploadExecute(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    if (!jsonObject.getBoolean("error")) {
                        Log.d("result", result);
                        try {
                            setUserRaw(jsonObject.getString("user"));
                        } catch (Exception e){
                            e.printStackTrace();
                            Log.e("SetUserRaw", e.getLocalizedMessage()+", caused:"+e.getCause());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, url);
        Log.d("url", url);
        uploadToServer.execute();
    }

    //Requesting permission to access the Storage
    private void requestStoragePermission(){
        if(ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            return;
        }

        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){


        }
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==STORAGE_PERMISSION_CODE){
            if(grantResults.length>0&&grantResults[0] == PackageManager.PERMISSION_DENIED){
                Toast.makeText(context,"Permission denied",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context,"Permission Granted", Toast.LENGTH_LONG).show();
            }
        }
    }

}
