package com.example.andriypuhach.android_teamvoy_test.activities;

import android.app.DownloadManager;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.andriypuhach.android_teamvoy_test.FacebookManager;
import com.example.andriypuhach.android_teamvoy_test.R;
import com.example.andriypuhach.android_teamvoy_test.WorkaroundMapFragment;
import com.example.andriypuhach.android_teamvoy_test.models.Account;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by andriypuhach on 1/19/15.
 */
public class AccountActivity extends FragmentActivity {
    private GoogleMap googleMap;
    private float lastX;
    private ViewFlipper fbPhotoFlipper;
    private SharedPreferences mPrefs;
    private TextView placeView;

    private String fetchCityNameUsingGoogleMap(Location lc)
    {
        String googleMapUrl = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + lc.getLatitude() + ","
                + lc.getLongitude() + "&sensor=false&language=fr";
        try
        {
            OkHttpClient client = new OkHttpClient();
            Request request= new Request.Builder()
                    .url(googleMapUrl)
                    .build();

            JSONObject googleMapResponse = new JSONObject(client.newCall(request).execute().body().string());

            // many nested loops.. not great -> use expression instead
            // loop among all results
            JSONArray results = (JSONArray) googleMapResponse.get("results");
            for (int i = 0; i < results.length(); i++)
            {
                // loop among all addresses within this result
                JSONObject result = results.getJSONObject(i);
                if (result.has("address_components"))
                {
                    JSONArray addressComponents = result.getJSONArray("address_components");
                    // loop among all address component to find a 'locality' or 'sublocality'
                    for (int j = 0; j < addressComponents.length(); j++)
                    {
                        JSONObject addressComponent = addressComponents.getJSONObject(j);
                        if (result.has("types"))
                        {
                            JSONArray types = addressComponent.getJSONArray("types");

                            // search for locality and sublocality
                            String cityName = null;

                            for (int k = 0; k < types.length(); k++)
                            {
                                if ("locality".equals(types.getString(k)) && cityName == null)
                                {
                                    if (addressComponent.has("long_name"))
                                    {
                                        cityName = addressComponent.getString("long_name");
                                    }
                                    else if (addressComponent.has("short_name"))
                                    {
                                        cityName = addressComponent.getString("short_name");
                                    }
                                }
                                if ("sublocality".equals(types.getString(k)))
                                {
                                    if (addressComponent.has("long_name"))
                                    {
                                        cityName = addressComponent.getString("long_name");
                                    }
                                    else if (addressComponent.has("short_name"))
                                    {
                                        cityName = addressComponent.getString("short_name");
                                    }
                                }
                            }
                            if (cityName != null)
                            {

                                return cityName;

                            }
                        }
                    }
                }
            }
        }
        catch (Exception ignored)
        {
            ignored.printStackTrace();
        }
        return null;
    }



    class AccountLoader extends AsyncTask<Void,Void,Account>{

        @Override
        protected Account doInBackground(Void... params) {
            Account account;
            try {
                account = FacebookManager.getUserInfo(AccountActivity.this);
                account.setPhotoURLs(FacebookManager.getUserPhotoPathes(AccountActivity.this));
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString("Account", new Gson().toJson(account));
                editor.apply();
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
            if(account!=null) {
                ageView.setText(String.valueOf(account.getAge()));
                nameView.setText(account.getName());
                surnameView.setText(account.getSurname());
                aboutView.setText(account.getAbout());
                relView.setText(account.getRelationships());
                List<Account.Work> thWorks = account.getWorks();
                List<String> pathes = account.getPhotoURLs();
                StringBuilder works = new StringBuilder();
                for (int i = 0; i < thWorks.size(); i++) {
                    works.append("\n" + thWorks.get(i).getName() + "\n" + thWorks.get(i).getPosition() + "\n" + thWorks.get(i).getDescription() + "\n");
                    if (i != thWorks.size() - 1)
                        works.append("------------------------");
                }
                fbPhotoFlipper.removeAllViews();
                for (String str : pathes) {
                    ImageView view = new ImageView(getApplicationContext());
                    fbPhotoFlipper.addView(view);
                    ImageLoader.getInstance().displayImage(str, view);
                }
                workView.setText(works.toString());
            }
        }
    }
    class PlaceLoader extends AsyncTask<Location,Void,String>{
        @Override
        protected void onProgressUpdate(Void... values) {
            placeView.setText("...");

        }

        @Override
        protected void onPostExecute(String s) {
            placeView.setText(s);
        }

        @Override
        protected String doInBackground(Location... params) {
            Geocoder geocoder =
                    new Geocoder(getApplicationContext(), Locale.getDefault());
            // Get the current location from the input parameter list
            Location loc = params[0];
            // Create a list to contain the result address
            List<Address> addresses = null;
            if(geocoder.isPresent()) {
                try {

                    addresses = geocoder.getFromLocation(loc.getLatitude(),
                            loc.getLongitude(), 1);
                } catch (IOException e1) {
                    Log.e("LocationSampleActivity",
                            "IO Exception in getFromLocation()");
                    e1.printStackTrace();
                    return ("IO Exception trying to get address");
                } catch (IllegalArgumentException e2) {
                    String errorString = "Illegal arguments " +
                            Double.toString(loc.getLatitude()) +
                            " , " +
                            Double.toString(loc.getLongitude()) +
                            " passed to address service";
                    Log.e("LocationSampleActivity", errorString);
                    e2.printStackTrace();
                    return errorString;
                }
                // If the reverse geocode returned an address
                if (addresses != null && addresses.size() > 0) {
                    // Get the first address
                    Address address = addresses.get(0);
                /*
                 * Format the first line of address (if available),
                 * city, and country name.
                 */
                    String addressText = String.format(
                            "%s, %s, %s",
                            // If there's a street address, add it
                            address.getMaxAddressLineIndex() > 0 ?
                                    address.getAddressLine(0) : "",
                            // Locality is usually a city
                            address.getLocality(),
                            // The country of the address
                            address.getCountryName());
                    // Return the text
                    return addressText;
                } else {
                    return "No address found";
                }
            }
            else{
             return fetchCityNameUsingGoogleMap(loc)   ;
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.account);
        mPrefs= getPreferences(MODE_PRIVATE);
        placeView=(TextView)findViewById(R.id.tvPlace);
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
            googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    googleMap.clear();
                    Location location = googleMap.getMyLocation();
                    account.setLocation(location);
                    SharedPreferences.Editor editor= mPrefs.edit();
                    editor.putString("Account",new Gson().toJson(account));
                    editor.apply();

                    new PlaceLoader().execute(location);

                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    LatLng latLng = new LatLng(latitude, longitude);

                    googleMap.addMarker(new MarkerOptions().position(latLng));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    return false;
                }
            });

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
                    editor.apply();
                    new PlaceLoader().execute(location);
                    Toast.makeText(getApplicationContext(),"Ви змінили вашу локацію",Toast.LENGTH_SHORT).show();
                }
            });
            if (account!=null && account.getLocation()!=null){
                Location lc=account.getLocation();
                googleMap.addMarker(new MarkerOptions().position(new LatLng(lc.getLatitude(), lc.getLongitude())));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lc.getLatitude(), lc.getLongitude())));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                new PlaceLoader().execute(lc);
            }
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
