package com.example.andriypuhach.android_teamvoy_test.services;

import com.example.andriypuhach.android_teamvoy_test.models.Movie;
import com.example.andriypuhach.android_teamvoy_test.models.MovieRequestResult;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by andriypuhach on 1/13/15.
 */
public interface RetrofitMovieService {
    final static String apiKey = "a37df950b3c1b256fded657ae4562b93";

    @GET("/movie/{thing}?api_key=" + apiKey)
    public void getMovies(@Path("thing") String thing, @Query("page") int page, Callback<MovieRequestResult> movieRes);
    @GET("/movie/{thing}?api_key=" + apiKey)
    public MovieRequestResult getMovies(@Path("thing") String thing, @Query("page") int page);
    @GET("/movie/{id}?api_key=" + apiKey)
    public void getDetails(@Path("id") int id,@Query("append_to_response")String appendToResponse,Callback<Movie.Details> det);
    @GET("/search/movie?api_key=" + apiKey)
    public void search(@Query("query") String query, @Query("page") int page,@Query("search_type") String search, Callback<MovieRequestResult> movieRes);
    @GET("/authentication/token/new?api_key=" + apiKey)
    public void getToken(Callback<JsonElement> jsonResult);
    @GET("/authentication/token/validate_with_login?api_key=" + apiKey)
    public void validateToken(@Query("request_token") String token, @Query("username") String username, @Query("password") String password, Callback<JsonElement> jsonResult);
    @GET("/authentication/session/new?api_key=" + apiKey)
    public void getNewSession(@Query("request_token") String token, Callback<JsonElement> jsonResult);
    //TODO: /account{id}/ що за параметр такий? і чому неважливо що в нього передаєШ?
    @GET("/account/100/watchlist/movies?api_key=" + apiKey)
    public void getWatchListMovies(@Query("session_id") String sessionId, @Query("page") int page, Callback<MovieRequestResult> movieRes);
    @GET("/account/100/favorite/movies?api_key=" + apiKey)
    public void getFavoriteMovies(@Query("session_id") String sessionId, @Query("page") int page, Callback<MovieRequestResult> movieRes);
    @POST("/account/100/favorite?api_key="+apiKey)
    public void  setFavorite(@Body JsonObject body, @Query("session_id") String sessionId,Callback<JsonElement> res);
    @POST("/account/100/watchlist?api_key="+apiKey)
    public void  setWatchlist(@Body JsonObject body, @Query("session_id") String sessionId,Callback<JsonElement> res);
}