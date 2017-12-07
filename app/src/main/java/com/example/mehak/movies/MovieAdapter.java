package com.example.mehak.movies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MovieAdapter extends ArrayAdapter<Movie> {

    public MovieAdapter(Context context, ArrayList<Movie> movies){

        super(context,0,movies);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null)
        {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
        }

        Movie currentMovie= getItem(position);

        TextView nameTextView= (TextView)listItemView.findViewById(R.id.movieName);
        nameTextView.setText(currentMovie.getMovie_name());

        TextView genreTextView= (TextView)listItemView.findViewById(R.id.movieGenre);
        genreTextView.setText(currentMovie.getGenre());

        TextView yearTextView= (TextView)listItemView.findViewById(R.id.movieYear);
        yearTextView.setText(currentMovie.getYear_of_release());

        TextView langTextView= (TextView)listItemView.findViewById(R.id.movieLanguage);
        langTextView.setText(currentMovie.getLanguage());

        ImageView imageView = (ImageView) listItemView.findViewById(R.id.image);
        if (currentMovie.checkImage()) {
            imageView.setImageResource(currentMovie.getImage_id());
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.INVISIBLE);
        }





        return listItemView;
    }
}
