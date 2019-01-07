package aviee.develop.music.myultimatemusicexperienceforspotify;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ToolbarWidgetWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kobakei.ratethisapp.RateThisApp;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.SpotifyNativeAuthUtil;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import aviee.develop.music.myultimatemusicexperienceforspotify.adapter.AlarmAdapter;
import aviee.develop.music.myultimatemusicexperienceforspotify.adapter.SongBaseAdapter;
import aviee.develop.music.myultimatemusicexperienceforspotify.constant.Constants;
import aviee.develop.music.myultimatemusicexperienceforspotify.database.DbHelper;
import aviee.develop.music.myultimatemusicexperienceforspotify.pojo.Album;
import aviee.develop.music.myultimatemusicexperienceforspotify.pojo.Artists;
import aviee.develop.music.myultimatemusicexperienceforspotify.pojo.Items;
import aviee.develop.music.myultimatemusicexperienceforspotify.pojo.Song;
import aviee.develop.music.myultimatemusicexperienceforspotify.pojo.Track;
import aviee.develop.music.myultimatemusicexperienceforspotify.pojo.UserLibrary;


public class MainActivity extends Activity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback {

    private String TAG = "MainActivity";

    public static final String CLIENT_ID = "303a946e67b34fbd844052b6ad997636";
    public static final String REDIRECT_URI = "festevo://callback";
    public static Player mPlayer;
    public static final int REQUEST_CODE = 1337;
    public static String token = "";

    ArrayList<Song> songList;
    SongBaseAdapter songBaseAdapter;
    ListView listView;
    ProgressBar progressBar;
    public static TextView mSelectedTrackTitle;
    public static ImageView mSelectedTrackImage;
    public static ImageView mPlayerControl;
    Toolbar toolbar;
    static int currentSelected = 0;
    public static boolean pause;
    SearchView searchView;
    public static int selectedPosition = -100;
    public static Song currentSongPlaying;
    RelativeLayout relativeLayoutSearchView;
    public static boolean shuffle;
    public static Button shuffleButton;
    public static SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private boolean mIsListening;
    public static boolean authenticate;
    public static boolean stayWithinApplication;
    private View speechView;
    private boolean prevSelected;
    BottomNavigationView bottomNavigationView;

    private DbHelper db;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("songList", songList);
        outState.putBoolean(getString(R.string.authenticate), authenticate);
        outState.putBoolean(getString(R.string.pause), pause);
        outState.putBoolean(getString(R.string.shuffle), shuffle);
        outState.putInt(getString(R.string.CurrentSelected), currentSelected);
        outState.putInt(getString(R.string.SelectedPosition), selectedPosition);
        outState.putSerializable(getString(R.string.SelectedSong), currentSongPlaying);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        songList = (ArrayList<Song>) savedInstanceState.getSerializable("songList");
        currentSongPlaying = (Song) savedInstanceState.getSerializable(getString(R.string.SelectedSong));

        authenticate = (Boolean) savedInstanceState.getBoolean(getString(R.string.authenticate));
        pause = (Boolean) savedInstanceState.getBoolean(getString(R.string.pause));
        shuffle = (Boolean) savedInstanceState.getBoolean(getString(R.string.shuffle));
        if (shuffle) {
            shuffleButton.setText(getString(R.string.NoShuffle));
        } else {
            shuffleButton.setText(getString(R.string.Shuffle));
        }

        currentSelected = (Integer) savedInstanceState.getInt(getString(R.string.CurrentSelected));
        selectedPosition = (Integer) savedInstanceState.getInt(getString(R.string.SelectedPosition));
        if (currentSongPlaying != null) {
            pause = true;
            mSelectedTrackTitle.setText(currentSongPlaying.getTrackName() + " ~ " + currentSongPlaying.getArtist());
            Picasso.with(MainActivity.this).load(currentSongPlaying.getAlbumImage()).into(mSelectedTrackImage);
        }
        if (currentSongPlaying == null)
            mPlayerControl.setImageResource(0);
        else
            mPlayerControl.setImageResource(R.drawable.baseline_play_arrow_24);

        songBaseAdapter = new SongBaseAdapter(MainActivity.this, songList);
        listView.setAdapter(songBaseAdapter);

        songBaseAdapter.notifyDataSetChanged();
    }

    private void showRateDialog() {
        // Monitor launch times and interval from installation
        RateThisApp.onCreate(this);
        // If the condition is satisfied, "Rate this app" dialog will be shown
        RateThisApp.showRateDialogIfNeeded(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shuffle = false;
        pause = true;
        songList = new ArrayList<>();
        stayWithinApplication = false;
        db = new DbHelper(this);


        setUpUI();
        toolbarOnClick();
        setUpSearchView();
        requestRecordAudioPermission();
        setUpSpeech();
        setUpMPlayerListener();
        setUpListView();
        setUpBottomNavigationViewListener();

        setUpSpotifyRequest();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    /*
    Params: None
    Returns: Void
    Functionality: Set up request and attempt user login to Spotify using token,
                   client_id and redirect uri which should be provided
     */
    private void setUpSpotifyRequest(){
        final AuthenticationRequest request = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                .setScopes(new String[]{"user-read-private", "streaming", "user-library-read", "user-modify-playback-state", "user-read-playback-state"})
                .build();

        AuthenticationClient.openLoginInBrowser(this, request);
    }

    /*
    Params: None
    Returns: Void
    Functionality: Set up bottom bottomnavigationview listenter to handle whenever user clicks bottomnavigationview
     */
    private void setUpBottomNavigationViewListener(){
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_library:
                                break;
                            case R.id.action_alarms:
                                Intent i = new Intent(getApplicationContext(), AlarmActivity.class);

                                startActivity(i);

                        }
                        return true;
                    }
                });
    }

    /*
   Params: None
   Returns: Void
   Functionality: Set up all the UI for the activity here.
    */
    private void setUpUI(){
        relativeLayoutSearchView = (RelativeLayout) findViewById(R.id.relativeLayoutSearchView);
        toolbar = (Toolbar) findViewById(R.id.toolbar4);

        //Set up shuffle button
        shuffleButton = (Button) findViewById(R.id.shuffleButton);

        //Set up listview
        listView = (ListView) findViewById(R.id.songListView);
        listView.setScrollingCacheEnabled(false);
        listView.setSelector(R.drawable.list_selector);

        //Set up progress bar
        progressBar = (ProgressBar) findViewById(R.id.secondBar);

        //Set up player controller
        mSelectedTrackTitle = (TextView) findViewById(R.id.selected_track_title);
        mSelectedTrackImage = (ImageView) findViewById(R.id.selected_track_image);
        mPlayerControl = (ImageView) findViewById(R.id.player_control);

        //On start make the shuffle button invisible
        shuffleButton.setVisibility(View.INVISIBLE);

        //On start make the fab invisible
        fab = findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);

        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);
    }
    /*
       Params: None
       Returns: Void
       Functionality: Set up the music player listener here to handle user interaction with music player
     */
    private void setUpMPlayerListener(){
        mPlayerControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pause) {
                    mPlayer.resume(new Player.OperationCallback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Error error) {

                        }
                    });
                } else {
                    mPlayer.pause(new Player.OperationCallback() {
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
    }

    /*
       Params: None
       Returns: Void
       Functionality: Handle user interaction with toolbar
     */
    private void toolbarOnClick() {
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSongPlaying != null && songList.contains(currentSongPlaying)) {
                    stayWithinApplication = true;
                    Intent intent = new Intent(MainActivity.this, DisplaySong.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Can only view Music Player for songs in library currently.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /*
       Params: View
       Returns: Void
       Functionality: Toggle ability to shuffle songs
     */
    public void shuffleSongs(View view) {
        if (shuffle) {
            shuffle = false;
            shuffleButton.setText(getString(R.string.Shuffle));
        } else {
            shuffle = true;
            shuffleButton.setText(getString(R.string.NoShuffle));
            selectRandomSong();
        }
    }

    /*
       Params: Void
       Returns: Void
       Functionality: Select a random song in library
     */
    private void selectRandomSong() {
        int newPosition = Constants.randomWithRange(0, songList.size() - 1);
        Song song = (Song) listView.getAdapter().getItem(newPosition);
        currentSongPlaying = song;
        mSelectedTrackTitle.setText(song.getTrackName() + " ~ " + song.getArtist());
        Picasso.with(MainActivity.this).load(song.getAlbumImage()).into(mSelectedTrackImage);
        currentSelected = newPosition;
        mPlayer.refreshCache();
        mPlayer.playUri(null, song.getTrackValue(), 0, 0);
        songBaseAdapter.notifyDataSetChanged();
        selectedPosition = newPosition;
        listView.smoothScrollToPosition(newPosition);
    }

    /*
       Params: Void
       Returns: Void
       Functionality: Set up listview of songs
     */
    private void setUpListView() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song song = (Song) parent.getItemAtPosition(position);
                currentSongPlaying = song;
                mSelectedTrackTitle.setText(song.getTrackName() + " ~ " + song.getArtist());
                Log.i(TAG, "Song Image: " + song.getAlbumImage());
                Picasso.with(MainActivity.this).load(song.getAlbumImage()).into(mSelectedTrackImage);
                currentSelected = position;
                mPlayer.playUri(null, song.getTrackValue(), 0, 0);
                selectedPosition = position;
                songBaseAdapter.notifyDataSetChanged();
                listView.smoothScrollToPosition(position);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > 0) {
                    relativeLayoutSearchView.setVisibility(View.GONE);
                }
                if (firstVisibleItem == 0) {
                    relativeLayoutSearchView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /*
       Params: Void
       Returns: Void
       Functionality: Handle user interaction with searchview
     */
    private void setUpSearchView() {
        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setQueryHint(getString(R.string.Find));
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
                    songBaseAdapter.notifyDataSetChanged();
                    return false;
                } else {
                    return false;
                }
            }
        });

    }

    /*
       Params: Void
       Returns: Void
       Functionality: Request permission from user to use audio within the application
     */
    private void requestRecordAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String requiredPermission = Manifest.permission.RECORD_AUDIO;

            // If the user previously denied this permission then show a message explaining why
            // this permission is needed
            if (checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{requiredPermission}, 101);
            }
        }
    }

    /*
       Params: requestCode, resultCode, intent
       Returns: Void
       Functionality: Upon Spotify authenticating user, handle callback to application and begin initializing Spotify player
     */
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
                        Log.e(TAG, "Could not initialize player: " + throwable.getMessage());
                    }
                });
            } else {
                Log.i(TAG, "Not able to identify user");
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if (uri != null) {
            final AuthenticationResponse response = AuthenticationResponse.fromUri(uri);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
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
                            Log.e(TAG, "Could not initialize player: " + throwable.getMessage());
                        }
                    });
                    break;
                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    break;
                // Handle other cases
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        Spotify.destroyPlayer(this);

        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.destroy();
        }


        super.onDestroy();
    }


    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("MainActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            case kSpPlaybackNotifyPlay:
                pause = false;
                mPlayerControl.setImageResource(R.drawable.baseline_pause_24);
                break;
            case kSpPlaybackNotifyPause:
                pause = true;
                mPlayerControl.setImageResource(R.drawable.baseline_play_arrow_24);
                break;
            case kSpPlaybackNotifyPrev:
                try {
                    prevSelected = true;
                    if (!shuffle) {
                        int newPosition = currentSelected - 1;
                        if (newPosition >= 0) {
                            Song song = (Song) listView.getAdapter().getItem(newPosition);
                            currentSongPlaying = song;
                            mSelectedTrackTitle.setText(song.getTrackName() + " ~ " + song.getArtist());
                            Picasso.with(MainActivity.this).load(song.getAlbumImage()).into(mSelectedTrackImage);
                            currentSelected = newPosition;
                            mPlayer.refreshCache();
                            mPlayer.playUri(null, song.getTrackValue(), 0, 0);
                        } else {
                            currentSelected = 0;
                            Song song = (Song) listView.getAdapter().getItem(currentSelected);
                            currentSongPlaying = song;
                            mSelectedTrackTitle.setText(song.getTrackName() + " ~ " + song.getArtist());
                            Picasso.with(MainActivity.this).load(song.getAlbumImage()).into(mSelectedTrackImage);
                            mPlayer.playUri(null, song.getTrackValue(), 0, 0);
                        }
                    } else {
                        selectRandomSong();
                    }
                    selectedPosition = currentSelected;
                    songBaseAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                break;
            case kSpPlaybackNotifyAudioDeliveryDone:
                try {
                    if (!prevSelected) {
                        if (!shuffle) {
                            int newPosition = currentSelected + 1;
                            if (newPosition <= songList.size() - 1) {
                                Song song = (Song) listView.getAdapter().getItem(newPosition);
                                currentSongPlaying = song;
                                mSelectedTrackTitle.setText(song.getTrackName() + " ~ " + song.getArtist());
                                Picasso.with(MainActivity.this).load(song.getAlbumImage()).into(mSelectedTrackImage);
                                currentSelected = newPosition;
                                mPlayer.refreshCache();
                                mPlayer.playUri(null, song.getTrackValue(), 0, 0);
                            } else {
                                currentSelected = 0;
                                Song song = (Song) listView.getAdapter().getItem(currentSelected);
                                currentSongPlaying = song;
                                mSelectedTrackTitle.setText(song.getTrackName() + " ~ " + song.getArtist());
                                Picasso.with(MainActivity.this).load(song.getAlbumImage()).into(mSelectedTrackImage);
                                mPlayer.playUri(null, song.getTrackValue(), 0, 0);
                            }
                        } else {
                            selectRandomSong();
                        }
                        selectedPosition = currentSelected;
                        songBaseAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                prevSelected = false;
                break;
            default:
                break;
        }
    }


    @Override
    public void onPlaybackError(Error error) {
        Log.d(TAG, "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d(TAG, "User logged in");
        if(!authenticate) {
            db.restoreDB();
            try {
                String myUrl = "https://api.spotify.com/v1/me/tracks";
                //String to place our result in
                String result;
                //Instantiate new instance of our class
                HttpGetRequest getRequest = new HttpGetRequest();
                //Perform the doInBackground method, passing in our url
                result = getRequest.execute(myUrl).get();
                authenticate = true;
            } catch (InterruptedException e) {
                Log.e(TAG, e.toString());
            } catch (ExecutionException e) {
                Log.e(TAG, e.toString());
            }
        }
        else
        {
            songList = db.getAllSongs();
            showScreen();
        }

    }


    @Override
    public void onLoggedOut() {
        Log.d(TAG, "User logged out");
    }

    @Override
    public void onLoginFailed(Error var1) {
        Log.d(TAG, "Login failed");
    }

    @Override
    public void onTemporaryError() {

        Log.d(TAG, "Temporary error occurred");

    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d(TAG, "Received connection message: " + message);
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(getApplicationContext(), MyPreferencesActivity.class);
        startActivity(intent);
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
            String result = getString(R.string.NoConnection);
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
                connection.setUseCaches(true);

                connection.setRequestProperty("Authorization", "Bearer " + token);
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.addRequestProperty("Cache-Control", "max-age=0");

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
                        song.setSongLength(track.getDuration_ms());
                        Album album = track.getAlbum();
                        song.setAlbumImage(album.getImages()[0].getUrl());
                        song.setAlbumID(album.getId());
                        song.setReleaseDate(track.getAlbum().getRelease_date());
                        StringBuilder artists = new StringBuilder();
                        ArrayList<String> artistUris = new ArrayList<>();
                        for (Artists artist : track.getArtists()) {
                            artists.append(artist.getName() + ", ");
                            artistUris.add(artist.getUri());
                        }
                        final String allArtists = artists.toString().replaceAll(", $", "");
                        song.setArtist(allArtists);
                        song.setTrackValue(track.getUri());
                        song.setTrackName(track.getName());
                        song.setArtistUri(artistUris);

                        db.insertSong(song);
                        songList.add(song);
                    }
                    return userLibrary.getNext();
                } else {
                    return result;
                }
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                return result;
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                return result;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null || result.equals("null") || result.equals(getString(R.string.NoConnection))) {
                fab.setVisibility(View.VISIBLE);
                shuffleButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                songBaseAdapter = new SongBaseAdapter(MainActivity.this, songList);
                listView.setAdapter(songBaseAdapter);
                showRateDialog();

                db.printAllSongs();
            } else {
                try {
                    //Some url endpoint that you may have
                    String myUrl = result;
                    //Instantiate new instance of our class
                    HttpGetRequest getRequest = new HttpGetRequest();
                    //Perform the doInBackground method, passing in our url
                    getRequest.execute(myUrl).get();
                } catch (InterruptedException e) {
                    Log.e(TAG, e.toString());
                    showScreen();
                } catch (ExecutionException e) {
                    Log.e(TAG, e.toString());
                    showScreen();
                }
            }
        }


    }

    private void showScreen() {
        fab.setVisibility(View.VISIBLE);
        shuffleButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        songBaseAdapter = new SongBaseAdapter(MainActivity.this, songList);
        listView.setAdapter(songBaseAdapter);
        showRateDialog();
    }

    FloatingActionButton fab;

    private void setUpSpeech() {
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer.pause(new Player.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "Paused Player");
                    }

                    @Override
                    public void onError(Error error) {
                        Log.e(TAG, error.toString());
                    }
                });
                if (!mIsListening) {
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                }
                speechView = view;

                Snackbar.make(speechView, "Say Shuffle or Random to play a random song.\n" +
                        "Say Play {Song Title} to play a specific song.", Snackbar.LENGTH_LONG).show();


            }
        });

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        SpeechRecognitionListener listener = new SpeechRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);
    }

    protected class SpeechRecognitionListener implements RecognitionListener {

        @Override
        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginingOfSpeech");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "onEndOfSpeech");
        }

        @Override
        public void onError(int error) {
            Log.d(TAG, "error = " + error);
        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }

        @Override
        public void onPartialResults(Bundle partialResults) {

            Log.i(TAG, "Partial results: " + partialResults.toString());

        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "onReadyForSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onResults(Bundle results) {
            Log.d(TAG, "onResults");
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            // matches are the return values of speech recognition engine
            // Use these values for whatever you wish to do
            if (!matches.isEmpty()) {
                String query = matches.get(0).toString();
                Snackbar.make(speechView, query, Snackbar.LENGTH_LONG).show();
                String[] queryArray = query.split(" ", 2);
                if (queryArray.length > 1) {
                    String command = queryArray[0];
                    switch (command.toLowerCase()) {
                        case "play":
                            String songName = queryArray[1];
                            for (int i = 0; i < songList.size(); i++) {
                                if (songList.get(i).getTrackName().toLowerCase().contains(songName.toLowerCase())) {
                                    Song song = songList.get(i);
                                    playGivenSong(song, i);
                                }
                            }
                            break;
                        case "shuffle":
                            if (queryArray[1].toLowerCase().equals(getString(R.string.ShuffleSomeMusic).toLowerCase())) {
                                shuffle = true;
                                shuffleButton.setText(getString(R.string.NoShuffle));
                                selectRandomSong();
                            }
                            break;
                        default:
                            for (int i = 0; i < songList.size(); i++) {
                                songName = matches.get(0).toString();
                                if (songList.get(i).getTrackName().toLowerCase().contains(songName.toLowerCase())) {
                                    Song song = songList.get(i);
                                    playGivenSong(song, i);
                                }
                            }
                            break;
                    }
                } else if (queryArray.length == 1) {
                    String command = queryArray[0];
                    switch (command.toLowerCase()) {
                        case "shuffle":
                            shuffle = true;
                            shuffleButton.setText(getString(R.string.NoShuffle));
                            selectRandomSong();
                            break;
                        case "random":
                            shuffle = true;
                            shuffleButton.setText(getString(R.string.NoShuffle));
                            selectRandomSong();
                            break;
                        case "randomize":
                            shuffle = true;
                            shuffleButton.setText(getString(R.string.NoShuffle));
                            selectRandomSong();
                            break;
                        case "pause":
                            mPlayer.pause(new Player.OperationCallback() {
                                @Override
                                public void onSuccess() {
                                    Log.i("MainActivity", "Paused Player");
                                }

                                @Override
                                public void onError(Error error) {
                                    Log.e("MainActivity", "Unable to play song.");
                                }
                            });
                            break;
                        case "play":
                            shuffle = true;
                            selectRandomSong();
                            break;
                        case "begin":
                            mPlayer.resume(new Player.OperationCallback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Error error) {

                                }
                            });
                            break;
                        case "new":
                            shuffle = true;
                            selectRandomSong();
                            break;
                        case "resume":
                            mPlayer.resume(new Player.OperationCallback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Error error) {

                                }
                            });
                            break;
                        case "start":
                            mPlayer.resume(new Player.OperationCallback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Error error) {

                                }
                            });
                            break;
                        default:
                            for (int i = 0; i < songList.size(); i++) {
                                String songName = matches.get(0).toString();
                                if (songList.get(i).getTrackName().toLowerCase().contains(songName.toLowerCase())) {
                                    Song song = songList.get(i);
                                    playGivenSong(song, i);
                                }
                            }
                            break;

                    }
                }
            } else {
                Snackbar.make(speechView, "Unable to find results.", Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        public void onRmsChanged(float rmsdB) {
        }
    }

    /*
       Params: Song, Integer
       Returns: Void
       Functionality: Play song and set up UI to display song being played
     */
    private void playGivenSong(Song song, int i) {
        currentSongPlaying = song;
        mSelectedTrackTitle.setText(song.getTrackName() + " ~ " + song.getArtist());
        Log.i(TAG, "Song Image: " + song.getAlbumImage());
        Picasso.with(MainActivity.this).load(song.getAlbumImage()).into(mSelectedTrackImage);
        currentSelected = i;
        mPlayer.playUri(null, song.getTrackValue(), 0, 0);
        selectedPosition = i;
        songBaseAdapter.notifyDataSetChanged();
        listView.smoothScrollToPosition(i);
    }


}
