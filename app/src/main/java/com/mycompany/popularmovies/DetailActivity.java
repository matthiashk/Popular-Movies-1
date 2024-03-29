package com.mycompany.popularmovies;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Provides a detail view of the selected movie item.
 */

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
        if (extras != null) {
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

        // allow user to scroll the view containing the plot synopsis
        ((TextView) findViewById(R.id.details_plot))
                .setMovementMethod(new ScrollingMovementMethod());

        // format the user rating, add '/10'
        String formattedUserRating = userRating + "/10";

        ((TextView) findViewById(R.id.details_user_rating))
                .setText(formattedUserRating);

        ((TextView) findViewById(R.id.details_release_date))
                .setText(releaseDate);

        // construct url for the full posterpath
        String baseURL = "http://image.tmdb.org/t/p/";
        String thumbSize = "w185";
        String posterURL;
        posterURL = baseURL + thumbSize + posterPath;

        ImageView imageView = ((ImageView) findViewById(R.id.details_imageview));

        Picasso.with(this)
                .load(posterURL)
                .centerCrop()
                .resize(600, 900)
                .into(imageView);
    }
}
