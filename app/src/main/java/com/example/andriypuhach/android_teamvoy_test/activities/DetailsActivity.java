package com.example.andriypuhach.android_teamvoy_test.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ViewFlipper;


import com.example.andriypuhach.android_teamvoy_test.NotesUpdater;
import com.example.andriypuhach.android_teamvoy_test.R;
import com.example.andriypuhach.android_teamvoy_test.adapters.DetailsListAdapter;
import com.example.andriypuhach.android_teamvoy_test.dialogs.CreateNoteDialog;
import com.example.andriypuhach.android_teamvoy_test.dialogs.EditNoteDialog;
import com.example.andriypuhach.android_teamvoy_test.models.Movie;
import com.example.andriypuhach.android_teamvoy_test.models.Note;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends Activity {
    private float lastX;
    private ViewFlipper viewFlipper;
    private static final int SELECT_PHOTO=100;
    public static byte [] currentImageByteArray;
    private CreateNoteDialog cdd ;
    private EditNoteDialog edd;
    private Note selectedNote;


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId()==R.id.notesListView){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            selectedNote=(Note)DetailsListAdapter.notesAdapter.getItem(info.position);
            menu.setHeaderTitle(selectedNote.getNoteTitle());
            List<String> listMenuItems= new ArrayList<>();
            listMenuItems.add("Edit");
            listMenuItems.add("Delete");
            String []menuArray=new String[2];
            listMenuItems.toArray(menuArray);
            for (int i = 0; i < menuArray.length; i++) {
                menu.add(Menu.NONE, i, i, menuArray[i]);
            }
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Delete") {
            Note.removeNote(selectedNote.getMovieId(),selectedNote.getNoteTitle(), selectedNote.getText());
            Note.saveNotes();
            Note.refreshNotes();
            List<Note> notes=Note.getMovieNotes(selectedNote.getMovieId());
            NotesUpdater.update(DetailsListAdapter.notesView,DetailsListAdapter.notesAdapter,notes,250);
        }
        else
        if(item.getTitle()=="Edit"){
            edd.setEditedNote(selectedNote);
            edd.show();
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId()==R.id.add_note){
            cdd.show();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        final Intent intent = getIntent();
        final Movie movie = (Movie) intent.getSerializableExtra("Movie");
        viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper);
        viewFlipper.removeAllViews();

        List<String> lPathes=movie.getDetails().getImagePathes();
        String [] iPathes=new String[lPathes.size()];
        lPathes.toArray(iPathes);
        for(String path : iPathes){
            ImageView view = new ImageView(getApplicationContext());
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(view);
            ImageLoader.getInstance().displayImage(Movie.transformPathToURL(path, Movie.ImageSize.W600),view);
        }
        Note.refreshNotes();
        ListView listView=(ListView)findViewById(R.id.detailsList);
        DetailsListAdapter adapter = new DetailsListAdapter(this,movie, Note.getMovieNotes(movie.getId()));
        listView.setAdapter(adapter);

        cdd=new CreateNoteDialog(DetailsActivity.this);
        cdd.setTitle("Додати нотатку");
        cdd.setMovieInfo(movie.getId(),movie.getOriginal_title());
        edd=new EditNoteDialog(DetailsActivity.this);
        edd.setTitle("Редагувати нотатку");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        currentImageByteArray=null;
        switch(requestCode){

            case SELECT_PHOTO:{
                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    currentImageByteArray=stream.toByteArray();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            break;
        }
    }

    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = touchevent.getX();
                break;
            case MotionEvent.ACTION_UP:
                float currentX = touchevent.getX();
                if (lastX < currentX) {
                    if (viewFlipper.getDisplayedChild() == 0)
                        break;
                    viewFlipper.setInAnimation(this, R.anim.slide_in_from_left);
                    viewFlipper.setOutAnimation(this, R.anim.slide_out_to_right);
                    viewFlipper.showNext();
                }
                if (lastX > currentX) {
                    if (viewFlipper.getDisplayedChild() == 1)
                        break;
                    viewFlipper.setInAnimation(this, R.anim.slide_in_from_right);
                    viewFlipper.setOutAnimation(this, R.anim.slide_out_to_left);
                    viewFlipper.showPrevious();
                }
                break;
        }
        return false;
    }
}

