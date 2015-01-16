package com.example.andriypuhach.android_teamvoy_test.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.andriypuhach.android_teamvoy_test.Joiner;
import com.example.andriypuhach.android_teamvoy_test.R;
import com.example.andriypuhach.android_teamvoy_test.models.Movie;
import com.example.andriypuhach.android_teamvoy_test.models.MovieDetails;
import com.example.andriypuhach.android_teamvoy_test.models.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Джон on 16.01.2015.
 */
public class DetailsListAdapter extends BaseAdapter {
    private final static int VIEW_TYPE_INFO=0;
    private final static int VIEW_TYPE_NOTES=1;
    private final static int VIEW_TYPE_INSERT_NOTE=2;
    private Activity activity;
    private Context context;
    private LayoutInflater inflater;
    private Movie movie;
    private List<Note> notes;
    private NotesListAdapter notesAdapter;


    public static final int SELECT_PHOTO=100;
    public static byte [] currentImageByteArray;
    public DetailsListAdapter(Activity act,Movie movie,List<Note> notes){
        this.activity=act;
        this.context=act.getApplicationContext();
        this.movie=movie;
        this.notes=notes;
        inflater=LayoutInflater.from(context);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0) return VIEW_TYPE_INFO;
        else if(position==1) return VIEW_TYPE_NOTES;
        else return VIEW_TYPE_INSERT_NOTE;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            int type= getItemViewType(position);
            switch (type){
                case VIEW_TYPE_INFO:{
                    DetailsInfoHolder holder = new DetailsInfoHolder();
                    if(convertView==null) {
                        convertView = inflater.inflate(R.layout.details_row_info, parent, false);

                        holder.tvBudget = (TextView) convertView.findViewById(R.id.tvBudget);
                        holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
                        holder.tvCompanies = (TextView) convertView.findViewById(R.id.tvCompany);
                        holder.tvGenres = (TextView) convertView.findViewById(R.id.tvGenre);
                        holder.tvHomePage = (TextView) convertView.findViewById(R.id.tvHomePage);
                        holder.tvOverview = (TextView) convertView.findViewById(R.id.tvOverview);
                        holder.tvRevenue = (TextView) convertView.findViewById(R.id.tvRevenue);
                        holder.tvTagLine = (TextView) convertView.findViewById(R.id.tvTagline);
                        holder.tvStatus = (TextView) convertView.findViewById(R.id.tvStatus);
                        holder.tvReleaseDate = (TextView) convertView.findViewById(R.id.tvYear);

                        holder.tvTitle.setText(movie.getOriginal_title());
                        holder.tvOverview.setText(movie.getDetails().getOverview());
                        holder.tvTagLine.setText(movie.getDetails().getTagLine());
                        holder.tvHomePage.setText(movie.getDetails().getHomepage());
                        holder.tvReleaseDate.setText(movie.getRelease_date().toLocalDate().toString());
                        holder.tvBudget.setText(String.valueOf(movie.getDetails().getBudget()));
                        holder.tvRevenue.setText(String.valueOf(movie.getDetails().getRevenue()));
                        holder.tvStatus.setText(movie.getDetails().getStatus());
                        List<String> genres = new ArrayList<>();
                        List<String> companies = new ArrayList<>();
                        for (MovieDetails.Genre g : movie.getDetails().getGenres()) {
                            genres.add(g.getName());
                        }
                        for (MovieDetails.Company c : movie.getDetails().getProduction_companies()) {
                            companies.add(c.getName());
                        }
                        holder.tvGenres.setText(Joiner.join(genres, ','));
                        holder.tvCompanies.setText(Joiner.join(genres, ','));
                    }
                    else{
                        convertView.setTag(holder);
                    }
                }
                break;
                case VIEW_TYPE_NOTES:{
                    DetailsNotesHolder dtnHolder= new DetailsNotesHolder();
                    if(convertView==null) {
                        Note.refreshNotes();
                        convertView = inflater.inflate(R.layout.details_row_notes, parent, false);
                        dtnHolder.lvNotes = (ListView) convertView.findViewById(R.id.notesListView);
                        notesAdapter = new NotesListAdapter(context);
                        notesAdapter.setNotes(notes);
                        dtnHolder.lvNotes.setAdapter(notesAdapter);
                        dtnHolder.lvNotes.invalidate();
                    }
                    else{
                        convertView.setTag(dtnHolder);
                    }
                }
                break;
                case VIEW_TYPE_INSERT_NOTE:{
                    final DetailsInsertNoteHolder dtInHolder=new DetailsInsertNoteHolder();
                    currentImageByteArray=null;
                        convertView = inflater.inflate(R.layout.details_row_notes_insert, parent, false);
                        dtInHolder.etText = (EditText) convertView.findViewById(R.id.noteTitleEdit);
                        dtInHolder.etTitle = (EditText) convertView.findViewById(R.id.noteTextEdit);
                        dtInHolder.btnUpload = (Button) convertView.findViewById(R.id.uploadPhotoBtn);
                        dtInHolder.btnSubmit = (Button) convertView.findViewById(R.id.submitNoteBtn);
                        dtInHolder.hsvHolder=(HorizontalScrollView)convertView.findViewById(R.id.imagesHoder);
                        dtInHolder.btnUpload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                               Intent i = new Intent(Intent.ACTION_PICK);
                               i.setType("image/*");
                               activity.startActivityForResult(i, SELECT_PHOTO);
                            }
                        });
                        dtInHolder.btnSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Note.refreshNotes();
                                Note note = new Note();
                                note.setMovieId(movie.getId());
                                note.setMovieTitle(movie.getTitle());
                                note.setNoteTitle(dtInHolder.etTitle.getText().toString());
                                note.setText(dtInHolder.etText.getText().toString());
                                note.setImage(currentImageByteArray);
                                Note.notes.add(note);
                                Note.saveNotes();
                                Note.refreshNotes();
                            }
                        });

                 }
                break;
            }
        return convertView;
    }
    private static class DetailsInfoHolder{
        TextView tvTitle;
        TextView tvStatus;
        TextView tvReleaseDate;
        TextView tvTagLine;
        TextView tvHomePage;
        TextView tvBudget;
        TextView tvRevenue;
        TextView tvGenres;
        TextView tvCompanies;
        TextView tvOverview;
    }
    private static class DetailsNotesHolder{
        ListView lvNotes;
    }
    private static class DetailsInsertNoteHolder{
       EditText etTitle;
       EditText etText;
       HorizontalScrollView hsvHolder;
       Button btnUpload;
       Button btnSubmit;

    }
}
