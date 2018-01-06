package com.example.mehak.movies;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mehak.movies.Classes.Movie;
import com.example.mehak.movies.Classes.MovieTrailer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    private final String INFO_URL = "http://api.themoviedb.org/3/movie/";
    public static final String MOVIE_DETAIL = "movieDetails";
    private String movie_id;
    ArrayList<MovieTrailer> trailers;

    private Movie movie;
    String[] trailer_key;
    String s;


    //String userId;
    //public final Viewholder viewHolder;


    FirebaseAuth auth;
    FirebaseUser user;
    private DatabaseReference mDatabase;
    ImageView sendText;
    EditText reviewPost;

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
       // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        //userId = getActivity().getIntent().getStringExtra("UserId");

        Bundle args = getArguments();
        if (savedInstanceState == null || !savedInstanceState.containsKey("Trailers")) {
            trailers = new ArrayList<>();
        } else {
            Log.v("DETAIL ACTIVITY", "bundle received.");

            trailers = savedInstanceState.getParcelableArrayList("Trailers");
        }
        if (args != null) {
            movie = args.getParcelable(MOVIE_DETAIL);
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Viewholder viewHolder = new Viewholder(rootView);
            rootView.setTag(viewHolder);
            viewHolder = (Viewholder) rootView.getTag();
            movie_id = movie.movie_id;
            // final FloatingActionButton fab=(FloatingActionButton)getActivity().findViewById(R.id.fab);

            if (savedInstanceState == null) {
                getTrailer();
            }
            viewHolder.imageView.setAdjustViewBounds(true);
            Picasso.with(getActivity()).load(movie.thumbnail).resize(780, 450).into(viewHolder.imageView);
            viewHolder.titleView.setText(movie.title);
            // if (!(movie.plot).equals(""))
            viewHolder.plotView.setText(movie.plot);
            viewHolder.rating.setRating((Float.parseFloat(movie.user_rating)) / 2);
            viewHolder.dateView.setText(movie.release_date);
            //final String s = viewHolder.reviewPost.getText().toString();
            sendText = (ImageView) rootView.findViewById(R.id.sendBtn);
            reviewPost = (EditText) rootView.findViewById(R.id.postSection);
            // final String s = reviewPost.getText().toString();
            reviewPost.setFocusable(true);



            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (trailer_key != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/watch?v=" + trailer_key[0]));
                        startActivity(intent);
                    } else {

                        Toast.makeText(getContext(), "Sorry trailer not available!", Toast.LENGTH_SHORT).show();
                    }

                }
            });


            sendText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    s = reviewPost.getText().toString();

                    if (s.length() == 0){
                        Toast.makeText(getContext(), "Please write a review", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        mDatabase.child(movie_id).child(user.getUid()).setValue(s).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Successfully posted!", Toast.LENGTH_SHORT).show();
                                    reviewPost.getText().clear();
                                }
                                else
                                {
                                    Toast.makeText(getContext(), "error",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }
            });


            return rootView;
        } else
            return null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("Trailers", trailers);
    }

    public void getTrailer() {
        FetchTrailerTask movieTask = new FetchTrailerTask();
        movieTask.execute();
    }

    public class FetchTrailerTask extends AsyncTask<String, Void, String[]> {

        String json_str = null;


        @Override
        protected String[] doInBackground(String... param) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                final String VIDEOS = "videos";
                final String API_KEY = "api_key";
                Uri uri = Uri.parse(INFO_URL + movie_id + "/" + VIDEOS).buildUpon().
                        appendQueryParameter(API_KEY, getActivity().getString(R.string.api_key))
                        .build();
                URL url = new URL(uri.toString());
                Log.v(TAG, url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
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
            } catch (IOException e) {
                Log.e(TAG, "Error blah ", e);
                return null;
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
            try {
                trailer_key = getTrailerData(json_str);
            } catch (JSONException e) {
                Log.e(TAG, "JSON Error", e);
            }
            return trailer_key;
        }


        String[] resultList;

        private String[] getTrailerData(String str) throws JSONException {
            final String MOVIEDB_RESULT = "results";
            final String MOVIEDB_KEY = "key";
            JSONObject jsonObject = new JSONObject(str);
            JSONArray movieArray = jsonObject.getJSONArray(MOVIEDB_RESULT);
            Log.v("no.", String.valueOf(movieArray.length()));
            if (movieArray.length() > 0) {
                resultList = new String[movieArray.length()];
                JSONObject movie_obj = movieArray.getJSONObject(0);
                MovieTrailer trailer = new MovieTrailer();
                trailer.key = movie_obj.getString(MOVIEDB_KEY);
                // Log.v("key", trailer.key);
                resultList[0] = trailer.key;
                // Log.v("ok", "printed");
                return resultList;
            } else {

                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] strings) {

            Log.v("nothing", "nothing");
        }
    }


}
