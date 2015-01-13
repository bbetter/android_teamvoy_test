
package com.example.andriypuhach.android_teamvoy_test.asyncs;

import android.net.Uri;
import android.os.AsyncTask;

import com.example.andriypuhach.android_teamvoy_test.models.MovieRequestResult;
import com.example.andriypuhach.android_teamvoy_test.rest.RestClient;


/**
 * Created by Джон on 05.01.2015.
 */
public class SearchMovieAsync extends AsyncTask<String, Void, MovieRequestResult> {
    @Override
    protected MovieRequestResult doInBackground(String... params) {
        int currentPageNumber = 1;
        if (params.length > 1) {
            currentPageNumber = Integer.parseInt(params[1]);
        }
        String query=params[0];
        MovieRequestResult result = RestClient.getApi().search(Uri.encode(query),currentPageNumber);
        return result;
    }
}

