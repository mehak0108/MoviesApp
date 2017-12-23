package com.example.mehak.movies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.example.mehak.movies.Classes.Movie;
//import com.example.mehak.movies.Settings.SettingsActivity;


public class DashboardActivity extends AppCompatActivity implements OngoingFragment.Callback{

    public static String mSortBy;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(true, new AccordionTransformer());

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);


        /*TextView ongoing= (TextView)findViewById(R.id.ongoing);
        ongoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this,OngoingActivity.class);
                startActivity(intent);
            }
        });

        TextView retro = (TextView)findViewById(R.id.retro);
        retro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this,RetroActivity.class);
                startActivity(intent);
            }
        });*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)  {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String sort_by = prefs.getString(getString(R.string.pref_general_key), getString(R.string.popularity));
        if (mSortBy!=null && !sort_by.equals(mSortBy)){
            OngoingFragment mf=(OngoingFragment) getSupportFragmentManager().
                    findFragmentById(R.id.movies_fragment);
            if(mf!=null)
                mf.onPreferenceChanged(sort_by);
        }
        else
            mSortBy= sort_by;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemThatWasSelected = item.getItemId();
        if(menuItemThatWasSelected == R.id.action_search){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Movie movie) {

        /*if(mtwoPane){
            //In two pane mode, show the detail view in this activity by
            //adding or replacing the detail fragment using fragment transaction
            Bundle args=new Bundle();
            args.putParcelable(DetailActivityFragment.MOVIE_DETAIL,movie);
            DetailActivityFragment fragment=new DetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.details_container,fragment,DETAILFRAGMENT_TAG)
                    .commit();*/
       // }else {
            Intent intent = new Intent(this,DetailActivity.class);
            intent.putExtra("MOVIE", movie);
            startActivity(intent);
       // }
    }
}
