package movies.smartzig.com.smartmovies.utils;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;

import movies.smartzig.com.smartmovies.details.Reviews;
import movies.smartzig.com.smartmovies.details.Trailers;

public class MovieUtils {

    /*private static final String PREF_FAVORITES = "PREF_FAVORITES";
    private static final String KEY = "MovieUtils";*/

    public static List<MovieItem> getMoviesStringFromJson(String movieJsonStr)
            throws JSONException {



        /* Movie information. Each one is an element of the "list" array */
        final String MOVIE_LIST = "results";

        final String RESPONSE_CODE = "cod";


        JSONObject forecastJson = new JSONObject(movieJsonStr);

        /* Is there an error? */
        if (forecastJson.has(RESPONSE_CODE)) {
            int errorCode = forecastJson.getInt(RESPONSE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        List<MovieItem> movies;
        Gson gson = new Gson();
        movies = Arrays.asList(gson.fromJson(forecastJson.getJSONArray(MOVIE_LIST).toString(),
                MovieItem[].class));


        return movies;
    }

    public static List<Trailers> getTrailersStringFromJson(String trailerJsonStr)
            throws JSONException {

        /* Movie information. Each one is an element of the "list" array */
        final String TRAILER_LIST = "results";
        final String RESPONSE_CODE = "cod";


        JSONObject trailerJson = new JSONObject(trailerJsonStr);

        /* Is there an error? */
        if (trailerJson.has(RESPONSE_CODE)) {
            int errorCode = trailerJson.getInt(RESPONSE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        List<Trailers> trailers;
        Gson gson = new Gson();
        trailers = Arrays.asList(gson.fromJson(trailerJson.getJSONArray(TRAILER_LIST).toString(),
                Trailers[].class));


        return trailers;
    }


    public static List<Reviews> getReviewsStringFromJson(String jsonReviewResponse)
            throws JSONException {

        final String RESPONSE_CODE = "cod";


        JSONObject reviewJson = new JSONObject(jsonReviewResponse);

        /* Movie information. Each one is an element of the "list" array */
        final String REVIEW_LIST = "results";


        /* Is there an error? */
        if (reviewJson.has(RESPONSE_CODE)) {
            int errorCode = reviewJson.getInt(RESPONSE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        List<Reviews> reviews;
        Gson gson = new Gson();
        reviews = Arrays.asList(gson.fromJson(reviewJson.getJSONArray(REVIEW_LIST).toString(),
                Reviews[].class));


        return reviews;
    }


    /*public static  ArrayList<MovieItem> getFavoriteListFromPrefs(SharedPreferences mPrefs) {
        ArrayList<MovieItem> favoriteList = new ArrayList<>();
        try{
            Gson gson = new Gson();
            String json = mPrefs.getString(PREF_FAVORITES, "");
            if(json !=null && !json.equals("")) {
                favoriteList = gson.fromJson(json, new TypeToken<List<MovieItem>>(){}.getType());

            }else{
                Log.i(KEY, "There's no Item saved in Prefs, returning empty.");
                favoriteList = new ArrayList<>();
            }

        }catch (Exception e){
            Log.e(KEY, "Error on get Person from Prefs");
        }
        return favoriteList;
    }


    public static void setFavoriteListIntoPrefs(SharedPreferences mPrefs, ArrayList<MovieItem> favorites) {
        Gson gson = new Gson();
        String json = gson.toJson(favorites);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(PREF_FAVORITES, json);
        editor.apply();
        Log.e(KEY, "Favorite List Saved");
        }*/

}