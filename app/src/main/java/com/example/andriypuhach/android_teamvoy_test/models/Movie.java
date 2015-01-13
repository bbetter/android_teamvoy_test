package com.example.andriypuhach.android_teamvoy_test.models;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;


public class Movie implements Serializable {
    private final transient static String ImageURL = "https://image.tmdb.org/t/p/";
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
 }
