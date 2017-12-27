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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class DashboardActivity extends AppCompatActivity implements OngoingFragment.Callback{

    private FirebaseAuth mAuth;
    public static String mSortBy;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
       // getSupportFragmentManager().beginTransaction().replace(R.id.movies_fragment, new OngoingFragment()).commit();
       // getSupportFragmentManager().beginTransaction().replace(R.id.movies_fragment, new RetroFragment()).commit();




    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        if (currentUser == null){
            sendToStart();
        }
        else{
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(this,getSupportFragmentManager());
            viewPager.setAdapter(adapter);
            viewPager.setPageTransformer(true, new AccordionTransformer());

            TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    private void sendToStart(){

        Intent startIntent = new Intent(DashboardActivity.this, MainActivity.class);
        startActivity(startIntent);
        finish();
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
        if (menuItemThatWasSelected == R.id.logout_btn){
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }
        return true;
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
