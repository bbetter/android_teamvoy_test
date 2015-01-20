package com.example.andriypuhach.android_teamvoy_test.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import com.example.andriypuhach.android_teamvoy_test.MovieDatabaseHelper;
import com.example.andriypuhach.android_teamvoy_test.R;
import com.example.andriypuhach.android_teamvoy_test.models.Movie;

/**
 * Created by andriypuhach on 1/16/15.
 */
public class CreateNoteDialog extends Dialog {
    public final static int SELECT_PHOTO_CREATE=100;
    public static ImageView imageView;
    public static String createImagePath=null;
    private Activity activity;
    private Movie movie;
    public CreateNoteDialog(Activity act) {
        super(act);
        this.activity=act;
    }
    public void setMovieInfo(Movie movie){
        this.movie=movie;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_note_dialog);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        final EditText noteTitle=(EditText)findViewById(R.id.noteTitleEdit);
        final EditText noteText =(EditText)findViewById(R.id.noteTextEdit);
        imageView=(ImageView)findViewById(R.id.uploadedImage);
        Button uploadButton = (Button)findViewById(R.id.uploadPhotoBtn);
        Button submitButton = (Button)findViewById(R.id.submitNoteBtn);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                activity.startActivityForResult(i,SELECT_PHOTO_CREATE);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title= noteTitle.getText().toString();
                String text = noteText.getText().toString();
                Movie.Details.Note newNote=new Movie.Details.Note();
                newNote.setNoteText(text);
                newNote.setNoteTitle(title);
                newNote.setImagePath(createImagePath);
                MovieDatabaseHelper dbHelper= new MovieDatabaseHelper(activity);
                dbHelper.insertNote(movie, newNote);
                dismiss();
            }
        });
    }
}
