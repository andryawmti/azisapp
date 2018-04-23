package com.kudubisa.app.navdrawertemplate.remote;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by asus on 4/17/18.
 */

public class Common {
    private static final String HOST = "http://dev2.dadadasnoopdog.com";

    private final static String LOGIN_PREFS = "login_prefs";
    private final static String EMAIL = "email";
    private final static String PASSWORD = "password";
    private final static  String USER_RAW = "userRaw";

    SharedPreferences preferences;

    public String getRealPathFromURI(Uri contentURI, Context context) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public String getHost(){
        return HOST;
    }

    public String getFullUrl(String url){
        return HOST+url;
    }

    public String getUserRaw(Context context){
        preferences = context.getSharedPreferences(LOGIN_PREFS, MODE_PRIVATE);
        return preferences.getString(USER_RAW,"");
    }

    public void setUserRaw(String userRaw){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_RAW, userRaw);
        editor.commit();
    }

    public void setEmail(String email){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(EMAIL, email);
        editor.commit();
    }

    public void setPassword(String password){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PASSWORD, password);
        editor.commit();
    }
}
