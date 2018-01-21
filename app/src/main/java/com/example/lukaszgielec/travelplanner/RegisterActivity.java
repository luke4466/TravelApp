package com.example.lukaszgielec.travelplanner;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    TextView messageTextView;
    EditText usernameEditText;
    EditText passwordEditText;
    EditText nameEditText;
    EditText lastNameEditText;
    Button doneButton;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        nameEditText = findViewById(R.id.nameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        messageTextView = findViewById(R.id.message);

        progressBar = findViewById(R.id.progressBar);

        doneButton = findViewById(R.id.registerButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterTask registerTask = new RegisterTask();
                registerTask.execute();
            }
        });


    }


    private class RegisterTask extends AsyncTask<Void,Void,Void>{


        String username;
        String password;
        String name;
        String last_name;


        JSONObject JSONresponse = new JSONObject();

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            username = usernameEditText.getText().toString();
            password = passwordEditText.getText().toString();
            name = nameEditText.getText().toString();
            last_name = lastNameEditText.getText().toString();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            try{
                JSONObject postData = new JSONObject();
                postData.put("username",username);
                postData.put("password",password);
                postData.put("name",name);
                postData.put("last_name",last_name);

                JSONresponse = DatabaseConnector.performPostCall("http://192.168.0.12:3000/users", postData);
                Log.i("RegisterActivity",JSONresponse.toString());

            }catch (Exception e){
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                if(JSONresponse.getInt("responseCode") == 201 ){
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                }else {

                    JSONObject errors = JSONresponse.getJSONObject("errors");

                    String errorText = "";

                        if(errors.has("username")){
                            errorText+="username "+errors.getJSONArray("username").get(0).toString();
                            errorText+="\n";
                        }
                        if(errors.has("password")){
                            errorText+="password "+errors.getJSONArray("password").get(0).toString();
                            errorText+="\n";
                        }
                        if(errors.has("name")){
                            errorText+="name "+errors.getJSONArray("name").get(0).toString();
                            errorText+="\n";
                        }
                        if(errors.has("last_name")){
                            errorText+="last_name "+errors.getJSONArray("last_name").get(0).toString();
                            errorText+="\n";
                        }




                    messageTextView.setText(errorText);
                    messageTextView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                }









            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }


}
