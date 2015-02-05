package com.example.andriypuhach.android_teamvoy_test.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.andriypuhach.android_teamvoy_test.FacebookManager;
import com.example.andriypuhach.android_teamvoy_test.MovieDatabaseHelper;
import com.example.andriypuhach.android_teamvoy_test.R;
import com.example.andriypuhach.android_teamvoy_test.TheMovieDBAccount;
import com.example.andriypuhach.android_teamvoy_test.adapters.MovieListAdapter;
import com.example.andriypuhach.android_teamvoy_test.models.Movie;
import com.example.andriypuhach.android_teamvoy_test.models.MovieRequestResult;
import com.example.andriypuhach.android_teamvoy_test.rest.RestClient;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends Activity implements Callback<MovieRequestResult> {
    //region UI STUFF
    private TabHost tabs;
    private Button theMovieDatabaseLoginButton;
    public static ListView listView;
    private EditText searchEditText;

    private UiLifecycleHelper uiHelper; //facebook helper thing
    private int [] resources={R.string.tmbd_login_text,R.string.tmbd_logout_text};

    class AddRemoveCallback implements Callback<JsonElement>{
        String goal;
        boolean addRemove;
        public AddRemoveCallback(String goal,boolean addRemove){
            this.goal=goal;
            this.addRemove=addRemove;
        }
        @Override
        public void success(JsonElement jsonElement, Response response) {
            Toast.makeText(getApplicationContext(),"Успішно "+((addRemove)?"додано":"видалено"),Toast.LENGTH_LONG).show();
            currentPage=1;
            if(goal.equals("watchlist") && !addRemove)
                refreshWatchList(false);
            else if(!addRemove)
                refreshFavorites(false);
        }

        @Override
        public void failure(RetrofitError error) {
            Toast.makeText(getApplicationContext(),"Не вдалось здійснити дану операцію",Toast.LENGTH_LONG).show();
        }
    }

    public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {
        // The minimum amount of items to have below your current scroll position
        // before loading more.
        private int visibleThreshold = 5;
        // The current offset index of data you have loaded
        private int currentPage = 0;
        // The total number of items in the dataset after the last load
        private int previousTotalItemCount = 0;
        // True if we are still waiting for the last set of data to load.
        private boolean loading = true;
        // Sets the starting page index
        private int startingPageIndex = 0;

        public EndlessScrollListener() {
        }

        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        public EndlessScrollListener(int visibleThreshold, int startPage) {
            this.visibleThreshold = visibleThreshold;
            this.startingPageIndex = startPage;
            this.currentPage = startPage;
        }

        // This happens many times a second during a scroll, so be wary of the code you place here.
        // We are given a few useful parameters to help us work out if we need to load some more data,
        // but first we check if we are waiting for the previous load to finish.
        @Override
        public void onScroll(AbsListView view,int firstVisibleItem,int visibleItemCount,int totalItemCount)
        {
            // If the total item count is zero and the previous isn't, assume the
            // list is invalidated and should be reset back to initial state
            if (totalItemCount < previousTotalItemCount) {
                this.currentPage = this.startingPageIndex;
                this.previousTotalItemCount = totalItemCount;
                if (totalItemCount == 0) { this.loading = true; }
            }
            // If it’s still loading, we check to see if the dataset count has
            // changed, if so we conclude it has finished loading and update the current page
            // number and total item count.
            if (loading && (totalItemCount > previousTotalItemCount)) {
                loading = false;
                previousTotalItemCount = totalItemCount;
                currentPage++;
            }

            // If it isn’t currently loading, we check to see if we have breached
            // the visibleThreshold and need to reload more data.
            // If we do need to reload some more data, we execute onLoadMore to fetch the data.
            if (!loading && (totalItemCount - visibleItemCount)<=(firstVisibleItem + visibleThreshold)) {
                onLoadMore(currentPage + 1, totalItemCount);
                loading = true;
            }
        }

        // Defines the process for actually loading more data based on page
        public abstract void onLoadMore(int page, int totalItemsCount);

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // Don't take any action on changed
        }
    }
    /**
     * метод оновлює список фільмів і заголовок
     * @param movies
     */
    void refreshList(ArrayList<Movie> movies,boolean append){
        if(append)
        ((MovieListAdapter)listView.getAdapter()).addMovies(movies);
        else {
            listAdapter.setMovies(movies);
            listView.setAdapter(listAdapter);
        }
    }
    /**
     * метод оновлює дані в ListAdapter виконавши запит на пошук рядка
     * @param search рядок для пошуку
     */
    void refreshListBySearch(String search,boolean next) {
        if(currentSearchType.equals("Звичайний пошук")) {
            RestClient.getApi().search(search, next?(currentPage+1):currentPage,"ngram",this);
        }
        else{
            MovieDatabaseHelper dbHelper = new MovieDatabaseHelper(getApplicationContext());
            final ArrayList<Movie> movies=(ArrayList<Movie>)dbHelper.searchByNote(search);
            totalPages = (movies.size() < 20) ? 1 : (int) Math.ceil((double) movies.size() / 20.0);
            refreshList(movies,false);
        }
    }
    /**
     * метод оновлює список фільмів що знаходяться у списку "до перегляду"
     */
    void refreshWatchList(boolean next){
        RestClient.getApi().getWatchListMovies(RestClient.sessionId,next?(currentPage+1):currentPage,this);
    }
    /**
     * оновлює список данними в залежності від обраної вкладки
     */
    void refreshListByTab(boolean next) {
        RestClient.getApi().getMovies(currentTask,next?(currentPage+1):currentPage,this);
    }
    private TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
        @Override
        public void onTabChanged(String tabId) {
            currentTask = tabId;
            currentPage=1;
            if(listView.getAdapter()!=null)
                ((MovieListAdapter)listView.getAdapter()).getMovies().clear();


            switch(currentTask){
                case "favorite":refreshFavorites(false);
                    break;
                case "watchlist":refreshWatchList(false);
                    break;
                default:refreshListByTab(false);
                    break;
            }
        }
    };

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getTitle().toString()){
            case "Add To Favorites":{
                if(!tmbdConnected) {
                    if (!isMovieInList(Movie.favorites, selectedMovie)) {
                        Movie.favorites.add(selectedMovie);
                        Movie.saveFavorites();
                        Toast.makeText(getApplicationContext(), "Успішно додано", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Цей фільм уже у вашому списку", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    JsonObject object= new Gson().fromJson("{'media_type':'movie','media_id':" + selectedMovie.getId() + ",'favorite':true}",JsonObject.class);
                    RestClient.getApi().setFavorite(object,RestClient.sessionId,new AddRemoveCallback("favorite",true));
                }
            }
            break;
            case "Add To Watchlist":{
                JsonObject object= new Gson().fromJson("{'media_type':'movie','media_id':" + selectedMovie.getId() + ",'watchlist':true}",JsonObject.class);
                RestClient.getApi().setWatchlist(object, RestClient.sessionId,new AddRemoveCallback("watchlist",true));
            }
            break;
            case "Share":{
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "I like " +
                        selectedMovie.getTitle() +
                        "You should check it\n" +
                        Movie.transformPathToURL(selectedMovie.getPoster_path(), Movie.ImageSize.W150));
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
            break;
            case "Delete":{
                if(!tmbdConnected) {
                    removeById(selectedMovie.getId(), Movie.favorites);
                    Movie.saveFavorites();
                }
                else{
                    if(currentTask.equals("watchlist")){
                        JsonObject object= new Gson().fromJson("{'media_type':'movie','media_id':" + selectedMovie.getId() + ",'watchlist':false}",JsonObject.class);
                        RestClient.getApi().setWatchlist(object, RestClient.sessionId,new AddRemoveCallback("watchlist",false));

                    }
                    else {
                        JsonObject object= new Gson().fromJson("{'media_type':'movie','media_id':" + selectedMovie.getId() + ",'favorite':false}",JsonObject.class);
                        RestClient.getApi().setFavorite(object, RestClient.sessionId,new AddRemoveCallback("favorite",false));
                    }

                }
            }
            break;
            case "Post To Facebook":{
                FacebookManager.postToFacebook(selectedMovie.getOriginal_title(), MainActivity.this);
            }
            break;

        }
        return true;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        if (view.getId() == R.id.listView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            selectedMovie = listAdapter.getMovie(info.position);
            menu.setHeaderTitle(selectedMovie.getTitle());
            List<String> listMenuItems= new ArrayList<>();
            listMenuItems.add("Share");
            listMenuItems.add("Add To Favorites");
            if(tmbdConnected)
                listMenuItems.add("Add To Watchlist");
            if(Session.getActiveSession().isOpened())
                listMenuItems.add("Post To Facebook");
            if(currentTask.equals("favorite")) {
                listMenuItems.remove("Share");
                listMenuItems.remove("Add To Favorites");
                listMenuItems.add("Delete");
            }
            if(currentTask.equals("watchlist")){
                listMenuItems.remove("Share");
                listMenuItems.remove("Add To Watchlist");
                listMenuItems.add("Delete");
            }
            String [] menuItems=new String[listMenuItems.size()];
            listMenuItems.toArray(menuItems);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }
    /**
     * оновлює список улюблених фільмів, або із бази даних або із сайту themoviedb.org
     */
    void refreshFavorites(boolean next) {
        if(!tmbdConnected) {
            Movie.refreshFavorites();
            if (Movie.favorites != null) {
                totalPages = (Movie.favorites.size() < 20) ? 1 : (int) Math.ceil((double) Movie.favorites.size() / 20.0);
                int begin = ((next?++currentPage:currentPage - 1) * 20);
                int end = begin + 20;
                if (Movie.favorites.size() <= end)
                    end = Movie.favorites.size();
                List<Movie> mvs=(Movie.favorites.size()==1)?Movie.favorites:new ArrayList<>(Movie.favorites.subList(begin,end));
                refreshList((ArrayList<Movie>)mvs,false);
            } else {
                listAdapter.setMovies(new ArrayList<Movie>());
                listView.setAdapter(listAdapter);
            }
        }
        else{
            RestClient.getApi().getFavoriteMovies(RestClient.sessionId,next?(currentPage+1):currentPage,this);
        }
    }
    private OnItemClickListener detailsListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            MovieListAdapter adapter = (MovieListAdapter) listView.getAdapter();
            final Movie movie = adapter.getMovie(position);
            intent.putExtra("Movie",movie);
            startActivity(intent);
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.openAccount){
            startActivity(new Intent(MainActivity.this,AccountActivity.class));
        }
        return true;
    }

//endregion
    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            if (state.isOpened()) {
                Log.d("FacebookSampleActivity", "Facebook session opened");
            } else if (state.isClosed()) {
                Log.d("FacebookSampleActivity", "Facebook session closed");
            }
        }
    };

    private int currentPage = 1;
    private int totalPages = 1000;

    private String currentTask = "popular";
    private String currentSearchType="";


    private MovieListAdapter listAdapter;
    private Movie selectedMovie = null;

    private boolean tmbdConnected=false;
    private SharedPreferences prefs;

    /**
     *  метод перевіряє чи користувач під'єднаний до мережі
     * @return повертає true якщо під'єднаний інакше(навіть ящко підключення саме триває) - false
     */
    boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    /**
     * метод перевіряє чи є фільм у заданому списку
     * @param movieList список
     * @param movie фільм
     * @return true якщо фільм присутній інакше false
     */
    private boolean isMovieInList(List<Movie> movieList, Movie movie) {
        for (Movie m : movieList) {
            if (movie.getOriginal_title().equals(m.getTitle()) && movie.getRelease_date().equals(m.getRelease_date()))
                return true;
        }
        return false;
    }

    /**
     * видаляє із списку фільмів заданий(необхідно для видалення локальних улюблених фільмів
     * @param id id фільма
     * @param movies список
     */
    private void removeById(int id,List<Movie> movies){
        for(Movie m:movies){
            if(m.getId()==id) {
                movies.remove(m);
                return ;
            }

        }
    }

    /**
     * метод проводить повну автентифікацію користувача на сайті themoviedb.org
     * @param username ім'я користувача
     * @param password пароль
     */
    void full_authenticate(final String username, final String password){
        Toast.makeText(getApplicationContext(),"Wait until connection is established",Toast.LENGTH_LONG).show();
        RestClient.getApi().getToken(new Callback<JsonElement>() {
            @Override
            public void success(JsonElement element, Response response) {
                String requestToken=element.getAsJsonObject().get("request_token").toString();
                RestClient.requestToken=requestToken.substring(1,requestToken.length()-1);
                RestClient.getApi().validateToken(RestClient.requestToken, username, password, new Callback<JsonElement>() {
                    @Override
                    public void success(JsonElement jsonElement, Response response) {
                        if(jsonElement.getAsJsonObject().get("success").toString().equals("true")){
                            SharedPreferences.Editor editor=prefs.edit();
                            editor.putString("RequestToken",RestClient.requestToken);
                            editor.apply();
                            RestClient.getApi().getNewSession(RestClient.requestToken,new Callback<JsonElement>() {
                                @Override
                                public void success(JsonElement jsonElement, Response response) {
                                    String sessionId=jsonElement.getAsJsonObject().get("session_id").toString();
                                    RestClient.sessionId=sessionId.substring(1,sessionId.length()-1);
                                    SharedPreferences.Editor editor=prefs.edit();
                                    editor.putString("SessionID",RestClient.sessionId);
                                    editor.apply();

                                        tmbdConnected = true;
                                        theMovieDatabaseLoginButton.setText(getResources().getText(resources[tmbdConnected ? 1 : 0]));
                                        tabs.getTabWidget().getChildTabViewAt(4).setVisibility(tmbdConnected ? View.VISIBLE : View.GONE);

                                        if (currentTask.equals("watchlist") || currentTask.equals("favorites")) {
                                            currentTask = "popular";
                                            tabs.setCurrentTab(0);
                                        }
                                    Toast.makeText(getApplicationContext(),"Connection is successfully established",Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Toast.makeText(getApplicationContext(),"Can't get new session. Try again later!",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(getApplicationContext(),"Wrong Password,Try again!",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(),"Can't get new request token. Try again later!",Toast.LENGTH_LONG).show();
            }
        });

    }
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(this, statusCallback);
        uiHelper.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        prefs=getPreferences(MODE_PRIVATE);
        RestClient.sessionId=prefs.getString("SessionID","");
        RestClient.requestToken=prefs.getString("RequestToken","");

        if(RestClient.sessionId.equals("") && !RestClient.requestToken.equals("")){
            TheMovieDBAccount.getNewSession();
            tmbdConnected=true;
        }
        listAdapter = new MovieListAdapter(this);
        theMovieDatabaseLoginButton =(Button)findViewById(R.id.login_tmbd_button);
        theMovieDatabaseLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tmbdConnected) {
                    LinearLayout layout = new LinearLayout(getApplicationContext());
                    layout.setOrientation(LinearLayout.VERTICAL);
                    String nick = prefs.getString("nickname", "");
                    String pass = prefs.getString("password", "");
                    final EditText edName = new EditText(getApplicationContext());
                    edName.setHint("nickname");
                    edName.setText(nick);
                    edName.setSingleLine();
                    edName.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                    final EditText edPass = new EditText(getApplicationContext());
                    edPass.setText(pass);
                    edPass.setHint("password");
                    edPass.setSingleLine();

                    edPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edPass.setTransformationMethod(PasswordTransformationMethod.getInstance());

                    final CheckBox savePass = new CheckBox(getApplicationContext());
                    savePass.setText("Зберегти пароль?");
                    layout.addView(edName);
                    layout.addView(edPass);
                    layout.addView(savePass);
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Login in Tmbd")
                            .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String name = edName.getText().toString();
                                    String pass = edPass.getText().toString();
                                    if (savePass.isChecked()) {
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putString("nickname", name);
                                        editor.putString("password", pass);
                                        editor.apply();
                                    }
                                        RestClient.sessionId="";
                                        RestClient.requestToken="";
                                        full_authenticate(name, pass);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setView(layout).create().show();
                } else {
                    SharedPreferences.Editor editor=prefs.edit();
                    editor.putString("SessionID","");
                    editor.putString("RequestToken","");
                    editor.apply();
                    tmbdConnected = false;
                    if (currentTask.equals("watchlist") || currentTask.equals("favorites")) {
                        currentTask = "popular";
                        tabs.setCurrentTab(0);
                    }
                }
                theMovieDatabaseLoginButton.setText(getResources().getText(resources[tmbdConnected ? 1 : 0]));
                tabs.getTabWidget().getChildTabViewAt(4).setVisibility(tmbdConnected ? View.VISIBLE : View.GONE);
            }
        });
        LoginButton facebookLoginButton = (LoginButton) findViewById(R.id.fb_login_button);
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile", "user_status", "user_birthday", "user_about_me", "user_relationships"));
        facebookLoginButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser graphUser) {
                if (graphUser != null) {
                    setTitle("Welcome, " + graphUser.getName());
                } else {
                    setTitle("Movie info welcomes you");
                }
            }
        });
        searchEditText= (EditText) findViewById(R.id.searchMovieEdit);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isOnline()) {
                    currentPage=1;
                    if (searchEditText.getText().toString().equals("")) {
                        tabs.setVisibility(View.VISIBLE);
                        currentTask = tabs.getCurrentTabTag();
                        refreshListByTab(false);
                    } else {
                        currentTask = "search";
                        tabs.setVisibility(View.GONE);
                        String encoded=Uri.encode(searchEditText.getText().toString());
                        refreshListBySearch(encoded,false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(detailsListener);
        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            switch (currentTask) {
                                default:
                                    refreshListByTab(true);
                                    break;
                                case "watchlist":
                                    refreshWatchList(true);
                                    break;
                                case "favorite":
                                    refreshFavorites(true);
                                case "search":
                                    refreshListBySearch(Uri.encode(((EditText) findViewById(R.id.searchMovieEdit)).getText().toString()), true);
                            }

                        }
                    });
                }
        });
        registerForContextMenu(listView);
        tabs = (TabHost) findViewById(R.id.tabhost);
        tabs.setup();
        tabs.setOnTabChangedListener(tabChangeListener);
        TabHost.TabSpec spec = tabs.newTabSpec("popular");
        spec.setContent(R.id.popular);
        spec.setIndicator("Популярні");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("upcoming");
        spec.setContent(R.id.upcoming);
        spec.setIndicator("Скоро виходять");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("top_rated");
        spec.setContent(R.id.top_rated);
        spec.setIndicator("Топ рейтингу");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("favorite");
        spec.setContent(R.id.favorite);
        spec.setIndicator("Улюблені");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("watchlist");
        spec.setContent(R.id.watchlist);
        spec.setIndicator("Watch List");
        tabs.addTab(spec);

        theMovieDatabaseLoginButton.setText(getResources().getText(resources[tmbdConnected ? 1 : 0]));
        tabs.getTabWidget().getChildTabViewAt(4).setVisibility(tmbdConnected ? View.VISIBLE : View.GONE);
        tabs.setCurrentTab(0);

        Spinner spinner =(Spinner) findViewById(R.id.searchTypeSpinner);
        List<String> list = new ArrayList<>();
        list.add("Звичайний пошук");
        list.add("За нотатками");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, list);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSearchType=(String)parent.getItemAtPosition(position);
                if (isOnline()) {
                    currentPage=1;
                    if (searchEditText.getText().toString().equals("")) {
                        currentTask = tabs.getCurrentTabTag();
                        refreshListByTab(false);
                    } else {
                        currentTask = "search";
                        String encoded=Uri.encode(searchEditText.getText().toString());
                        refreshListBySearch(encoded,false);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        RestClient.sessionId=prefs.getString("SessionID","");
        RestClient.requestToken=prefs.getString("RequestToken","");
        tmbdConnected=false;
        if(!RestClient.requestToken.equals("")) {
            if (!RestClient.sessionId.equals("")) {
                tmbdConnected = true;
            }
            else{
                TheMovieDBAccount.getNewSession();
            }
        }
        theMovieDatabaseLoginButton.setText(getResources().getText(resources[tmbdConnected ? 1 : 0]));
        tabs.getTabWidget().getChildTabViewAt(4).setVisibility(tmbdConnected ? View.VISIBLE : View.GONE);

    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        uiHelper.onSaveInstanceState(savedState);
    }

    // Коли успішно отримали фільми
    @Override
    public void success(MovieRequestResult movieRequestResult, Response response) {
        if (movieRequestResult.getResults() != null) {
            currentPage=movieRequestResult.getPage();
            totalPages = movieRequestResult.getTotal_pages();
            refreshList((ArrayList<Movie>) movieRequestResult.getResults(), (currentPage > 1));
        }
    }

    @Override
    public void failure(RetrofitError error) {

        if(!isOnline())
        Toast.makeText(this,"Будь ласка під'єднайтесь до мережі інтернет.",Toast.LENGTH_LONG).show();
    }
}
