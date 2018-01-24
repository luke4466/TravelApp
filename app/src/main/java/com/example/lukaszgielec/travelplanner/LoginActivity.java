package com.example.lukaszgielec.travelplanner;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends AppCompatActivity {

    EditText usernameEditText;
    EditText passwordEditText;
    TextView messageTextView;

    ProgressBar progressBar;

    @Override
    protected void onStart() {
        super.onStart();


        ChangeIPDialog changeIPDialog = new ChangeIPDialog();
        changeIPDialog.showDialog(this);

    }

    private class SendDeviceIDTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            JSONObject jsonObject = DatabaseConnector.sendDeviceID(getApplicationContext());
            Log.i("wysłane IDdevice",jsonObject.toString());
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        messageTextView = findViewById(R.id.message);
        progressBar = findViewById(R.id.progressBar);




        Button registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginTask loginTask = new LoginTask();
                loginTask.execute();
            }
        });
    }


    private class LoginTask extends AsyncTask<Void,Void,Void>{

        String username;
        String password;
        JSONObject JSONresponse = new JSONObject();

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            username = usernameEditText.getText().toString();
            password = passwordEditText.getText().toString();


        }

        @Override
        protected Void doInBackground(Void... voids) {

            try{
                JSONObject data = new JSONObject();
                data.put("username",username);
                data.put("password",password);
                JSONresponse = DatabaseConnector.performPostCall("/login", data,getApplicationContext());
                Log.i("LoginActivity",JSONresponse.toString());


                if (JSONresponse.getInt("responseCode") == 200) {
                    if (JSONresponse.getJSONObject("response").has("token")) {
                        SharedPreferences prefs = getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("token", JSONresponse.getJSONObject("response").getString("token"));
                        editor.putString("id", JSONresponse.getJSONObject("response").getString("id"));
                        editor.commit();
                        DatabaseConnector.sendDeviceID(getApplicationContext());

                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            try {
                if (JSONresponse.getInt("responseCode") == 200) {
                    if (JSONresponse.getJSONObject("response").has("token")){

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                    }



                } else {

                    JSONObject errors = JSONresponse.getJSONObject("errors");

                    String errorText = "";

                    if (errors.has("login")) {
                        errorText += errors.getJSONArray("login").get(0).toString();
                        errorText += "\n";
                    }


                    messageTextView.setText(errorText);
                    messageTextView.setVisibility(View.VISIBLE);

                    progressBar.setVisibility(View.GONE);
                }
            }catch (Exception e){
                e.printStackTrace();
            }


            super.onPostExecute(aVoid);



        }
    }

    @Override
    public void onBackPressed() {

    }


    public class ChangeIPDialog {

        EditText ipEditText;
        Dialog dialog;

        public void showDialog(Activity activity) {
            dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.change_ip_dialog);
            ipEditText = dialog.findViewById(R.id.ipEditText);
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName(),MODE_PRIVATE);
            ipEditText.setText(sharedPreferences.getString("ip","192.168.0.12:3000"));

            final Button dialogButton = dialog.findViewById(R.id.button);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName(),MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("ip", ipEditText.getText().toString());
                    editor.commit();




                    if(sharedPreferences.contains("token")){
                        SendDeviceIDTask sendDeviceIDTask = new SendDeviceIDTask();
                        sendDeviceIDTask.execute();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }

                    dialog.dismiss();
                }
            });
            dialog.show();

        }
    }


}
