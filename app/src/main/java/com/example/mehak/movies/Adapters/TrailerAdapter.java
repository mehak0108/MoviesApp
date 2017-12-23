package com.example.mehak.movies.Adapters;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.mehak.movies.Classes.MovieTrailer;

import java.util.List;

public class TrailerAdapter extends BaseAdapter {

    private Context mContext;
    private int item_layout_id;
    private  int textView_id;
    private List<MovieTrailer> keys;

    private Context context;

    public TrailerAdapter(Context context, List<TrailerAdapter> obj){



    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
