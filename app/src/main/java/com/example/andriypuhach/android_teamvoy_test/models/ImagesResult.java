package com.example.andriypuhach.android_teamvoy_test.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andriypuhach on 1/13/15.
 */
public class ImagesResult implements Serializable {
    private List<Image> backdrops;
    private List<Image> posters;



    public List<Image> getPosters() {
        return posters;
    }

    public void setPosters(List<Image> posters) {
        this.posters = posters;
    }

    public List<String> getBackdropPathes(){
        List<String> list = new ArrayList<String>();

        for(Image i:backdrops){
            list.add(i.getFile_path());
        }
        return list;
    }

    public List<String> getPosterPathes(){
        List<String> list = new ArrayList<>();

        for(Image i:posters){
            list.add(i.getFile_path());
        }
        return list;
    }

    public List<Image> getBackdrops() {
        return backdrops;
    }

    public void setBackdrops(List<Image> backdrops) {
        this.backdrops = backdrops;
    }
}
