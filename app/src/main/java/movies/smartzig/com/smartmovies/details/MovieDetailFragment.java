package movies.smartzig.com.smartmovies.details;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import movies.smartzig.com.smartmovies.R;
import movies.smartzig.com.smartmovies.utils.MovieItem;
import movies.smartzig.com.smartmovies.utils.MovieUtils;

public class MovieDetailFragment extends Fragment {

        private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

        private SharedPreferences mPrefs;

        private MovieItem mMovie;
        private boolean isFavorite;

        private static ArrayList<MovieItem> favoriteList;   //This is updated favorite movie list

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

        public MovieDetailFragment() {

        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (getArguments() != null && getArguments().containsKey("MOVIE")) {
                mMovie = getArguments().getParcelable("MOVIE");
            }

            mPrefs = this.getActivity().getSharedPreferences(this.getResources().getString(R.string.pref_key), Context.MODE_PRIVATE);
            checkIsFavorite();
            setHasOptionsMenu(true);
        }

    private void checkIsFavorite()
    {

        favoriteList = MovieUtils.getFavoriteListFromPrefs(mPrefs);
        isFavorite  = favoriteList.contains(mMovie);

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

            ImageView movieBackdrop = ( activity.findViewById(R.id.movie_backdrop));
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

            Log.d(LOG_TAG, "Current selected movie id is: " + String.valueOf(mMovie.getId()));

            return rootView;
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
            if(isFavorite) {
                favoriteMenuItem.setIcon(R.drawable.ic_baseline_favorite_24px);
            }else
            {
                favoriteMenuItem.setIcon(R.drawable.ic_baseline_favorite_border_24px);
            }
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.it_favorite_movie:
                updateFavorite();

                if(isFavorite) {
                    item.setIcon(R.drawable.ic_baseline_favorite_24px);
                }else
                {
                    item.setIcon(R.drawable.ic_baseline_favorite_border_24px);
                }
                return true;

            default:
                break;
        }

        return false;
    }

    private void updateFavorite() {


        if(!isFavorite) {
            favoriteList.add(mMovie);

        }else
        {
            favoriteList.remove(mMovie);

        }

        /* use database on the next version */
        MovieUtils.setFavoriteListIntoPrefs(mPrefs,favoriteList);
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