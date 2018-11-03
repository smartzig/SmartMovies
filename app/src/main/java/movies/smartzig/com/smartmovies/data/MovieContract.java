package movies.smartzig.com.smartmovies.data;

import android.database.Cursor;
import android.provider.BaseColumns;

import movies.smartzig.com.smartmovies.utils.MovieItem;

public class MovieContract {

    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "MOVIE";


        public static final String COLUMN_ID =  "ID_MOVIE";
        public static final String COLUMN_VOTE_AVERAGE =  "NB_VOTE_AVERAGE";
        public static final String COLUMN_ORIGINAL_TITLE =  "DS_ORIGINAL_TITLE";
        public static final String COLUMN_BACKDROP_PATH =  "DS_BACKDROP_PATH";
        public static final String COLUMN_OVERVIEW =  "DS_OVERVIEW";
        public static final String COLUMN_RELEASE_DATE =  "DT_RELEASE";
        public static final String COLUMN_POSTER_PATH =  "DS_POSTER_PATH";
        public static final String COLUMN_CREATION_DATE =  "CREATION_DATE";

    }
    public static MovieItem cursorToEntity(Cursor cursor) {

        return new MovieItem(
                cursor.getLong(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_BACKDROP_PATH)),
                cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_ORIGINAL_TITLE)),
                cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_VOTE_AVERAGE)),
                cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_OVERVIEW)),
                cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_POSTER_PATH)),
                cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_RELEASE_DATE))
        );

    }
}
