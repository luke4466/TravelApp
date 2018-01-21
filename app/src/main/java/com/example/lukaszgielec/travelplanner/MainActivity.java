package com.example.lukaszgielec.travelplanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    MainFragment mainFragment = new MainFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_home:

                    transaction.replace(R.id.fragment_container, mainFragment);
                    transaction.commit();

                    return true;
                case R.id.navigation_dashboard:
                    //startActivity(new Intent(getApplicationContext(), TripActivity.class),null);

                    transaction.replace(R.id.fragment_container, profileFragment);
                    transaction.commit();
                    return true;
                case R.id.navigation_notifications:

                    transaction.replace(R.id.fragment_container, mainFragment);
                    transaction.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mainFragment);
        transaction.commit();


    }

    @Override
    public void onBackPressed() {

    }


}
