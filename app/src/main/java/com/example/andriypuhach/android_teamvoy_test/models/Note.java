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
import java.util.Iterator;
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
    private static int uniqueNoteId=1;

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    private Movie movie;

    private String noteTitle;
    private String text;
    private byte[] image;

    public static int getUniqueNoteId() {
        return uniqueNoteId;
    }


    public static void setUniqueNoteId(int uniqueNoteId) {
        Note.uniqueNoteId = uniqueNoteId;
    }

    public int getMovieId() {
        return movie.getId();
    }

    public Note(){
        uniqueNoteId++;
        movie=new Movie();
    }
    public void setMovieId(int movieId) {
        movie.setId(movieId);
    }

    public String getMovieTitle() {
        return movie.getTitle();
    }

    public void setMovieTitle(String movieTitle) {
        movie.setTitle(movieTitle);
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
        appDir.mkdirs();
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
    public static void replace(Note oldNote,Note newNote){
        removeNote(oldNote.getMovieId(),oldNote.getNoteTitle(),oldNote.getText());
        notes.add(newNote);
    }

    public static void removeNote(int movieId,String noteTitle,String noteText){
        Iterator<Note> iterator = Note.notes.iterator();
        while(iterator.hasNext()){
            Note n = iterator.next();
            if(n.getMovieId()==movieId && n.getNoteTitle().toLowerCase().equals(noteTitle.toLowerCase())
                    && (n.getText().equals(noteText.toLowerCase()))){
                iterator.remove();
            }
        }
    }
}
