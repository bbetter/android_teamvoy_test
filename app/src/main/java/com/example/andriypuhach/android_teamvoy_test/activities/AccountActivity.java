package com.example.andriypuhach.android_teamvoy_test.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.andriypuhach.android_teamvoy_test.CustomScrollView;
import com.example.andriypuhach.android_teamvoy_test.FacebookManager;
import com.example.andriypuhach.android_teamvoy_test.R;
import com.example.andriypuhach.android_teamvoy_test.WorkaroundMapFragment;
import com.example.andriypuhach.android_teamvoy_test.models.Account;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Date;
import java.util.List;

/**
 * Created by andriypuhach on 1/19/15.
 */
public class AccountActivity extends FragmentActivity {
    private GoogleMap googleMap;
    private float lastX;
    private ViewFlipper fbPhotoFlipper;
    private SharedPreferences mPrefs;
    class AccountLoader extends AsyncTask<Void,Void,Account>{

        @Override
        protected Account doInBackground(Void... params) {
            Account account;
            try {
                account = FacebookManager.getUserInfo(AccountActivity.this);
                account.setPhotoURLs(FacebookManager.getUserPhotoPathes(AccountActivity.this));
                account.setLocation(googleMap.getMyLocation());
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString("Account", new Gson().toJson(account));
                editor.commit();
            }catch(Exception e){
                account= new Gson().fromJson(mPrefs.getString("Account",""),Account.class);
            }
           return account;
        }

        @Override
        protected void onPostExecute(Account account) {
            TextView nameView=(TextView)findViewById(R.id.tvUserName);
            TextView surnameView=(TextView)findViewById(R.id.tvUserSurname);
            TextView relView=(TextView)findViewById(R.id.tvRelStatus);
            TextView aboutView=(TextView)findViewById(R.id.tvAbout);
            TextView ageView=(TextView)findViewById(R.id.tvAge);
            TextView workView=(TextView)findViewById(R.id.tvWorks);
            fbPhotoFlipper=(ViewFlipper)findViewById(R.id.fbPhotoFlipper);
            ageView.setText(String.valueOf(account.getAge()));
            nameView.setText(account.getName());
            surnameView.setText(account.getSurname());
            aboutView.setText(account.getAbout());
            relView.setText(account.getRelationships());
            List<Account.Work> thWorks=account.getWorks();
            List<String> pathes=account.getPhotoURLs();
            StringBuilder works=new StringBuilder();
            for(int i=0;i<thWorks.size();i++){
                works.append("\n"+thWorks.get(i).getName()+"\n"+thWorks.get(i).getPosition()+"\n"+thWorks.get(i).getDescription()+"\n");
                if(i!=thWorks.size()-1)
                works.append("------------------------");
            }
            fbPhotoFlipper.removeAllViews();
            for(String str:pathes){
                ImageView view = new ImageView(getApplicationContext());
                fbPhotoFlipper.addView(view);
                ImageLoader.getInstance().displayImage(str,view);
            }
            workView.setText(works.toString());
        }
    }


    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.account);
        mPrefs= getPreferences(MODE_PRIVATE);
        initilizeMap();
        new AccountLoader().execute();
    }
    private void initilizeMap() {
        final ScrollView scView=(ScrollView)findViewById(R.id.scrollView);
        if (googleMap == null) {
            googleMap = ((WorkaroundMapFragment)getSupportFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            ((WorkaroundMapFragment)getSupportFragmentManager().findFragmentById(
                    R.id.map)).setListener(new WorkaroundMapFragment.OnTouchListener(){

                @Override
                public void onTouch() {
                    scView.requestDisallowInterceptTouchEvent(true);
                }
            });

            googleMap.clear();
            googleMap.setMyLocationEnabled(true);
            final Account account=new Gson().fromJson(mPrefs.getString("Account",""),Account.class);
            Location lc=account.getLocation();
            if(lc!=null) {
                googleMap.addMarker(new MarkerOptions().position(new LatLng(lc.getLatitude(), lc.getLongitude())));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lc.getLatitude(), lc.getLongitude())));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }
            googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    googleMap.clear();
                    googleMap.addMarker(new MarkerOptions().position(latLng));

                    Location location = new Location("Current Marker Position");
                    location.setLatitude(latLng.latitude);
                    location.setLongitude(latLng.longitude);
                    location.setTime(new Date().getTime());

                    account.setLocation(location);
                    SharedPreferences.Editor editor= mPrefs.edit();
                    editor.putString("Account",new Gson().toJson(account));
                    editor.commit();
                    Toast.makeText(getApplicationContext(),"Ви змінили вашу локацію",Toast.LENGTH_SHORT);
                }
            });
            googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    googleMap.clear();
                    Location location = googleMap.getMyLocation();
                    account.setLocation(location);
                    SharedPreferences.Editor editor= mPrefs.edit();
                    editor.putString("Account",new Gson().toJson(account));
                    editor.commit();

                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    LatLng latLng = new LatLng(latitude, longitude);

                    googleMap.addMarker(new MarkerOptions().position(latLng));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    return false;
                }
            });


            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }
    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                float currentX = event.getX();
                if (lastX < currentX) {
                    if (fbPhotoFlipper.getDisplayedChild() == 0)
                        break;
                    fbPhotoFlipper.setInAnimation(getApplicationContext(), R.anim.slide_in_from_left);
                    fbPhotoFlipper.setOutAnimation(getApplicationContext(), R.anim.slide_out_to_right);
                    fbPhotoFlipper.showNext();
                }
                if (lastX > currentX) {
                    if (fbPhotoFlipper.getDisplayedChild() == 1)
                        break;
                    fbPhotoFlipper.setInAnimation(getApplicationContext(), R.anim.slide_in_from_right);
                    fbPhotoFlipper.setOutAnimation(getApplicationContext(), R.anim.slide_out_to_left);
                    fbPhotoFlipper.showPrevious();
                }
                break;
        }
        return false;
    }
}
