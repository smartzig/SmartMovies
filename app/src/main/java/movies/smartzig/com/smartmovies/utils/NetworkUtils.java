package movies.smartzig.com.smartmovies.utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();


    private static final String DYNAMIC_MOVIE_DB_URL =
            "https://api.themoviedb.org/3/movie/";

    private static final String BASE_URL = DYNAMIC_MOVIE_DB_URL;

    /*
     * NOTE: These values only effect responses from OpenWeatherMap, NOT from the fake weather
     * server. They are simply here to allow us to teach you how to build a URL if you were to use
     * a real API.If you want to connect your app to OpenWeatherMap's API, feel free to! However,
     * we are not going to show you how to do so in this course.
     */

    /* The language used to bring the results */
    private static final String language = "language";
    /* The api key that we use to authenticate the app */
    private static final String apiKey = "INVALID KEY";
    /* The number of pages we want our API to return */
    private static final int pages = 1;

    private final static String API_PARAM = "api_key";
    private final static String LANGUAGE_PARAM = "language";
    private  final static String PAGE_PARAM = "page";


    /**
     * Builds the URL used to talk to the data server
     *
     * @param searchMethodQuery The Search Method that will be queried for. Eg. top_rated
     * @return The URL to use to query the Movie Db server.
     */
    public static URL buildUrl(String searchMethodQuery) {
        Uri builtUri = Uri.parse(BASE_URL+searchMethodQuery).buildUpon()
                .appendQueryParameter(API_PARAM, apiKey)
                .appendQueryParameter(LANGUAGE_PARAM, language)
                .appendQueryParameter(PAGE_PARAM, Integer.toString(pages))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
