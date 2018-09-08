package aviee.develop.music.myultimatesongexperienceforspotify;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.session.MediaSession;
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
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
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

import aviee.develop.music.myultimatesongexperienceforspotify.adapter.SongBaseAdapter;
import aviee.develop.music.myultimatesongexperienceforspotify.constant.Constants;
import aviee.develop.music.myultimatesongexperienceforspotify.pojo.Album;
import aviee.develop.music.myultimatesongexperienceforspotify.pojo.Artists;
import aviee.develop.music.myultimatesongexperienceforspotify.pojo.Items;
import aviee.develop.music.myultimatesongexperienceforspotify.pojo.Song;
import aviee.develop.music.myultimatesongexperienceforspotify.pojo.Track;
import aviee.develop.music.myultimatesongexperienceforspotify.pojo.UserLibrary;


public class MainActivity extends Activity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback {

    private String TAG = "MainActivity";
    // TODO: Replace with your client ID
    public static final String CLIENT_ID = "303a946e67b34fbd844052b6ad997636";

    // TODO: Replace with your redirect URI
    public static final String REDIRECT_URI = "https://google.com";

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
    boolean pause;
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

    MediaSession mSession;


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

        progressBar.setVisibility(View.INVISIBLE);
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
        relativeLayoutSearchView = (RelativeLayout) findViewById(R.id.relativeLayoutSearchView);
        toolbar = (Toolbar) findViewById(R.id.toolbar4);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        shuffleButton = (Button) findViewById(R.id.shuffleButton);

        toolbarOnClick();

        listView = (ListView) findViewById(R.id.songListView);
        listView.setScrollingCacheEnabled(false);
        listView.setSelector(R.drawable.list_selector);

        setUpSearchView();


        progressBar = (ProgressBar) findViewById(R.id.secondBar);
        songList = new ArrayList<>();
        mSelectedTrackTitle = (TextView) findViewById(R.id.selected_track_title);
        mSelectedTrackImage = (ImageView) findViewById(R.id.selected_track_image);
        mPlayerControl = (ImageView) findViewById(R.id.player_control);
        pause = true;
        mPlayerControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "CLICKED!!!!");
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
        requestRecordAudioPermission();
        setUpSpeech();
        stayWithinApplication = false;

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming", "user-library-read"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        shuffleButton.setVisibility(View.INVISIBLE);
        fab.setVisibility(View.INVISIBLE);
        setUpListView();

    }

    private void toolbarOnClick() {

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition >= 0) {
                    stayWithinApplication = true;
                    Intent intent = new Intent(MainActivity.this, DisplaySong.class);
                    startActivity(intent);
                }
            }
        });
    }

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

    private void setUpListView() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song song = (Song) parent.getItemAtPosition(position);
                currentSongPlaying = song;
                mSelectedTrackTitle.setText(song.getTrackName() + " ~ " + song.getArtist());
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
                switch (scrollState) {
                    /*case SCROLL_STATE_IDLE:
                        relativeLayoutSearchView.setVisibility(View.VISIBLE);
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        relativeLayoutSearchView.setVisibility(View.GONE);
                        break;*/
                }
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

    private void enableHttpCaching() {
        try {
            File httpCacheDir = new File(getApplicationContext().getCacheDir(), "http");
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            Log.i(TAG, "HTTP response cache installation failed:" + e);
        }
    }

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
                        authenticate = true;

                        Toast.makeText(getApplicationContext(), "Able to identify user!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, "Could not initialize player: " + throwable.getMessage());
                        Toast.makeText(getApplicationContext(), "Unable to authorize user currently. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
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
        Toast.makeText(getApplicationContext(), "Playback Error", Toast.LENGTH_SHORT).show();

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
        Toast.makeText(getApplicationContext(), "Successfully Logged In.", Toast.LENGTH_LONG).show();
        try {
            String myUrl = "https://api.spotify.com/v1/me/tracks";
            //String to place our result in
            String result;
            //Instantiate new instance of our class
            HttpGetRequest getRequest = new HttpGetRequest();
            //Perform the doInBackground method, passing in our url
            result = getRequest.execute(myUrl).get();
        } catch (InterruptedException e) {
            Log.e(TAG, e.toString());
            Toast.makeText(getApplicationContext(), "Error in retrieving library", Toast.LENGTH_LONG).show();
        } catch (ExecutionException e) {
            Log.e(TAG, e.toString());
            Toast.makeText(getApplicationContext(), "Error in retrieving library", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onLoggedOut() {
        Log.d(TAG, "User logged out");
        Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onLoginFailed(Error var1) {
        Log.d(TAG, "Login failed");
        Toast.makeText(getApplicationContext(), "Unable to connect to account.", Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), "If account is not premium, please upgrade account to use application.", Toast.LENGTH_LONG).show();

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming", "user-library-read"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

    }

    @Override
    public void onTemporaryError() {

        Log.d(TAG, "Temporary error occurred");
        Toast.makeText(getApplicationContext(), "Unable to connect to account. Please try again.", Toast.LENGTH_SHORT).show();


        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming", "user-library-read"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);


    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d(TAG, "Received connection message: " + message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
            String result = "NO CONNECTION MADE";


            try {
                Toast.makeText(getApplicationContext(), "Attempting to get User Library.", Toast.LENGTH_LONG).show();
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


                        songList.add(song);
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.i(TAG, song.toString());
                    }
                    return userLibrary.getNext();
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to get User Library.", Toast.LENGTH_LONG).show();
                    return result;
                }
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                Toast.makeText(getApplicationContext(), "Unable to get User Library.", Toast.LENGTH_LONG).show();
                return result;
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                Toast.makeText(getApplicationContext(), "Unable to get User Library.", Toast.LENGTH_LONG).show();
                return result;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null || result.equals("null") || result.equals("NO CONNECTION MADE")) {
                fab.setVisibility(View.VISIBLE);
                shuffleButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                songBaseAdapter = new SongBaseAdapter(MainActivity.this, songList);
                listView.setAdapter(songBaseAdapter);
                showRateDialog();
            } else {
                try {
                    songBaseAdapter = new SongBaseAdapter(MainActivity.this, songList);
                    listView.setAdapter(songBaseAdapter);
                    //Some url endpoint that you may have
                    String myUrl = result;
                    //Instantiate new instance of our class
                    HttpGetRequest getRequest = new HttpGetRequest();
                    //Perform the doInBackground method, passing in our url
                    getRequest.execute(myUrl).get();
                } catch (InterruptedException e) {
                    Log.e(TAG, e.toString());
                    Toast.makeText(getApplicationContext(), "Unable to get User Library.", Toast.LENGTH_LONG).show();
                    showScreen();
                } catch (ExecutionException e) {
                    Log.e(TAG, e.toString());
                    Toast.makeText(getApplicationContext(), "Unable to get User Library.", Toast.LENGTH_LONG).show();
                    showScreen();
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

    private void playGivenSong(Song song, int i) {
        currentSongPlaying = song;
        mSelectedTrackTitle.setText(song.getTrackName() + " ~ " + song.getArtist());
        Picasso.with(MainActivity.this).load(song.getAlbumImage()).into(mSelectedTrackImage);
        currentSelected = i;
        mPlayer.playUri(null, song.getTrackValue(), 0, 0);
        selectedPosition = i;
        songBaseAdapter.notifyDataSetChanged();
        listView.smoothScrollToPosition(i);
    }


}
