package com.example.andriypuhach.android_teamvoy_test.rest;

import com.example.andriypuhach.android_teamvoy_test.services.RetrofitMovieService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.squareup.okhttp.OkHttpClient;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by andriypuhach on 1/13/15.
 */


public class RestClient {
    private static final String BASE_URL="https://api.themoviedb.org/3";
    private static RetrofitMovieService  api;
    private static class DateTimeTypeConverter implements JsonDeserializer<DateTime>
    {
        @Override
        public DateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException{
            try {
                if (json.getAsString()=="") return DateTime.now();
                DateTime joda = new DateTime(json.getAsString());
                return joda;
            } catch (IllegalArgumentException e) {
                Date date = context.deserialize(json, Date.class);
                return new DateTime(date);
            }
        }
    }
    static{
        OkHttpClient ok = new OkHttpClient();
        ok.setConnectTimeout(60, TimeUnit.SECONDS);
        ok.setReadTimeout(60,TimeUnit.SECONDS);
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-mm-dd")
                .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
                .create();
        api  = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BASE_URL)
                .setConverter(new GsonConverter(gson))
                .setClient(new OkClient(ok))
                .build().create(RetrofitMovieService.class);

    }
    public static RetrofitMovieService getApi(){
        return api;
    }
}
