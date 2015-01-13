package com.example.andriypuhach.android_teamvoy_test.activities;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
            final Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            MovieListAdapter adapter = (MovieListAdapter) ((HeaderViewListAdapter) listView.getAdapter()).getWrappedAdapter();
            final Movie movie = adapter.getMovie(position - 1);
            if (movie.getDetails() == null) {
                RestClient.getApi().getDetails(movie.getId(),new Callback<MovieDetails>(){
                    @Override
                    public void success(final MovieDetails movieDetails, Response response) {
                           RestClient.getApi().getImagePathes(movie.getId(),new Callback<ImagesResult>() {
                               @Override
                               public void success(ImagesResult imagesResult, Response response) {
                                   movieDetails.setImagePathes(imagesResult.getPosterPathes());
                                   movie.setDetails(movieDetails);
                                   intent.putExtra("Movie", movie);
                                   startActivity(intent);
                               }

                               @Override
                               public void failure(RetrofitError error) {
                                 Toast.makeText(getApplicationContext(),"have some troubles with ths movie try another one",Toast.LENGTH_LONG);
                               }
                           });

                    }
                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }
            else{
                intent.putExtra("Movie",movie);
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
            refreshListByTab(1);
        }
    };


    void refreshListBySearch(String search) {
        RestClient.getApi().search(search,currentPage,new Callback<MovieRequestResult>(){
            @Override
            public void success(MovieRequestResult result, Response response) {
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
            }

            @Override
            public void failure(RetrofitError error) {
                currentPage=0;
                totalPages=0;
                listAdapter.setMovies(new ArrayList<Movie>());
                ((TextView) header.findViewById(R.id.currentPageView)).setText(currentPage + " of " + totalPages);
                if (listView.getHeaderViewsCount() == 0)
                    listView.addHeaderView(header);
                listView.setAdapter(listAdapter);
            }
        });
    }


    void refreshListByTab(int page) {
        RestClient.getApi().getMovies(currentTask,currentPage,new Callback<MovieRequestResult>() {
            @Override
            public void success(MovieRequestResult movieRequestResult, Response response) {
                if(movieRequestResult.getResults()!=null) {
                    currentPage = movieRequestResult.getPage();
                    totalPages = movieRequestResult.getTotal_pages();
                    listAdapter.setMovies((ArrayList<Movie>) movieRequestResult.getResults());
                    ((TextView) header.findViewById(R.id.currentPageView)).setText(currentPage + " of " + totalPages);
                    if (listView.getHeaderViewsCount() == 0)
                        listView.addHeaderView(header);
                    if (listView.getHeaderViewsCount() == 0)
                        listView.addHeaderView(header);
                    listView.setAdapter(listAdapter);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(),"trouble",Toast.LENGTH_LONG);
            }
        });
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
                        refreshListByTab(currentPage);
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
                        refreshListByTab(currentPage);
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
                        refreshListByTab(1);
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
