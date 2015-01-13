package com.example.andriypuhach.android_teamvoy_test.models;


import java.io.Serializable;
import java.util.List;


public class MovieRequestResult implements Serializable {

    private List<Movie> results;

    private int page;

    private int total_pages;

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }
}
