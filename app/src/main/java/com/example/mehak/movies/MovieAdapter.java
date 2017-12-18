package com.example.mehak.movies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MovieAdapter extends BaseAdapter {
    private Context context;
    private List<Movie> list;

    public MovieAdapter(Context context, ArrayList<Movie> movies){
        
        this.context = context;
        list = movies;

    }


    public void add(Movie obj) {
        this.list.add(obj);
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Movie getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position,  View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null)
        {
            listItemView = LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
        }

        Movie currentMovie;
        currentMovie= getItem(position);

        TextView nameTextView= (TextView)listItemView.findViewById(R.id.movieName);
        nameTextView.setText(currentMovie.title);

        TextView genreTextView= (TextView)listItemView.findViewById(R.id.movieRating);
        genreTextView.setText("User Rating: " + currentMovie.user_rating);

        TextView langTextView= (TextView)listItemView.findViewById(R.id.movieLanguage);
        langTextView.setText("Language: " + currentMovie.language);

        ImageView imageView = (ImageView) listItemView.findViewById(R.id.image);
        Picasso.with(context).load(list.get(position).poster_path).into(imageView);

        return listItemView;
    }

}
