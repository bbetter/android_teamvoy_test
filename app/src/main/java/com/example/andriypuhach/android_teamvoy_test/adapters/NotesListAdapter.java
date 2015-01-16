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
import com.example.andriypuhach.android_teamvoy_test.models.Movie;
import com.example.andriypuhach.android_teamvoy_test.models.Note;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Джон on 16.01.2015.
 */
public class NotesListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Note> notes;
    public NotesListAdapter(Context context){
        this.context=context;
        inflater=LayoutInflater.from(context);
    }
    public void setNotes(List<Note> notes){
        this.notes=notes;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return notes.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        NoteViewHolder noteViewHolder = new NoteViewHolder();
        if(convertView==null){
            convertView=inflater.inflate(R.layout.note_row,null);
            noteViewHolder.tvNoteText = (TextView)convertView.findViewById(R.id.tvNoteText);
            noteViewHolder.tvNoteTitle=(TextView)convertView.findViewById(R.id.tvNoteTitle);
            noteViewHolder.ivImage=(ImageView)convertView.findViewById(R.id.noteImage);
            convertView.setTag(noteViewHolder);
        }
        else{
            noteViewHolder=(NoteViewHolder)convertView.getTag();
        }
        Note note=notes.get(position);
        noteViewHolder.tvNoteText.setText(note.getText());
        noteViewHolder.tvNoteTitle.setText(note.getNoteTitle());
        Bitmap bitmap;
        if(notes.get(position).getImage()!=null) {
            bitmap = BitmapFactory.decodeByteArray(note.getImage(), 0, note.getImage().length);
            noteViewHolder.ivImage.setImageBitmap(bitmap);
        }
        return convertView;
    }
    public static class NoteViewHolder{
        TextView tvNoteTitle;
        TextView tvNoteText;
        ImageView ivImage;
    }
}
