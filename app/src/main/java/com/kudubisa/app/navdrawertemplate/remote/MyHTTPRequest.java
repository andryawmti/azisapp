package com.kudubisa.app.navdrawertemplate.remote;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by asus on 4/14/18.
 */

public class MyHTTPRequest extends AsyncTask<String, Void, String> {
    private static final String BASE_URL = "http://dev2.dadadasnoopdog.com";
    private static final String MY_HTTP_REQUEST = "MyHTTPRequest";
    Context mContext;
    JSONObject mJSONObject;
    MJSONParser mjsonParser;
    String mUrl;
    String method;
    HTTPResponse mHttpResponse;
    View mView;
    ProgressBar mProgressBar;
    public MyHTTPRequest(Context context, View view, String url, String method, JSONObject jsonObject,
                         HTTPResponse httpResponse, ProgressBar progressBar) {
        this.mContext = context;
        this.mView = view;
        this.mUrl = url;
        this.method = method;
        this.mJSONObject = jsonObject;
        this.mHttpResponse = httpResponse;
        this.mProgressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        String error = "HTTP Response Code is not 200";
        String body="";
        int rc=0;
        try{
            URL url = new URL(BASE_URL+mUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(30000);
            con.setConnectTimeout(30000);
            con.setRequestMethod(method);
            con.setDoOutput(true);
            con.setDoInput(true);
            OutputStream os = con.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);
            mjsonParser = new MJSONParser(mJSONObject);
            bw.write(mjsonParser.getJSONObject());
            bw.flush();
            bw.close();
            os.close();
            rc = con.getResponseCode();
            if (rc >= HttpURLConnection.HTTP_OK && rc <= HttpURLConnection.HTTP_ACCEPTED) {
                Log.d(MY_HTTP_REQUEST, "HTTP_OK:"+rc);
                InputStreamReader isr = new InputStreamReader(con.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine())!=null) {
                    sb.append(line);
                }
                Log.d(MY_HTTP_REQUEST, sb.toString());
                body = sb.toString();
            }
        }catch(Exception e){
            e.printStackTrace();
            Log.e(MY_HTTP_REQUEST,e.getMessage());
            error = e.getMessage();
        }

        if (rc >= HttpURLConnection.HTTP_OK && rc <= HttpURLConnection.HTTP_ACCEPTED) {
            Log.d("fuck", "you"+rc);
            return body;
        }else{
            Log.d("fuck", "her"+rc);
            return error;
        }

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mHttpResponse.response(s, mView);
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    public static abstract class HTTPResponse{
        public abstract void response(String body, View view);
    }
}
