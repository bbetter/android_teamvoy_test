package com.example.andriypuhach.android_teamvoy_test;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.andriypuhach.android_teamvoy_test.models.Movie;
import com.example.andriypuhach.android_teamvoy_test.rest.RestClient;
import com.google.gson.JsonElement;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by andriypuhach on 1/22/15.
 */
public class TheMovieDBAccount {

    private static Callback<JsonElement> getTokenCallBack=new Callback<JsonElement>(){

        @Override
        public void success(JsonElement jsonElement, Response response) {
            String requestToken=jsonElement.getAsJsonObject().get("request_token").toString();
            RestClient.requestToken=requestToken.substring(1,requestToken.length()-1);
        }

        @Override
        public void failure(RetrofitError error) {

        }
    };
    private static Callback<JsonElement> validateTokenCallBack= new Callback<JsonElement>() {
        @Override
        public void success(JsonElement jsonElement, Response response) {

        }

        @Override
        public void failure(RetrofitError error) {

        }
    };
    private static Callback<JsonElement> getNewSessionCallBack= new Callback<JsonElement>() {
        @Override
        public void success(JsonElement jsonElement, Response response) {
            String sessionId=jsonElement.getAsJsonObject().get("session_id").toString();
            RestClient.sessionId=sessionId.substring(1,sessionId.length()-1);
        }

        @Override
        public void failure(RetrofitError error) {

        }
    };
        public static void getNewSession(){
            RestClient.getApi().getNewSession(RestClient.requestToken,getNewSessionCallBack);
        }
        public static void getNewToken(){
            RestClient.getApi().getToken(getTokenCallBack);
        }
        public static void validateToken(final String username,final String password){
            RestClient.getApi().validateToken(RestClient.requestToken, username, password, getTokenCallBack);

        }


}
