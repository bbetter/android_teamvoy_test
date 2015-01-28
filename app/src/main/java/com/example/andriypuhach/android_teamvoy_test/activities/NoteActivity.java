package com.example.andriypuhach.android_teamvoy_test.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andriypuhach.android_teamvoy_test.R;
import com.example.andriypuhach.android_teamvoy_test.models.Movie;
import com.squareup.picasso.Picasso;

/**
 * Created by andriypuhach on 28.01.15.
 */
public class NoteActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.separate_note);
        final Intent intent = getIntent();
        Movie.Details.Note note=(Movie.Details.Note)intent.getSerializableExtra("Note");
        TextView titleView=(TextView)findViewById(R.id.tvspNoteTitle);
        TextView textView=(TextView)findViewById(R.id.tvspNoteText);
        ImageView imageView=(ImageView)findViewById(R.id.ivspNoteImage);
        titleView.setText(note.getNoteTitle());
        textView.setText(note.getNoteText());
        Picasso.with(getApplicationContext()).load("file:///"+note.getImagePath()).error(R.drawable.failed_to_load).into(imageView);

    }
}
