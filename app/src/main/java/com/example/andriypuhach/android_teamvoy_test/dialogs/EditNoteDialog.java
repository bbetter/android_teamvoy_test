package com.example.andriypuhach.android_teamvoy_test.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
public class EditNoteDialog extends Dialog {
    private Activity activity;
    private final static int SELECT_PHOTO=100;
    private Note editedNote;

    public EditNoteDialog(Activity activity){
        super(activity);
        this.activity=activity;
    }
    public void setEditedNote(Note editedNote){
        this.editedNote=editedNote;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_note_dialog);
        final EditText noteTitle=(EditText)findViewById(R.id.noteTitleEdit);
        final EditText noteText =(EditText)findViewById(R.id.noteTextEdit);
        final HorizontalScrollView horView=(HorizontalScrollView)findViewById(R.id.imagesHolder);
        Button uploadButton = (Button)findViewById(R.id.uploadPhotoBtn);
        Button submitButton = (Button)findViewById(R.id.submitNoteBtn);
        noteTitle.setText(editedNote.getNoteTitle());
        noteText.setText(editedNote.getText());


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
                newNote.setNoteTitle(title);
                newNote.setText(text);
                newNote.setMovieId(editedNote.getMovieId());
                newNote.setImage(DetailsActivity.currentImageByteArray);
                newNote.setMovieTitle(editedNote.getMovieTitle());

                Note.replace(editedNote,newNote);
                Note.saveNotes();
                NotesUpdater.update(DetailsListAdapter.notesView, DetailsListAdapter.notesAdapter, Note.getMovieNotes(newNote.getMovieId()), 250);
                dismiss();
            }
        });
    }
}
