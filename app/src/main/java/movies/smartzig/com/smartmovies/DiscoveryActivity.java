package movies.smartzig.com.smartmovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.List;

import movies.smartzig.com.smartmovies.MovieAdapter.MovieAdapterOnClickHandler;
import movies.smartzig.com.smartmovies.data.MovieDbHelper;
import movies.smartzig.com.smartmovies.details.MovieDetailActivity;
import movies.smartzig.com.smartmovies.utils.MovieItem;
import movies.smartzig.com.smartmovies.utils.MovieUtils;
import movies.smartzig.com.smartmovies.utils.NetworkUtils;


public class DiscoveryActivity extends AppCompatActivity implements MovieAdapterOnClickHandler, SharedPreferences.OnSharedPreferenceChangeListener {

    private TextView mErrorMessageDisplay;
    private TextView mNoFavoriteMessageDisplay;
    private MovieAdapter mMovieAdapter;
    private RecyclerView mRecyclerView;

    private SharedPreferences mPrefs;


    private ProgressBar mLoadingIndicator;


    private String searchTypeHandler;

    private MovieDbHelper dbHelper;
    private SQLiteDatabase mDb;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0f);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.smartGreen)));
        }

        // Create a DB helper (this will create the DB if run for the first time)
        dbHelper = new MovieDbHelper(getApplicationContext());

        // Keep a reference to the mDb until paused or killed. Get a writable database
        mDb = dbHelper.getWritableDatabase();

        setupSharedPreferences();
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
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

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

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        refresh(searchTypeHandler);
    }


    private void setupSharedPreferences() {

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //favoriteList = MovieUtils.getFavoriteListFromPrefs(mPrefs);

        searchTypeHandler = mPrefs.getString(this.getResources().getString(R.string.pref_search_key), this.getResources().getString(R.string.pref_default_search));

        // Register the listener
        mPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_search_key))) {
            searchTypeHandler = mPrefs.getString(this.getResources().getString(R.string.pref_search_key), this.getResources().getString(R.string.title_top_rated));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.discovery, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(searchTypeHandler.equals(this.getResources().getString(R.string.pref_search_favorite)))
        {   //need to refresh favoritelist
            refresh(searchTypeHandler);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);

        mDb.close();
    }

    /**
     * This method will refresh the information
     */
    private void refresh(String sortBy) {
        showMovieView();

        if (sortBy.equals(this.getResources().getString(R.string.pref_search_favorite))) {

            handleMovieData(dbHelper.fetchAllMovies(mDb));
        } else {
            new FetchMovieListTask().execute(sortBy);
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

        Intent intentToStartDetailActivity = new Intent(this, MovieDetailActivity.class);
        intentToStartDetailActivity.putExtra("MOVIE", movie);
        startActivity(intentToStartDetailActivity);
    }


    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_top_rated:

                    searchTypeHandler = getResources().getString(R.string.pref_search_top_rated);
                    refresh(searchTypeHandler);
                    return true;

                case R.id.navigation_popular:

                    searchTypeHandler = getResources().getString(R.string.pref_search_popular);
                    refresh(searchTypeHandler);
                    return true;

                case R.id.navigation_favorite:
                    searchTypeHandler = getResources().getString(R.string.pref_search_favorite);
                    refresh(searchTypeHandler);
                    return true;
            }
            return false;
        }

    };

    private void handleMovieData(List<MovieItem> movieData) {

        mLoadingIndicator.setVisibility(View.INVISIBLE);

        if (movieData != null && !movieData.isEmpty()) {
            showMovieView();
            mMovieAdapter.setMovieData(movieData);
        } else {
            if (searchTypeHandler.equals(getResources().getString(R.string.pref_search_favorite)))
            {
                showNoFavoriteMessage();
            }else{
                showErrorMessage();
            }

        }
    }

    private void showMovieView() {
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