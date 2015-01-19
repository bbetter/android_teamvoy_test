package com.example.andriypuhach.android_teamvoy_test.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
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
public class EditNoteDialog extends Dialog {
    private Activity activity;
    private Movie.Details.Note editedNote;

    public final static int SELECT_PHOTO_EDIT=200;
    public static String editImagePath;
    public static HorizontalScrollView horView;

    public EditNoteDialog(Activity activity){
        super(activity);
        this.activity=activity;
    }
    public void setEditedNote(Movie.Details.Note editedNote){
        this.editedNote=editedNote;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_note_dialog);
        final EditText noteTitle=(EditText)findViewById(R.id.noteTitleEdit);
        final EditText noteText =(EditText)findViewById(R.id.noteTextEdit);
        horView=(HorizontalScrollView)findViewById(R.id.imagesHolder);
        Button uploadButton = (Button)findViewById(R.id.uploadPhotoBtn);
        Button submitButton = (Button)findViewById(R.id.submitNoteBtn);
        noteTitle.setText(editedNote.getNoteTitle());
        noteText.setText(editedNote.getNoteText());
        editImagePath=editedNote.getImagePath();
        Bitmap bitmap = BitmapFactory.decodeFile(editImagePath);
        HorizontalScrollView.LayoutParams params=new HorizontalScrollView.LayoutParams(200,200);
        ImageView testView = new ImageView(activity.getApplicationContext());
        testView.setLayoutParams(params);
        testView.setImageBitmap(bitmap);
        EditNoteDialog.horView.removeAllViews();
        EditNoteDialog.horView.addView(testView);


        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                activity.startActivityForResult(i,SELECT_PHOTO_EDIT);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title= noteTitle.getText().toString();
                String text = noteText.getText().toString();
                Movie.Details.Note newNote=new Movie.Details.Note();
                newNote.setId(editedNote.getId());
                newNote.setNoteTitle(title);
                newNote.setNoteText(text);
                newNote.setImagePath(editImagePath);
                MovieDatabaseHelper database=new MovieDatabaseHelper(activity);
                database.updateNote(newNote);
                dismiss();
            }
        });
    }
}
