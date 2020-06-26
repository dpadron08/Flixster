package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import okhttp3.Headers;

public class MovieTrailerActivity extends YouTubeBaseActivity {

    private static final String TAG = "MovieTrailerActivity";
    //public static final String api_key = "AIzaSyC2rfYaSgCmw0MQhw485zsVjeUn3tL_CYU";

    //public String api_key;
    public final String endpoint = "https://api.themoviedb.org/3/movie/%d/videos?api_key=%s";

    Movie movie;
    String videoKey;

    YouTubePlayerView playerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_trailer);

        //api_key = getString(R.string.api_key);

        // resolve the player view from the layout
        playerView = (YouTubePlayerView) findViewById(R.id.player);

        /* construct youtube url */

        // get movie from intent
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        int id = movie.getId();

        // also valid, just never include the en=US
        //endpoint += "/movie/" + id + "/videos" + "?api_key=" + moveidbKey;

        /* do API call to get key using the id and /movie/{movie_id}/videos endpoint */
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(endpoint, id, this.getString(R.string.moviedb_key)), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "key: " + results.toString());
                    videoKey = results.getJSONObject(0).getString("key");
                    initializeYoutube(videoKey);

                } catch (JSONException e) {
                    Log.e(TAG, "Hit JSON exception");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });


    }

    private void initializeYoutube(final String videoId) {
        // should initialize with API key stored in secrets.xml like this:
        //playerView.initialize(getString(R.string.youtube_api_key), new YouTubePlayer.OnInitializedListener() {
        playerView.initialize(getString(R.string.api_key), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                YouTubePlayer youTubePlayer, boolean b) {
                // do any work here to cue video, play video, etc.
                youTubePlayer.cueVideo(videoId);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                YouTubeInitializationResult youTubeInitializationResult) {
                // log the error
                Log.e("MovieTrailerActivity", "Error initializing YouTube player");
            }
        });
    }
}