package com.example.avi.ultimatespotifyplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avi.ultimatespotifyplayer.adapter.SongBaseAdapter;
import com.example.avi.ultimatespotifyplayer.notification.NotificationGenerator;
import com.example.avi.ultimatespotifyplayer.pojo.Album;
import com.example.avi.ultimatespotifyplayer.pojo.Artists;
import com.example.avi.ultimatespotifyplayer.pojo.Images;
import com.example.avi.ultimatespotifyplayer.pojo.Items;
import com.example.avi.ultimatespotifyplayer.pojo.Song;
import com.example.avi.ultimatespotifyplayer.pojo.Track;
import com.example.avi.ultimatespotifyplayer.pojo.UserLibrary;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback {
    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "6b60dd5180e84673b1b90c9346dcd908";

    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "https://google.com";

    private Player mPlayer;

    private static final int REQUEST_CODE = 1337;

    private String token = "";

    ArrayList<Song> songList;

    SongBaseAdapter songBaseAdapter;

    ListView listView;

    ProgressBar progressBar;

    TextView mSelectedTrackTitle;
    ImageView mSelectedTrackImage;
    private ImageView mPlayerControl;

    static int currentSelected = 0;

    boolean pause;

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming", "user-library-read"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        listView = (ListView) findViewById(R.id.songListView);
        listView.setScrollingCacheEnabled(false);
        progressBar = (ProgressBar) findViewById(R.id.secondBar);
        songList = new ArrayList<>();
        mSelectedTrackTitle = (TextView) findViewById(R.id.selected_track_title);
        mSelectedTrackImage = (ImageView) findViewById(R.id.selected_track_image);
        mPlayerControl = (ImageView) findViewById(R.id.player_control);
        pause = true;
        mPlayerControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pause) {
                    mPlayer.pause(new Player.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            Log.i("MainActivity", "Paused Player");
                        }

                        @Override
                        public void onError(Error error) {
                            Log.e("MainActivity", error.toString());
                        }
                    });
                } else {
                    mPlayer.resume(new Player.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            Log.i("MainActivity", "Paused Player");
                        }

                        @Override
                        public void onError(Error error) {
                            Log.e("MainActivity", "Unable to play song.");
                        }
                    });
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song song = (Song) parent.getItemAtPosition(position);
                mSelectedTrackTitle.setText(song.getTrackName() + " ~ " + song.getArtist());
                Picasso.with(MainActivity.this).load(song.getAlbumImage()).into(mSelectedTrackImage);
                currentSelected = position;
                mPlayer.playUri(null, song.getTrackValue(), 0, 0);
            }
        });

        searchView = (SearchView) findViewById(R.id.searchView);

        searchView.setActivated(true);
        searchView.setQueryHint("Find");
        searchView.onActionViewExpanded();
        searchView.setIconified(false);
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (songBaseAdapter != null) {
                    songBaseAdapter.getFilter().filter(newText);
                    return false;
                } else {
                    return false;
                }
            }
        });
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        if (mPlayer != null) {
            mPlayer.resume(new Player.OperationCallback() {
                @Override
                public void onSuccess() {
                    Log.d("MainActivity", "Able to play the player");
                }

                @Override
                public void onError(Error error) {
                    Log.i("MainActivity", "Not able to play the player");
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayer != null) {
            mPlayer.pause(new Player.OperationCallback() {
                @Override
                public void onSuccess() {
                    Log.i("MainActivity", "Able to pause the player");
                }

                @Override
                public void onError(Error error) {
                    Log.i("MainActivity", "Not able to pause the player");
                }
            });
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE) {
            final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(MainActivity.this);
                        mPlayer.addNotificationCallback(MainActivity.this);
                        token = response.getAccessToken();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("MainActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary

            case kSpPlaybackNotifyPlay:
                pause = true;
                mPlayerControl.setImageResource(R.drawable.baseline_pause_24);
                break;
            case kSpPlaybackNotifyPause:
                pause = false;
                mPlayerControl.setImageResource(R.drawable.baseline_play_arrow_24);
                break;

            case kSpPlaybackNotifyAudioDeliveryDone:
                int newPosition = currentSelected + 1;
                if (newPosition <= songList.size() - 1) {
                    Song song = (Song) listView.getAdapter().getItem(newPosition);
                    mSelectedTrackTitle.setText(song.getTrackName() + " ~ " + song.getArtist());
                    Picasso.with(MainActivity.this).load(song.getAlbumImage()).into(mSelectedTrackImage);
                    currentSelected = newPosition;
                    mPlayer.refreshCache();
                    mPlayer.playUri(null, song.getTrackValue(), 0, 0);
                } else {
                    currentSelected = 0;
                    Song song = (Song) listView.getAdapter().getItem(currentSelected);
                    mSelectedTrackTitle.setText(song.getTrackName() + " ~ " + song.getArtist());
                    Picasso.with(MainActivity.this).load(song.getAlbumImage()).into(mSelectedTrackImage);
                    mPlayer.playUri(null, song.getTrackValue(), 0, 0);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("MainActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");

        //First thing we want to do is to get all of the songs for the user given.
        try {
            //Some url endpoint that you may have
            String myUrl = "https://api.spotify.com/v1/me/tracks";
            //String to place our result in
            String result;
            //Instantiate new instance of our class
            HttpGetRequest getRequest = new HttpGetRequest();
            //Perform the doInBackground method, passing in our url
            result = getRequest.execute(myUrl).get();
        } catch (InterruptedException e) {
            Log.e("MainActivity", e.toString());
        } catch (ExecutionException e) {
            Log.e("MainActivity", e.toString());
        }


    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error var1) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }


    public class HttpGetRequest extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String stringUrl = params[0];
            String result = "NO CONNECTION MADE";


            try {
                //Create a URL object holding our url
                URL myUrl = new URL(stringUrl);
                //Create a connection
                HttpURLConnection connection = (HttpURLConnection)
                        myUrl.openConnection();

                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                connection.setRequestProperty("Authorization", "Bearer " + token);
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestMethod("GET");

                //Connect to our url
                connection.connect();

                if (connection.getResponseCode() == 200) {
                    InputStream responseBody = connection.getInputStream();
                    InputStreamReader responseBodyReader =
                            new InputStreamReader(responseBody, "UTF-8");


                    //Create object mapper object and map JSON that is retrieved into an Arraylist of POJO we created called Music_Results
                    ObjectMapper objectMapper = new ObjectMapper();
                    //If property is unknown, mark it as ok so application does not crash
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);


                    UserLibrary userLibrary = objectMapper.readValue(responseBodyReader, UserLibrary.class);

                    for (Items item : userLibrary.getItems()) {
                        Song song = new Song();
                        Track track = item.getTrack();
                        song.setAlbum(track.getAlbum().getName());
                        Album album = track.getAlbum();
                        song.setAlbumImage(album.getImages()[0].getUrl());
                        song.setReleaseDate(track.getAlbum().getRelease_date());
                        StringBuilder artists = new StringBuilder();
                        for (Artists artist : track.getArtists()) {
                            artists.append(artist.getName() + ", ");
                        }
                        final String allArtists = artists.toString().replaceAll(", $", "");
                        song.setArtist(allArtists);
                        song.setTrackValue(track.getUri());
                        song.setTrackName(track.getName());

                        songList.add(song);
                    }

                    return userLibrary.getNext();

                } else {
                    return result;
                }
            } catch (IOException e) {
                Log.e("MainActivity", e.toString());
            }


            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null || result.equals("null")) {
                progressBar.setVisibility(View.INVISIBLE);
                songBaseAdapter = new SongBaseAdapter(MainActivity.this, songList);
                listView.setAdapter(songBaseAdapter);
            } else {
                try {
                    //Some url endpoint that you may have
                    String myUrl = result;
                    //Instantiate new instance of our class
                    HttpGetRequest getRequest = new HttpGetRequest();
                    //Perform the doInBackground method, passing in our url
                    getRequest.execute(myUrl).get();
                } catch (InterruptedException e) {
                    Log.e("MainActivity", e.toString());
                } catch (ExecutionException e) {
                    Log.e("MainActivity", e.toString());
                }
            }
        }
    }
}
