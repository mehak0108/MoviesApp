package com.example.mehak.movies;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mehak.movies.Adapters.MovieAdapter;
import com.example.mehak.movies.Classes.Movie;
import com.example.mehak.movies.data.MovieDbHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    Movie movie;
    FloatingActionButton mFabSearch;

    /*public static OngoingFragment newInstance(int page, String title) {
        OngoingFragment var = new OngoingFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        var.setArguments(args);
        return var;
    }*/

    /*public interface Callback {
        public void onItemSelected(Movie movie);
    }*/

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

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

        //fabSearch
        mFabSearch = (FloatingActionButton) rootView.findViewById(R.id.fabSearch);
        mFabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()){
                    Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
                    startActivity(searchIntent);
                }
                else {
                    Toast.makeText(getContext(), "No Connection!\nCheck your Internet Connection", Toast.LENGTH_LONG).show();
                }
            }

        });
        return rootView;
    }

    public void fetchFavorites() {

        MovieDbHelper db = new MovieDbHelper(getContext());
        ArrayList<Movie> movieDetails = db.getAllMovies();
        int n=movieDetails.size();
        if (n > 0) {
            moviesList = movieDetails;
            Movie movies[]=new Movie[n];
            for(int i=0;i<n;i++) {
                movies[i] = moviesList.get(i);
                Log.e("Name", movies[i].title);
                adapter.add(movies[i]);
            }

            adapter.notifyDataSetChanged();
        }
        else {
            Toast.makeText(getActivity(), "No Favourites Added!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(getContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void onPreferenceChanged(String sort_by) {
        Log.v(TAG, "PREFERENCE CHANGED");
        moviesList.clear();

        if (sort_by.equals(getString(R.string.favorites))) {

            fetchFavorites();
        } else {
            if (isNetworkAvailable()) {

                FetchMovieTask movieTask = new FetchMovieTask();
                movieTask.execute(sort_by);
            } else
                Toast.makeText(getContext(), "No Connection!\nCheck your Internet Connection",
                        Toast.LENGTH_LONG).show();

        }
    }

    public void updateMovie() {
        if (isNetworkAvailable()) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sort_by = prefs.getString(getString(R.string.pref_general_key), getString(R.string.popularity));
            moviesList.clear();

            if(sort_by.equals(getString(R.string.favorites))){
                fetchFavorites();
            }
            else{
                FetchMovieTask movieTask = new FetchMovieTask();
                movieTask.execute(sort_by);
            }
        }
        else {
            Toast.makeText(getContext(), "No Connection!\nCheck your Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {

        Movie[] movieObj;
        int i;
        String json_str = null;
        final String PAGE = "page";
        final String RELEASE_YEAR = "primary_release_year";
        Uri uri;

        @Override
        protected Movie[] doInBackground(String... param) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            final String SORT_BY = "sort_by";
            final String API_KEY = "api_key";
            String sort_by_category = param[0];
            for (i = 1; i < 4; i++) {

                uri = Uri.parse(BASE_URL).buildUpon().
                        appendQueryParameter(SORT_BY, sort_by_category + ".desc").
                        appendQueryParameter(API_KEY, getActivity().getString(R.string.api_key)).
                        appendQueryParameter(PAGE, String.valueOf(i)).
                        appendQueryParameter(RELEASE_YEAR, String.valueOf(2018)).
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

            for (Movie current : m)
                adapter.add(current);
            adapter.notifyDataSetChanged();


        }
    }

    /*String newDateString;
    static Date startDate;
    static Calendar cal;

    public static Date stringToDate(String s) {
        //  String startDateString = "08/01/2017";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");


        try {
            startDate = df.parse(s);
            Log.w(TAG, startDate.toString());
            Log.w(TAG, df.format(startDate));

            //newDateString = df.format(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return startDate;

    }


    public static int getYear(Date date) {
        cal = Calendar.getInstance();
        cal.setTime(date);
        Log.e(TAG, String.valueOf(cal.get(Calendar.YEAR)));
        return cal.get(Calendar.YEAR);
    }*/

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
