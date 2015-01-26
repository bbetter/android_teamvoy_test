package com.example.andriypuhach.android_teamvoy_test.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.andriypuhach.android_teamvoy_test.MovieDatabaseHelper;
import com.example.andriypuhach.android_teamvoy_test.R;
import com.example.andriypuhach.android_teamvoy_test.adapters.DetailsListAdapter;
import com.example.andriypuhach.android_teamvoy_test.dialogs.CreateNoteDialog;
import com.example.andriypuhach.android_teamvoy_test.dialogs.EditNoteDialog;
import com.example.andriypuhach.android_teamvoy_test.models.CastNCrewResult;
import com.example.andriypuhach.android_teamvoy_test.models.Movie;
import com.example.andriypuhach.android_teamvoy_test.rest.RestClient;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DetailsActivity extends Activity {
    //region viewFlipper variables
    private float lastX;
    private ViewFlipper viewFlipper;
    //endregion
    private ListView detailsListView;
    private DetailsListAdapter detailsListAdapter;

    private CreateNoteDialog cdd ;
    private EditNoteDialog edd;
    private Movie.Details.Note selectedNote;
    private MovieDatabaseHelper dbHelper;
    private Movie movie;


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if(v.getId()==R.id.notesListView){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            selectedNote=(Movie.Details.Note)DetailsListAdapter.notesAdapter.getItem(info.position);
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
            dbHelper.deleteNote(selectedNote.getId());
            movie.getDetails().setNotes(dbHelper.selectNoteByMovieID(movie.getId()));
            detailsListAdapter.setMovie(movie);
            detailsListView.setVisibility(View.INVISIBLE);
            detailsListView.setVisibility(View.VISIBLE);
        }
        else
        if(item.getTitle()=="Edit"){
            edd=new EditNoteDialog(DetailsActivity.this);
            edd.setTitle("Редагувати нотатку");
            edd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    movie.getDetails().setNotes(dbHelper.selectNoteByMovieID(movie.getId()));
                    detailsListAdapter.setMovie(movie);
                    detailsListView.setVisibility(View.INVISIBLE);
                    detailsListView.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(),"Нотатку відредаговано",Toast.LENGTH_SHORT).show();
                }
            });
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
            cdd=new CreateNoteDialog(DetailsActivity.this);
            cdd.setTitle("Додати нотатку");
            cdd.setMovieInfo(movie);
            cdd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    movie.getDetails().setNotes(dbHelper.selectNoteByMovieID(movie.getId()));
                    detailsListAdapter.setMovie(movie);
                    detailsListView.invalidateViews();
                    detailsListView.scrollBy(0,0);
                    Toast.makeText(getApplicationContext(),"Нотатку додано",Toast.LENGTH_SHORT).show();
                }
            });
            cdd.show();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        dbHelper=new MovieDatabaseHelper(this);
        final Intent intent = getIntent();
        movie = (Movie) intent.getSerializableExtra("Movie");
        movie.getDetails().setNotes(dbHelper.selectNoteByMovieID(movie.getId()));
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

            RestClient.getApi().getCastNCrew(movie.getId(),new Callback<CastNCrewResult>() {
                @Override
                public void success(CastNCrewResult castNCrewResult, Response response) {

                  movie.getDetails().setCast(castNCrewResult.getCast());
                  movie.getDetails().setCrew(castNCrewResult.getCrew());
                    detailsListView=(ListView)findViewById(R.id.detailsList);
                    detailsListAdapter= new DetailsListAdapter(DetailsActivity.this,movie);
                    detailsListView.setAdapter(detailsListAdapter);
                    detailsListView.setVisibility(View.INVISIBLE);
                    detailsListView.setVisibility(View.VISIBLE);
                }

                @Override
                public void failure(RetrofitError error) {
                    movie.getDetails().setCast(new ArrayList<Movie.Details.CastPerson>());
                    movie.getDetails().setCrew(new ArrayList<Movie.Details.CrewPerson>());
                    detailsListView=(ListView)findViewById(R.id.detailsList);
                    detailsListAdapter= new DetailsListAdapter(DetailsActivity.this,movie);
                    detailsListAdapter.setMovie(movie);
                    detailsListView.setAdapter(detailsListAdapter);
                    detailsListView.setVisibility(View.INVISIBLE);
                    detailsListView.setVisibility(View.VISIBLE);
                }
            });



      }

    /**
     * метод перетворює URI отримане при виборі із галереї зображень у абсолютний шлях до цього зображення
     * @param uri
     * @return шлях до зображення
     */
    private String getImagePath(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_CANCELED){

        }
        else
        if(data!=null) {
            switch (requestCode) {

                case CreateNoteDialog.SELECT_PHOTO_CREATE: {
                    CreateNoteDialog.createImagePath = null;
                    Uri selectedImage = data.getData();
                    String filePath = getImagePath(selectedImage);
                    CreateNoteDialog.createImagePath=filePath;
                    ImageLoader.getInstance().displayImage("file:///"+filePath,CreateNoteDialog.imageView);
                }
                break;
                case EditNoteDialog.SELECT_PHOTO_EDIT: {
                    Uri selectedImage = data.getData();
                    String filePath = getImagePath(selectedImage);
                    EditNoteDialog.editImagePath = filePath;
                    ImageLoader.getInstance().displayImage("file:///"+filePath,EditNoteDialog.imageView);
                }
                break;
            }
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

