package aviee.develop.music.myultimatemusicexperienceforspotify;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.squareup.picasso.Picasso;

import org.mortbay.jetty.Main;

import aviee.develop.music.myultimatemusicexperienceforspotify.pojo.Song;

public class AlarmActivity extends AppCompatActivity {

    SwipeMenuListView listView;
    BottomNavigationView bottomNavigationView;

    Toolbar toolbar;
    public static TextView mSelectedTrackTitle;
    public static ImageView mSelectedTrackImage;
    public static ImageView mPlayerControl;
    PlaybackState playbackState;

    Song currentSong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        listView = findViewById(R.id.alarmListView);
        playbackState = MainActivity.mPlayer.getPlaybackState();


        createMenu();
        setUpUI();
        toolbarOnClick();
        createBottomNavigationView();
        setUpMPlayerListener();
    }

    private void createBottomNavigationView(){

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_library:
                                finish();
                            case R.id.action_alarms:
                               break;
                        }
                        return true;
                    }
                });
        //bottomNavigationView.setSelectedItemId(R.id.action_alarms);

    }

    private void createMenu() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2Px(getApplicationContext(), 90));
                // set item title
                openItem.setTitle("Open");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2Px(getApplicationContext(), 90));
                // set a icon
                deleteItem.setIcon(R.mipmap.baseline_delete_forever_white_24);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        listView.setMenuCreator(creator);
    }


    public static int dp2Px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void setUpUI(){
        toolbar = (Toolbar) findViewById(R.id.toolbar4);

        //Set up player controller
        mSelectedTrackTitle = (TextView) findViewById(R.id.selected_track_title);
        mSelectedTrackImage = (ImageView) findViewById(R.id.selected_track_image);
        mPlayerControl = (ImageView) findViewById(R.id.player_control);

        if(MainActivity.currentSongPlaying!=null){
            currentSong = MainActivity.currentSongPlaying;
            mSelectedTrackTitle.setText(currentSong.getTrackName() + " ~ " + currentSong.getArtist());
            Picasso.with(AlarmActivity.this).load(currentSong.getAlbumImage()).into(mSelectedTrackImage);
            if(!MainActivity.pause){
                mPlayerControl.setImageResource(R.drawable.baseline_pause_24);
            }
            else{
                mPlayerControl.setImageResource(R.drawable.baseline_play_arrow_24);
            }
        }
        else
        {
            mPlayerControl.setImageResource(0);

        }
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
                if (MainActivity.currentSongPlaying != null) {
                    MainActivity.stayWithinApplication = true;
                    Intent intent = new Intent(AlarmActivity.this, DisplaySong.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Can only view Music Player for songs in library currently.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /*
       Params: None
       Returns: Void
       Functionality: Set up the music player listener here to handle user interaction with music player
     */
    private void setUpMPlayerListener(){
        if (playbackState.isPlaying) {
            mPlayerControl.setImageResource(R.drawable.baseline_pause_24);
        } else {
            mPlayerControl.setImageResource(R.drawable.baseline_play_arrow_24);
        }
        mPlayerControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playbackState = MainActivity.mPlayer.getPlaybackState();
                if (playbackState.isPlaying) {
                    mPlayerControl.setImageResource(R.drawable.baseline_play_arrow_24);
                    MainActivity.mPlayer.pause(new Player.OperationCallback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Error error) {

                        }
                    });
                } else {
                    mPlayerControl.setImageResource(R.drawable.baseline_pause_24);
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
}
