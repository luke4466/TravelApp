package com.example.lukaszgielec.travelplanner;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddPlaceActivity extends AppCompatActivity {

    int townID = 0;
    int tripID = 0;

    LinearLayout placesContainer;
    EditText search;
    ProgressBar progressBar;

    JSONArray places = new JSONArray();
    JSONArray tripPlaces = new JSONArray();

    MyClickListener myClickListener = new MyClickListener();
    MyReviewsListener myReviewsListener = new MyReviewsListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        townID = getIntent().getIntExtra("town_id",0);
        tripID = getIntent().getIntExtra("trip_id",0);
        if (townID == 0 || tripID == 0){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }

        getSupportActionBar().setTitle("Dodaj miejsca");

        placesContainer = findViewById(R.id.placesContainer);
        search = findViewById(R.id.search);
        progressBar = findViewById(R.id.progressBar);


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                populatePlaces(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();

        PlacesProviderTask placesProviderTask = new PlacesProviderTask();
        placesProviderTask.execute();
    }

    private class PlacesProviderTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            search.setClickable(false);
        }

        JSONObject response = new JSONObject();

        @Override
        protected Void doInBackground(Void... voids) {
            try{

                response = DatabaseConnector.performGetCall("http://192.168.0.12:3000/towns/"+townID+"/places","");
                Log.i("places",response.toString());
                places = response.getJSONObject("response").getJSONArray("places");

                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName(),MODE_PRIVATE);
                JSONObject response = DatabaseConnector.performGetCall("http://192.168.0.12:3000/trips/"+tripID,sharedPreferences.getString("token",""));
                Log.i("tripPlaces",response.toString());
                tripPlaces = response.getJSONObject("response").getJSONArray("places");


                for (int i =0; i<places.length();i++){
                    for (int j = 0; j<tripPlaces.length(); j++){

                        if (tripPlaces.getJSONObject(j).getInt("id") == places.getJSONObject(i).getInt("id")){

                            places.getJSONObject(i).put("added",true);

                        }

                    }


                }



            }catch (Exception e){
                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            progressBar.setVisibility(View.INVISIBLE);
            search.setClickable(true);
            populatePlaces("");

        }
    }


    private void populatePlaces(String query){

        try{
            placesContainer.removeAllViews();

            if (query.equals("")){

                for (int i = 0; i< places.length(); i++){
                    placesContainer.addView(populatePlacesView(i));
                    if (places.getJSONObject(i).has("childID")){
                        places.getJSONObject(i).remove("childID");
                        places.getJSONObject(i).put("childID",i);
                    }else {
                        places.getJSONObject(i).put("childID",i);
                    }
                }

            }else {
                //JSONArray placesToPopulate = new JSONArray();
                int childID = 0;
                for (int i= 0; i<places.length();i++){

                    //Log.i("query",query+" "+i);
                    if(places.getJSONObject(i).getString("name").toLowerCase().contains(query.toLowerCase()) || places.getJSONObject(i).getString("type").toLowerCase().contains(query.toLowerCase())){

                        if (places.getJSONObject(i).has("childID")){
                            places.getJSONObject(i).remove("childID");
                            places.getJSONObject(i).put("childID",childID);
                            childID++;
                        }else {
                            places.getJSONObject(i).put("childID",childID);
                            childID++;
                        }

                        placesContainer.addView(populatePlacesView(i));

                    }
                }


            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private View populatePlacesView(int index){
        View cardView = getLayoutInflater().inflate(R.layout.place_add_cardview,null);



        try{
            cardView.findViewById(R.id.cardView).setTag(places.getJSONObject(index).getInt("id"));
            cardView.findViewById(R.id.cardView).setOnClickListener(myReviewsListener);

            TextView placeName = cardView.findViewById(R.id.placeName);
            TextView placeAddress = cardView.findViewById(R.id.placeAddress);
            TextView descPlace = cardView.findViewById(R.id.descPlace);
            TextView placeType = cardView.findViewById(R.id.placeType);
            TextView reviewsCount = cardView.findViewById(R.id.reviewsCount);
            ImageView button = cardView.findViewById(R.id.addButton);


            placeName.setText(places.getJSONObject(index).getString("name"));
            placeAddress.setText(places.getJSONObject(index).getString("address"));
            descPlace.setText(places.getJSONObject(index).getString("description"));
            placeType.setText(places.getJSONObject(index).getString("type"));
            reviewsCount.setText(places.getJSONObject(index).getString("review_count"));


            if (places.getJSONObject(index).has("added")){
                if (places.getJSONObject(index).getBoolean("added")){
                    button.setImageResource(R.drawable.ic_delete_black_48dp);
                }else {
                    button.setImageResource(R.drawable.ic_add_black_48dp);
                }
            }else {
                button.setImageResource(R.drawable.ic_add_black_48dp);
            }


            button.setTag(index);
            button.setOnClickListener(myClickListener);
        }catch (Exception e){
            e.printStackTrace();
        }

        return cardView;

    }


    private class MyClickListener implements View.OnClickListener{

        int i;
        @Override
        public void onClick(View view) {
            try{

                i = (int) view.getTag();
                if (places.getJSONObject(i).has("added")){
                    if (places.getJSONObject(i).getBoolean("added")){

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
                                        try{
                                            deleteTask.execute(places.getJSONObject(i).getInt("id"),places.getJSONObject(i).getInt("childID"),i);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }

                                    }

                                })
                                .setNegativeButton("Nie", null)
                                .show();

                    }else {
                        AddTask addTask = new AddTask();
                        addTask.execute(places.getJSONObject(i).getInt("id"),places.getJSONObject(i).getInt("childID"),i);
                    }
                }else {
                    AddTask addTask = new AddTask();
                    addTask.execute(places.getJSONObject(i).getInt("id"),places.getJSONObject(i).getInt("childID"),i);
                }




            }catch (Exception e){
                e.printStackTrace();
            }



        }
    }



    private class DeleteTask extends AsyncTask<Integer,Void,Void>{

        int childID;
        int placesINDEX;
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        JSONObject response = new JSONObject();
        @Override
        protected Void doInBackground(Integer... integers) {

            try{
                childID = integers[1];
                placesINDEX = integers[2];

                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName(),MODE_PRIVATE);
                    response = DatabaseConnector.performGetCall("http://192.168.0.12:3000/trips/"+tripID+"/removeplace?place="+integers[0],sharedPreferences.getString("token",""));
                    Log.i("DELETE response", response.toString());



            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try{
                if (response.getInt("responseCode") == 200){
                    progressBar.setVisibility(View.INVISIBLE);

                    View rootView = placesContainer.getChildAt(childID);
                    ImageView button = rootView.findViewById(R.id.addButton);
                    button.setImageResource(R.drawable.ic_add_black_48dp);
                    button.invalidate();
                    places.getJSONObject(placesINDEX).remove("added");
                    placesContainer.invalidate();
                    //populatePlaces(search.getText().toString());


                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }

    private class AddTask extends AsyncTask<Integer,Void,Void>{

        int childID;
        int placesINDEX;
        JSONObject response;
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Integer... integers) {

            try{
                childID = integers[1];
                placesINDEX = integers[2];

                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName(),MODE_PRIVATE);
                response = DatabaseConnector.performGetCall("http://192.168.0.12:3000/trips/"+tripID+"/addplace?place="+integers[0],sharedPreferences.getString("token",""));
                Log.i("DELETE response", response.toString());



            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try{
                if (response.getInt("responseCode") == 200){
                    progressBar.setVisibility(View.INVISIBLE);

                    View rootView = placesContainer.getChildAt(childID);
                    ImageView button = rootView.findViewById(R.id.addButton);
                    button.setImageResource(R.drawable.ic_delete_black_48dp);
                    button.invalidate();
                    places.getJSONObject(placesINDEX).put("added",true);
                    placesContainer.invalidate();
                    //populatePlaces(search.getText().toString());


                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }


    boolean isLoadingReviews = false;
    private class MyReviewsListener implements View.OnClickListener{

        Context mContext;
        int placeID;
        @Override
        public void onClick(View view) {

            if(!isLoadingReviews){
                mContext = view.getContext();
                ShowReviewsTask showReviewsTask = new ShowReviewsTask();
                placeID = (int)view.getTag();
                showReviewsTask.execute(placeID);
            }


        }

        private class ShowReviewsTask extends AsyncTask<Integer,Void,Void>{

            @Override
            protected void onPreExecute() {
                isLoadingReviews = true;
                progressBar.setVisibility(View.VISIBLE);
            }


            JSONArray reviews = new JSONArray();
            @Override
            protected Void doInBackground(Integer... integers) {
                try{

                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName(),MODE_PRIVATE);
                    JSONObject response = DatabaseConnector.performGetCall("http://192.168.0.12:3000/places/"+integers[0]+"/reviews",sharedPreferences.getString("token",""));
                    Log.i("reviews",response.toString());
                    reviews = response.getJSONObject("response").getJSONArray("reviews");




                }catch (Exception e){
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                isLoadingReviews = false;
                progressBar.setVisibility(View.INVISIBLE);
                ReviewsDialog reviewsDialog = new ReviewsDialog();
                reviewsDialog.showDialog((Activity)mContext,reviews);

            }
        }

        private class ReviewsDialog {

            Dialog dialog;
            public void showDialog(Activity activity, JSONArray reviews){
                dialog = new Dialog(activity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.reviews_dialog);
                Button button = dialog.findViewById(R.id.addReviewButton);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        search.clearFocus();

                        AddReviewTask addReviewTask = new AddReviewTask();
                        addReviewTask.execute();
                    }
                });
                LinearLayout reviewsContainer = dialog.findViewById(R.id.reviewsContainer);

                try{
                    for(int i = 0; i< reviews.length();i++){
                        View cardView = getLayoutInflater().inflate(R.layout.review,null);

                        TextView date = cardView.findViewById(R.id.date);
                        TextView author = cardView.findViewById(R.id.author);
                        TextView content = cardView.findViewById(R.id.content);

                        date.setText(reviews.getJSONObject(i).getString("created_at"));
                        author.setText(reviews.getJSONObject(i).getString("author"));
                        content.setText(reviews.getJSONObject(i).getString("content"));

                        reviewsContainer.addView(cardView);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }



                //usersAutoCompleteTextView = dialog.findViewById(R.id.usernameSearch);
                //errorMsg = dialog.findViewById(R.id.errorMsg);
                // progressBar = dialog.findViewById(R.id.progressBar);

                //Button dialogButton = dialog.findViewById(R.id.confirmButton);
                //dialogButton.setOnClickListener(new View.OnClickListener() {
                //    @Override
                //    public void onClick(View v) {
                //        TripActivity.AddParticipantDialog.AddParticipantTask addParticipantTask = new TripActivity.AddParticipantDialog.AddParticipantTask();
                //        addParticipantTask.execute();
                //    }
                //});
                dialog.show();
                //TripActivity.AddParticipantDialog.UsernameProviderTask usernameProviderTask = new TripActivity.AddParticipantDialog.UsernameProviderTask();
                //usernameProviderTask.execute();

            }

            private class AddReviewTask extends AsyncTask<Void,Void,Void>{


                int childID;
                @Override
                protected Void doInBackground(Void... voids) {
                    try{

                        for (int i = 0;i<places.length(); i++){
                          if (places.getJSONObject(i).getInt("id")==placeID){

                              childID = places.getJSONObject(i).getInt("childID");

                          }
                        }

                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName(),MODE_PRIVATE);
                        JSONObject postData = new JSONObject();
                        postData.put("content",((EditText)dialog.findViewById(R.id.reviewsInput)).getText().toString());
                        JSONObject response = DatabaseConnector.performPostCall("http://192.168.0.12:3000/places/"+placeID+"/reviews",postData,sharedPreferences.getString("token",""));
                        Log.i("reviews",response.toString());

                    }catch (Exception e){
                        e.printStackTrace();
                    }



                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    dialog.cancel();
                    search.clearFocus();

                    TextView reviewsCount = placesContainer.getChildAt(childID).findViewById(R.id.reviewsCount);
                    int actualReviewsCount = Integer.parseInt(reviewsCount.getText().toString());
                    actualReviewsCount++;
                    reviewsCount.setText(""+actualReviewsCount);
                    placesContainer.getChildAt(childID).invalidate();
                    //placesContainer.invalidate();

                }
            }
        }
    }


}
