package movies.smartzig.com.smartmovies.details;

import android.os.AsyncTask;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import movies.smartzig.com.smartmovies.utils.MovieUtils;
import movies.smartzig.com.smartmovies.utils.NetworkUtils;

public class ReviewsTask extends AsyncTask<Long, Void, List<Reviews>> {

    private final Listener mListener;

    ReviewsTask(Listener listener) {
        mListener = listener;
    }

    @Override
    protected List<Reviews> doInBackground(Long... params) {

        if (params.length == 0) {
            return null;
        }
        long movieId = params[0];

        try {


            URL reviewRequestUrl = NetworkUtils.buildReviewUrl(movieId);

            String jsonReviewResponse = NetworkUtils
                    .getResponseFromHttpUrl(reviewRequestUrl);

            return MovieUtils
                    .getReviewsStringFromJson(jsonReviewResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Reviews> reviews) {
        if (reviews != null) {
            mListener.on_reviews_loaded(reviews);
        } else {
            mListener.on_reviews_loaded(new ArrayList<Reviews>());
        }
    }

    /**
     * Interface definition for a callback to be invoked when reviews are loaded.
     */
    interface Listener {
        void on_reviews_loaded(List<Reviews> reviews);
    }
}