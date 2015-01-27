package com.example.andriypuhach.android_teamvoy_test.services;

import com.example.andriypuhach.android_teamvoy_test.TheMovieDBAccount;
import com.example.andriypuhach.android_teamvoy_test.models.CastNCrewResult;
import com.example.andriypuhach.android_teamvoy_test.models.Movie;
import com.example.andriypuhach.android_teamvoy_test.models.MovieRequestResult;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
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
    @GET("/movie/{id}?api_key=" + apiKey)
    public void getDetails(@Path("id") int id, Callback<Movie.Details> det);
    @GET("/movie/{id}/credits?api_key="+apiKey)
    public CastNCrewResult getCastNCrew(@Path("id")int id);
    @GET("/movie/{id}/images?api_key=" + apiKey)
    public void getImagePathes(@Path("id") int id, Callback<JsonElement> imRes);
    @GET("/movie/{id}/videos?api_key=" + apiKey)
    public JsonElement getVideos(@Path("id") int id);
    @GET("/search/movie?api_key=" + apiKey)
    public void search(@Query("query") String query, @Query("page") int page, Callback<MovieRequestResult> movieRes);
    @GET("/authentication/token/new?api_key=" + apiKey)
    public void getToken(Callback<JsonElement> jsonResult);
    @GET("/authentication/token/validate_with_login?api_key=" + apiKey)
    public void validateToken(@Query("request_token") String token, @Query("username") String username, @Query("password") String password, Callback<JsonElement> jsonResult);
    @GET("/authentication/session/new?api_key=" + apiKey)
    public void getNewSession(@Query("request_token") String token, Callback<JsonElement> jsonResult);
    @GET("/account?api_key=" + apiKey)
    public void getAccountInfo(@Query("session_id") String sessionId, Callback<JsonElement> jsonResult);
    @GET("/account/100/watchlist/movies?api_key=" + apiKey)
    public void getWatchListMovies(@Query("session_id") String sessionId, @Query("page") int page, Callback<MovieRequestResult> movieRes);
    @GET("/account/100/favorite/movies?api_key=" + apiKey)
    public void getFavoriteMovies(@Query("session_id") String sessionId, @Query("page") int page, Callback<MovieRequestResult> movieRes);
    @POST("/account/100/favorite?api_key="+apiKey)
    public void  setFavorite(@Body JsonObject body, @Query("session_id") String sessionId,Callback<JsonElement> res);
    @POST("/account/100/watchlist?api_key="+apiKey)
    public void  setWatchlist(@Body JsonObject body, @Query("session_id") String sessionId,Callback<JsonElement> res);
}