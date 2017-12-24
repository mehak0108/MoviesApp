package com.example.mehak.movies;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class Viewholder {

   // public final ListView review_list ;
    public final ImageView trailer_list ;
    public final ImageView imageView ;
    public final TextView plotView;
    public final TextView titleView;
    public final RatingBar rating;
    public final TextView dateView;


    public Viewholder(View rootView){
        //review_list=(ListView)rootView.findViewById(R.id.reviews_list);
        trailer_list=(ImageView) rootView.findViewById(R.id.play);
        imageView = (ImageView) rootView.findViewById(R.id.thumbnail);
        titleView=(TextView) rootView.findViewById(R.id.title_textview);
        plotView=(TextView) rootView.findViewById(R.id.plot_textview);
        rating=(RatingBar) rootView.findViewById(R.id.ratingbar);
        dateView=(TextView) rootView.findViewById(R.id.r_date_textview);

    }
}
