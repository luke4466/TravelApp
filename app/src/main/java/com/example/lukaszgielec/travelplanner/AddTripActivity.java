package com.example.lukaszgielec.travelplanner;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class AddTripActivity extends AppCompatActivity {

    AutoCompleteTextView townName;

    ProgressBar progressBar;

    JSONArray JSONtowns = new JSONArray();

    TextView errorMsg;

    EditText tripName;
    EditText startDate;
    EditText endDate;
    Button createButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        getSupportActionBar().setTitle("Nowy wyjazd");


        TownsProviderTask townsProviderTask = new TownsProviderTask();
        townsProviderTask.execute();

        errorMsg = findViewById(R.id.errorMsg);

        townName = findViewById(R.id.townName);
        tripName = findViewById(R.id.tripName);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);

        startDate.setFocusable(false);
        endDate.setFocusable(false);

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GregorianCalendar gregorianCalendar = new GregorianCalendar();
                new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        Log.i("datePicker","i "+i+" i1 "+i1+" i2 "+i2);

                        GregorianCalendar start_date = new GregorianCalendar();
                        start_date.set(GregorianCalendar.YEAR,i);
                        start_date.set(GregorianCalendar.MONTH,i1);
                        start_date.set(GregorianCalendar.DAY_OF_MONTH,i2);
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/YYYY");
                        startDate.setText(format.format(start_date.getTime()).toString());

                    }
                }, gregorianCalendar.get(GregorianCalendar.YEAR), gregorianCalendar.get(GregorianCalendar.MONTH), gregorianCalendar.get(GregorianCalendar.DAY_OF_MONTH)).show();

            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GregorianCalendar gregorianCalendar = new GregorianCalendar();
                new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        Log.i("datePicker","i "+i+" i1 "+i1+" i2 "+i2);

                        GregorianCalendar start_date = new GregorianCalendar();
                        start_date.set(GregorianCalendar.YEAR,i);
                        start_date.set(GregorianCalendar.MONTH,i1);
                        start_date.set(GregorianCalendar.DAY_OF_MONTH,i2);
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/YYYY");
                        endDate.setText(format.format(start_date.getTime()).toString());

                    }
                }, gregorianCalendar.get(GregorianCalendar.YEAR), gregorianCalendar.get(GregorianCalendar.MONTH), gregorianCalendar.get(GregorianCalendar.DAY_OF_MONTH)).show();
            }
        });

        createButton = findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateTripTask createTripTask = new CreateTripTask();
                createTripTask.execute();
            }
        });
        progressBar = findViewById(R.id.progressBar);
    }


    private class CreateTripTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            createButton.setClickable(false);
        }
        JSONObject feedback = new JSONObject();
        @Override
        protected Void doInBackground(Void... voids) {

            try {


/*
                if (tripName.getText().toString().isEmpty()){
                    errorText+="Podaj nazwę wyjazdu \n";
                }



                if (!postData.has("town_id")){
                    errorText+="Wybierz miasto z proponowanych\n";
                }

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/YYYY");
                Date date = new Date();
                Date date1= new Date();
                try {
                    date = format.parse(startDate.getText().toString());
                    date1 = format.parse(endDate.getText().toString());

                    if (!date.before(date1) && !date.equals(date1)){
                        errorText+="Data wyjazdu musi być przed datą przyjazdu\n";
                    }


                } catch (ParseException e) {
                    //e.printStackTrace();
                    errorText+="Data jest błędna użyj formatu dd/MM/YYYY\n";

                }


                if (!errorText.equals("")){

                }else {
*/
                    JSONObject postData = new JSONObject();

                for (int i = 0; i<JSONtowns.length(); i++){

                    if (townName.getText().toString().matches(JSONtowns.getJSONObject(i).getString("name"))){

                        postData.put("town_id",JSONtowns.getJSONObject(i).getInt("id"));
                        break;
                    }

                }

                    postData.put("name",tripName.getText().toString());
                    postData.put("start_date",startDate.getText().toString());
                    postData.put("end_date",endDate.getText().toString());


                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName(),MODE_PRIVATE);
                    feedback = DatabaseConnector.performPostCall("/trips",postData,sharedPreferences.getString("token",""),getApplicationContext());
                    Log.i("CreateTrip",feedback.toString());







            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try{

                if (feedback.getInt("responseCode") == 200){
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }else {

                    JSONObject errors = feedback.getJSONObject("errors");

                    String errorText = "";

                    if(errors.has("start_date")){
                        errorText+="start date "+errors.getJSONArray("start_date").get(0).toString();
                        errorText+="\n";
                    }
                    if(errors.has("end_date")){
                        errorText+="end date "+errors.getJSONArray("end_date").get(0).toString();
                        errorText+="\n";
                    }
                    if(errors.has("town_id")){
                        errorText+="town "+errors.getJSONArray("town_id").get(0).toString();
                        errorText+="\n";
                    }
                    if(errors.has("name")){
                        errorText+="name "+errors.getJSONArray("name").get(0).toString();
                        errorText+="\n";
                    }


                    errorMsg.setText(errorText);
                    errorMsg.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    createButton.setClickable(true);

                }

            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }


    private class TownsProviderTask extends AsyncTask<Void,Void,Void>{

        JSONObject response;

        @Override
        protected Void doInBackground(Void... voids) {
            try{

                response = DatabaseConnector.performGetCall("/towns","",getApplicationContext());
                Log.i("townsList",response.toString());

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try{

                if(response.getInt("responseCode") == 200){

                    ArrayList<String> townsNames = new ArrayList<>();
                    JSONtowns = response.getJSONObject("response").getJSONArray("towns");

                    for (int i = 0; i<JSONtowns.length();i++){
                        townsNames.add(JSONtowns.getJSONObject(i).getString("name"));
                    }

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, townsNames);
                    townName.setAdapter(adapter);
                }

            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }
}
