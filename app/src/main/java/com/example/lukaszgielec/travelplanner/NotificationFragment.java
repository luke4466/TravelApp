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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends android.support.v4.app.Fragment {


    //TYPES
    final int TRIP_ADD_NOTIFY = 1;
    final int TRIP_REMOVE_NOTIFY = 2;
    final int PLACE_ADD_NOTIFY = 4;//
    final int PLACE_REMOVE_NOTIFY = 5;//

    JSONArray notifies = new JSONArray();
    LinearLayout notifiesContainer;
    MyOnClickListener myOnClickListener = new MyOnClickListener();

    TextView notifiesNotFound;



    ProgressBar progressBar;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        progressBar = rootView.findViewById(R.id.progressBar);
        notifiesContainer = rootView.findViewById(R.id.notifiesContainer);
        notifiesNotFound = rootView.findViewById(R.id.notifiesNotFound);


        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        GetNotifiesTask getNotifiesTask = new GetNotifiesTask();
        getNotifiesTask.execute();

    }

    private class GetNotifiesTask  extends AsyncTask<Void,Void,Void> {


        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        JSONObject JSONresponse;

        @Override
        protected Void doInBackground(Void... voids) {


            try {

                SharedPreferences sharedPreferences = getContext().getSharedPreferences(getContext().getPackageName(), MODE_PRIVATE);
                JSONresponse = DatabaseConnector.performGetCall("/notifies", sharedPreferences.getString("token", ""), getContext());
                Log.i("response", JSONresponse.toString());

                if (JSONresponse.getInt("responseCode") == 200) {
                    notifies = JSONresponse.getJSONObject("response").getJSONArray("notifies");
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            try {


                if (JSONresponse.getInt("responseCode") == 200) {

                    for (int i = 0; i < notifies.length(); i++) {
                        JSONObject JSONnotify = notifies.getJSONObject(i);
                        View view = getLayoutInflater().inflate(R.layout.nofity_cardview, null);
                        TextView notifyTitle = view.findViewById(R.id.notifyTitle);
                        TextView notifyContent = view.findViewById(R.id.notifyContent);
                        TextView notifyDate = view.findViewById(R.id.notifyDate);
                        ImageView notifyIcon = view.findViewById(R.id.notifyIcon);



                        notifyTitle.setText(JSONnotify.getString("trip_name"));
                        notifyContent.setText(JSONnotify.getString("message"));
                        notifyDate.setText(JSONnotify.getString("created_at"));

                        if (JSONnotify.getInt("notify_type") == PLACE_ADD_NOTIFY || JSONnotify.getInt("notify_type") == TRIP_ADD_NOTIFY){
                            notifyIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_48dp));

                        }else {
                            notifyIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete_black_48dp));

                        }

                        CardView cardView1 = view.findViewById(R.id.cardView);
                        cardView1.setTag(JSONnotify.getInt("trip_id"));
                        cardView1.setOnClickListener(myOnClickListener);
                        notifiesContainer.addView(view);
                    }

                    if (notifies.length() == 0) notifiesNotFound.setVisibility(View.VISIBLE);


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            progressBar.setVisibility(View.INVISIBLE);


        }



        }

        private class MyOnClickListener implements View.OnClickListener{


            @Override
            public void onClick(View view) {

                if (view.getTag() != null){

                    Intent intent = new Intent(view.getContext(),TripActivity.class);
                    intent.putExtra("trip_id",(int)view.getTag());
                    startActivity(intent);


                }


            }
        }


}





