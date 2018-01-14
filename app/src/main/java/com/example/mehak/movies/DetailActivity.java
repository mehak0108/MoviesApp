package com.example.mehak.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class DetailActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initToolbar();

        if (savedInstanceState == null){
            Bundle args = new Bundle();
            if (getIntent().getParcelableExtra("MOVIE")== null)
                Log.v("not ok", "intent is null");
            else
                Log.v("ok", "intent is not null");
            args.putParcelable(DetailFragment.MOVIE_DETAIL,(getIntent().getParcelableExtra("MOVIE")));
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.details_container, fragment).commit();

        }

    }

    private void initToolbar() {

        Log.e("working tool", "ok");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar_detail);
        if (toolbar != null) {


            Log.e("working tool", "ok");
            setSupportActionBar(toolbar);
        }
    }


}
