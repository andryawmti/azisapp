package com.kudubisa.app.navdrawertemplate.remote;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Iterator;

/**
 * Created by asus on 4/14/18.
 */

public class MJSONParser {
    JSONObject mJSONObject;

    public MJSONParser(JSONObject mJSONObject) {
        this.mJSONObject = mJSONObject;
    }

    public String getJSONObject() throws Exception{
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        Iterator<String> itr =  mJSONObject.keys();
        while (itr.hasNext()){
            String key = itr.next();
            Object value = mJSONObject.get(key);
            if(first){
                first = false;
            }else{
                sb.append("&");
            }
            sb.append(URLEncoder.encode(key, "utf-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(value.toString(), "utf-8"));
        }
        return sb.toString();
    }
}
