package com.example.mehak.movies;


import android.app.Activity;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mehak.movies.Adapters.MovieAdapter;
import com.example.mehak.movies.Classes.Movie;

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
public class SearchFragment extends Fragment {
    private String BASE_URL = "http://api.themoviedb.org/3/search/movie";
    public static final String MOVIE_DETAIL = "movieDetails";
    ArrayList<Movie> moviesList;
    MovieAdapter adapter;
    Movie movie;
    SearchView searchView;
    private ProgressDialog mSearchProgress;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movies", moviesList);
        Log.e("changed", "changed");
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(TAG, "onCreateView");

        mSearchProgress = new ProgressDialog(getContext());

        Bundle args = getArguments();
        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            moviesList = new ArrayList<Movie>();
            adapter = new MovieAdapter(getActivity(), moviesList);

        } else {
            moviesList = savedInstanceState.getParcelableArrayList("movies");
            adapter = new MovieAdapter(getActivity(), moviesList);

        }


        if (args != null) {
            movie = args.getParcelable(MOVIE_DETAIL);
            View rootView = inflater.inflate(R.layout.search_movie_list, container, false);
            ListView lv = (ListView) rootView.findViewById(R.id.searchList);
            adapter = new MovieAdapter(getActivity(), moviesList);
            lv.setAdapter(adapter);

            searchView = (SearchView) rootView.findViewById(R.id.search_view);
            searchView.setQueryHint("Enter the movie name to search");

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    if (isNetworkAvailable()) {
                        moviesList.clear();
                        mSearchProgress.setTitle("Searching");
                        mSearchProgress.setMessage("Please wait!!");
                        mSearchProgress.setCanceledOnTouchOutside(false);
                        mSearchProgress.show();
                        FetchMovieTask movieTask = new FetchMovieTask();
                        movieTask.execute(s);
                    }
                    else {
                        Toast.makeText(getContext(), "No Connection!\nCheck your Internet Connection", Toast.LENGTH_LONG).show();
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isNetworkAvailable()) {
                    ((ItemSelected) getActivity()).onItemSelected(moviesList.get(position));
                }
                else {
                    Toast.makeText(getContext(), "No Connection!\nCheck your Internet Connection",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

            return rootView;
        } else
            return null;

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(getContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {

        Movie[] movieObj;
        int i;
        String json_str = null;
        final String PAGE = "page";
        final String QUERY = "query";
        Uri uri;

        @Override
        protected Movie[] doInBackground(String... param) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            final String API_KEY = "api_key";
            String searchQuery = param[0];
            for (i = 1; i < 15; i++) {

                uri = Uri.parse(BASE_URL).buildUpon().
                        appendQueryParameter(API_KEY, getActivity().getString(R.string.api_key)).
                        appendQueryParameter(PAGE, String.valueOf(i)).
                        appendQueryParameter(QUERY, searchQuery).
                        build();
                Log.w(TAG, uri.toString());

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
            mSearchProgress.dismiss();
            for (Movie current : m)
                adapter.add(current);
            adapter.notifyDataSetChanged();
        }
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
                movie = new Movie();
                movie.title = jsonobject.getString(MOVIEDB_TITLE);
                movie.user_rating = jsonobject.getString(USER_RATING);
                movie.language = jsonobject.getString(LANG);
                movie.poster_path = IMAGE_URL + jsonobject.getString(MOVIEDB_POSTER_PATH);
                movie.plot = jsonobject.getString(OVERVIEW);
                if (!jsonobject.getString(image_path).endsWith(".jpg")) {
                    movie.thumbnail = movie.poster_path;
                } else
                    movie.thumbnail = (IMAGE_URL + jsonobject.getString(image_path));
                movie.release_date = jsonobject.getString(RELEASE_DATE);
                movie.movie_id = jsonobject.getString(ID);
                resultList[i] = movie;
                moviesList.add(movie);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultList;

    }

}
