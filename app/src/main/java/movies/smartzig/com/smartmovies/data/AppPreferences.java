package movies.smartzig.com.smartmovies.data;

public class AppPreferences {


    private static final String DEFAULT_SEARCH_METHOD = "top_rated";


    public static String getPreferredSearchMethod() {

        return DEFAULT_SEARCH_METHOD;
    }

}