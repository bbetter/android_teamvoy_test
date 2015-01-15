package com.example.andriypuhach.android_teamvoy_test.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;


import com.example.andriypuhach.android_teamvoy_test.R;
import com.example.andriypuhach.android_teamvoy_test.models.Movie;
import com.example.andriypuhach.android_teamvoy_test.models.MovieDetails;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DetailsActivity extends Activity {
    private float lastX;
    private ViewFlipper viewFlipper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        final Intent intent = getIntent();
        final Movie movie = (Movie) intent.getSerializableExtra("Movie");
        viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper);
        viewFlipper.removeAllViews();
        List<String> lPathes=movie.getDetails().getImagePathes();
        String [] iPathes=new String[lPathes.size()];
        lPathes.toArray(iPathes);
        for(String path : iPathes){
            ImageView view = new ImageView(getApplicationContext());
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(view);
            ImageLoader.getInstance().displayImage(Movie.transformPathToURL(path, Movie.ImageSize.W600),view);
        }
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


    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                lastX = touchevent.getX();
                break;
            case MotionEvent.ACTION_UP:
                float currentX = touchevent.getX();

                // Handling left to right screen swap.
                if (lastX < currentX) {

                    // If there aren't any other children, just break.
                    if (viewFlipper.getDisplayedChild() == 0)
                        break;

                    // Next screen comes in from left.
                    viewFlipper.setInAnimation(this, R.anim.slide_in_from_left);
                    // Current screen goes out from right.
                    viewFlipper.setOutAnimation(this, R.anim.slide_out_to_right);

                    // Display next screen.
                    viewFlipper.showNext();
                }

                // Handling right to left screen swap.
                if (lastX > currentX) {

                    // If there is a child (to the left), kust break.
                    if (viewFlipper.getDisplayedChild() == 1)
                        break;

                    // Next screen comes in from right.
                    viewFlipper.setInAnimation(this, R.anim.slide_in_from_right);
                    // Current screen goes out from left.
                    viewFlipper.setOutAnimation(this, R.anim.slide_out_to_left);

                    // Display previous screen.
                    viewFlipper.showPrevious();
                }
                break;
        }
        return false;
    }
}

