package com.example.mehak.movies;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mehak.movies.Classes.Movie;
import com.example.mehak.movies.Classes.MovieTrailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    private final String INFO_URL = "http://api.themoviedb.org/3/movie/";
    public static final String MOVIE_DETAIL = "movieDetails";
    private String movie_id;
    ArrayList<MovieTrailer> trailers;
    private Movie movie;

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args!=null){
            movie = args.getParcelable(MOVIE_DETAIL);
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Viewholder viewHolder = new Viewholder(rootView);
            rootView.setTag(viewHolder);
            viewHolder = (Viewholder) rootView.getTag();
            movie_id = movie.movie_id;
            final FloatingActionButton fab=(FloatingActionButton)getActivity().findViewById(R.id.fab);
            /*viewHolder.review_list.setAdapter(mReviewAdapter);
            mTrailerAdapter = new CustomAdapter(getActivity(), trailer_keys);
            viewHolder.trailer_list.setAdapter(mTrailerAdapter);
            movie_id = movie.movie_id;
            if (savedInstanceState == null) {
                getReview();
                getTrailer();
            }*/
            viewHolder.imageView.setAdjustViewBounds(true);
            Picasso.with(getActivity()).load(movie.thumbnail).resize(780, 450).into(viewHolder.imageView);
            viewHolder.titleView.setText(movie.title);
           // if (!(movie.plot).equals(""))
                viewHolder.plotView.setText(movie.plot);
            viewHolder.rating.setRating((Float.parseFloat(movie.user_rating)) / 2);
            viewHolder.dateView.setText(movie.release_date);
            /*viewHolder.trailer_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent trailer_intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/watch?v=" + trailer_keys.get(position)));
                    startActivity(trailer_intent);
                }
            });*/

            return rootView;
        }
        else
            return null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("Trailers", trailers);
    }
}
