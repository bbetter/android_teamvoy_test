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
import android.widget.HorizontalScrollView;
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
public class CreateNoteDialog extends Dialog {
    public final static int SELECT_PHOTO_CREATE=100;
    public static ImageViewWithContextView imageView;
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
                            createImagePath=null;
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
        setContentView(R.layout.create_note_dialog);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        final EditText noteTitle=(EditText)findViewById(R.id.noteTitleEdit);
        final EditText noteText =(EditText)findViewById(R.id.noteTextEdit);
        imageView=(ImageViewWithContextView)findViewById(R.id.uploadedImage);
        Button uploadButton = (Button)findViewById(R.id.uploadPhotoBtn);
        Button submitButton = (Button)findViewById(R.id.submitNoteBtn);
        registerForContextMenu(imageView);


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
                dbHelper.insertNote(activity.getApplicationContext(),movie, newNote);
                dismiss();
            }
        });

    }

}
