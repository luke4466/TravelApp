package com.example.lukaszgielec.travelplanner;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import static android.content.Context.MODE_PRIVATE;
import static com.example.lukaszgielec.travelplanner.R.layout.trip_cardview;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends android.support.v4.app.Fragment {


    LinearLayout LinearLayoutContainer;
    ProgressBar progressBar;
    TextView tripsNotFoundMsg;
    TextView addTripButton;

    MyOnClickListener myOnClickListener = new MyOnClickListener();
    public MainFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        LinearLayoutContainer = view.findViewById(R.id.container);
        progressBar = view.findViewById(R.id.progressBar);
        tripsNotFoundMsg = view.findViewById(R.id.tripsNotFound);

        addTripButton = view.findViewById(R.id.addTripButton);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        addTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),AddTripActivity.class));
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();

        TripsTask tripsTask = new TripsTask();
        tripsTask.execute();
    }

    private class TripsTask extends AsyncTask<Void,Void,Void>{


        JSONObject JSONresponse = new JSONObject();

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            LinearLayoutContainer.removeAllViews();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try{

                SharedPreferences sharedPreferences = getContext().getSharedPreferences(getContext().getPackageName(),MODE_PRIVATE);
                JSONresponse = DatabaseConnector.performGetCall("http://192.168.0.12:3000/trips",sharedPreferences.getString("token",""));
                Log.i("response",JSONresponse.toString());

            }catch (Exception e){
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            try{

                if (JSONresponse.getInt("responseCode") == 200){

                    JSONArray JSONtrips = JSONresponse.getJSONObject("response").getJSONArray("trips");

                    for (int i = 0; i<JSONtrips.length();i++){
                        JSONObject JSONtrip = JSONtrips.getJSONObject(i);
                        View cardView = getLayoutInflater().inflate(R.layout.trip_cardview,null);
                        TextView tripTitle = cardView.findViewById(R.id.tripTitle);
                        TextView townName = cardView.findViewById(R.id.townName);
                        TextView startDate = cardView.findViewById(R.id.dateStart);
                        TextView endDate = cardView.findViewById(R.id.dateEnd);
                        TextView placesCount = cardView.findViewById(R.id.placesCount);
                        TextView participantsCount = cardView.findViewById(R.id.participantsCount);


                        tripTitle.setText(JSONtrip.getString("trip"));
                        townName.setText(JSONtrip.getString("town"));
                        startDate.setText(JSONtrip.getString("start_date"));
                        endDate.setText(JSONtrip.getString("end_date"));
                        placesCount.setText(JSONtrip.getString("places_count"));
                        participantsCount.setText(JSONtrip.getString("users_count"));

                        CardView cardView1 = cardView.findViewById(R.id.cardView);
                        cardView1.setTag(JSONtrip.getInt("id"));
                        cardView1.setOnClickListener(myOnClickListener);
                        LinearLayoutContainer.addView(cardView);
                    }

                    if (JSONtrips.length() == 0) tripsNotFoundMsg.setVisibility(View.VISIBLE);


                }



                progressBar.setVisibility(View.GONE);


            }catch (Exception e){
                e.printStackTrace();
            }




        }
    }

    private class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            int id = (int) view.getTag();
            Log.i("id",""+id);
            Intent intent = new Intent(getContext(),TripActivity.class);
            intent.putExtra("trip_id",id);
            startActivity(intent);
        }
    }
}
