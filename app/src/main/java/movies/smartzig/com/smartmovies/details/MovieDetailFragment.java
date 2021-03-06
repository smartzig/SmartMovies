package movies.smartzig.com.smartmovies.details;


import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import movies.smartzig.com.smartmovies.R;
import movies.smartzig.com.smartmovies.data.MovieDbHelper;
import movies.smartzig.com.smartmovies.utils.MovieItem;
import movies.smartzig.com.smartmovies.utils.NetworkUtils;

public class MovieDetailFragment extends Fragment implements
        TrailerAdapter.OnItemClickListener, TrailersTask.Listener, ReviewsTask.Listener, ReviewAdapter.OnItemClickListener {


    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private static final String MOVIE_KEY = "MOVIE";


    private MovieItem mMovie;
    private boolean isFavorite;

    private static List<MovieItem> favoriteList;   //This is updated favorite movie list

    @BindView(R.id.movie_title)
    TextView mMovieTitleView;
    @BindView(R.id.movie_overview)
    TextView mMovieOverviewView;
    @BindView(R.id.movie_release_date)
    TextView mMovieReleaseDateView;
    @BindView(R.id.movie_user_rating)
    TextView mMovieRatingView;
    @BindView(R.id.movie_poster)
    ImageView mMoviePosterView;

    @BindViews({R.id.user_rating_first_star, R.id.user_rating_second_star, R.id.user_rating_third_star, R.id.user_rating_fourth_star, R.id.user_rating_fifth_star})
    List<ImageView> ratingStarViews;

    public static final String EXTRA_TRAILERS = "EXTRA_TRAILERS";
    public static final String EXTRA_REVIEWS = "EXTRA_REVIEWS";
    @BindView(R.id.movie_trailer_list)
    RecyclerView mRecyclerViewTrailers;
    @BindView(R.id.review_list)
    RecyclerView mRecyclerViewReviews;
    private TrailerAdapter mTrailerListAdapter;
    private ReviewAdapter mReviewAdapter;

    private MovieDbHelper dbHelper;
    private SQLiteDatabase mDb;

    public MovieDetailFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(MOVIE_KEY)) {
            mMovie = getArguments().getParcelable(MOVIE_KEY);
        }

        // Create a DB helper (this will create the DB if run for the first time)
        dbHelper = new MovieDbHelper(this.getContext());

        // Keep a reference to the mDb until paused or killed. Get a writable database
        mDb = dbHelper.getWritableDatabase();

        checkIsFavorite();
        setHasOptionsMenu(true);
    }

    private void checkIsFavorite() {

        favoriteList = dbHelper.fetchAllMovies(mDb);
        isFavorite = favoriteList.contains(mMovie);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();
        assert activity != null;
        CollapsingToolbarLayout appBarLayout =
                activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null && activity instanceof MovieDetailActivity) {
            appBarLayout.setTitle(mMovie.getOriginalTitle());
        }

        ImageView movieBackdrop = (activity.findViewById(R.id.movie_backdrop));
        if (movieBackdrop != null) {
            Picasso.get()
                    .load(mMovie.getBackdropPath())
                    .config(Bitmap.Config.RGB_565)
                    .into(movieBackdrop);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_details, container, false);
        ButterKnife.bind(this, rootView);

        mMovieTitleView.setText(mMovie.getOriginalTitle());
        mMovieOverviewView.setText(mMovie.getOverview());
        mMovieReleaseDateView.setText(mMovie.getReleaseDate());
        Picasso.get()
                .load(mMovie.getPosterPath())
                .config(Bitmap.Config.RGB_565)
                .into(mMoviePosterView);

        refreshRatingStars();
        loadTrailers(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_TRAILERS)) {
            List<Trailers> trailers = savedInstanceState.getParcelableArrayList(EXTRA_TRAILERS);
            mTrailerListAdapter.add(trailers);

        } else {
            getTrailers();
        }


        loadReviews(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_REVIEWS)) {
            List<Reviews> reviews = savedInstanceState.getParcelableArrayList(EXTRA_REVIEWS);
            mReviewAdapter.add(reviews);
        } else {
            getReviews();
        }
        Log.d(LOG_TAG, "Current selected movie id is: " + String.valueOf(mMovie.getId()));

        return rootView;
    }


    private void loadTrailers(Bundle savedInstanceState) {

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewTrailers.setLayoutManager(layoutManager);
        mTrailerListAdapter = new TrailerAdapter(new ArrayList<Trailers>(), this);
        mRecyclerViewTrailers.setAdapter(mTrailerListAdapter);
        mRecyclerViewTrailers.setNestedScrollingEnabled(false);

        //  Request for the trailers if only savedInstanceState == null
        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_TRAILERS)) {
            List<Trailers> trailers = savedInstanceState.getParcelableArrayList(EXTRA_TRAILERS);
            mTrailerListAdapter.add(trailers);

        } else {
            getTrailers();
        }
    }

    private void getTrailers() {
        if (NetworkUtils.networkStatus(Objects.requireNonNull(getContext()))) {
            TrailersTask task = new TrailersTask(MovieDetailFragment.this);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mMovie.getId());
        } else {
            Toast toast = Toast.makeText(getContext(), getString(R.string.message_network_alert), Toast.LENGTH_LONG);
            toast.show();

        }
    }

    private void loadReviews(Bundle savedInstanceState) {

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewReviews.setLayoutManager(layoutManager);
        mReviewAdapter = new ReviewAdapter(new ArrayList<Reviews>(), this);
        mRecyclerViewReviews.setAdapter(mReviewAdapter);
        mRecyclerViewReviews.setNestedScrollingEnabled(false);


        // Request for the reviews if only savedInstanceState == null
        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_REVIEWS)) {
            List<Reviews> reviews = savedInstanceState.getParcelableArrayList(EXTRA_REVIEWS);
            mReviewAdapter.add(reviews);
        } else {
            getReviews();
        }
    }

    private void getReviews() {
        if (NetworkUtils.networkStatus(Objects.requireNonNull(getContext()))) {
            ReviewsTask task = new ReviewsTask(MovieDetailFragment.this);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mMovie.getId());
        }
        //no need for a second alert message


    }

    @Override
    public void onLoadFinished(List<Trailers> trailers) {
        mTrailerListAdapter.add(trailers);


    }

    @Override
    public void watch_trailer(Trailers trailers, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailers.getTrailerUrl())));
    }

    @Override
    public void read_reviews(Reviews review, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(review.getUrl())));
    }

    /*Implemented method from ReviewsTask Class*/
    @Override
    public void on_reviews_loaded(List<Reviews> reviews) {
        mReviewAdapter.add(reviews);
    }


    @Override
    public void onResume() {
        super.onResume();
        checkIsFavorite();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_detail_fragment, menu);

        MenuItem favoriteMenuItem = menu.findItem(R.id.it_favorite_movie);
        if (isFavorite) {
            favoriteMenuItem.setIcon(R.drawable.ic_baseline_favorite_24px);
        } else {
            favoriteMenuItem.setIcon(R.drawable.ic_baseline_favorite_border_24px);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.it_favorite_movie:
                updateFavorite();

                if (isFavorite) {
                    item.setIcon(R.drawable.ic_baseline_favorite_24px);
                } else {
                    item.setIcon(R.drawable.ic_baseline_favorite_border_24px);
                }
                return true;

            default:
                break;
        }

        return false;
    }

    private void updateFavorite() {


        if (!isFavorite) {
            dbHelper.insertMovie(mDb,mMovie);


        } else {
            favoriteList.remove(mMovie);
            dbHelper.deleteMovie(mDb,mMovie.getId());
        }

        checkIsFavorite();
    }

    private void refreshRatingStars() {
        if (mMovie.getVoteAverage() != null && !mMovie.getVoteAverage().isEmpty()) {
            String ratingStar = getResources().getString(R.string.movie_rating,
                    mMovie.getVoteAverage());
            mMovieRatingView.setText(ratingStar);
            float userRating = Float.valueOf(mMovie.getVoteAverage()) / 2;
            int integerPart = (int) userRating;

            // Fill stars
            for (int i = 0; i < integerPart; i++) {
                ratingStarViews.get(i).setImageResource(R.drawable.ic_star_24dp);
            }
            // Fill half star
            if (Math.round(userRating) > integerPart) {
                ratingStarViews.get(integerPart).setImageResource(
                        R.drawable.ic_star_half_24dp);
            }
        } else {
            mMovieRatingView.setVisibility(View.GONE);
        }
    }

}