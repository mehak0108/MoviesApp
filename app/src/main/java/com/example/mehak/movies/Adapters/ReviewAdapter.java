package com.example.mehak.movies.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.mehak.movies.Classes.MovieReview;
import com.example.mehak.movies.R;
import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends BaseAdapter {

    private Context context;
    private List<MovieReview> list;


    public ReviewAdapter(Context context, ArrayList<MovieReview> moviesReview){

        this.context = context;
        list = moviesReview;

    }

    public void add(MovieReview obj) {
        this.list.add(obj);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public MovieReview getItem(int i) {
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
            listItemView = LayoutInflater.from(context).inflate(R.layout.list_review,parent,false);
        }

        MovieReview currentMovieReview;
        currentMovieReview = getItem(position) ;

        TextView userTextView= (TextView)listItemView.findViewById(R.id.user_id);
        userTextView.setText(currentMovieReview.userId);

        TextView reviewTextView= (TextView)listItemView.findViewById(R.id.review_posted);
        reviewTextView.setText(currentMovieReview.reviewPosted);


        return listItemView;
    }
}
