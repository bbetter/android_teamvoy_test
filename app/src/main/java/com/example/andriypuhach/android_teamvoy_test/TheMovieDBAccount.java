package com.example.andriypuhach.android_teamvoy_test;

import android.content.Context;
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

        public static void authenticate(final Context context ,final String username,final String password){
        Toast.makeText(context,"Wait until connection is established",Toast.LENGTH_LONG).show();

        RestClient.getApi().getToken(new Callback<JsonElement>() {
            @Override
            public void success(JsonElement element, Response response) {
                String requestToken=element.getAsJsonObject().get("request_token").toString();
                RestClient.requestToken=requestToken.substring(1,requestToken.length()-1);
                Log.i("REQUEST_TOKEN",RestClient.requestToken);
                RestClient.getApi().validateToken(RestClient.requestToken, username, password, new Callback<JsonElement>() {
                    @Override
                    public void success(JsonElement jsonElement, Response response) {
                        if(jsonElement.getAsJsonObject().get("success").toString().equals("true")){
                            RestClient.getApi().getNewSession(RestClient.requestToken,new Callback<JsonElement>() {
                                @Override
                                public void success(JsonElement jsonElement, Response response) {
                                    String sessionId=jsonElement.getAsJsonObject().get("session_id").toString();
                                    RestClient.sessionId=sessionId.substring(1,sessionId.length()-1);
                                    Log.i("SESSION_ID",RestClient.sessionId);
                                    Toast.makeText(context,"Connection is successfully established",Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                }
                            });
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("Error",error.getUrl()+"\n"+error.getBody());
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                return ;
            }
        });

    }

}
