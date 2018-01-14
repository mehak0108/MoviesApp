package com.example.mehak.movies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.example.mehak.movies.Classes.Movie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;


public class DashboardActivity extends AppCompatActivity implements OngoingFragment.Callback{

    public FirebaseAuth auth;
    public static String mSortBy;
   // public static boolean mtwoPane;
    private static final String DETAILFRAGMENT_TAG="DFTAG";

    Toolbar toolbar;
    Drawer result;
    public static ActionBarDrawerToggle toggle;
    public static AppCompatActivity activity;
    public static FragmentManager manager;
    public FirebaseUser currentUser;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

       /* if(findViewById(R.id.details_container) !=null){
            Log.v("main","twopane");
            mtwoPane=true;
            //Add detail fragment dynamically in case of 2 pane layout
            if(savedInstanceState==null){
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.details_container, new DetailFragment(),DETAILFRAGMENT_TAG).
                        commit();
            }
        }
        else
            mtwoPane=false;*/


        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        initToolbar();

        activity = DashboardActivity.this;
        manager = getSupportFragmentManager();

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {

                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                Log.v("Picasso", ""+uri);
                set(imageView, uri, placeholder);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }
        });

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.nav_menu_header)
                .addProfiles(
                        new ProfileDrawerItem().withName(currentUser.getDisplayName()).withEmail(currentUser.getEmail()).withIcon(currentUser.getPhotoUrl())

                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.NavSettings).withIcon(R.drawable.ic_settings_black_24dp),
                        new SecondaryDrawerItem().withName(R.string.NavAboutUs),
                        new SecondaryDrawerItem().withName(R.string.NavLogout)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Log.v("Position", "" + position);

                        switch (position) {
                            case 1:

                                break;
                            case 2:
                                Intent SettingsIntent = new Intent(DashboardActivity.this, SettingsActivity.class);
                                startActivity(SettingsIntent);
                                break;
                            case 3:
                                Intent AboutUsIntent = new Intent(DashboardActivity.this, AboutActivity.class);
                                startActivity(AboutUsIntent);
                                break;
                            case 4:
                                if (isNetworkAvailable()) {
                                    Intent LogoutIntent = new Intent(DashboardActivity.this, MainActivity.class);
                                    startActivity(LogoutIntent);
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "No Connection!\nCheck your Internet Connection", Toast.LENGTH_LONG).show();
                                }
                                break;
                        }

                        result.getDrawerLayout().closeDrawers();
                        return true;
                    }
                }).build();


        toggle = new ActionBarDrawerToggle(this, result.getDrawerLayout(), R.string.NavigationDrawerOpen, R.string.NavigationDrawerClose);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toggle.syncState();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = auth.getCurrentUser();

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
        mSortBy= sort_by;

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemThatWasSelected = item.getItemId();

        if(toggle.onOptionsItemSelected(item)) {
            return true;
        }


        if(menuItemThatWasSelected == R.id.action_search){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

        }
        if (menuItemThatWasSelected == R.id.logout_btn) {

            if (isNetworkAvailable()) {
                FirebaseAuth.getInstance().signOut();
                sendToStart();
            } else {
                Toast.makeText(getApplicationContext(), "No Connection!\nCheck your Internet Connection", Toast.LENGTH_LONG).show();
            }
        }
        return true;
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

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar_dashboard);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }


}
