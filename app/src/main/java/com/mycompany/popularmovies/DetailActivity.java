package com.mycompany.popularmovies;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // setup variables to get movie data from mainactivity

        String movieTitle = null;
        String posterPath = null;
        String plot = null;
        String userRating = null;
        String releaseDate = null;

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            movieTitle = extras.getString("title");
            posterPath = extras.getString("posterpath");
            plot = extras.getString("plot");
            userRating = extras.getString("userrating");
            releaseDate = extras.getString("releasedate");
        }

        ((TextView) findViewById(R.id.details_movie_title))
                .setText(movieTitle);

        ((TextView) findViewById(R.id.details_plot))
                .setText(plot);

        ((TextView) findViewById(R.id.details_plot))
                .setMovementMethod(new ScrollingMovementMethod());

        // format the user rating, add '/10'
        String formattedUserRating = userRating + "/10";

        ((TextView) findViewById(R.id.details_user_rating))
                .setText(formattedUserRating);

        // extract the year from the release date string
        String movieYear = releaseDate.substring(0, 4);

        ((TextView) findViewById(R.id.details_release_date))
                .setText(movieYear);

        // construct url for the full posterpath
        String baseURL = "http://image.tmdb.org/t/p/";
        String thumbSize = "w185";
        String posterURL = null;
        posterURL = baseURL + thumbSize + posterPath;

        ImageView imageView = ((ImageView) findViewById(R.id.details_imageview));

        Picasso.with(this)
                .load(posterURL)
                //.placeholder(R.drawable.weather)
                .centerCrop()
                .resize(600, 900)
                .into(imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
