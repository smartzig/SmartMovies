package movies.smartzig.com.smartmovies.details;

import android.os.AsyncTask;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import movies.smartzig.com.smartmovies.utils.MovieUtils;
import movies.smartzig.com.smartmovies.utils.NetworkUtils;

public class TrailersTask extends AsyncTask<Long, Void, List<Trailers>> {

    public static String LOG_TAG = TrailersTask.class.getSimpleName();

    private final Listener mListener;

    TrailersTask(Listener mListener) {
        this.mListener = mListener;
    }

    @Override
    protected List<Trailers> doInBackground(Long... params) {

        if (params.length == 0) {
            return null;
        }
        long movieId = params[0];
        try {


            URL trailerRequestUrl = NetworkUtils.buildTrailerUrl(movieId);


            String jsonTrailerResponse = NetworkUtils
                    .getResponseFromHttpUrl(trailerRequestUrl);

            return MovieUtils
                    .getTrailersStringFromJson(jsonTrailerResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(List<Trailers> trailers) {
        if (trailers != null) {
            mListener.onLoadFinished(trailers);
        } else {
            mListener.onLoadFinished(new ArrayList<Trailers>());
        }
    }


    interface Listener {
        void onLoadFinished(List<Trailers> trailers);
    }

}
