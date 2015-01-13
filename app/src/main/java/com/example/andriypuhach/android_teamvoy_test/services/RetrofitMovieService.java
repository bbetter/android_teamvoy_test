package com.example.andriypuhach.android_teamvoy_test.services;

import android.media.ImageReader;

import com.example.andriypuhach.android_teamvoy_test.models.ImagesResult;
import com.example.andriypuhach.android_teamvoy_test.models.Movie;
import com.example.andriypuhach.android_teamvoy_test.models.MovieDetails;
import com.example.andriypuhach.android_teamvoy_test.models.MovieRequestResult;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by andriypuhach on 1/13/15.
 */
public interface RetrofitMovieService {
    static final String apiKey = "a37df950b3c1b256fded657ae4562b93";
    @GET("/movie/{thing}?api_key="+apiKey)
    public void getMovies(@Path("thing")String thing,@Query("page")int page, Callback<MovieRequestResult> movieRes);
    @GET("/movie/{id}?api_key="+apiKey)
    public void getDetails(@Path("id")int id,Callback<MovieDetails> det);
    @GET("/movie/{id}/images?api_key="+apiKey)
    public void getImagePathes(@Path("id")int id,Callback<ImagesResult> imRes);
    @GET("/search/movie?api_key="+apiKey)
    public void search(@Query("query")String query,@Query("page")int page,Callback<MovieRequestResult> movieRes);

}
