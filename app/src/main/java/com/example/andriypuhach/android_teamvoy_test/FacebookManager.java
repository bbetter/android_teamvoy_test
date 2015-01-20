package com.example.andriypuhach.android_teamvoy_test;

import android.app.Activity;
import android.util.Log;
import android.widget.ActionMenuView;
import android.widget.Toast;

import com.example.andriypuhach.android_teamvoy_test.activities.MainActivity;
import com.example.andriypuhach.android_teamvoy_test.models.Account;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Session;
import com.squareup.okhttp.Response;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimePrinter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Джон on 15.01.2015.
 */
public class FacebookManager {
    private static boolean checkPermissions(List<String> permissions) {
        Session s = Session.getActiveSession();
        if (s != null) {
            List<String> hadPermissions=s.getPermissions();
            for(String req:permissions){
              if(!hadPermissions.contains(req)) return false;
            }
            return true;
        } else
            return false;
    }
    private static void requestPermissions(Activity activity,List<String> permissions,boolean readWrite) {
        Session s = Session.getActiveSession();
        if (s != null) {
                if (readWrite)
                    s.requestNewPublishPermissions(new Session.NewPermissionsRequest(activity, Joiner.join(permissions, ',')));
                else
                    s.requestNewReadPermissions(new Session.NewPermissionsRequest(activity, Joiner.join(permissions, ',')));
            }
    }
    public static void postToFacebook(String text, final Activity activity){
        if(checkPermissions(Arrays.asList("publish_action"))){
            Request request = Request.newStatusUpdateRequest(
                    Session.getActiveSession(), "I like "+text,
                    new Request.Callback() {
                        @Override
                        public void onCompleted(com.facebook.Response response) {
                            if (response.getError() == null)
                                Toast.makeText(activity,
                                        "Status updated successfully",
                                        Toast.LENGTH_LONG).show();
                        }


                    });
            request.executeAsync();
        }
        else{
            requestPermissions(activity,Arrays.asList("publish_action"),true);
        }
    }
    public static List<String> getUserPhotoPathes(final Activity activity){
        final List<String> pathes=new ArrayList<>();
        if(checkPermissions(Arrays.asList("user_photos"))){
            new Request(
                    Session.getActiveSession(),
                    "/me/photos",
                    null,
                    HttpMethod.GET,
                    new Request.Callback() {
                        @Override
                        public void onCompleted(com.facebook.Response response) {
                            JSONArray array =(JSONArray)response.getGraphObject().getProperty("data");
                            try {
                                for (int i = 0; i < array.length(); ++i) {
                                    JSONObject item = array.getJSONObject(i);
                                    pathes.add(item.getString("source"));
                                }
                            }
                            catch(JSONException ignored){}
                        }
                    }).executeAndWait();
        }
        else{
            requestPermissions(activity,Arrays.asList("user_photos"),false);
        }
        return pathes;
    }
    public static Account getUserInfo(final Activity activity){
        final Account account = new Account();
        if(checkPermissions(Arrays.asList("public_profile","user_about_me","user_status","user_relationships","user_birthday","user_work_history"))) {

            new Request(
                    Session.getActiveSession(),
                    "/me",
                    null,
                    HttpMethod.GET,
                    new Request.Callback() {
                        @Override
                        public void onCompleted(com.facebook.Response response) {
                            account.setAbout(response.getGraphObject().getProperty("bio").toString());
                            account.setName(response.getGraphObject().getProperty("first_name").toString());
                            account.setSurname(response.getGraphObject().getProperty("last_name").toString());
                            account.setRelationships(response.getGraphObject().getProperty("relationship_status").toString());
                            account.setAge(DateTime.now().getYear()-
                                    DateTime.parse(response.getGraphObject().getProperty("birthday").toString(),DateTimeFormat.forPattern("MM/dd/yyyy")).getYear());
                            JSONArray array=(JSONArray)response.getGraphObject().getProperty("work");
                            List<Account.Work> works=new ArrayList<>();
                            try {
                            for(int i=0;i<array.length();++i){
                                Account.Work work= account.new Work();
                                String description =((JSONObject)array.get(i)).getString("description");
                                String position=(((JSONObject)array.get(i)).getJSONObject("position")).getString("name");
                                String employee=(((JSONObject)array.get(i)).getJSONObject("employer")).getString("name");
                                work.setName(employee);
                                work.setDescription(description);
                                work.setPosition(position);
                                works.add(work);
                            }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            account.setWorks(works);
                        }
                    }
            ).executeAndWait();
        }
        else{
            requestPermissions(activity,Arrays.asList("public_profile","user_about_me","user_status","user_relationships","user_birthday","user_work_history"),false);
        }
        return account;
    }
}
