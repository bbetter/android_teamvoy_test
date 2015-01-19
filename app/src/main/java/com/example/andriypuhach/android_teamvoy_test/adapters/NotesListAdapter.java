package com.example.andriypuhach.android_teamvoy_test.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andriypuhach.android_teamvoy_test.R;
import com.example.andriypuhach.android_teamvoy_test.dialogs.CreateNoteDialog;
import com.example.andriypuhach.android_teamvoy_test.models.Movie;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Джон on 16.01.2015.
 */
public class NotesListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Movie.Details.Note> notes;
    public NotesListAdapter(Context context){
        this.context=context;
        inflater=LayoutInflater.from(context);
    }
    public void setNotes(List<Movie.Details.Note> notes){
        this.notes=notes;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Object getItem(int position) {
        return notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tvNoteTitle;
        TextView tvNoteText;
        ImageView ivImage;

        if(convertView==null){
            convertView=inflater.inflate(R.layout.note_row,parent,false);
            tvNoteText = (TextView)convertView.findViewById(R.id.tvNoteText);
            tvNoteTitle=(TextView)convertView.findViewById(R.id.tvNoteTitle);
            ivImage=(ImageView)convertView.findViewById(R.id.noteImage);
            convertView.setTag(new NoteViewHolder(ivImage,tvNoteTitle,tvNoteText));
        }
        else{
            NoteViewHolder noteViewHolder=(NoteViewHolder)convertView.getTag();
            ivImage=noteViewHolder.ivImage;
            tvNoteText=noteViewHolder.tvNoteText;
            tvNoteTitle=noteViewHolder.tvNoteTitle;
        }
        Movie.Details.Note note=notes.get(position);
        tvNoteText.setText(note.getNoteText());
        tvNoteTitle.setText(note.getNoteTitle());
        ImageLoader.getInstance().displayImage("file:///"+note.getImagePath(),ivImage);
        return convertView;
    }
    static class NoteViewHolder{
        public final TextView tvNoteTitle;
        public final TextView tvNoteText;
        public final ImageView ivImage;

        public NoteViewHolder(ImageView image, TextView title,TextView text) {
            this.tvNoteText=text;
            this.tvNoteTitle=title;
            this.ivImage=image;
        }
    }
}
