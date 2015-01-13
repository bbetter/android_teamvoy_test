package com.example.andriypuhach.android_teamvoy_test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Джон on 05.01.2015.
 */
public class MyLogger {
    public static final String JSON_PARSING_LEVEL_WARNING = "There was a trouble while parsing json respond";
    public static final String JSON_READING_LEVEL_WARNING = "There was a trouble while trying to read json from server";
    private final static String LOG_FILE = "teamvoyTest.log";

    public static void appendLog(String tag, String text) {
        File logFile = new File(LOG_FILE);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {

            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(tag + ":" + text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
