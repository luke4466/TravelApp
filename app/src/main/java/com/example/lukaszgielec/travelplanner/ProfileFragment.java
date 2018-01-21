package com.example.lukaszgielec.travelplanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Text;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends  android.support.v4.app.Fragment {


    TextView name;
    TextView FABletter;
    Button logoutButton;
    TextView username;

    ProgressBar progressBar;

    public ProfileFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_profile_fragment, container, false);

        name = view.findViewById(R.id.nameTextView);
        username = view.findViewById(R.id.username);
        logoutButton = view.findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                view.setClickable(false);
                SharedPreferences sharedPreferences = getContext().getSharedPreferences(getContext().getPackageName(),MODE_PRIVATE);
                //JSONObject result = DatabaseConnector.performGetCall("http://192.168.0.12:3000/logout",sharedPreferences.getString("token",""));
                //Log.i("Logout",result.toString());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                startActivity(new Intent(getContext(),LoginActivity.class));
            }
        });


        FABletter = view.findViewById(R.id.FABletter);

        progressBar = view.findViewById(R.id.progressBar);



        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProfileTask profileTask = new ProfileTask();
        profileTask.execute();
    }


    private class ProfileTask extends AsyncTask<Void,Void,Void>{

        JSONObject profile = new JSONObject();

        @Override
        protected Void doInBackground(Void... voids) {

            try{
                SharedPreferences sharedPreferences = getContext().getSharedPreferences(getContext().getPackageName(),MODE_PRIVATE);

                profile = DatabaseConnector.performGetCall("http://192.168.0.12:3000/users/"+sharedPreferences.getString("id","0"),sharedPreferences.getString("token",""));
                Log.i("profile",profile.toString());




            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try{

                if(profile.getInt("responseCode") == 200){
                    profile = profile.getJSONObject("response");
                    name.setText(profile.getString("name") + " " + profile.getString("last_name"));
                    username.setText(profile.getString("username"));

                    FABletter.setText(""+profile.getString("username").toUpperCase().charAt(0));


                    name.setVisibility(View.VISIBLE);
                    username.setVisibility(View.VISIBLE);
                    FABletter.setVisibility(View.VISIBLE);

                    progressBar.setVisibility(View.INVISIBLE);
                    logoutButton.setVisibility(View.VISIBLE);

                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
