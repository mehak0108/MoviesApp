package com.example.mehak.movies;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class OngoingFragment extends Fragment {
    private String BASE_URL = "http://api.themoviedb.org/3/discover/movie";
    ArrayList<Movie> moviesList;
    MovieAdapter adapter;
    private String title;
    private int page;

    public static OngoingFragment newInstance(int page, String title) {
        OngoingFragment var = new OngoingFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        var.setArguments(args);
        return var;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movies", moviesList);
        Log.w(TAG, "ok");
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public OngoingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(TAG, "onCreateView");
        if (savedInstanceState == null || !savedInstanceState.containsKey("moviesList")) {
            moviesList = new ArrayList<Movie>();
            adapter = new MovieAdapter(getActivity(), moviesList);
            updateMovie();
        } else {
            moviesList = savedInstanceState.getParcelableArrayList("moviesList");
            adapter = new MovieAdapter(getActivity(), moviesList);

        }

        View rootView = inflater.inflate(R.layout.movie_list, container, false);
        ListView lv = (ListView) rootView.findViewById(R.id.list);
        adapter = new MovieAdapter(getActivity(), moviesList);
        lv.setAdapter(adapter);
        return rootView;
    }

    public void updateMovie() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_by = prefs.getString(getString(R.string.pref_general_key), getString(R.string.popularity));
        moviesList.clear();
        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute(sort_by);

    }

    class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {

        Movie[] movieObj;
        int i;
        String json_str = null;
        final String SORT_BY = "sort_by";
        final String API_KEY = "api_key";
        final String PAGE = "page";
        final String DATENEW = "release_date.gte";
        final String DATEOLD = "release_date.lte";
        Uri uri;

       /* public Date dateToString() throws ParseException {
            Date date;
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            String dateInString= "2017-01-01";
            date = df.parse(dateInString);

            return date;

        }*/


        @Override
        protected Movie[] doInBackground(String... param) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String sort_by_category = param[0];
            for (i = 1; i < 4; i++) {


                uri = Uri.parse(BASE_URL).buildUpon().
                        appendQueryParameter(SORT_BY, sort_by_category + ".desc").
                        appendQueryParameter(API_KEY, getActivity().getString(R.string.api_key)).
                        appendQueryParameter(PAGE, String.valueOf(i)).

                       // appendQueryParameter(DATENEW, "2017-10-01").
                        build();
                Log.w(TAG, uri.toString());


                    /*uri = Uri.parse(BASE_URL).buildUpon().
                            appendQueryParameter(SORT_BY, sort_by_category + ".desc").
                            appendQueryParameter(API_KEY, getActivity().getString(R.string.api_key)).
                            appendQueryParameter(PAGE, String.valueOf(i)).
                            appendQueryParameter(DATEOLD, String.valueOf(2016 - 01 - 01)).
                            build();
*/

                try {
                    URL url = new URL(uri.toString());
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuilder buffer = new StringBuilder();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    json_str = buffer.toString();


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(TAG, "Error closing stream", e);
                        }
                    }
                }

                movieObj = getMoviedata(json_str);

            }
            return movieObj;
        }

        @Override
        protected void onPostExecute(Movie[] m) {
            //adapter.clear();
            for (Movie current : m)
                adapter.add(current);
            adapter.notifyDataSetChanged();


        }
    }

    String newDateString;
    Date startDate;

    public Date stringToDate(String s) {
        String startDateString = "08/01/2017";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");


        try {
            startDate = df.parse(s);
            //newDateString = df.format(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return startDate;

    }


    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    private Movie[] getMoviedata(String s) {
        final String MOVIEDB_RESULT = "results";
        final String MOVIEDB_TITLE = "title";
        final String MOVIEDB_POSTER_PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String USER_RATING = "vote_average";
        final String image_path = "backdrop_path";
        final String IMAGE_URL = "http://image.tmdb.org/t/p/w342/";
        final String ID = "id";
        final String LANG = "original_language";

        JSONObject obj = null;
        Movie[] resultList = null;
        try {

            obj = new JSONObject(s);

            JSONArray ar = obj.getJSONArray(MOVIEDB_RESULT);
            resultList = new Movie[ar.length()];
            for (int i = 0; i < ar.length(); i++) {
                JSONObject jsonobject = ar.getJSONObject(i);


                if (getYear(stringToDate(jsonobject.getString(RELEASE_DATE))) >= getYear(stringToDate("2017-08-08"))) {
                    Movie movie = new Movie();
                    movie.title = jsonobject.getString(MOVIEDB_TITLE);
                    movie.user_rating = jsonobject.getString(USER_RATING);
                    movie.language = jsonobject.getString(LANG);
                    movie.poster_path = IMAGE_URL + jsonobject.getString(MOVIEDB_POSTER_PATH);
                    resultList[i] = movie;
                    moviesList.add(movie);
                }
                else
                {
                    Log.w(TAG, "no prob");
                    continue;

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultList;

    }


}
