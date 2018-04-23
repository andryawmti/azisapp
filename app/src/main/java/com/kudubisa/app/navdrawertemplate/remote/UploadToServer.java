package com.kudubisa.app.navdrawertemplate.remote;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;

import static android.view.View.GONE;

/**
 * Created by asus on 4/17/18.
 */

public class UploadToServer extends AsyncTask<Void, Integer, String> {
    private static final String BASE_URL = "http://dev2.dadadasnoopdog.com";

    private String TAG = "upload";
    private ProgressBar mProgressBar;
    private long totalSize = 0;
    private ResultUpload mResultUpload;
    private AndroidMultiPartEntity entity;
    private String url;

    public UploadToServer(ProgressBar mProgressBar, AndroidMultiPartEntity entity, ResultUpload mResultUpload, String url) {
        this.mProgressBar = mProgressBar;
        this.mResultUpload = mResultUpload;
        this.entity = entity;
        this.url = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        // Making progress bar visible
    }

    @Override
    protected String doInBackground(Void... params) {
        return uploadFile();
    }

    @SuppressWarnings("deprecation")
    private String uploadFile() {
        String responseString = null;

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(BASE_URL+url);
        try {

            totalSize = entity.getContentLength();
            httppost.setEntity(entity);
            // Making server call
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r_entity = response.getEntity();

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // Server response
                responseString = EntityUtils.toString(r_entity);
            } else {
                responseString = "Error occurred! Http Status Code: "
                        + statusCode;
            }
        } catch (ClientProtocolException e) {
            responseString = e.toString();
        } catch (IOException e) {
            responseString = e.toString();
        }

        return responseString;

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.e(TAG, "Response from server: " + result);
        mProgressBar.setVisibility(GONE);
        mResultUpload.resultUploadExecute(result);
    }

    public static abstract class ResultUpload{
        public abstract void resultUploadExecute(String result);

    }

}