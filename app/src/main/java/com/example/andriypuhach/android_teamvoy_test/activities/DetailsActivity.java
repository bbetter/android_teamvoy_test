package com.example.andriypuhach.android_teamvoy_test.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.andriypuhach.android_teamvoy_test.R;
import com.example.andriypuhach.android_teamvoy_test.models.Movie;
import com.example.andriypuhach.android_teamvoy_test.models.MovieDetails;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DetailsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        Intent intent = getIntent();
        LinearLayout photoLayout = (LinearLayout) findViewById(R.id.photosLayout);
        Movie movie = (Movie) intent.getSerializableExtra("Movie");
        TextView titleView = (TextView) findViewById(R.id.tvTitle);
        TextView overView = (TextView) findViewById(R.id.tvOverview);
        TextView yearView = (TextView) findViewById(R.id.tvYear);
        TextView genresView = (TextView) findViewById(R.id.tvGenre);
        TextView companyView = (TextView) findViewById(R.id.tvCompany);
        TextView tagLineView = (TextView) findViewById(R.id.tvTagline);
        TextView budgetView = (TextView) findViewById(R.id.tvBudget);
        TextView revenueView = (TextView) findViewById(R.id.tvRevenue);
        TextView homePageView = (TextView) findViewById(R.id.tvHomePage);
        TextView statusPageView = (TextView) findViewById(R.id.tvStatus);
        StringBuilder genres = new StringBuilder();
        StringBuilder companies = new StringBuilder();
        List<MovieDetails.Genre> genresList;
        List<MovieDetails.Company> companiesList;
        genresList = movie.getDetails().getGenres();
        companiesList = movie.getDetails().getProduction_companies();
        String separator = ",";
        if (genresList.size() != 0) {
            genres.append(genresList.get(0).getName());
            for (MovieDetails.Genre g : genresList.subList(1, genresList.size())) {
                genres.append(separator).append(g.getName());
            }
        }
        if (companiesList.size() != 0) {
            companies.append(companiesList.get(0).getName());
            for (MovieDetails.Company c : companiesList.subList(1, companiesList.size())) {
                companies.append(separator).append(c.getName());
            }
        }
        photoLayout.removeAllViews();
        List<String> iPathes = movie.getDetails().getImagePathes();
        for (String str : iPathes) {
            ImageView tempView = new ImageView(getApplicationContext());
            photoLayout.addView(tempView);
            ImageLoader.getInstance().displayImage(Movie.transformPathToURL(str, Movie.ImageSize.W600), tempView);
        }
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        titleView.setText(movie.getOriginal_title());
        yearView.setText(String.valueOf(movie.getRelease_date().toLocalDate()));
        genresView.setText(genres.toString());
        companyView.setText(companies.toString());
        overView.setText(movie.getDetails().getOverview());
        budgetView.setText(currencyFormatter.format(movie.getDetails().getBudget()));
        revenueView.setText(currencyFormatter.format(movie.getDetails().getRevenue()));
        tagLineView.setText(movie.getDetails().getTagLine());
        homePageView.setText(movie.getDetails().getHomepage());
        statusPageView.setText(movie.getDetails().getStatus());
    }
}

