package com.example.andriypuhach.android_teamvoy_test;

import android.app.Activity;
import android.widget.Toast;

import com.example.andriypuhach.android_teamvoy_test.activities.MainActivity;
import com.facebook.Request;
import com.facebook.Session;

/**
 * Created by Джон on 15.01.2015.
 */
public class FacebookManager {
    private static boolean checkPermissions() {
        Session s = Session.getActiveSession();
        if (s != null) {
            return s.getPermissions().contains("publish_actions");
        } else
            return false;
    }
    private static void requestPermissions(Activity activity) {
        Session s = Session.getActiveSession();
        if (s != null)
            s.requestNewPublishPermissions(new Session.NewPermissionsRequest(activity,"publish_actions"));
    }
    public static void postToFacebook(String text, final Activity activity){
        if(checkPermissions()){
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
            requestPermissions(activity);
        }
    }
}
