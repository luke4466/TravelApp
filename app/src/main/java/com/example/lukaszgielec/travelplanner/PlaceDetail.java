package com.example.lukaszgielec.travelplanner;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlaceDetail extends AppCompatActivity implements OnMapReadyCallback {

    MapView mMapView;
    GoogleMap googleMap;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    EditText reviewsEditText;
    Button reviewsSendButton;


    ProgressBar progressBar;
    ProgressBar progressBarReviews;
    int placeID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        Log.i("MOJE","ONCREATE");
        placeID = getIntent().getIntExtra("place_id",0);
        if (placeID == 0){
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }


        reviewsEditText = findViewById(R.id.reviewEditText);
        reviewsEditText.clearFocus();
        reviewsEditText.clearFocus();
        reviewsSendButton = findViewById(R.id.sendReviewButton);
        reviewsSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddReviewTask addReviewTask = new AddReviewTask();
                addReviewTask.execute();
            }
        });

        progressBar = findViewById(R.id.progressBar);
        progressBarReviews = findViewById(R.id.progressReviews);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager =  findViewById(R.id.reviewsViewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                progressBarReviews.setProgress(position+1);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        mMapView = findViewById(R.id.map);


        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately


        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);


        GetPlaceDetails getPlaceDetails = new GetPlaceDetails();
        getPlaceDetails.execute();

    }




    JSONArray reviews = new JSONArray();
    JSONObject response;

    private class GetPlaceDetails extends AsyncTask<Void,Void,Void>{


        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            reviewsEditText.clearFocus();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            try{

                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName(),MODE_PRIVATE);
                response = DatabaseConnector.performGetCall("/places/"+placeID,sharedPreferences.getString("token",""),getApplicationContext());
                Log.i("responsePlaceDetails",response.toString());




            }catch (Exception e){
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.INVISIBLE);
            try{
                reviewsEditText.clearFocus();

                if(response.getInt("responseCode") == 200 ){
                    reviews = response.getJSONObject("response").getJSONArray("reviews");

                    progressBarReviews.setMax(reviews.length());
                    progressBarReviews.setProgress(1);

                    mSectionsPagerAdapter.notifyDataSetChanged();

                    if (reviews.length() > 0){
                        mViewPager.setCurrentItem(0);
                    }
                    getSupportActionBar().setTitle(response.getJSONObject("response").getString("name"));
                    // For dropping a marker at a point on the Map
                   // for (int i = 0; i < 1; i++) {
                        LatLng latLng = new LatLng(response.getJSONObject("response").getDouble("lat"),response.getJSONObject("response").getDouble("lon"));
                        googleMap.addMarker(new MarkerOptions().position(latLng).title(response.getJSONObject("response").getString("name")).snippet(response.getJSONObject("response").getString("address")));

                   // }


                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            finish();
            Intent intent = new Intent(getApplicationContext(), PlaceDetail.class);
            intent.putExtra("place_id", placeID);

            startActivity(intent);

        }else {
            finish();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // For showing a move to my location button
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 1);

        }else {
            googleMap.setMyLocationEnabled(true);

        }







    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        Map<Integer,ReviewFragment> reviewFragments = new HashMap<>();

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            JSONObject review = new JSONObject();
            try {
                review = reviews.getJSONObject(position);


            }catch (Exception e){
                e.printStackTrace();
            }

            Log.i("new review fragment",review.toString());
            Log.i("position",""+position);
            if (reviewFragments.containsKey(position)){
                return reviewFragments.get(position);
            }else {
                reviewFragments.put(position,new ReviewFragment().addReview(review));
                return reviewFragments.get(position);
            }

        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            reviewFragments.clear();

        }

        @Override
        public int getCount() {
            return reviews.length();
        }
    }


    private class AddReviewTask extends AsyncTask<Void,Void,Void>{


        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

            if (reviewsEditText.getText().toString().isEmpty()){
                new AlertDialog.Builder(progressBar.getContext())
                        .setTitle("Wstawianie recenzji")
                        .setMessage("Recenzja nie może być pusta")
                        .setPositiveButton("ok", null)
                        .show();

                progressBar.setVisibility(View.INVISIBLE);
                this.cancel(true);
            }
        }



        @Override
        protected Void doInBackground(Void... voids) {
            try{

                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName(),MODE_PRIVATE);
                JSONObject postData = new JSONObject();
                postData.put("content", reviewsEditText.getText().toString());
                JSONObject response = DatabaseConnector.performPostCall("/places/"+placeID+"/reviews",postData,sharedPreferences.getString("token",""),getApplicationContext());
                Log.i("reviews",response.toString());

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.INVISIBLE);
            reviewsEditText.setText("");
            reviewsEditText.setHint("Dodano!");


            finish();
            Intent intent = new Intent(getApplicationContext(),PlaceDetail.class);
            intent.putExtra("place_id",placeID);
            startActivity(intent);

        }
    }

}
