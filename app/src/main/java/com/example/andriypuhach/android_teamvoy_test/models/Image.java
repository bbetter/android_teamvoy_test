package com.example.andriypuhach.android_teamvoy_test.models;

import java.io.Serializable;

/**
 * Created by andriypuhach on 1/13/15.
 */
public class Image implements Serializable {
    private String file_path;

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }
}
