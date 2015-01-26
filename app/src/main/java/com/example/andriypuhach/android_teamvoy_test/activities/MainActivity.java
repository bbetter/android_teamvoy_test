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
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends Activity {
    //region UI STUFF
    private TabHost tabs;
    private LoginButton facebookLoginButton;
    private Button theMovieDatabaseLoginButton;
    private ListView listView;
    private EditText searchEditText;
    private View header;

    private UiLifecycleHelper uiHelper; //facebook helper thing
    int [] resources={R.string.tmbd_login_text,R.string.tmbd_logout_text};

    /**
     * метод створює заголовок списку
     * @return повертає View створеного заголовку
     */
    View createHeader() {
        View v = getLayoutInflater().inflate(R.layout.header, null);
        v.findViewById(R.id.nextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentTask) {
                    case "popular":
                        if (currentPopularPage < totalPages) {
                            currentPopularPage++;
                        }
                        break;

                    case "upcoming":
                        if (currentUpcomingPage<totalPages) {
                            currentUpcomingPage++;
                        }
                        break;
                    case "top_rated":
                        if(currentTopRatedPage<totalPages){
                            currentTopRatedPage++;
                        }
                        break;
                    case "favorite":
                        if(currentFavoritePage<totalPages) {
                            currentFavoritePage++;
                            refreshFavorites();
                            return ;
                        }
                        break;
                    case "watchlist":
                        if(currentWatchListPage<totalPages){
                            currentWatchListPage++;
                            refreshWatchList();
                        }
                    case "search":
                        if(currentSearchPage<totalPages){
                            currentSearchPage++;
                            refreshListBySearch(searchEditText.getText().toString());
                            return;
                        }
                    default:
                        if(currentPopularPage<totalPages){
                            currentPopularPage++;
                        }
                        break;
                }
                refreshListByTab();
            }
        });

        v.findViewById(R.id.prevButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentTask) {
                    case "popular":
                        if (currentPopularPage > 1) {
                            currentPopularPage--;
                        }
                        break;

                    case "upcoming":
                        if (currentUpcomingPage>1) {
                            currentUpcomingPage--;
                        }
                        break;
                    case "top_rated":
                        if(currentTopRatedPage>1){
                            currentTopRatedPage--;
                        }
                        break;
                    case "favorite":
                        if(currentFavoritePage>1) {
                            currentFavoritePage--;
                            refreshFavorites();
                            return ;
                        }
                        break;
                    case "search":
                        if(currentSearchPage>1){
                            currentSearchPage--;
                            refreshListBySearch(Uri.encode(searchEditText.getText().toString()));
                            return;
                        }
                    case "watchlist":
                        if(currentWatchListPage>1){
                            currentWatchListPage--;
                            refreshWatchList();
                        }
                    default:
                        if(currentPopularPage>1){
                            currentPopularPage--;
                        }
                        break;
                }
                refreshListByTab();
            }
        });
        return v;
    }
    /**
     * метод оновлює список фільмів і заголовок
     * @param movies
     * @param currentPage
     * @param totalPages
     */
    void refreshList(ArrayList<Movie> movies,int currentPage,int totalPages){
        listAdapter.setMovies(movies);
        ((TextView) header.findViewById(R.id.currentPageView)).setText(currentPage + " of " + totalPages);
        if (listView.getHeaderViewsCount() == 0)
            listView.addHeaderView(header);
        listView.setAdapter(listAdapter);
    }
    /**
     * метод оновлює дані в ListAdapter виконавши запит на пошук рядка
     * @param search рядок для пошуку
     */
    void refreshListBySearch(String search) {
        if(currentSearchType=="Звичайний пошук") {
            RestClient.getApi().search(search, currentSearchPage, new Callback<MovieRequestResult>() {
                @Override
                public void success(MovieRequestResult result, Response response) {
                    listView.setVisibility(View.VISIBLE);
                    if (result.getResults() != null) {
                        currentSearchPage = result.getPage();
                        totalPages = result.getTotal_pages();
                        refreshList((ArrayList<Movie>)result.getResults(),currentSearchPage,totalPages);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    if (!isOnline()) {
                        Toast.makeText(getApplicationContext(), "Connection trouble. Please reconnect to the Internet", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
        else{
            MovieDatabaseHelper dbHelper = new MovieDatabaseHelper(getApplicationContext());
            ArrayList<Movie> movies=(ArrayList<Movie>)dbHelper.searchByNote(search);
            totalPages = (movies.size() < 20) ? 1 : (int) Math.ceil((double) movies.size() / 20.0);
            refreshList(movies,currentSearchPage,totalPages);
        }
    }
    /**
     * метод оновлює список фільмів що знаходяться у списку "до перегляду"
     */
    void refreshWatchList(){
        RestClient.getApi().getWatchListMovies(RestClient.sessionId,currentWatchListPage,new Callback<MovieRequestResult>() {
            @Override
            public void success(MovieRequestResult movieRequestResult, Response response) {
                listView.setVisibility(View.VISIBLE);
                if (movieRequestResult.getResults() != null) {
                    int cp = movieRequestResult.getPage();
                    totalPages = movieRequestResult.getTotal_pages();
                    refreshList((ArrayList<Movie>)movieRequestResult.getResults(),cp,totalPages);
                }
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }
    /**
     * оновлює список данними в залежності від обраної вкладки
     */
    void refreshListByTab() {
        int currentPage=1;
        switch(currentTask){
            case "popular":currentPage=currentPopularPage;
                break;
            case "upcoming":currentPage=currentUpcomingPage;
                break;
            case "top_rated":currentPage=currentTopRatedPage;
                break;
            default:
                currentPage=currentPopularPage;
                break;
        }

        RestClient.getApi().getMovies(currentTask, currentPage, new Callback<MovieRequestResult>() {
            @Override
            public void success(MovieRequestResult movieRequestResult, Response response) {
                listView.setVisibility(View.VISIBLE);
                if (movieRequestResult.getResults() != null) {
                    int cp = movieRequestResult.getPage();
                    totalPages = movieRequestResult.getTotal_pages();
                    refreshList((ArrayList<Movie>) movieRequestResult.getResults(), cp, totalPages);
                }
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }
    private TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
        @Override
        public void onTabChanged(String tabId) {
            currentTask = tabId;
            if (!currentTask.equals("favorite")) {
                if(!currentTask.equals("watchlist")) {
                    refreshListByTab();
                }
                else{

                    refreshWatchList();
                }
            } else {
                refreshFavorites();
            }
        }
    };

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Add To Favorites") {
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
                RestClient.getApi().setFavorite("movie",selectedMovie.getId(),true,RestClient.sessionId,new Callback<JsonElement>() {
                    @Override
                    public void success(JsonElement jsonElement, Response response) {
                        Toast.makeText(getApplicationContext(), "Успішно додано", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }
        }
        else if(item.getTitle()=="Add To Watchlist"){
            RestClient.getApi().setWatchlist("movie",selectedMovie.getId(),true, RestClient.sessionId,new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {
                    Toast.makeText(getApplicationContext(), "Успішно додано", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void failure(RetrofitError error) {
                }
            });
        }
        else if(item.getTitle()=="Share"){
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "I like " +
                    selectedMovie.getTitle() +
                    "You should check it\n" +
                    Movie.transformPathToURL(selectedMovie.getPoster_path(), Movie.ImageSize.W150));

            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
        else if(item.getTitle()=="Delete"){
            if(!tmbdConnected) {
                removeById(selectedMovie.getId(), Movie.favorites);
                Movie.saveFavorites();

            }
            else{
                if(currentTask=="watchlist"){
                    RestClient.getApi().setWatchlist("movie",selectedMovie.getId(),false, RestClient.sessionId,new Callback<JsonElement>() {
                        @Override
                        public void success(JsonElement jsonElement, Response response) {

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.w("w","W");
                        }
                    });
                }
                else {
                    RestClient.getApi().setFavorite("movie",selectedMovie.getId(),false, RestClient.sessionId,new Callback<JsonElement>() {
                        @Override
                        public void success(JsonElement jsonElement, Response response) {

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.w("w","W");
                        }
                    });
                }

            }
            refreshFavorites();
            Toast.makeText(getApplicationContext(), "Успішно видалено", Toast.LENGTH_SHORT).show();
        }
        else if(item.getTitle()=="Post To Facebook"){
            FacebookManager.postToFacebook(selectedMovie.getOriginal_title(), MainActivity.this);
        }
        return true;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        if (view.getId() == R.id.listView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            selectedMovie = listAdapter.getMovie(info.position - 1);
            menu.setHeaderTitle(selectedMovie.getTitle());
            List<String> listMenuItems= new ArrayList<>();
            listMenuItems.add("Share");
            listMenuItems.add("Add To Favorites");
            if(tmbdConnected)
                listMenuItems.add("Add To Watchlist");
            if(Session.getActiveSession().isOpened())
                listMenuItems.add("Post To Facebook");
            if(currentTask=="favorite") {
                listMenuItems.remove("Share");
                listMenuItems.remove("Add To Favorites");
                listMenuItems.add("Delete");
            }
            if(currentTask=="watchlist"){
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
    public void refreshFavorites() {
        listView.setVisibility(View.VISIBLE);
        if(!tmbdConnected) {
            Movie.refreshFavorites();
            if (Movie.favorites != null) {
                totalPages = (Movie.favorites.size() < 20) ? 1 : (int) Math.ceil((double) Movie.favorites.size() / 20.0);
                int begin = ((currentFavoritePage - 1) * 20);
                int end = begin + 20;
                if (Movie.favorites.size() <= end)
                    end = Movie.favorites.size();
                refreshList(new ArrayList<>(Movie.favorites.subList(begin,end)),currentFavoritePage,totalPages);
            } else {
                listAdapter.setMovies(new ArrayList<Movie>());
                listView.setAdapter(listAdapter);
            }
        }
        else{
            RestClient.getApi().getFavoriteMovies(RestClient.sessionId,currentFavoritePage,new Callback<MovieRequestResult>() {
                @Override
                public void success(MovieRequestResult movieRequestResult, Response response) {
                    listView.setVisibility(View.VISIBLE);
                    if (movieRequestResult.getResults() != null) {
                        int cp = movieRequestResult.getPage();
                        totalPages = movieRequestResult.getTotal_pages();
                        refreshList((ArrayList<Movie>)movieRequestResult.getResults(),cp,totalPages);
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    }
    /**
     * on list item click listener, starts details activity
     */
    private OnItemClickListener detailsListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            MovieListAdapter adapter = (MovieListAdapter) ((HeaderViewListAdapter) listView.getAdapter()).getWrappedAdapter();
            final Movie movie = adapter.getMovie(position - 1);
            if (movie.getDetails() == null) {
                RestClient.getApi().getDetails(movie.getId(), new Callback<Movie.Details>() {
                    @Override
                    public void success(final Movie.Details details, Response response) {
                        RestClient.getApi().getImagePathes(movie.getId(), new Callback<JsonElement>() {
                            @Override
                            public void success(JsonElement imagesResult, Response response) {
                                JsonArray array=imagesResult.getAsJsonObject().getAsJsonArray("backdrops");
                                List<String> backdrops= new ArrayList<>();
                                for(int i=0;i<array.size();++i){
                                    String path=array.get(i).getAsJsonObject().get("file_path").getAsString();
                                    backdrops.add(path);
                                }
                                details.setImagePathes(backdrops);
                                movie.setDetails(details);
                                intent.putExtra("Movie", movie);
                                startActivity(intent);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Toast.makeText(getApplicationContext(), "have some troubles with ths movie try another one", Toast.LENGTH_LONG).show();
                            }
                        });

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            } else {
                intent.putExtra("Movie", movie);
                startActivity(intent);
            }
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

    private int currentPopularPage = 1;
    private int currentUpcomingPage = 1;
    private int currentTopRatedPage = 1;
    private int currentFavoritePage = 1;
    private int currentWatchListPage=1;
    private int currentSearchPage = 1;
    private int totalPages = 1000;

    private String currentTask = "popular";
    private String currentSearchType="";


    private MovieListAdapter listAdapter;
    private Movie selectedMovie = null;

    private boolean tmbdConnected=false;
    private SharedPreferences prefs;

    /**
     *  метод перевіряє чи користувач під'єднаний до мережі
     * @return повертає true якщо під'єднаний інакше(навіть ящко підключення саме триває) - ні
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
    private boolean isMovieInList(List<Movie> movieList, Movie movie) {
        for (Movie m : movieList) {
            if (movie.getOriginal_title().equals(m.getTitle()) && movie.getRelease_date().equals(m.getRelease_date()))
                return true;
        }
        return false;
    }
    private void removeById(int id,List<Movie> movies){
        for(Movie m:movies){
            if(m.getId()==id) {
                movies.remove(m);
                return ;
            }

        }
    }
    public void full_authenticate(final String username, final String password){
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

                                        if (currentTask == "watchlist" || currentTask == "favorites") {
                                            currentTask = "popular";
                                            tabs.setCurrentTab(0);
                                        }
                                    Toast.makeText(getApplicationContext(),"Connection is successfully established",Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Toast.makeText(getApplicationContext(),"Wrong Password,Try again!",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        return ;
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                return ;
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

        if(RestClient.sessionId=="" && RestClient.requestToken!=""){
            TheMovieDBAccount.getNewSession();
            tmbdConnected=true;
        }
        listAdapter = new MovieListAdapter(this);
        header = createHeader();
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
                    final EditText edPass = new EditText(getApplicationContext());
                    edPass.setText(pass);
                    edPass.setHint("password");
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
                    theMovieDatabaseLoginButton.setText(getResources().getText(resources[tmbdConnected ? 1 : 0]));
                    tabs.getTabWidget().getChildTabViewAt(4).setVisibility(tmbdConnected ? View.VISIBLE : View.GONE);

                    if (currentTask == "watchlist" || currentTask == "favorites") {
                        currentTask = "popular";
                        tabs.setCurrentTab(0);
                    }
                }
            }
        });
        facebookLoginButton =(LoginButton)findViewById(R.id.fb_login_button);
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
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (isOnline()) {
                        if (searchEditText.getText().toString().equals("")) {
                            currentTask = tabs.getCurrentTabTag();
                            refreshListByTab();
                        } else {
                            currentTask = "search";
                            refreshListBySearch(Uri.encode(searchEditText.getText().toString()));
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        listView.setOnItemClickListener(detailsListener);
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
        if(RestClient.requestToken!="") {
            if (RestClient.sessionId != "") {
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
}
