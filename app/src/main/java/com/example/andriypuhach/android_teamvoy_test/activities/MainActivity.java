package com.example.andriypuhach.android_teamvoy_test.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andriypuhach.android_teamvoy_test.FacebookManager;
import com.example.andriypuhach.android_teamvoy_test.MovieDatabaseHelper;
import com.example.andriypuhach.android_teamvoy_test.R;
import com.example.andriypuhach.android_teamvoy_test.adapters.MovieListAdapter;
import com.example.andriypuhach.android_teamvoy_test.models.ImagesResult;
import com.example.andriypuhach.android_teamvoy_test.models.Movie;
import com.example.andriypuhach.android_teamvoy_test.models.MovieRequestResult;
import com.example.andriypuhach.android_teamvoy_test.rest.RestClient;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends Activity {
    TabHost tabs;
    private UiLifecycleHelper uiHelper;
    private String currentSearchType="";
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
    private int currentSearchPage = 1;

    private String currentTask = "popular";
    private int totalPages = 1000;
    private ListView listView;
    private EditText searchView;
    private MovieListAdapter listAdapter;
    private Movie selectedMovie = null;
    private View header;
    private LoginButton btn;

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
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
                    case "search":
                        if(currentSearchPage<totalPages){
                            currentSearchPage++;
                            refreshListBySearch(searchView.getText().toString());
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
                            refreshListBySearch(Uri.encode(searchView.getText().toString()));
                            return;
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
    void refreshListBySearch(String search) {
        if(currentSearchType=="Звичайний пошук") {

            RestClient.getApi().search(search, currentSearchPage, new Callback<MovieRequestResult>() {
                @Override
                public void success(MovieRequestResult result, Response response) {
                    listView.setVisibility(View.VISIBLE);
                    if (result.getResults() != null) {
                        currentSearchPage = result.getPage();
                        totalPages = result.getTotal_pages();
                        ArrayList<Movie> movies = (ArrayList<Movie>) result.getResults();
                        listAdapter.setMovies(movies);
                        ((TextView) header.findViewById(R.id.currentPageView)).setText(currentSearchPage + " of " + totalPages);
                        if (listView.getHeaderViewsCount() == 0)
                            listView.addHeaderView(header);
                        listView.setAdapter(listAdapter);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    if (!isOnline()) {
                        Toast.makeText(getApplicationContext(), "Connection trouble. Please reconnect to the Internet", Toast.LENGTH_SHORT).show();
                        listView.setVisibility(View.GONE);
                    }
                }
            });
        }
        else{
            listView.setVisibility(View.GONE);
            MovieDatabaseHelper dbHelper = new MovieDatabaseHelper(getApplicationContext());
            List<Movie> result=dbHelper.searchByNote(search);
            totalPages = (result.size() < 20) ? 1 : (int) Math.ceil((double) result.size() / 20.0);
            ArrayList<Movie> movies = (ArrayList<Movie>) result;
            listAdapter.setMovies(movies);
            ((TextView) header.findViewById(R.id.currentPageView)).setText(currentSearchPage + " of " + totalPages);
            if (listView.getHeaderViewsCount() == 0)
                listView.addHeaderView(header);
            listView.setAdapter(listAdapter);
        }
    }
    //region detailsClickListener
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
                        RestClient.getApi().getImagePathes(movie.getId(), new Callback<ImagesResult>() {
                            @Override
                            public void success(ImagesResult imagesResult, Response response) {
                                details.setImagePathes(imagesResult.getBackdropPathes());
                                movie.setDetails(details);
                                intent.putExtra("Movie", movie);
                                startActivity(intent);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Toast.makeText(getApplicationContext(), "have some troubles with ths movie try another one", Toast.LENGTH_LONG);
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
    //endregion detailsClickListener
    //region tabChangeListener
    private TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
        @Override
        public void onTabChanged(String tabId) {
            currentTask = tabId;
            if (!currentTask.equals("favorite")) {
                refreshListByTab();
            } else {
                refreshFavorites();
            }
        }
    };

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
                    listAdapter.setMovies((ArrayList<Movie>) movieRequestResult.getResults());
                    if (listView.getHeaderViewsCount() == 0)
                        listView.addHeaderView(header);
                    ((TextView) header.findViewById(R.id.currentPageView)).setText(cp + " of " + totalPages);
                    listView.setAdapter(listAdapter);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (!isOnline()) {
                    Toast.makeText(getApplicationContext(), "Connection trouble. Please reconnect to the Internet", Toast.LENGTH_LONG).show();
                    listView.setVisibility(View.GONE);
                }

            }
        });
    }


    //endregion tabChangeListener
    //region contextMenu
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
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Add To Favorites") {
            if (!isMovieInList(Movie.favorites, selectedMovie)) {
                Movie.favorites.add(selectedMovie);
                Movie.saveFavorites();
                Toast.makeText(getApplicationContext(), "Успішно додано", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Цей фільм уже у вашому списку", Toast.LENGTH_SHORT).show();
            }
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
            removeById(selectedMovie.getId(),Movie.favorites);
            Movie.saveFavorites();
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
            if(Session.getActiveSession().isOpened())
                listMenuItems.add("Post To Facebook");
            if(currentTask=="favorite") {
                listMenuItems.remove("Share");
                listMenuItems.remove("Add To Favorites");
                listMenuItems.add("Delete");
            }
            String [] menuItems=new String[listMenuItems.size()];
            listMenuItems.toArray(menuItems);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }
    //endregion
    //region favorites
    public void refreshFavorites() {
      listView.setVisibility(View.VISIBLE);
      Movie.refreshFavorites();
        if (Movie.favorites!= null) {
            totalPages = (Movie.favorites.size() < 20) ? 1 : (int) Math.ceil((double) Movie.favorites.size() / 20.0);
            int begin =((currentFavoritePage-1)*20);
            int end =begin+20;
            if (Movie.favorites.size()<=end)
                end=Movie.favorites.size();
            listAdapter.setMovies(new ArrayList<>(Movie.favorites.subList(begin,end)));
            ((TextView) header.findViewById(R.id.currentPageView)).setText(currentFavoritePage + " of " + totalPages);
            if (listView.getHeaderViewsCount() == 0)
                listView.addHeaderView(header);
            listView.setAdapter(listAdapter);
        } else {
            listAdapter.setMovies(new ArrayList<Movie>());
            listView.setAdapter(listAdapter);
        }
    }
    //endregion
    //region optionsMenu

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(this, statusCallback);
        uiHelper.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        listAdapter = new MovieListAdapter(getApplicationContext());
        header = createHeader();
        btn=(LoginButton)findViewById(R.id.fb_login_button);
        btn.setReadPermissions(Arrays.asList("public_profile","user_status","user_birthday","user_about_me","user_relationships"));
        btn.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser graphUser) {
                if (graphUser != null) {
                    setTitle("Welcome, " + graphUser.getName());
                } else {
                    setTitle("Movie info welcomes you");
                }
            }
        });
        searchView = (EditText) findViewById(R.id.searchMovieEdit);
        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (isOnline()) {
                        if (searchView.getText().toString().equals("")) {
                            currentTask = tabs.getCurrentTabTag();
                            refreshListByTab();
                        } else {
                            currentTask = "search";
                            refreshListBySearch(Uri.encode(searchView.getText().toString()));
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
