package com.example.andriypuhach.android_teamvoy_test.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.andriypuhach.android_teamvoy_test.R;
import com.example.andriypuhach.android_teamvoy_test.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

/**
 * Created by andriypuhach on 27.01.15.
 */
public class YoutubeVideoActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{
    private String key;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.video);
        final Intent intent= getIntent();
        key=intent.getStringExtra("VideoKey");
        YouTubePlayerView youTubePlayerView=(YouTubePlayerView)findViewById(R.id.youtubeView);
        youTubePlayerView.initialize(Movie.Details.Videos.Video.YOUTUBE_API_KEY,this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.loadVideo(key);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
           
    }
}
