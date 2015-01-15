package com.example.andriypuhach.android_teamvoy_test.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andriypuhach.android_teamvoy_test.MovieListAdapter;
import com.example.andriypuhach.android_teamvoy_test.R;
import com.example.andriypuhach.android_teamvoy_test.models.ImagesResult;
import com.example.andriypuhach.android_teamvoy_test.models.Movie;
import com.example.andriypuhach.android_teamvoy_test.models.MovieDetails;
import com.example.andriypuhach.android_teamvoy_test.models.MovieRequestResult;
import com.example.andriypuhach.android_teamvoy_test.rest.RestClient;
import com.facebook.Request;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends Activity {
    TabHost tabs;
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            if (state.isOpened()) {
                Log.d("FacebookSampleActivity", "Facebook session opened");
            } else if (state.isClosed()) {
                Log.d("FacebookSampleActivity", "Facebook session closed");
            }
        }
    };


    private final File appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/android_teamvoy_test");

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
    private List<Movie> favorites;
    private Movie selectedMovie = null;
    private View header;
    private LoginButton btn;


    View createHeader() {
        View v = getLayoutInflater().inflate(R.layout.header, null);
        v.findViewById(R.id.nextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPage=1;
                switch (currentTask) {
                    case "popular":
                        if (currentPopularPage < totalPages) {
                            currentPopularPage++;
                            currentPage=currentPopularPage;
                        }
                        break;

                    case "upcoming":
                        if (currentUpcomingPage<totalPages) {
                            currentUpcomingPage++;
                            currentPage=currentUpcomingPage;
                        }
                        break;
                    case "top_rated":
                        if(currentTopRatedPage<totalPages){
                            currentTopRatedPage++;
                            currentPage=currentTopRatedPage;
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
                            currentPage=currentPopularPage;
                        }
                        break;
                }
                refreshListByTab();
            }
        });

        v.findViewById(R.id.prevButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPage=1;
                switch (currentTask) {
                    case "popular":
                        if (currentPopularPage > 1) {
                            currentPopularPage--;
                            currentPage=currentPopularPage;
                        }
                        break;

                    case "upcoming":
                        if (currentUpcomingPage>1) {
                            currentUpcomingPage--;
                            currentPage=currentUpcomingPage;
                        }
                        break;
                    case "top_rated":
                        if(currentTopRatedPage>1){
                            currentTopRatedPage--;
                            currentPage=currentTopRatedPage;
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
                            currentPage=currentPopularPage;
                        }
                        break;
                }
                refreshListByTab();
            }
        });
        return v;
    }

    void refreshListBySearch(String search) {
        RestClient.getApi().search(search, currentSearchPage, new Callback<MovieRequestResult>() {
            @Override
            public void success(MovieRequestResult result, Response response) {
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
                currentSearchPage = 0;
                totalPages = 0;
                listAdapter.setMovies(new ArrayList<Movie>());
                ((TextView) header.findViewById(R.id.currentPageView)).setText(currentSearchPage + " of " + totalPages);
                if (listView.getHeaderViewsCount() == 0)
                    listView.addHeaderView(header);
                listView.setAdapter(listAdapter);
            }
        });
    }

    //region detailsClickListener
    private OnItemClickListener detailsListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            MovieListAdapter adapter = (MovieListAdapter) ((HeaderViewListAdapter) listView.getAdapter()).getWrappedAdapter();
            final Movie movie = adapter.getMovie(position - 1);
            if (movie.getDetails() == null) {
                RestClient.getApi().getDetails(movie.getId(), new Callback<MovieDetails>() {
                    @Override
                    public void success(final MovieDetails movieDetails, Response response) {
                        RestClient.getApi().getImagePathes(movie.getId(), new Callback<ImagesResult>() {
                            @Override
                            public void success(ImagesResult imagesResult, Response response) {
                                movieDetails.setImagePathes(imagesResult.getBackdropPathes());
                                movie.setDetails(movieDetails);
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
                if (movieRequestResult.getResults() != null) {
                    int cp=movieRequestResult.getPage();
                    totalPages = movieRequestResult.getTotal_pages();
                    listAdapter.setMovies((ArrayList<Movie>) movieRequestResult.getResults());
                    if (listView.getHeaderViewsCount() == 0)
                        listView.addHeaderView(header);
                    ((TextView)header.findViewById(R.id.currentPageView)).setText(cp +" of "+totalPages);
                    listView.setAdapter(listAdapter);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), "trouble", Toast.LENGTH_LONG);
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

    public boolean checkPermissions() {
        Session s = Session.getActiveSession();
        if (s != null) {
            return s.getPermissions().contains("publish_actions");
        } else
            return false;
    }

    public void requestPermissions() {
        Session s = Session.getActiveSession();
        if (s != null)
            s.requestNewPublishPermissions(new Session.NewPermissionsRequest(
                    this, "publish_actions"));
    }

    private void postToFacebook(String text){
        if(checkPermissions()){
            Request request = Request.newStatusUpdateRequest(
                    Session.getActiveSession(), "I like "+text,
                    new Request.Callback() {
                        @Override
                        public void onCompleted(com.facebook.Response response) {
                            if (response.getError() == null)
                                Toast.makeText(MainActivity.this,
                                        "Status updated successfully",
                                        Toast.LENGTH_LONG).show();
                        }


                    });
            request.executeAsync();
        }
        else{
            requestPermissions();
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Add To Favorites") {
            if (!isMovieInList(favorites, selectedMovie)) {
                favorites.add(selectedMovie);
                Movie.serializeList(favorites, new File(appDir.getAbsolutePath() + "/favorites.movinf"));
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
            removeById(selectedMovie.getId(),favorites);
            Movie.serializeList(favorites, new File(appDir.getAbsolutePath() + "/favorites.movinf"));
            refreshFavorites();
            Toast.makeText(getApplicationContext(), "Успішно видалено", Toast.LENGTH_SHORT).show();
        }
        else if(item.getTitle()=="Post To Facebook"){
            postToFacebook(selectedMovie.getOriginal_title());
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
    private void initFavorites() {
        favorites = new ArrayList<>();
        appDir.mkdirs();
        File file = new File(appDir, "favorites.movinf");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        favorites = Movie.deserializeList(file);
    }

    public void refreshFavorites() {
        File file = new File(appDir, "favorites.movinf");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        favorites = Movie.deserializeList(file);
        if (favorites != null) {
            totalPages = (favorites.size() < 20) ? 1 : (int) Math.ceil((double) favorites.size() / 20.0);
            int begin =((currentFavoritePage-1)*20);
            int end =begin+20;
            if (favorites.size()<=end)
                end=favorites.size();
            listAdapter.setMovies(new ArrayList<>(favorites.subList(begin,end)));
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(this, statusCallback);
        uiHelper.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        listAdapter = new MovieListAdapter(getApplicationContext());

        initFavorites();
        header = createHeader();
        btn=(LoginButton)findViewById(R.id.fb_login_button);
        btn.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser graphUser) {
                if (graphUser != null) {
                    setTitle("Movie info welcomes you " + graphUser.getName());
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
                    if (searchView.getText().toString().equals("")) {
                        currentTask = tabs.getCurrentTabTag();
                        refreshListByTab();
                    } else {
                        currentTask = "search";
                        refreshListBySearch(Uri.encode(searchView.getText().toString()));
                    }
                    return true;
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
