package com.example.andriypuhach.android_teamvoy_test.asyncs;

import android.os.AsyncTask;

import com.example.andriypuhach.android_teamvoy_test.models.MovieRequestResult;
import com.example.andriypuhach.android_teamvoy_test.rest.RestClient;

public class DownloadMoviesAsync extends AsyncTask<String, Void, MovieRequestResult> {
    @Override
    protected MovieRequestResult doInBackground(String... params) {

        int currentPageNumber = 1;
        if (params.length > 1) {
            currentPageNumber = Integer.parseInt(params[1]);
        }
        MovieRequestResult result =RestClient.getApi().getMovies(params[0],currentPageNumber);
        return result;
    }
}