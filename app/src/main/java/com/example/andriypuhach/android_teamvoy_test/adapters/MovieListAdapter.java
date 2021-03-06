package com.example.andriypuhach.android_teamvoy_test.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.andriypuhach.android_teamvoy_test.R;
import com.example.andriypuhach.android_teamvoy_test.activities.MainActivity;
import com.example.andriypuhach.android_teamvoy_test.models.*;
import com.example.andriypuhach.android_teamvoy_test.rest.RestClient;
import com.squareup.picasso.Picasso;


import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class MovieListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Movie> movies;
    private LayoutInflater inflater;
    private ViewHolder holder;

    public MovieListAdapter(Context cntxt) {
        context = cntxt;
        inflater = LayoutInflater.from(context);
    }

    public void addMovies(ArrayList<Movie> mv){

        if(movies!=null) {
            movies.addAll(mv);
            notifyDataSetChanged();
        }
        else setMovies(mv);

    }
    public void setMovies(ArrayList<Movie> mv) {
        movies = mv;
        notifyDataSetChanged();
    }
    public List<Movie> getMovies(){
        return movies;
    }
    public Movie getMovie(int i) {
        return movies.get(i);
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.row, parent,false);
            holder = new ViewHolder();
            holder.tvTitle = (TextView) vi.findViewById(R.id.tvTitle);
            holder.rbRating = (RatingBar) vi.findViewById(R.id.rbRating);
            holder.ivPoster = (ImageView) vi.findViewById(R.id.poster);
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        Movie item=new Movie();
        try {
            item = movies.get(position);
        }
        catch(Exception e){
            Log.w("TAG", String.valueOf(position));
        }

        Picasso.with(context).load(Movie.transformPathToURL(item.getPoster_path(), Movie.ImageSize.W75)).error(R.drawable.failed_to_load).into(holder.ivPoster);
        DateTime releaseDate = item.getRelease_date();
        String dateYear = "";
        if (releaseDate != null && releaseDate!=DateTime.now()) {
            dateYear = "(" + String.valueOf(releaseDate.getYear()) + ")";
        }
        holder.tvTitle.setText(item.getTitle() + dateYear);
        holder.rbRating.setRating((float) item.getVote_average());


        return vi;
    }
    public Movie getMovieByMovieID(int id){
        for(Movie mv:movies){
            if(mv.getId()==id) return mv;
        }
        return null;
    }
    static class ViewHolder {
        TextView tvTitle;
        RatingBar rbRating;
        ImageView ivPoster;
    }

}
