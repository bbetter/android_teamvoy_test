package com.example.andriypuhach.android_teamvoy_test.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.andriypuhach.android_teamvoy_test.FacebookManager;
import com.example.andriypuhach.android_teamvoy_test.R;
import com.example.andriypuhach.android_teamvoy_test.Serializer;
import com.example.andriypuhach.android_teamvoy_test.WorkaroundMapFragment;
import com.example.andriypuhach.android_teamvoy_test.dialogs.EditAccountDialog;
import com.example.andriypuhach.android_teamvoy_test.models.Account;
import com.facebook.Session;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
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

    private SharedPreferences mPrefs;
    private TextView placeView;
    private Account account;
    private EditAccountDialog edAccDialog;
    private ScrollView scView;
    private SliderLayout slider;


    private String fetchCityNameUsingGoogleMap(Location lc) {
        String googleMapUrl = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + lc.getLatitude() + ","
                + lc.getLongitude() + "&sensor=false&language=fr";
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(googleMapUrl)
                    .build();

            JSONObject googleMapResponse = new JSONObject(client.newCall(request).execute().body().string());
            JSONArray results = (JSONArray) googleMapResponse.get("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                if (result.has("address_components")) {
                    JSONArray addressComponents = result.getJSONArray("address_components");
                    for (int j = 0; j < addressComponents.length(); j++) {
                        JSONObject addressComponent = addressComponents.getJSONObject(j);
                        if (result.has("types")) {
                            JSONArray types = addressComponent.getJSONArray("types");
                            String cityName = null;

                            for (int k = 0; k < types.length(); k++) {
                                if ("locality".equals(types.getString(k)) && cityName == null) {
                                    if (addressComponent.has("long_name")) {
                                        cityName = addressComponent.getString("long_name");
                                    } else if (addressComponent.has("short_name")) {
                                        cityName = addressComponent.getString("short_name");
                                    }
                                }
                                if ("sublocality".equals(types.getString(k))) {
                                    if (addressComponent.has("long_name")) {
                                        cityName = addressComponent.getString("long_name");
                                    } else if (addressComponent.has("short_name")) {
                                        cityName = addressComponent.getString("short_name");
                                    }
                                }
                            }
                            if (cityName != null) {

                                return cityName;

                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return null;
    }

    /**
     * метод оновляє інформацію в усіх полях в даній активності
     */
    private void refreshFields() {
        TextView nameView = (TextView) findViewById(R.id.tvUserName);
        TextView surnameView = (TextView) findViewById(R.id.tvUserSurname);
        TextView relView = (TextView) findViewById(R.id.tvRelStatus);
        TextView aboutView = (TextView) findViewById(R.id.tvAbout);
        TextView birthdayView = (TextView) findViewById(R.id.tvBirthday);
        TextView workView = (TextView) findViewById(R.id.tvWorks);
        if (account != null) {
            birthdayView.setText(String.valueOf(account.getBirthday().toLocalDate().toString()));
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
            slider.removeAllSliders();
            for (String str : pathes) {
                TextSliderView view = new TextSliderView(getApplicationContext());
                    view.image(str)
                        .description("Facebook Photos")
                        .error(R.drawable.failed_to_load)
                        .setScaleType(BaseSliderView.ScaleType.Fit);
                slider.addSlider(view);
            }

            workView.setText(works.toString());
        }
    }

    /**
     * метод завантажує інформацію про акаунт із SharedPreferences
     */
    private void loadAccount() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(DateTime.class, new Serializer())
                .create();
        account = gson.fromJson(mPrefs.getString("Account", ""), Account.class);
    }

    /**
     * метод зберігає інформацію про аккаунт у SharedPreferences
     */
    private void saveAccount() {
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(DateTime.class, new Serializer())
                .create();
        editor.putString("Account", gson.toJson(account));
        editor.apply();
    }

    class AccountLoader extends AsyncTask<Void, Void, Account> {
        @Override
        protected Account doInBackground(Void... params) {
            try {
                account = FacebookManager.getUserInfo(AccountActivity.this);
                account.setPhotoURLs(FacebookManager.getUserPhotoPathes(AccountActivity.this));
                saveAccount();
            } catch (Exception e) {

            }
            return account;
        }

        @Override
        protected void onPostExecute(Account account) {
            refreshFields();
        }
    }

    /**
     * клас призначений для завантаження інформації про локацію позначену на карті
     * або за допомогою GeoCoder або зробивши запит до api і зпарсивши json
     */
    class PlaceLoader extends AsyncTask<Location, Void, String> {
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
            Location loc = params[0];
            List<Address> addresses = null;
            if (geocoder.isPresent()) {
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

                if (addresses != null && addresses.size() > 0) {

                    Address address = addresses.get(0);

                    String addressText = String.format(
                            "%s, %s, %s",

                            address.getMaxAddressLineIndex() > 0 ?
                                    address.getAddressLine(0) : "",

                            address.getLocality(),

                            address.getCountryName());

                    return addressText;
                } else {
                    return "No address found";
                }
            } else {
                return fetchCityNameUsingGoogleMap(loc);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.in_account_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.editAccount) {
            edAccDialog = new EditAccountDialog(this, account);
            edAccDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    account = edAccDialog.account;
                    saveAccount();
                    refreshFields();
                }
            });
            edAccDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    loadAccount();
                    refreshFields();
                }
            });
            edAccDialog.show();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.account);
        scView = (ScrollView) findViewById(R.id.scrollView);
        mPrefs = getPreferences(MODE_PRIVATE);
        placeView = (TextView) findViewById(R.id.tvPlace);
        slider=(SliderLayout)findViewById(R.id.fbPhotoSlider);
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        slider.setDuration(8000);
        final TextView textView = new TextView(getApplicationContext());
        textView.setText("Ваші дані були змінені, бажаєте синхронізуватись із фейсбуком?");
        if (Session.getActiveSession().isOpened()) {
            new AlertDialog.Builder(AccountActivity.this)
                    .setCustomTitle(textView)
                    .setPositiveButton("Так", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                new AccountLoader().execute();
                            } catch (Exception e) {
                                this.onClick(dialog, Dialog.BUTTON_NEGATIVE);
                            }
                        }
                    })
                    .setNegativeButton("Ні", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                loadAccount();
                                refreshFields();
                            } catch (Exception e) {
                                Log.w("Ex", "Exception");
                            }
                        }
                    })

                    .show();
        } else {
            loadAccount();
            refreshFields();
        }
    }

    private void initilizeMap() {

           if (googleMap == null) {
            googleMap = ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(
                    R.id.map)).setListener(new WorkaroundMapFragment.OnTouchListener() {

                @Override
                public void onTouch() {
                    scView.requestDisallowInterceptTouchEvent(true);
                }
            });
            if (googleMap != null) {
                googleMap.clear();
                googleMap.setMyLocationEnabled(true);

                googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        if(googleMap!=null) {
                            googleMap.clear();
                            Location location = googleMap.getMyLocation();
                            account.setLocation(location);
                            saveAccount();
                            new PlaceLoader().execute(location);

                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            LatLng latLng = new LatLng(latitude, longitude);

                            googleMap.addMarker(new MarkerOptions().position(latLng));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                            return true;
                        }
                            return false;

                    }
                });

                googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        if (googleMap != null) {
                            googleMap.clear();
                            googleMap.addMarker(new MarkerOptions().position(latLng));

                            Location location = new Location("Current Marker Position");
                            location.setLatitude(latLng.latitude);
                            location.setLongitude(latLng.longitude);
                            location.setTime(new Date().getTime());

                            account.setLocation(location);
                            saveAccount();
                            new PlaceLoader().execute(location);
                            Toast.makeText(getApplicationContext(), "Ви змінили вашу локацію", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                if (account != null && account.getLocation() != null) {
                    Location lc = account.getLocation();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }

}
