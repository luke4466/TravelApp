package com.example.lukaszgielec.travelplanner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

/**
 * Created by Lukasz Gielec on 23.01.2018.
 */

public class ReviewFragment extends Fragment {


    JSONObject review = new JSONObject();

    public ReviewFragment addReview(JSONObject aReview){
        review = aReview;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.review_view, container, false);
        TextView content = (TextView) rootView.findViewById(R.id.reviewContent);
        TextView author = (TextView) rootView.findViewById(R.id.reviewAuthor);
        TextView date = (TextView) rootView.findViewById(R.id.reviewDate);

        try {

            if (savedInstanceState != null){
                if (savedInstanceState.containsKey("review")){
                    review = new JSONObject(savedInstanceState.getString("review"));

                }
            }


            content.setText(review.getString("content"));
            author.setText(review.getString("author"));
            date.setText(review.getString("created_at"));
        }catch (Exception e){
            e.printStackTrace();
        }

        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("review",review.toString());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null){
            if (savedInstanceState.containsKey("review")){
                try{
                    review = new JSONObject(savedInstanceState.getString("review"));
                }catch (Exception e){

                }
            }
        }

    }
}
