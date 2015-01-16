package com.example.andriypuhach.android_teamvoy_test.models;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Джон on 15.01.2015.
 */
public class Note implements Serializable {
    public static transient List<Note> notes;
    private final transient static File appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/android_teamvoy_test");
    static{
        notes= new ArrayList<>();

    }
    private int movieId;
    private String movieTitle;

    private String noteTitle;
    private String text;
    private byte[] image;

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public static List<Note> getMovieNotes(int id){
        List<Note> mvNotes=new ArrayList<>();
        for(Note note:notes){
            if(note.getMovieId()==id)
            mvNotes.add(note);
        }
        return mvNotes;
    }

    public static void refreshNotes(){
        File file = new File(appDir, "movienotes.movinf");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        notes = deserializeList(file);
    }
    public static void saveNotes(){
        serializeList(notes, new File(appDir.getAbsolutePath() + "/movienotes.movinf"));
    }
    private static void serializeList(List<Note> movies,File file){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(movies);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static List<Note> deserializeList(File file){
        List<Note> movies=new ArrayList<>();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            movies = (List<Note>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException e) {

        }
        catch (ClassNotFoundException e) {

        }
        return movies;
    }
}
