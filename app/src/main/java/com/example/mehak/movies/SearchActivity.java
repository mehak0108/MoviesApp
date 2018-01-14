package com.example.mehak.movies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.mehak.movies.Classes.Movie;

public class SearchActivity extends AppCompatActivity implements ItemSelected {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initToolbar();

        if (savedInstanceState == null){
            Bundle args = new Bundle();
            if (getIntent().getParcelableExtra("MOVIE")== null)
                Log.v("not ok", "intent is null");
            else
                Log.v("ok", "intent is not null");
            args.putParcelable(SearchFragment.MOVIE_DETAIL,(getIntent().getParcelableExtra("MOVIE")));
            SearchFragment fragment = new SearchFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.search_container, fragment).commit();
        }
    }

    @Override
    public void onItemSelected(Movie movie) {

        /*if(mtwoPane){
            //In two pane mode, show the detail view in this activity by
            //adding or replacing the detail fragment using fragment transaction
            Bundle args=new Bundle();
            args.putParcelable(DetailFragment.MOVIE_DETAIL,movie);
            DetailFragment fragment=new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.details_container,fragment,DETAILFRAGMENT_TAG)
                    .commit();
        }else {*/

        Intent intent = new Intent(this,DetailActivity.class);
        intent.putExtra("MOVIE", movie);
        startActivity(intent);
        //}
    }

    private void initToolbar() {

        Log.e("working tool", "ok");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        if (toolbar != null) {
            Log.e("working tool", "ok");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

}
