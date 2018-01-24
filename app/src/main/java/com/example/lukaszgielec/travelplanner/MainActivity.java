package com.example.lukaszgielec.travelplanner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    MainFragment mainFragment = new MainFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    NotificationFragment notificationFragment = new NotificationFragment();
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

                    transaction.replace(R.id.fragment_container, notificationFragment);
                    transaction.commit();
                    return true;
            }
            return false;
        }
    };

    BottomNavigationView navigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mainFragment);
        transaction.commit();
        navigation.setSelectedItemId(R.id.navigation_home);




    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        navigation.setSelectedItemId(R.id.navigation_home);

    }
}
