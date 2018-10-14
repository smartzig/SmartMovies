package movies.smartzig.com.smartmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import movies.smartzig.com.smartmovies.utils.MovieItem;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private static final float POSTER_ASPECT_RATIO = 1.5f;

    /** The context we use to utility methods, app resources and layout inflaters */
   // private final Context mContext;

    private List<MovieItem> mMovieData;
    /**
     * Interface to handle clicks on items within this Adapter.
     */
    final private MovieAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface MovieAdapterOnClickHandler {
        //TODO CHANGE IT
        void onClick(MovieItem movie);
    }

    /**
     * Creates the Adapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * Cache of the children views for a movie list item.
     */
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView mMoviePosterImageView;

        MovieAdapterViewHolder(View view) {
            super(view);


            mMoviePosterImageView = view.findViewById(R.id.iv_movie_poster);
            view.setOnClickListener(this);

        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieItem movie = mMovieData.get(adapterPosition);
            mClickHandler.onClick(movie);
        }


    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new ForecastAdapterViewHolder that holds the View for each list item
     */
    @NonNull
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);


        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);


        int gridColsNumber = context.getResources()
                .getInteger(R.integer.number_of_grid_columns);

        view.getLayoutParams().height = (int) (viewGroup.getWidth() / gridColsNumber *
                POSTER_ASPECT_RATIO);

        return new MovieAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MovieAdapterViewHolder movieAdapterViewHolder, int position) {


        final MovieItem movieBinded = mMovieData.get(position);

    //    movieAdapterViewHolder.mMovieTittleTextView.setText(movieBinded.getTitle());
      //  movieAdapterViewHolder.mReleaseDateTextView.setText(movieBinded.getReleaseDate());
       // movieAdapterViewHolder.mVoteAverageTextView.setText(movieBinded.getVoteAverage().toString());

        String posterUrl = movieBinded.getPosterPath();

        // Warning: onError() will not be called, if url is null.
        // Empty url leads to app crash.
        if (posterUrl == null) {
            Picasso.get().load(R.drawable.image_placeholder).into(movieAdapterViewHolder.mMoviePosterImageView);

        }else{
            Picasso.get()
                    .load(posterUrl)
                    .into(movieAdapterViewHolder.mMoviePosterImageView);
        }





    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {
        if (null == mMovieData) return 0;
        return mMovieData.size();
    }


    /**
     * This method is used to set the data on the Adapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new Adapter to display it.
     *
     * @param movieData The new weather data to be displayed.
     */
    void setMovieData(List<MovieItem> movieData) {
        mMovieData = movieData;
        notifyDataSetChanged();
    }
}
