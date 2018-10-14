package movies.smartzig.com.smartmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.List;

import movies.smartzig.com.smartmovies.MovieAdapter.MovieAdapterOnClickHandler;
import movies.smartzig.com.smartmovies.data.AppPreferences;
import movies.smartzig.com.smartmovies.details.MovieDetailActivity;
import movies.smartzig.com.smartmovies.utils.MovieItem;
import movies.smartzig.com.smartmovies.utils.MovieUtils;
import movies.smartzig.com.smartmovies.utils.NetworkUtils;


public class DiscoveryActivity extends AppCompatActivity implements MovieAdapterOnClickHandler {

    private static final String SEARCH_TOP_RATED = "top_rated";
    private static final String SEARCH_POPULAR = "popular";
    private static final String SEARCH_FAVORITE = "favorite";

    private  TextView mErrorMessageDisplay;
    private  TextView mNoFavoriteMessageDisplay;
    private  MovieAdapter mMovieAdapter;
    private  RecyclerView mRecyclerView;

    private SharedPreferences mPrefs;
    public static List<MovieItem> favoriteList;

    private ProgressBar mLoadingIndicator;


    private String searchTypeHandler;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);

        if(getSupportActionBar() !=null) {
            getSupportActionBar().setElevation(0f);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.smartGreen)));
        }

        mPrefs = getSharedPreferences(this.getResources().getString(R.string.pref_key), Context.MODE_PRIVATE);
        favoriteList = MovieUtils.getFavoriteListFromPrefs(mPrefs);

        /*
         * RecyclerView that will handle the movies list.
         */
        mRecyclerView = findViewById(R.id.recyclerview_movie_list);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);

        /* This TextView is used to display a no favorite message */
        mNoFavoriteMessageDisplay = findViewById(R.id.tv_no_favorite_message_display);
        /*
         * Progress Bar that will tell the user when we are loading data.
         */
        mLoadingIndicator =  findViewById(R.id.pb_loading_indicator);

        int gridColsNumber = this.getResources()
                .getInteger(R.integer.number_of_grid_columns);

        /* setLayoutManager associates the LayoutManager we created above with our RecyclerView */
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridColsNumber));

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);


        /*
         * The Adapter is responsible for linking our data with the Views that
         * will end up displaying our data.
         */
        mMovieAdapter = new MovieAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mMovieAdapter);

        searchTypeHandler = AppPreferences.getPreferredSearchMethod();


        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        refresh(searchTypeHandler);

    }



    @Override
    public void onResume() {
        super.onResume();
        favoriteList = MovieUtils.getFavoriteListFromPrefs(mPrefs);

        if(favoriteList.isEmpty())
        {
            refresh(searchTypeHandler);
        }
    }

    /**
     * This method will refresh the information
     */
    private void refresh(String sort_by) {
        showMovieView();

        if(sort_by.equals(SEARCH_FAVORITE))
        {
            handleMovieData(favoriteList);
        }else{
            new FetchMovieListTask().execute(sort_by);
        }

    }

    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item
     * clicks.
     *
     * @param movie The weather for the day that was clicked
     */
    @Override
    public void onClick(MovieItem movie) {

        Context context = this;
        Class destinationClass = MovieDetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
       intentToStartDetailActivity.putExtra("MOVIE", movie);
        startActivity(intentToStartDetailActivity);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_top_rated:

                    searchTypeHandler = SEARCH_TOP_RATED;
                    refresh(SEARCH_TOP_RATED);
                    return true;

                case R.id.navigation_popular:

                    searchTypeHandler = SEARCH_POPULAR;
                    refresh(SEARCH_POPULAR);
                    return true;

                case R.id.navigation_favorite:
                    searchTypeHandler = SEARCH_FAVORITE;
                    refresh(SEARCH_FAVORITE);
                    return true;
            }
            return false;
        }

    };

    private void handleMovieData(List<MovieItem> movieData) {

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if(searchTypeHandler.equals(SEARCH_FAVORITE) && favoriteList.isEmpty())
        {
            showNoFavoriteMessage();
        }
        else if (movieData != null) {
            showMovieView();
            mMovieAdapter.setMovieData(movieData);
        } else {
                showErrorMessage();
        }
    }

    private  void showMovieView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mNoFavoriteMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showNoFavoriteMessage() {
        /* First, hide the currently visible data */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mNoFavoriteMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        mNoFavoriteMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private class FetchMovieListTask extends AsyncTask<String, Void, List<MovieItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<MovieItem> doInBackground(String... params) {

            /* If there's no zip code, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }
            try {
            String preferedSearchMethod = params[0];

                URL movieDbRequestUrl = NetworkUtils.buildUrl(preferedSearchMethod);


                    String jsonMovieResponse = NetworkUtils
                            .getResponseFromHttpUrl(movieDbRequestUrl);

                    return MovieUtils
                            .getMoviesStringFromJson(jsonMovieResponse);



            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<MovieItem> movieData) {

            handleMovieData(movieData);

        }
    }

}