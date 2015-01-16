package com.example.andriypuhach.android_teamvoy_test;

import android.view.ViewGroup;
import android.widget.ListView;

import com.example.andriypuhach.android_teamvoy_test.adapters.NotesListAdapter;
import com.example.andriypuhach.android_teamvoy_test.models.Note;

import java.util.List;

/**
 * Created by andriypuhach on 1/16/15.
 */
public class NotesUpdater {
    public static void update(ListView lview,NotesListAdapter adapter,List<Note> notes,int rowSize){
        Note.refreshNotes();
        ViewGroup.LayoutParams layoutParams = lview.getLayoutParams();
        layoutParams.height = rowSize * lview.getCount();
        lview.setLayoutParams(layoutParams);
        adapter.setNotes(notes);
        lview.setAdapter(adapter);
        lview.invalidate();
    }
}
