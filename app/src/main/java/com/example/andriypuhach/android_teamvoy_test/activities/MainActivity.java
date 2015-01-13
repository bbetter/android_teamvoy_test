package com.example.andriypuhach.android_teamvoy_test.activities;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;


import com.example.andriypuhach.android_teamvoy_test.MyLogger;
import com.example.andriypuhach.android_teamvoy_test.asyncs.DownloadDetailsAsync;
import com.example.andriypuhach.android_teamvoy_test.asyncs.DownloadMoviesAsync;
import com.example.andriypuhach.android_teamvoy_test.asyncs.SearchMovieAsync;
import com.example.andriypuhach.android_teamvoy_test.models.Movie;
import com.example.andriypuhach.android_teamvoy_test.MovieListAdapter;
//import com.example.andriypuhach.android_teamvoy_test.asyncs.DownloadDetailsAsync;
import com.example.andriypuhach.android_teamvoy_test.models.MovieDetails;
import com.example.andriypuhach.android_teamvoy_test.models.MovieRequestResult;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.example.andriypuhach.android_teamvoy_test.R;

public class MainActivity extends Activity {
    TabHost tabs;
    private String currentTask = "popular";
    private int currentPage = 1;
    private int totalPages = 1000;
    private ListView listView;
    private EditText searchView;
    private MovieListAdapter listAdapter;
    private View header;
    //region detailsClickListener
    private OnItemClickListener detailsListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            MovieListAdapter adapter = (MovieListAdapter) ((HeaderViewListAdapter) listView.getAdapter()).getWrappedAdapter();
            Movie movie = adapter.getMovie(position - 1);
            MovieDetails details = null;
            if (movie.getDetails() == null) {
                DownloadDetailsAsync task = new DownloadDetailsAsync();
                try {
                    details = task.execute(movie.getId()).get();
                } catch (InterruptedException | ExecutionException e) {
                    MyLogger.appendLog("TAG", "Interrupted OR Execution Exception in ItemClickListener");
                }
                movie.setDetails(details);
            }
            intent.putExtra("Movie", movie);
            startActivity(intent);
        }
    };
    //endregion detailsClickListener
    //region tabChangeListener
    private TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
        @Override
        public void onTabChanged(String tabId) {
            currentTask = tabId;
            refreshListByTab(tabId, 1);
        }
    };


    void refreshListBySearch(String search) {
        SearchMovieAsync searchMovieAsync = new SearchMovieAsync();
        try {
            MovieRequestResult result = searchMovieAsync.execute(search, String.valueOf(currentPage)).get();
            if (result.getResults() != null) {
                currentPage = result.getPage();
                totalPages = result.getTotal_pages();
                ArrayList<Movie> movies = (ArrayList<Movie>) result.getResults();
                listAdapter.setMovies(movies);
                ((TextView) header.findViewById(R.id.currentPageView)).setText(currentPage + " of " + totalPages);
                if (listView.getHeaderViewsCount() == 0)
                    listView.addHeaderView(header);
                listView.setAdapter(listAdapter);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();

        } finally {
            searchMovieAsync.cancel(true);
        }
    }


    void refreshListByTab(String tabId, int page) {
        DownloadMoviesAsync rlistAsyncTask = new DownloadMoviesAsync();
        try {
            MovieRequestResult result = rlistAsyncTask.execute(tabId, String.valueOf(page)).get();
            currentPage = result.getPage();
            totalPages = result.getTotal_pages();
            ArrayList<Movie> movies = (ArrayList<Movie>) result.getResults();
            listAdapter.setMovies(movies);
            ((TextView) header.findViewById(R.id.currentPageView)).setText(currentPage + " of " + totalPages);
            if (listView.getHeaderViewsCount() == 0)
                listView.addHeaderView(header);
            listView.setAdapter(listAdapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            rlistAsyncTask.cancel(true);
        }
    }

    View createHeader() {
        View v = getLayoutInflater().inflate(R.layout.header, null);
        ((TextView) v.findViewById(R.id.currentPageView)).setText(currentPage + " of " + totalPages);
        v.findViewById(R.id.nextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage < totalPages) {
                    currentPage++;
                    if (currentTask != "search")
                        refreshListByTab(currentTask, currentPage);
                    else
                       refreshListBySearch(searchView.getText().toString());
                }
            }
        });

        v.findViewById(R.id.prevButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage > 1) {
                    currentPage--;
                    if (currentTask != "search")
                        refreshListByTab(currentTask, currentPage);
                    else
                        refreshListBySearch(searchView.getText().toString());
                }
            }
        });
        return v;
    }

    //endregion tabChangeListener
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        listAdapter = new MovieListAdapter(getApplicationContext());
        header = createHeader();
        searchView = (EditText) findViewById(R.id.searchMovieEdit);
        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (searchView.getText().toString().equals("")) {
                        currentTask = tabs.getCurrentTabTag();
                        refreshListByTab(currentTask, 1);
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
        tabs.setCurrentTab(0);
    }
}
