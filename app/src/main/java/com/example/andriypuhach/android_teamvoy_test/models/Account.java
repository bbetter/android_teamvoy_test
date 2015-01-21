package com.example.andriypuhach.android_teamvoy_test.models;

import android.location.Location;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

/**
 * Created by andriypuhach on 1/19/15.
 */
public class Account {

    private String name;
    private String surname;

    private Location location;
    private DateTime birthday;
    private String about;
    private String relationships;
    private List<String> photoURLs;
    private List<Work> works;

    public Account() {
    }

    public List<Work> getWorks() {
        return works;
    }

    public void setWorks(List<Work> works) {
        this.works = works;
    }

    public class Work{
        String description;
        String position;
        String name;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public DateTime getBirthday() {
        return birthday;
    }

    public void setBirthday(DateTime birthday) {
        this.birthday = birthday;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public List<String> getPhotoURLs() {
        return photoURLs;
    }

    public void setPhotoURLs(List<String> photoURLs) {
        this.photoURLs = photoURLs;
    }

    public String getRelationships() {
        return relationships;
    }

    public void setRelationships(String relationships) {
        this.relationships = relationships;
    }
}
