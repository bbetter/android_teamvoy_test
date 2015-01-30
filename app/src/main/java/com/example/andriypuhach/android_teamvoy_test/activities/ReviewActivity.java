package com.example.andriypuhach.android_teamvoy_test.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andriypuhach.android_teamvoy_test.R;
import com.example.andriypuhach.android_teamvoy_test.models.Movie;
import com.squareup.picasso.Picasso;

/**
 * Created by andriypuhach on 30.01.15.
 */
public class ReviewActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.separate_review);
        final Intent intent =getIntent();
        Movie.Details.Reviews.Review review=(Movie.Details.Reviews.Review)intent.getSerializableExtra("Review");
        String imagePath=intent.getStringExtra("Image");
        ((TextView)findViewById(R.id.tvspReviewAuthor)).setText(review.getAuthor());
        ((TextView)findViewById(R.id.tvspReviewText)).setText(review.getContent());
        Picasso.with(getApplicationContext()).load(Movie.transformPathToURL(imagePath, Movie.ImageSize.W600)).error(R.drawable.failed_to_load).into((ImageView)findViewById(R.id.ivspReviewImage));
    }
}
