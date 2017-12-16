package com.example.mehak.movies.data;

import android.content.Context;

import com.example.mehak.movies.Movie;
import com.example.mehak.movies.data.MovieContract.MovieEntry;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by mehak on 15/12/17.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context){
        super(context,DATABASE_NAME,null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE= "CREATE TABLE if not exists " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " integer PRIMARY KEY," +
                MovieEntry.COLUMN_MID + "," +
                MovieEntry.COLUMN_TITLE + "," +
                MovieEntry.COLUMN_POSTER + "," +
                MovieEntry.COLUMN_DATE + "," +
                MovieEntry.COLUMN_OVERVIEW + "," +
                MovieEntry.COLUMN_RATING + "," +
                MovieEntry.COLUMN_LANGUAGE + "," +
                MovieEntry.COLUMN_BACKDROP +""+
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public ArrayList<Movie> getAllMovies(){
        ArrayList<Movie> movieList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MovieEntry.TABLE_NAME,null,
                null,null,null,null,null,null);

        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie();
                movie.poster_path=cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER));
                movie.movie_id=cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MID));
                movie.title=cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_TITLE));
                movie.thumbnail=cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_BACKDROP));
                movie.plot=cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW));
                movie.user_rating=cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_RATING));
                movie.release_date=cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_DATE));
                movie.language = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_LANGUAGE));
                movieList.add(movie);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return movieList;
    }

    public boolean hasObject(String m_id){
        SQLiteDatabase db=this.getWritableDatabase();
        final String selectString= "SELECT * FROM "+MovieEntry.TABLE_NAME+" WHERE "+
                MovieEntry.COLUMN_MID+"=?";
        Cursor cursor = db.rawQuery(selectString,new String[] {m_id});
        boolean hasObject=false;
        if(cursor.moveToFirst())
            hasObject = true;
        cursor.close();
        db.close();
        return hasObject;
    }
}
