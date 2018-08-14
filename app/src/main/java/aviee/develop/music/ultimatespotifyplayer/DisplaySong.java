package aviee.develop.music.ultimatespotifyplayer;

import android.net.http.HttpResponseCache;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;
import com.squareup.picasso.Picasso;

import org.mortbay.jetty.Main;

import aviee.develop.music.ultimatespotifyplayer.constant.Constants;
import aviee.develop.music.ultimatespotifyplayer.pojo.Song;

import static aviee.develop.music.ultimatespotifyplayer.MainActivity.mSpeechRecognizer;

public class DisplaySong extends AppCompatActivity {

    String TAG = "DisplaySong";

    Song displaySong;

    ImageView songImageView;
    TextView displayTrackName;
    TextView displayAlbumName;
    SeekBar seekBar;
    PlaybackState playbackState;
    ImageView mediaPlayController;
    TextView textViewStartTime;
    TextView textViewEndTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_song);


        displaySong = MainActivity.currentSongPlaying;

        songImageView = (ImageView) findViewById(R.id.songImageView);
        displayTrackName = findViewById(R.id.textViewTrackName);
        displayAlbumName = findViewById(R.id.textViewAlbum);
        textViewStartTime = findViewById(R.id.textViewStartTime);
        textViewEndTime = findViewById(R.id.textViewEndTime);

        seekBar = findViewById(R.id.seekBar);
        mediaPlayController = findViewById(R.id.imageViewPlayController);
        playbackState = MainActivity.mPlayer.getPlaybackState();

        setUpMediaPlayController();

        Picasso.with(DisplaySong.this).load(displaySong.getAlbumImage()).into(songImageView);
        displayTrackName.setText(displaySong.getTrackName());
        displayAlbumName.setText(displaySong.getAlbum());

        setupSeekBar();


    }

    private void setUpMediaPlayController() {
        if (playbackState.isPlaying) {
            mediaPlayController.setImageResource(R.drawable.baseline_pause_circle_filled_black_48);
        } else {
            mediaPlayController.setImageResource(R.drawable.baseline_play_circle_filled_white_black_48);
        }
        mediaPlayController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playbackState = MainActivity.mPlayer.getPlaybackState();
                if (playbackState.isPlaying) {
                    mediaPlayController.setImageResource(R.drawable.baseline_play_circle_filled_white_black_48);
                    MainActivity.mPlayer.pause(new Player.OperationCallback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Error error) {

                        }
                    });
                } else {
                    mediaPlayController.setImageResource(R.drawable.baseline_pause_circle_filled_black_48);
                    MainActivity.mPlayer.resume(new Player.OperationCallback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Error error) {

                        }
                    });
                }
            }
        });
    }

    private void setupSeekBar() {
        try {
            final int totalSeconds = Integer.parseInt(displaySong.getSongLength()) / 1000;
            seekBar.setMax(Integer.parseInt(displaySong.getSongLength()) / 1000);
            final Handler mHandler = new Handler();
            DisplaySong.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (MainActivity.mPlayer != null) {
                        playbackState = MainActivity.mPlayer.getPlaybackState();
                        int mCurrentPosition = (int) playbackState.positionMs / 1000;
                        seekBar.setProgress(0); // call these two methods before setting progress.
                        seekBar.setMax(Integer.parseInt(displaySong.getSongLength()) / 1000);
                        seekBar.setProgress(mCurrentPosition);
                        displaySong = MainActivity.currentSongPlaying;
                        Picasso.with(DisplaySong.this).load(displaySong.getAlbumImage()).into(songImageView);
                        displayTrackName.setText(displaySong.getTrackName());
                        displayAlbumName.setText(displaySong.getAlbum());

                        //Update textviews

                        textViewStartTime.setText(Constants.secondsToString(mCurrentPosition));
                        textViewEndTime.setText(Constants.secondsToString(totalSeconds - mCurrentPosition));
                    }
                    mHandler.postDelayed(this, 1000);
                }
            });

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (MainActivity.mPlayer != null && fromUser) {
                        MainActivity.mPlayer.seekToPosition(new Player.OperationCallback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Error error) {

                            }
                        }, progress * 1000);

                    }
                }
            });


        } catch (Exception e) {
            Log.e(TAG, e.toString());
            Toast.makeText(getApplicationContext(), "Unable to play song", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        MainActivity.stayWithinApplication = false;
        if (MainActivity.mPlayer != null) {
            MainActivity.mPlayer.resume(new Player.OperationCallback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Error error) {

                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        if (!MainActivity.stayWithinApplication) {
            if (MainActivity.mPlayer != null) {
                MainActivity.mPlayer.pause(new Player.OperationCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Error error) {

                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        Spotify.destroyPlayer(this);


        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }

    public void previousOnClick(View view) {
        if (MainActivity.mPlayer != null) {
            MainActivity.mPlayer.skipToPrevious(new Player.OperationCallback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Error error) {

                }
            });
        }
    }

    public void nextOnClick(View view) {
        if (MainActivity.mPlayer != null) {
            MainActivity.mPlayer.skipToNext(new Player.OperationCallback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Error error) {

                }
            });
        }
    }
}
