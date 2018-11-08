package movies.smartzig.com.smartmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import movies.smartzig.com.smartmovies.utils.MovieItem;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movie.db";

    private static final int DATABASE_VERSION = 2;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (");
        sql.append(MovieContract.MovieEntry.COLUMN_ID + " INTEGER PRIMARY KEY UNIQUE , ");
        sql.append(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, ");
        sql.append(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, ");
        sql.append(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, ");
        sql.append(MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, ");
        sql.append(MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, ");
        sql.append(MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, ");
        sql.append(MovieContract.MovieEntry.COLUMN_CREATION_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP ");
        sql.append(" ); ");

        sqLiteDatabase.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data is not deleted.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public List<MovieItem> fetchAllMovies(SQLiteDatabase mDb) {
        List<MovieItem> movieList = new ArrayList<>();


        Cursor cursor = mDb.query(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                MovieContract.MovieEntry.COLUMN_ID
        );
        if (cursor != null) {

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                MovieItem movie = MovieContract.cursorToEntity(cursor);

                movieList.add(movie);

                cursor.moveToNext();
            }
            cursor.close();
        }
        return movieList;
    }


    public void insertMovie(SQLiteDatabase mDb, MovieItem movie) {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_ID, movie.getId());
        cv.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
        cv.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
        cv.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        cv.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());


        mDb.insert(MovieContract.MovieEntry.TABLE_NAME, null, cv);
    }

    public void deleteMovie(SQLiteDatabase mDb, long movieId) {
        mDb.delete(MovieContract.MovieEntry.TABLE_NAME, MovieContract.MovieEntry.COLUMN_ID + "=" + movieId, null);
    }
}
