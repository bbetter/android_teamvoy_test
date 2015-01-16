package com.example.andriypuhach.android_teamvoy_test.models;

import android.content.Context;
import android.os.Environment;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Movie implements Serializable {
    private final transient static String ImageURL = "https://image.tmdb.org/t/p/";
    private final transient static File appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/android_teamvoy_test");
    public static transient List<Movie> favorites;
    static{
        favorites=new ArrayList<>();
        refreshFavorites();
    }
    private int id;
    private int vote_count;
    private String original_title;
    private String title;
    private String poster_path;
    private DateTime release_date;
    private double vote_average;
    private double popularity;
    private boolean adult;
    private MovieDetails details;


    public static String transformPathToURL(String cutPath, ImageSize size) {
        return ImageURL + size.toString() + cutPath;
    }

    public String getPosterURL(ImageSize size) {
        return ImageURL + size.toString() + poster_path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public MovieDetails getDetails() {
        return details;
    }

    public void setDetails(MovieDetails details) {
        this.details = details;
    }

    public enum ImageSize {
        W75 {
            @Override
            public String toString() {
                return "w75";
            }
        },
        W150 {
            @Override
            public String toString() {
                return "w150";
            }
        },
        W300 {
            @Override
            public String toString() {
                return "w300";
            }
        },
        W600 {
            @Override
            public String toString() {
                return "w600";
            }
        }
    }

    public static String getImageURL() {
        return ImageURL;
    }

    public int getVote_count() {
        return vote_count;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }
    public DateTime getRelease_date() {
        return release_date;
    }

    public void setRelease_date(DateTime release_date) {
        this.release_date = release_date;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }


    public static void refreshFavorites(){
        File file = new File(appDir, "favorites.movinf");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        favorites = deserializeList(file);
    }
    public static void saveFavorites(){
        serializeList(favorites, new File(appDir.getAbsolutePath() + "/favorites.movinf"));
    }
    private static void serializeList(List<Movie> movies,File file){
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
    private static List<Movie> deserializeList(File file){
        List<Movie> movies=new ArrayList<>();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            movies = (List<Movie>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException e) {

        }
        catch (ClassNotFoundException e) {

        }
        return movies;
    }
 }