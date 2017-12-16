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
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class OngoingFragment extends Fragment {
    private String BASE_URL = "http://api.themoviedb.org/3/discover/movie";
    ArrayList<Movie> moviesList;
    MovieAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
            moviesList=new ArrayList<Movie>();
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

    public void updateMovie()
    {
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

        @Override
        protected Movie[] doInBackground(String... param) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String sort_by_category = param[0];
            final String SORT_BY = "sort_by";
            final String API_KEY = "api_key";
            final String PAGE = "page";

            for (i=1 ; i<=5; i++) {
                Uri uri = Uri.parse(BASE_URL).buildUpon().
                        appendQueryParameter(SORT_BY, sort_by_category + ".desc").
                        appendQueryParameter(API_KEY, getActivity().getString(R.string.api_key)).
                        appendQueryParameter(PAGE, String.valueOf(i)).
                    build();
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
            for (Movie current: m )
                adapter.add(current);
            adapter.notifyDataSetChanged();


        }
        }

        private Movie[] getMoviedata(String s){
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
                    Movie movie = new Movie();
                    movie.title = jsonobject.getString(MOVIEDB_TITLE);
                    movie.user_rating = jsonobject.getString(USER_RATING);
                    movie.language = jsonobject.getString(LANG);
                    movie.poster_path = IMAGE_URL + jsonobject.getString(MOVIEDB_POSTER_PATH);
                    resultList[i]= movie;
                    moviesList.add(movie);

                }
        } catch (JSONException e) {
                e.printStackTrace();
            }
            return resultList;

        }






}
