package com.example.lukaszgielec.travelplanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Lukasz Gielec on 20.01.2018.
 */

public abstract class DatabaseConnector {



    public static JSONObject  performPostCall(String requestURL,
                                              JSONObject postData, Context mContext) {

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getPackageName(),MODE_PRIVATE);
        String serverIP = "http://" + sharedPreferences.getString("ip","192.168.0.12:3000");

        JSONObject JSONresponse = new JSONObject();
        URL url;
        String response = "";
        try {
            url = new URL(serverIP+requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.addRequestProperty("Accept", "application/json");
            conn.addRequestProperty("Content-Type", "application/json");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(postData.toString());

            Log.i("writer",postData.toString());

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();
            Log.i("responseCode",""+responseCode);
            if (responseCode >= 200 && responseCode < 300) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("responseCode",responseCode);
                jsonObject.put("response",new JSONObject(response));
                JSONresponse = jsonObject;
            } else {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("responseCode",responseCode);
                JSONObject errors = new JSONObject(new JSONObject(response).getJSONObject("errors").toString());
                jsonObject.put("errors",errors);
                JSONresponse = jsonObject;
            }






        } catch (Exception e) {
            e.printStackTrace();
        }

        return JSONresponse;
    }

    public static JSONObject  performGetCall(String requestURL,
                                              String token, Context mContext) {

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getPackageName(),MODE_PRIVATE);
        String serverIP = "http://" + sharedPreferences.getString("ip","192.168.0.12:3000");

        JSONObject JSONresponse = new JSONObject();
        URL url;
        String response = "";
        try {
            url = new URL(serverIP+requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.addRequestProperty("Accept", "application/json");
            conn.addRequestProperty("Content-Type", "application/json");
            conn.addRequestProperty("Authorization", "Token token="+token);
            conn.setDoInput(true);


            int responseCode=conn.getResponseCode();
            Log.i("responseCode",""+responseCode);
            if (responseCode >= 200 && responseCode < 300) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("responseCode",responseCode);
                jsonObject.put("response",new JSONObject(response));
                JSONresponse = jsonObject;
            } else {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("responseCode",responseCode);
                JSONObject errors = new JSONObject(new JSONObject(response).getJSONObject("errors").toString());
                jsonObject.put("errors",errors);
                JSONresponse = jsonObject;
            }






        } catch (Exception e) {
            e.printStackTrace();
        }

        return JSONresponse;
    }

    public static JSONObject  performPostCall(String requestURL,
                                              JSONObject postData,String token, Context mContext) {

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getPackageName(),MODE_PRIVATE);
        String serverIP = "http://" + sharedPreferences.getString("ip","192.168.0.12:3000");

        JSONObject JSONresponse = new JSONObject();
        URL url;
        String response = "";
        try {
            url = new URL(serverIP+requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.addRequestProperty("Accept", "application/json");
            conn.addRequestProperty("Content-Type", "application/json");
            conn.addRequestProperty("Authorization", "Token token="+token);
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(postData.toString());

            Log.i("writer",postData.toString());

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();
            Log.i("responseCode",""+responseCode);
            if (responseCode >= 200 && responseCode < 300) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("responseCode",responseCode);
                jsonObject.put("response",new JSONObject(response));
                JSONresponse = jsonObject;
            } else {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("responseCode",responseCode);
                JSONObject errors = new JSONObject(new JSONObject(response).getJSONObject("errors").toString());
                jsonObject.put("errors",errors);
                JSONresponse = jsonObject;
            }






        } catch (Exception e) {
            e.printStackTrace();
        }

        return JSONresponse;
    }


    public static JSONObject sendDeviceID(Context mContext){

        String deviceID = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getPackageName(),MODE_PRIVATE);

        JSONObject postData = new JSONObject();
        try{
            postData.put("device_id",deviceID);
            postData.put("token",sharedPreferences.getString("token",""));

        }catch (Exception e){
            e.printStackTrace();
        }

        Log.i("DEVICEID",postData.toString());

        return performPostCall("/device_id",postData,mContext);


    }
}
