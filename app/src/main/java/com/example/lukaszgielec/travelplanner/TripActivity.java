package com.example.lukaszgielec.travelplanner;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class TripActivity extends AppCompatActivity {

    int tripID = 0;
    int townID = 0;

    ProgressBar progressBar;

    LinearLayout placesContainer;
    LinearLayout participantsContainer;
    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView infoTextView;
    TextView placesToSeeTextView;

    FloatingActionButton floatingActionButton;
    ImageView imageViewAppBar;
    PlaceDeleteListener placeDeleteListener = new PlaceDeleteListener();
    ParticipantDeleteListener participantDeleteListener = new ParticipantDeleteListener();
    PlaceOnClickListener placeOnClickListener = new PlaceOnClickListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tripID = getIntent().getIntExtra("trip_id",0);
        if (tripID == 0){
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }

        setContentView(R.layout.trip_activity);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        imageViewAppBar = findViewById(R.id.imageAppBar);

        //Picasso.with(getApplicationContext()).load(R.drawable.ic_add_black_48dp).into();

        progressBar = findViewById(R.id.progressBar);
        placesContainer = findViewById(R.id.placesContainer);
        participantsContainer = findViewById(R.id.participantsContainer);
        infoTextView = findViewById(R.id.info);
        placesToSeeTextView = findViewById(R.id.placesToSeeTextView);
        floatingActionButton = findViewById(R.id.fab);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AddPlaceActivity.class);
                intent.putExtra("town_id", townID);
                intent.putExtra("trip_id", tripID);
                startActivity(intent);
            }
        });

        TextView addParticipantButton = findViewById(R.id.addParticipant);
        addParticipantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddParticipantDialog alert = new AddParticipantDialog();
                alert.showDialog((Activity)view.getContext(), "Error de conexión al servidor");
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        TripDetailsTask tripDetailsTask = new TripDetailsTask();
        tripDetailsTask.execute();
    }

    private class TripDetailsTask extends AsyncTask<Void,Void,Void>{

        JSONObject response = new JSONObject();
        JSONArray places = new JSONArray();
        JSONArray participants = new JSONArray();

        SharedPreferences sharedPreferences;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            placesToSeeTextView.setText("Miejsca do odwiedzenia");
            placesContainer.removeAllViews();
            participantsContainer.removeAllViews();
        }

        Bitmap background;
        @Override
        protected Void doInBackground(Void... voids) {

            try{


                sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName(),MODE_PRIVATE);
                response = DatabaseConnector.performGetCall("/trips/"+tripID,sharedPreferences.getString("token",""),getApplicationContext());
                Log.i("responseTripDetails",response.toString());

                places = response.getJSONObject("response").getJSONArray("places");
                participants = response.getJSONObject("response").getJSONArray("users");
                townID = response.getJSONObject("response").getInt("town_id");


                background = BitmapFactory.decodeStream((InputStream)new URL("http://"+sharedPreferences.getString("ip","")+response.getJSONObject("response").getString("photo_url")).getContent());





            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {

            try{

                if (response.getInt("responseCode") == 200){

                   for (int i = 0; i<places.length();i++){
                        JSONObject JSONplace = places.getJSONObject(i);
                        View cardView = getLayoutInflater().inflate(R.layout.place_to_see_cardview,null);

                        TextView placeName = cardView.findViewById(R.id.placeName);
                        TextView placeAddress = cardView.findViewById(R.id.placeAddress);
                        TextView descPlace = cardView.findViewById(R.id.descPlace);
                        TextView placeType = cardView.findViewById(R.id.placeType);
                        ImageView deleteButton = cardView.findViewById(R.id.delete);


                        placeName.setText(JSONplace.getString("name"));
                        placeAddress.setText(JSONplace.getString("address"));
                        descPlace.setText(JSONplace.getString("description"));
                        placeType.setText(JSONplace.getString("type"));

                        deleteButton.setTag(JSONplace.getInt("id"));
                        deleteButton.setOnClickListener(placeDeleteListener);

                       CardView cardView1 = cardView.findViewById(R.id.cardView);
                       cardView1.setTag(JSONplace.getInt("id"));
                       cardView1.setOnClickListener(placeOnClickListener);


                        placesContainer.addView(cardView);
                    }

                    if (places.length() == 0) placesToSeeTextView.setText("Brak miejsc do odwiedzenia!");


                   for (int i = 0; i < participants.length();i++){

                       JSONObject JSONuser = participants.getJSONObject(i);

                       View cardView = getLayoutInflater().inflate(R.layout.participant_card_view,null);

                       TextView username = cardView.findViewById(R.id.username);
                       TextView FABletter = cardView.findViewById(R.id.FABletter);
                       ImageView deleteButton = cardView.findViewById(R.id.delete);

                       username.setText( JSONuser.getString("username"));
                       FABletter.setText(""+JSONuser.getString("username").toUpperCase().charAt(0));



                       if (JSONuser.getInt("id") == Integer.parseInt(sharedPreferences.getString("id","0"))){
                           deleteButton.setVisibility(View.VISIBLE);
                           deleteButton.setTag(JSONuser.getInt("id"));
                           deleteButton.setOnClickListener(participantDeleteListener);
                       }else if(Integer.parseInt(sharedPreferences.getString("id","0")) == response.getJSONObject("response").getInt("admin_id")){
                           deleteButton.setVisibility(View.VISIBLE);
                           deleteButton.setTag(JSONuser.getInt("id"));
                           deleteButton.setOnClickListener(participantDeleteListener);
                       }

                       participantsContainer.addView(cardView);

                   }

                    collapsingToolbarLayout.setTitle( ""+response.getJSONObject("response").getString("town_name"));
                    collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.transparent));
                    //collapsingToolbarLayout.setExpandedTitleColor();
                   //collapsingToolbarLayout.setTi(""+response.getJSONObject("response").getString("town_name"));
                    infoTextView.setText(""+response.getJSONObject("response").getString("name")+"\n"+response.getJSONObject("response").getString("start_date")+" - "+response.getJSONObject("response").getString("end_date"));
                    imageViewAppBar.setImageBitmap(background);


                }else {
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                }



            }catch (Exception e){
                e.printStackTrace();
            }


            progressBar.setVisibility(View.GONE);



        }
    }


    private class PlaceDeleteListener implements View.OnClickListener{

        int id;
        @Override
        public void onClick(View view) {
            id = (int) view.getTag();
            new AlertDialog.Builder(view.getContext())
                    .setIcon(R.drawable.ic_delete_black_48dp)
                    .setTitle("Usuwanie miejsca")
                    .setMessage("Jesteś pewny, że nie chcesz odwiedzić tego miejsca?")
                    .setPositiveButton("Tak", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i("TAK!!!","dsfsdgfdgf");
                            DeleteTask deleteTask = new DeleteTask();
                            deleteTask.execute(PLACE_TYPE_DELETE,id);


                        }

                    })
                    .setNegativeButton("Nie", null)
                    .show();

        }
    }

    private class ParticipantDeleteListener implements View.OnClickListener{

        int id;
        @Override
        public void onClick(View view) {
            id = (int) view.getTag();
            new AlertDialog.Builder(view.getContext())
                    .setIcon(R.drawable.ic_delete_black_48dp)
                    .setTitle("Usuwanie uczestnika")
                    .setMessage("Jesteś pewny, że chcesz usunąć tego uczesnika?")
                    .setPositiveButton("Tak", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i("TAK1111!!!","dsfsdgfdgf");
                            DeleteTask deleteTask = new DeleteTask();
                            deleteTask.execute(PARTICIPANT_TYPE_DELETE,id);


                        }

                    })
                    .setNegativeButton("Nie", null)
                    .show();

        }

        }



    final int PLACE_TYPE_DELETE = 0;
    final int PARTICIPANT_TYPE_DELETE = 1;
    private class DeleteTask extends AsyncTask<Integer,Void,Void>{

        boolean selfDelete = false;
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        JSONObject response = new JSONObject();
        @Override
        protected Void doInBackground(Integer... integers) {

            try{
                int type = integers[0];
                int id = integers[1];

                if (type == PLACE_TYPE_DELETE){

                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName(),MODE_PRIVATE);
                    response = DatabaseConnector.performGetCall("/trips/"+tripID+"/removeplace?place="+id,sharedPreferences.getString("token",""),getApplicationContext());
                    Log.i("DELETE response", response.toString());


                }else if(type == PARTICIPANT_TYPE_DELETE){

                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName(),MODE_PRIVATE);
                    response = DatabaseConnector.performGetCall("/trips/"+tripID+"/unjoin?user="+id,sharedPreferences.getString("token",""),getApplicationContext());
                    Log.i("DELETE response", response.toString());
                    if (id == Integer.parseInt(sharedPreferences.getString("id","0"))) selfDelete = true;

                }





            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try{
                if (response.getInt("responseCode") == 200){
                    //finish();

                    if (selfDelete){
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }else {
                        TripDetailsTask tripDetailsTask = new TripDetailsTask();
                        tripDetailsTask.execute();
                        //Intent intent = new Intent(getApplicationContext(), TripActivity.class);
                        //intent.putExtra("trip_id",tripID);
                        //startActivity(intent);
                    }

                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }


    public class AddParticipantDialog {

        TextView errorMsg;
        AutoCompleteTextView usersAutoCompleteTextView;
        ArrayList<String> usernameList = new ArrayList<>();
        Dialog dialog;
        ProgressBar progressBar;

        public void showDialog(Activity activity, String msg){
            dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.add_participant_dialog);
            usersAutoCompleteTextView = dialog.findViewById(R.id.usernameSearch);
            errorMsg = dialog.findViewById(R.id.errorMsg);
            progressBar = dialog.findViewById(R.id.progressBar);


            Button dialogButton = dialog.findViewById(R.id.confirmButton);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddParticipantTask addParticipantTask = new AddParticipantTask();
                    addParticipantTask.execute();
                }
            });
            dialog.show();
            UsernameProviderTask usernameProviderTask = new UsernameProviderTask();
            usernameProviderTask.execute();
        }

        JSONArray JSONusers = new JSONArray();
        private class UsernameProviderTask extends AsyncTask<Void,Void,Void>{

            JSONObject response = new JSONObject();

            @Override
            protected void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try{

                    response = DatabaseConnector.performGetCall("/users","",getApplicationContext());
                    Log.i("usersList",response.toString());
                    JSONusers = response.getJSONObject("response").getJSONArray("users");

                    for (int i = 0; i<JSONusers.length();i++){
                        usernameList.add(JSONusers.getJSONObject(i).getString("username"));
                    }


                }catch (Exception e){
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, usernameList);
                usersAutoCompleteTextView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);

            }
        }

        private class AddParticipantTask extends AsyncTask<Void,Void,Void>{

            JSONObject response = new JSONObject();

            @Override
            protected void onPreExecute() {
                errorMsg.setText("");
                progressBar.setVisibility(View.VISIBLE);

            }

            @Override
            protected Void doInBackground(Void... voids) {

                try{

                    int id = 0;
                    for (int i = 0; i < JSONusers.length(); i++){
                        if(usersAutoCompleteTextView.getText().toString().matches(JSONusers.getJSONObject(i).getString("username"))){
                            id = JSONusers.getJSONObject(i).getInt("id");
                        }
                    }
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName(),MODE_PRIVATE);
                    response = DatabaseConnector.performGetCall("/trips/"+tripID+"/join?user="+id,sharedPreferences.getString("token",""),getApplicationContext());
                    Log.i("ADD response", response.toString());



                }catch (Exception e){
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                try{

                    if (response.getInt("responseCode") == 200){
                        //finish();
                        dialog.dismiss();
                        TripDetailsTask tripDetailsTask = new TripDetailsTask();
                        tripDetailsTask.execute();
                        //Intent intent = new Intent(getApplicationContext(), TripActivity.class);
                        //intent.putExtra("trip_id",tripID);
                        //startActivity(intent);
                    }else {
                        errorMsg.setText(response.getJSONObject("errors").getJSONArray("username").getString(0));
                    }
                    progressBar.setVisibility(View.GONE);



                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }


    private class PlaceOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {

            if (view.getTag() != null){

                    Intent intent = new Intent(view.getContext(),PlaceDetail.class);
                    intent.putExtra("place_id",(int)view.getTag());
                    startActivity(intent);


            }


        }
    }
}
