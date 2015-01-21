package com.example.andriypuhach.android_teamvoy_test.services;

import com.example.andriypuhach.android_teamvoy_test.models.ImagesResult;
import com.example.andriypuhach.android_teamvoy_test.models.Movie;
import com.example.andriypuhach.android_teamvoy_test.models.MovieRequestResult;
import com.google.gson.JsonElement;
import com.squareup.okhttp.Call;

import org.json.JSONObject;

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
    public void getDetails(@Path("id")int id,Callback<Movie.Details> det);
    @GET("/movie/{id}/images?api_key="+apiKey)
    public void getImagePathes(@Path("id")int id,Callback<ImagesResult> imRes);
    @GET("/search/movie?api_key="+apiKey)
    public void search(@Query("query")String query,@Query("page")int page,Callback<MovieRequestResult> movieRes);
    @GET("/authentication/token/new?api_key="+apiKey)
    public void getToken(Callback<JsonElement> jsonResult);
    //authentication stuff
    @GET("/authentication/token/validate_with_login?api_key="+apiKey)
    public void validateToken(@Query("request_token")String token,@Query("username") String username,@Query("password")String password,Callback<JsonElement> jsonResult);
    @GET("/authentication/session/new?api_key="+apiKey)
    public void getNewSession(@Query("request_token")String token,Callback<JsonElement> jsonResult);
    @GET("/account?api_key="+apiKey)
    public void getAccountInfo(@Query("session_id")String sessionId,Callback<JsonElement> jsonResult);
}
