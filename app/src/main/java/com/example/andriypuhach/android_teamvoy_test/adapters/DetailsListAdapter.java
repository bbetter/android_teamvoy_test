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
import com.example.andriypuhach.android_teamvoy_test.NotesUpdater;
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
    private final static int VIEW_TYPE_INFO = 0;
    private final static int VIEW_TYPE_NOTES = 1;
    private Activity activity;
    private Context context;
    private LayoutInflater inflater;
    private Movie movie;
    private List<Note> notes;
    public static NotesListAdapter notesAdapter;
    public static ListView notesView;
    static{

    }

    public DetailsListAdapter(Activity act, Movie movie, List<Note> notes) {
        this.activity = act;
        this.context = act.getApplicationContext();
        this.movie = movie;
        this.notes = notes;
        inflater = LayoutInflater.from(context);
        notesView=(ListView)((View)inflater.inflate(R.layout.details_row_notes,null)).findViewById(R.id.notesListView);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return 2;
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
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return VIEW_TYPE_INFO;
        return VIEW_TYPE_NOTES;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            switch (type) {
                case VIEW_TYPE_INFO: {
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
                }
                break;
                case VIEW_TYPE_NOTES: {
                    convertView = inflater.inflate(R.layout.details_row_notes, parent, false);
                    holder.lvNotes = (ListView) convertView.findViewById(R.id.notesListView);

                }
                break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        switch(type){
            case VIEW_TYPE_INFO:
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
                break;
            case VIEW_TYPE_NOTES:
                notesAdapter = new NotesListAdapter(activity.getApplicationContext());
                NotesUpdater.update(holder.lvNotes,notesAdapter,notes,250);
                activity.registerForContextMenu(holder.lvNotes);
                convertView.invalidate();
                break;
        }
        return convertView;
    }

    private static class ViewHolder {
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
        ListView lvNotes;
    }
}
