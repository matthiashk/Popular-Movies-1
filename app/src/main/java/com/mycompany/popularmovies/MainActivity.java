package com.mycompany.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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
import java.util.Arrays;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    public static List<String> defaultImages = new ArrayList<>(Arrays.asList(
            "http://i.imgur.com/rFLNqWI.jpg",
            "http://i.imgur.com/C9pBVt7.jpg",
            "http://i.imgur.com/rT5vXE1.jpg",
            "http://i.imgur.com/aIy5R2k.jpg",
            "http://i.imgur.com/MoJs9pT.jpg",
            "http://i.imgur.com/S963yEM.jpg",
            "http://i.imgur.com/rLR2cyc.jpg",
            "http://i.imgur.com/SEPdUIx.jpg",
            "http://i.imgur.com/aC9OjaM.jpg",
            "http://i.imgur.com/76Jfv9b.jpg",
            "http://i.imgur.com/fUX7EIB.jpg",
            "http://i.imgur.com/syELajx.jpg",
            "http://i.imgur.com/COzBnru.jpg",
            "http://i.imgur.com/Z3QjilA.jpg"


    ));

    private ImageAdapter imageAdapter;

    public static String[] posterURLs = new String[20];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        imageAdapter = new ImageAdapter(this, defaultImages);
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(MainActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    @Override
    protected void onStart() {
        super.onStart();

        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute();

    }

    public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private String[] getMovieDataFromJson(String forecastJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_POSTER_PATH = "poster_path";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);

            JSONArray jArray = forecastJson.getJSONArray("results");

            // set the array to 20?
            String[] tmdbPosterPaths = new String[20];

            for (int i=0; i < jArray.length(); i++)
            {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    // Pulling items from the array
                    String posterPath = oneObject.getString(TMDB_POSTER_PATH);

                    //Log.v("getMovieDataFromJson", oneObjectsItem);
                    tmdbPosterPaths[i] = posterPath;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return tmdbPosterPaths;
        }

        @Override
        protected String[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {

                // TODO: REMOVE api key before submitting project!!!
                URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=aa336466223f0deecbe36bf1aafd76d3");

                //Log.v(LOG_TAG, "URL " + url.toString());

                // Create the request to OpenWeatherMap, and open the connection
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
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
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
        protected void onPostExecute(String[] strings) {

            // get the array of strings containing the poster paths and set to posterURLs
            if (strings != null) {
                int i = 0;

                for(String item: strings) {
                    posterURLs[i] = item;
                    //Log.v("MAINACTIVITY - item = ", item);
                    i++;
                }
            }

            // create new array containing complete poster urls
            String[] posterURLsFinal = new String[20];

            String baseURL = "http://image.tmdb.org/t/p/";

            String thumbSize = "w185";

            String posterPath = null;

            String finalURL = null;

            //Log.d("this is my array", "posterURLs: " + Arrays.toString(posterURLs));

            for (int i=0; i < posterURLs.length; i++) {

                posterPath = posterURLs[i];

                finalURL = baseURL + thumbSize + posterPath;

                posterURLsFinal[i] = finalURL;
            }

            List<String> uriPaths = imageAdapter.getUriList();

            // clear the default image list before adding
            uriPaths.clear();

            for (int i=0; i < posterURLsFinal.length; i++) {

                uriPaths.add(posterURLsFinal[i]);
            }

            imageAdapter.notifyDataSetChanged();

        }
    }

}
