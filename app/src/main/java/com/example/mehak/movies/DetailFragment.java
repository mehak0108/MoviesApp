package com.example.mehak.movies;


import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mehak.movies.Adapters.ReviewAdapter;
import com.example.mehak.movies.Classes.Movie;
import com.example.mehak.movies.Classes.MovieReview;
import com.example.mehak.movies.Classes.MovieTrailer;
import com.example.mehak.movies.Classes.User;
import com.example.mehak.movies.data.MovieContract.MovieEntry;
import com.example.mehak.movies.data.MovieDbHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    private final String INFO_URL = "http://api.themoviedb.org/3/movie/";
    public static final String MOVIE_DETAIL = "movieDetails";
    private String movie_id;
    View rootView;
    ArrayList<MovieTrailer> trailers;
    ArrayList<MovieReview> reviews;
    ReviewAdapter mReviewAdapter;
    MovieReview movieReview;
    User mUser;
    int check = 0;


    private Movie movie;
    String[] trailer_key;
    String s;

    Viewholder viewHolder;

    FirebaseAuth auth;
    FirebaseUser user;
    private DatabaseReference mDatabase;
    ImageView sendText;
    EditText reviewPost;

    public DetailFragment() {

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if(!DashboardActivity.mtwoPane)
            setHasOptionsMenu(true);*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.w("userId", user.getUid());

        checkUid();

        Bundle args = getArguments();
        if (savedInstanceState == null || !savedInstanceState.containsKey("Trailers") || !savedInstanceState.containsKey("Reviews")) {
            trailers = new ArrayList<>();
            reviews = new ArrayList<>();
        } else {
            Log.v("DETAIL ACTIVITY", "bundle received.");

            trailers = savedInstanceState.getParcelableArrayList("Trailers");
            reviews = savedInstanceState.getParcelableArrayList("Reviews");
        }
        if (args != null) {
            movie = args.getParcelable(MOVIE_DETAIL);
            rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            viewHolder = new Viewholder(rootView);
            rootView.setTag(viewHolder);
            viewHolder = (Viewholder) rootView.getTag();
            movie_id = movie.movie_id;
            final FloatingActionButton fab=(FloatingActionButton)getActivity().findViewById(R.id.fab);

            if (savedInstanceState == null) {
                getTrailer();
                getReview();
            }
            viewHolder.imageView.setAdjustViewBounds(true);
            Picasso.with(getActivity()).load(movie.thumbnail).resize(780, 450).into(viewHolder.imageView);
            viewHolder.titleView.setText(movie.title);

            if (!(movie.plot).equals(""))
                viewHolder.plotView.setText(movie.plot);
            else
                viewHolder.plotView.setText("Plot not avaliable!");

            viewHolder.rating.setRating((Float.parseFloat(movie.user_rating)) / 2);
            viewHolder.dateView.setText(movie.release_date);

            mReviewAdapter = new ReviewAdapter(getActivity(), reviews);
            viewHolder.review_list.setAdapter(mReviewAdapter);
            mReviewAdapter.notifyDataSetChanged();


            sendText = (ImageView) rootView.findViewById(R.id.sendBtn);
            reviewPost = (EditText) rootView.findViewById(R.id.postSection);
            reviewPost.setFocusable(true);

            viewHolder.review_list.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

            reviewPost.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

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
                    if (s.length() == 0) {
                        Toast.makeText(getContext(), "Please write a review", Toast.LENGTH_SHORT).show();
                    } else {
                        if (check == 1) {

                            mDatabase.child("Movies").child(movie_id).child(mUser.userName).setValue(s).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Successfully posted!", Toast.LENGTH_SHORT).show();
                                        reviewPost.getText().clear();

                                    } else {
                                        Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            mDatabase.child("Movies").child(movie_id).child(user.getDisplayName()).setValue(s).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Successfully posted!", Toast.LENGTH_SHORT).show();
                                        reviewPost.getText().clear();

                                    } else {
                                        Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                }
            });

            //favourites
            MovieDbHelper dbHelper = new MovieDbHelper(getContext());
            if (dbHelper.hasObject(movie_id)) {
                fab.setImageResource(R.drawable.fav_col);
            }
            else{
                Log.e("work", "12345");
                //fab.setImageResource(R.drawable.fav_white);
            }
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MovieDbHelper dbHelper = new MovieDbHelper(getContext());
                    if (!dbHelper.hasObject(movie_id)) {
                        saveToDatabase();
                        Log.e("fav", movie_id);
                        Toast.makeText(getContext(), "Added to Favorite!", Toast.LENGTH_SHORT).show();
                        fab.setImageResource(R.drawable.fav_col);
                    } else {
                        removeDatabase();
                        Toast.makeText(getContext(), "Removed from Favorite!", Toast.LENGTH_SHORT).show();
                        fab.setImageResource(R.drawable.fav_white);
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
        outState.putParcelableArrayList("Reviews", reviews);
        outState.putParcelableArrayList("Trailers", trailers);
    }

    // Trailers
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

            if (movieArray.length() > 0) {
                resultList = new String[movieArray.length()];
                JSONObject movie_obj = movieArray.getJSONObject(0);
                MovieTrailer trailer = new MovieTrailer();
                trailer.key = movie_obj.getString(MOVIEDB_KEY);
                // Log.v("key", trailer.key);
                resultList[0] = trailer.key;
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

    //checkUid
    public void checkUid(){
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String child = ds.getKey();
                    Log.w("child", child);
                    if (child.equals(user.getUid())){
                        getUser();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //get User
    public void getUser(){

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(user.getUid()).child("userName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = new User();
                mUser.userName = dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        check = 1;

    }

    //Reviews
    public void getReview() {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Movies").child(movie_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.v("Review", "2");
                reviews.clear();

                for (DataSnapshot q : dataSnapshot.getChildren()) {
                   // Log.v("ok", q.getKey());
                    movieReview = new MovieReview();
                    movieReview.userId = q.getKey();
                    movieReview.reviewPosted = q.getValue(String.class);
                  //  Log.v("hmm1111", q.getValue(String.class));
                    reviews.add(movieReview);
                }

                if (reviews.size() > 0) {

                    mReviewAdapter.notifyDataSetChanged();

                } /*else {
                    Toast.makeText(getActivity(), "No reviews till now!", Toast.LENGTH_SHORT).show();
                }*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //favorites
    private void saveToDatabase(){
        DatabaseTask movieTask=new DatabaseTask();
        movieTask.execute("save");
    }
    private void removeDatabase(){
        DatabaseTask movieTask=new DatabaseTask();
        movieTask.execute("remove");
       // if(DashboardActivity.mtwoPane && DashboardActivity.mSortBy.equals(R.string.favorites))
        if(DashboardActivity.mSortBy.equals(R.string.favorites))
        {
            OngoingFragment mf = (OngoingFragment) getActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.movies_fragment);
            mf.fetchFavorites();
            RetroFragment rf = (RetroFragment) getActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.movies_fragment);
            rf.fetchFavorites();
        }
    }

    class DatabaseTask extends AsyncTask<String, Void, Boolean> {
        final MovieDbHelper db = new MovieDbHelper(getContext());
        final SQLiteDatabase database = db.getWritableDatabase();

        @Override
        protected Boolean doInBackground(String... params) {
            if (params[0].equals("save")) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MovieEntry.COLUMN_MID, movie_id);
                contentValues.put(MovieEntry.COLUMN_TITLE, movie.title);
                contentValues.put(MovieEntry.COLUMN_POSTER, movie.poster_path);
                contentValues.put(MovieEntry.COLUMN_DATE, movie.release_date);
                contentValues.put(MovieEntry.COLUMN_OVERVIEW, movie.plot);
                contentValues.put(MovieEntry.COLUMN_RATING, movie.user_rating);
                contentValues.put(MovieEntry.COLUMN_BACKDROP, movie.thumbnail);
                long row_id = database.insert(MovieEntry.TABLE_NAME, null, contentValues);
                db.close();
                return row_id != -1;
            } else if (params[0].equals("remove")) {
                database.delete(MovieEntry.TABLE_NAME, MovieEntry.COLUMN_MID + "=?", new String[]{movie_id});
                db.close();
                return null;
            }
            return null;
        }
    }
}
