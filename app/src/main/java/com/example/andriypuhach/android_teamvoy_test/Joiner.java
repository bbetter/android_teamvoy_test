package com.example.andriypuhach.android_teamvoy_test;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Джон on 16.01.2015.
 */
public class Joiner {
    public static String join(List<String> stringList,char separator){
        Iterator i = stringList.iterator();
        StringBuilder sb = new StringBuilder();
        for (;;) {
            sb.append(i.next());
            if (! i.hasNext()) break;
            sb.append(separator);
        }
        return sb.toString();
    }
}
