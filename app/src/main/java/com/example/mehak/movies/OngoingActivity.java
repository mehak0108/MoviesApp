/*
package com.example.mehak.movies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

public class OngoingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_list);

        ArrayList<Movie> movies = new ArrayList<Movie>();
        movies.add(new Movie("Inception","Thriller","2010","English",R.drawable.inception));


        MovieAdapter adapter = new MovieAdapter(this,movies);
        ListView listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(adapter);


    }
}
*/

package com.example.mehak.movies;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class OngoingActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new OngoingFragment()).commit();

    }

}