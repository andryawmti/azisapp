package com.kudubisa.app.azisapp;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kudubisa.app.azisapp.remote.AndroidMultiPartEntity;
import com.kudubisa.app.azisapp.remote.Common;
import com.kudubisa.app.azisapp.remote.MyHTTPRequest;
import com.kudubisa.app.azisapp.remote.UploadToServer;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by asus on 7/22/18.
 */

public class AddContributionActivity extends AppCompatActivity {
    private ImageView selectPicture;
    private ImageView picture;
    private Button btnSave;

    @NotEmpty(message = "Title should not be empty")
    private EditText edTitle;

    @NotEmpty(message = "Latitude should not be empty")
    private EditText edLatitude;

    @NotEmpty(message = "Longitude should not be empty")
    private EditText edLongitude;

    @NotEmpty(message = "Description should not be empty")
    private EditText edDescription;

    private static final int STORAGE_PERMISSION_CODE=123;

    int PICK_IMAGE_REQUEST=1;
    Uri filePath=null;
    private String realPath = null;
    private Bitmap mImageBitmap;

    private Context context;

    private Common common;

    private ProgressBar progressBar;

    private Validator validator;

    private View view;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contribution);
        context = getApplicationContext();
        common = new Common();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Contribute");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        selectPicture = (ImageView) findViewById(R.id.selectPicture);
        btnSave = (Button) findViewById(R.id.btnSave);
        picture = (ImageView) findViewById(R.id.picture);
        edTitle = (EditText) findViewById(R.id.edTitle);
        edLatitude = (EditText) findViewById(R.id.edLatitude);
        edLongitude = (EditText) findViewById(R.id.edLongitude);
        edDescription = (EditText) findViewById(R.id.edDescription);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        selectPicture.setOnClickListener(onClickListener);
        btnSave.setOnClickListener(onClickListener);
        picture.setOnClickListener(onClickListener);

        validator = new Validator(this);
        validator.setValidationListener(validationListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            view = v;
            switch (v.getId()) {
                case R.id.selectPicture:
                    requestStoragePermission();
                    showGalleryIntent();
                    break;
                case R.id.picture:
                    requestStoragePermission();
                    showGalleryIntent();
                    break;
                case R.id.btnSave:
                    validator.validate();
                    break;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                getSupportFragmentManager().popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
                picture.setImageBitmap(mImageBitmap);
                try{
                    realPath = common.getRealPathFromURI(filePath, context);
                    Log.d("realPath", realPath);
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
     * @param desId
     */
    private void uploadDestinationPicture(String mFilePath, String desId){
        AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                new AndroidMultiPartEntity.ProgressListener() {

                    @Override
                    public void transferred(long num) {}
                });

        String apiToken = "";
        try {
            JSONObject userJson = new JSONObject(common.getUserRaw(context));
            apiToken = userJson.getString("api_token");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        File sourceFile = new File(mFilePath);
        // Adding file data to http body
        try {
            entity.addPart("picture", new FileBody(sourceFile));
            entity.addPart("destination_id", new StringBody(desId));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = "/api/destination/upload-destination-picture/"+desId+"?api_token="+apiToken;
        Log.d("url upload despic", url);
        UploadToServer uploadToServer = new UploadToServer(progressBar, entity, new UploadToServer.ResultUpload() {
            @Override
            public void resultUploadExecute(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    if (!jsonObject.getBoolean("error")) {
                        Log.d("result", result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, url);
        uploadToServer.execute();
    }

    //Requesting permission to access the Storage
    private void requestStoragePermission(){
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){}
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==STORAGE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){
                Toast.makeText(context,"Permission denied", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context,"Permission Granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveContribution(View view) {
        JSONObject params = new JSONObject();
        String api_token = "";
        String userId = "";
        try {
            JSONObject userJson = new JSONObject(common.getUserRaw(context));
            api_token = userJson.getString("api_token");
            userId = userJson.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            params.put("title", edTitle.getText().toString());
            params.put("latitude", edLatitude.getText().toString());
            params.put("longitude", edLongitude.getText().toString());
            params.put("description", edDescription.getText().toString());
            params.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "/api/destination/add-contribution?api_token="+api_token;

        MyHTTPRequest myHTTPRequest = new MyHTTPRequest(context, view, url, "POST", params, response, progressBar);
        myHTTPRequest.execute();
    }

    MyHTTPRequest.HTTPResponse response = new MyHTTPRequest.HTTPResponse() {
        @Override
        public void response(String body, View view) {
            Toast.makeText(context, body, Toast.LENGTH_SHORT).show();
            try {
                JSONObject result = new JSONObject(body);
                Toast.makeText(context, result.getString("message"), Toast.LENGTH_SHORT).show();
                if (!result.getBoolean("error")) {
                    if (!realPath.equals("") || !realPath.isEmpty() || !realPath.equals("null")) {
                        uploadDestinationPicture(realPath, result.getString("destination_id"));
                    }
                } else {
                    Toast.makeText(context, result.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };


    Validator.ValidationListener validationListener = new Validator.ValidationListener() {
        @Override
        public void onValidationSucceeded() {
            saveContribution(view);
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
}
