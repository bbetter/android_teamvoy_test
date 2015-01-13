package com.example.andriypuhach.android_teamvoy_test.asyncs;

import android.os.AsyncTask;

import com.example.andriypuhach.android_teamvoy_test.models.MovieDetails;
import com.example.andriypuhach.android_teamvoy_test.rest.RestClient;
import java.util.List;

public class DownloadDetailsAsync extends AsyncTask<Integer, Void, MovieDetails> {
    @Override
    protected MovieDetails doInBackground(Integer... params) {
        MovieDetails det = new MovieDetails();
        List<String> imagePathes;
        imagePathes = RestClient.getApi().getImagePathes(params[0]).getBackdropPathes();
            det = RestClient.getApi().getDetails(params[0]);
            det.setImagePathes(imagePathes);
        return det;
    }
}

