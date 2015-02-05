package com.example.andriypuhach.android_teamvoy_test.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.andriypuhach.android_teamvoy_test.MovieDatabaseHelper;
import com.example.andriypuhach.android_teamvoy_test.R;
import com.example.andriypuhach.android_teamvoy_test.adapters.ImageViewWithContextView;
import com.example.andriypuhach.android_teamvoy_test.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andriypuhach on 1/16/15.
 */
public class EditNoteDialog extends Dialog {
    private Activity activity;
    private Movie.Details.Note editedNote;

    public final static int SELECT_PHOTO_EDIT=200;
    public static String editImagePath;
    public static ImageViewWithContextView imageView;


    public EditNoteDialog(Activity activity){
        super(activity);
        this.activity=activity;
    }
    public void setEditedNote(Movie.Details.Note editedNote){
        this.editedNote=editedNote;
    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(v.getId()==R.id.uploadedImage) {
            List<String> listMenuItems = new ArrayList<>();
            listMenuItems.add("Delete");
            String[] menuItems = new String[listMenuItems.size()];
            listMenuItems.toArray(menuItems);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
                menu.getItem(i).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        editImagePath=null;
                        imageView.setVisibility(View.GONE);
                        return true;
                    }
                });
            }
        }
        super.onCreateContextMenu(menu,v,menuInfo);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_note_dialog);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        final EditText noteTitle=(EditText)findViewById(R.id.noteTitleEdit);
        final EditText noteText =(EditText)findViewById(R.id.noteTextEdit);
        Button uploadButton = (Button)findViewById(R.id.uploadPhotoBtn);
        Button submitButton = (Button)findViewById(R.id.submitNoteBtn);
        imageView = (ImageViewWithContextView)findViewById(R.id.uploadedImage);

        imageView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                List<String> listMenuItems= new ArrayList<>();
                listMenuItems.add("Delete");
                String [] menuItems=new String[listMenuItems.size()];
                listMenuItems.toArray(menuItems);
                for (int i = 0; i < menuItems.length; i++) {
                    menu.add(Menu.NONE, i, i, menuItems[i]);
                }
            }
        });
        noteTitle.setText(editedNote.getNoteTitle());
        noteText.setText(editedNote.getNoteText());
        editImagePath=editedNote.getImagePath();
        Picasso.with(activity.getApplicationContext()).load("file:///"+editImagePath).error(R.drawable.failed_to_load).into(imageView);



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
                database.updateNote(activity.getApplicationContext(),newNote);
                dismiss();
            }
        });
    }
}
