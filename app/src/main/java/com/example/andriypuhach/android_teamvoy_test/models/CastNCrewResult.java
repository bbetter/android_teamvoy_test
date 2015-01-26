package com.example.andriypuhach.android_teamvoy_test.models;

import java.util.List;

/**
 * Created by andriypuhach on 26.01.15.
 */
public class CastNCrewResult {
    private int id;
    private List<Movie.Details.CastPerson> cast;
    private List<Movie.Details.CrewPerson> crew;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Movie.Details.CastPerson> getCast() {
        return cast;
    }

    public void setCast(List<Movie.Details.CastPerson> cast) {
        this.cast = cast;
    }

    public List<Movie.Details.CrewPerson> getCrew() {
        return crew;
    }

    public void setCrew(List<Movie.Details.CrewPerson> crew) {
        this.crew = crew;
    }
}
