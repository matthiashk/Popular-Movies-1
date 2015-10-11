package com.mycompany.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity{

    public ArrayList<Movie> finalMovieData = new ArrayList<Movie>();

    private ImageAdapter mImageAdapter;

    private GridView mGridview;

    private String mSortOrder;

    public static ArrayList<String> posterUrls = new ArrayList<String>();

    /* Change the api key here  */
    private String API_KEY = ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridview = (GridView) findViewById(R.id.gridview);
        mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // access arraylist here and pass selected item info to detailactivity
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class)
                        .putExtra("title", finalMovieData.get(position).title)
                        .putExtra("posterpath", finalMovieData.get(position).posterPath)
                        .putExtra("plot", finalMovieData.get(position).plot)
                        .putExtra("userrating", finalMovieData.get(position).userRating)
                        .putExtra("releasedate", finalMovieData.get(position).releaseDate);

                startActivity(intent);
            }
        });


        // get the sort order from preferences and store in mSortOrder
        // so we can detect if the user changed it later
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mSortOrder = sharedPrefs.getString(
                getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_order_default));

        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(settings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // get preferences so we can check if the user changed them
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sortOrder = sharedPrefs.getString(
                getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_order_default));

        if (!mSortOrder.equals(sortOrder)) {

            // set new value
            mSortOrder = sortOrder;
            // fetch movies to with the new sort order
            FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
            fetchMoviesTask.execute();
        }
    }

    public void resultsFromFetch(ArrayList<Movie> movieData) {
        // getting movieData from onpostexecute
        finalMovieData = movieData;
    }

    /**
     * Gets movie data from JSON and store into an arraylist.
     */
    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private ArrayList<Movie> getMovieDataFromJson(String forecastJsonStr)
                throws JSONException {

            final String TMDB_TITLE = "title";
            final String TMDB_POSTER_PATH = "poster_path";
            final String TMDB_PLOT = "overview";
            final String TMDB_USER_RATING = "vote_average";
            final String TMDB_RELEASE_DATE = "release_date";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);

            JSONArray jArray = forecastJson.getJSONArray("results");

            ArrayList<Movie> movieData = new ArrayList<>();

            for (int i=0; i < jArray.length(); i++) {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    // Pulling items from the array
                    String title = oneObject.getString(TMDB_TITLE);
                    String posterPath = oneObject.getString(TMDB_POSTER_PATH);
                    String plot = oneObject.getString(TMDB_PLOT);
                    String userRating = oneObject.getString(TMDB_USER_RATING);
                    String releaseDate = oneObject.getString(TMDB_RELEASE_DATE);

                    //Log.v("getMovieDataFromJson", oneObjectsItem);
                    movieData.add(new Movie(title, posterPath, plot, userRating, releaseDate));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return movieData;
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            // get sort order from preferences
            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String sortOrder = sharedPrefs.getString(
                    getString(R.string.pref_sort_order_key),
                    getString(R.string.pref_sort_order_default));

            try {
                final String MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_BY_PARAM = "sort_by";
                final String API_KEY_PARAM = "api_key";
                final String VOTE_COUNT = "vote_count.gte";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL)
                        .buildUpon()
                        .appendQueryParameter(SORT_BY_PARAM, sortOrder)
                        .appendQueryParameter(VOTE_COUNT, "10")
                        .appendQueryParameter(API_KEY_PARAM, API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
                //Log.v(LOG_TAG, moviesJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

                return null;
            } finally{

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movieData) {

            // this method sends the movieData to the main activity
            resultsFromFetch(movieData);

            posterUrls.clear();

            // get the array of strings containing the poster paths and set to posterUrls
            if (movieData != null) {
                for(Movie item: movieData) {
                    posterUrls.add(item.posterPath);
                }
            }

            // create new array containing complete poster urls
            ArrayList<String> posterUrlsFinal = new ArrayList<>();

            String baseURL = "http://image.tmdb.org/t/p/";

            String thumbSize = "w185";

            String posterPath;

            String finalURL;

            //Log.d("this is my array", "posterUrls: " + Arrays.toString(posterUrls));

            for (int i=0; i < posterUrls.size(); i++) {
                posterPath = posterUrls.get(i);
                finalURL = baseURL + thumbSize + posterPath;
                posterUrlsFinal.add(finalURL);
            }

            List<String> uriPaths = new ArrayList<>();
            uriPaths.clear(); // clear the default image list before adding

            for (int i=0; i < posterUrlsFinal.size(); i++) {
                uriPaths.add(posterUrlsFinal.get(i));
            }

            mImageAdapter = new ImageAdapter(getApplicationContext(), uriPaths);
            mGridview.setAdapter(mImageAdapter);
            mImageAdapter.notifyDataSetChanged();
        }
    }
}
