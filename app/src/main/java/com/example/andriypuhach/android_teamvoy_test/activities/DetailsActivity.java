package com.example.andriypuhach.android_teamvoy_test.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.andriypuhach.android_teamvoy_test.MovieDatabaseHelper;
import com.example.andriypuhach.android_teamvoy_test.R;
import com.example.andriypuhach.android_teamvoy_test.adapters.DetailsExpandableListAdapter;
import com.example.andriypuhach.android_teamvoy_test.dialogs.CreateNoteDialog;
import com.example.andriypuhach.android_teamvoy_test.dialogs.EditNoteDialog;
import com.example.andriypuhach.android_teamvoy_test.models.CastNCrewResult;
import com.example.andriypuhach.android_teamvoy_test.models.Movie;
import com.example.andriypuhach.android_teamvoy_test.rest.RestClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailsActivity extends Activity {
    private ExpandableListView detailsListView;
    private DetailsExpandableListAdapter detailsListAdapter;

    private SliderLayout slider;

    private CreateNoteDialog cdd ;
    private EditNoteDialog edd;
    private Movie.Details.Note selectedNote;
    private MovieDatabaseHelper dbHelper;
    private Movie movie;


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if(v.getId()==R.id.detailsList){
            ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;

            int type = ExpandableListView.getPackedPositionType(info.packedPosition);
            int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
            int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition);
            selectedNote=movie.getDetails().getNotes().get(childPosition);
            if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                switch(groupPosition){
                    case DetailsExpandableListAdapter.VIEW_TYPE_NOTES:
                        menu.add("Delete");
                        menu.add("Edit");
                        break;

                    default:

                }
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
                }
            });
            edd.setEditedNote(selectedNote);
            edd.setCanceledOnTouchOutside(true);
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
                }
            });
            cdd.setCanceledOnTouchOutside(true);
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
        slider = (SliderLayout)findViewById(R.id.slider);
       // slider.removeAllViews();
        List<String> lPathes=movie.getDetails().getImagePathes();
        String [] iPathes=new String[lPathes.size()];
        lPathes.toArray(iPathes);
        for(String path : iPathes){
            TextSliderView view = new TextSliderView(this);
                view.description(movie.getTitle())
                    .image(Movie.transformPathToURL(path, Movie.ImageSize.W600))
                    .error(R.drawable.failed_to_load)
                    .setScaleType(BaseSliderView.ScaleType.Fit);
            slider.addSlider(view);
        }
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        slider.setDuration(8000);
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    List<Movie.Details.Video> videos = new Gson().fromJson(RestClient.getApi().getVideos(movie.getId()).getAsJsonObject().get("results"),new TypeToken<List<Movie.Details.Video>>(){}.getType());
                    CastNCrewResult castNCrewResult =RestClient.getApi().getCastNCrew(movie.getId());
                    movie.getDetails().setCast(castNCrewResult.getCast());
                    movie.getDetails().setCrew(castNCrewResult.getCrew());
                    movie.getDetails().setVideos(videos);
                }
            });
                  thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    detailsListView=(ExpandableListView)findViewById(R.id.detailsList);
    detailsListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if(groupPosition==3){
                    if(movie.getDetails().getVideos().size()>childPosition && movie.getDetails().getVideos().size()!=0) {
                        Movie.Details.Video video = movie.getDetails().getVideos().get(childPosition);
                        final Intent intent = new Intent(DetailsActivity.this, YoutubeVideoActivity.class);
                        intent.putExtra("VideoKey", video.getKey());
                        startActivity(intent);

                        return true;
                    }

                }
                else if(groupPosition==4){
                    if(movie.getDetails().getNotes().size()>childPosition && movie.getDetails().getNotes().size()!=0){
                        Movie.Details.Note note= movie.getDetails().getNotes().get(childPosition);
                        final Intent intent = new Intent(DetailsActivity.this,NoteActivity.class);
                        intent.putExtra("Note",note);
                        startActivity(intent);
                    }
                }
                return false;
            }
        });
                  detailsListAdapter= new DetailsExpandableListAdapter(DetailsActivity.this,movie);
                  detailsListView.setAdapter(detailsListAdapter);
                  registerForContextMenu(detailsListView);
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
            Uri selectedImage = data.getData();
            String filePath = getImagePath(selectedImage);
            switch (requestCode) {

                case CreateNoteDialog.SELECT_PHOTO_CREATE: {
                    CreateNoteDialog.createImagePath = null;
                    CreateNoteDialog.createImagePath=filePath;
                    Picasso.with(getApplicationContext()).load("file:///" + filePath).error(R.drawable.failed_to_load).into(CreateNoteDialog.imageView);

                }
                break;
                case EditNoteDialog.SELECT_PHOTO_EDIT: {
                    EditNoteDialog.editImagePath = filePath;
                    Picasso.with(getApplicationContext()).load("file:///" + filePath).error(R.drawable.failed_to_load).into(EditNoteDialog.imageView);
                }
                break;
            }
        }
    }
}

