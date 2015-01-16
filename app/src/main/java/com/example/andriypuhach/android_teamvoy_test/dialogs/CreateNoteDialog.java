package com.example.andriypuhach.android_teamvoy_test.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;

import com.example.andriypuhach.android_teamvoy_test.NotesUpdater;
import com.example.andriypuhach.android_teamvoy_test.R;
import com.example.andriypuhach.android_teamvoy_test.activities.DetailsActivity;
import com.example.andriypuhach.android_teamvoy_test.adapters.DetailsListAdapter;
import com.example.andriypuhach.android_teamvoy_test.adapters.NotesListAdapter;
import com.example.andriypuhach.android_teamvoy_test.models.Note;

/**
 * Created by andriypuhach on 1/16/15.
 */
public class CreateNoteDialog extends Dialog {
    private final static int SELECT_PHOTO=100;
    private int movieId=0;
    private String movieTitle="";
    private Activity activity;
    public CreateNoteDialog(Activity act) {
        super(act);
        this.activity=act;
    }
    public void setMovieInfo(int id,String ttl){
        movieId=id;
        movieTitle=ttl;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_note_dialog);
        final EditText noteTitle=(EditText)findViewById(R.id.noteTitleEdit);
        final EditText noteText =(EditText)findViewById(R.id.noteTextEdit);
        final HorizontalScrollView  horView=(HorizontalScrollView)findViewById(R.id.imagesHolder);
        Button uploadButton = (Button)findViewById(R.id.uploadPhotoBtn);
        Button submitButton = (Button)findViewById(R.id.submitNoteBtn);


        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                activity.startActivityForResult(i,SELECT_PHOTO);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note.refreshNotes();
                String title= noteTitle.getText().toString();
                String text = noteText.getText().toString();
                Note newNote=new Note();
                newNote.setText(text);
                newNote.setNoteTitle(title);
                newNote.setImage(DetailsActivity.currentImageByteArray);
                newNote.setMovieId(movieId);
                newNote.setMovieTitle(movieTitle);
                Note.notes.add(newNote);
                Note.saveNotes();
                NotesUpdater.update(DetailsListAdapter.notesView, DetailsListAdapter.notesAdapter, Note.getMovieNotes(movieId), 250);
                dismiss();
            }
        });
    }
}
