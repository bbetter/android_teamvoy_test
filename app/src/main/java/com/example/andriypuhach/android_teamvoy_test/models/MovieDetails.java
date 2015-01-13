package com.example.andriypuhach.android_teamvoy_test.models;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

public class MovieDetails implements Serializable {
    private float budget;
    private float revenue;
    private String homepage;
    private String tagLine;
    private String overview;
    private String status;

    private List<Genre> genres;
    private List<Company> production_companies;
    private List<Country> production_countries;
    private List<Language> spoken_languages;
    private List<String> imagePathes;

    public MovieDetails() {

    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public float getBudget() {
        return budget;
    }

    public void setBudget(float budget) {
        this.budget = budget;
    }

    public String getTagLine() {
        return tagLine;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }


    public List<String> getImagePathes() {
        return imagePathes;
    }

    public void setImagePathes(List<String> imagePathes) {
        this.imagePathes = imagePathes;
    }

    public float getRevenue() {
        return revenue;
    }

    public void setRevenue(float revenue) {
        this.revenue = revenue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Company> getProduction_companies() {
        return production_companies;
    }

    public void setProduction_companies(List<Company> production_companies) {
        this.production_companies = production_companies;
    }

    public List<Country> getProduction_countries() {
        return production_countries;
    }

    public void setProduction_countries(List<Country> production_countries) {
        this.production_countries = production_countries;
    }

    public List<Language> getSpoken_languages() {
        return spoken_languages;
    }

    public void setSpoken_languages(List<Language> spoken_languages) {
        this.spoken_languages = spoken_languages;
    }

    public class Language implements Serializable {
        private String iso;
        private String name;

        public String getIso() {
            return iso;
        }

        public void setIso(String iso) {
            this.iso = iso;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class Company implements Serializable {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class Country implements Serializable {
        private String iso;
        private String name;

        public String getIso() {
            return iso;
        }

        public void setIso(String iso) {
            this.iso = iso;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class Genre implements Serializable {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}