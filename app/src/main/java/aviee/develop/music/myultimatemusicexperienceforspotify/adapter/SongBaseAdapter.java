package aviee.develop.music.myultimatemusicexperienceforspotify.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;

import org.mortbay.jetty.Main;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import aviee.develop.music.myultimatemusicexperienceforspotify.MainActivity;
import aviee.develop.music.myultimatemusicexperienceforspotify.R;
import aviee.develop.music.myultimatemusicexperienceforspotify.YoutubeActivity;
import aviee.develop.music.myultimatemusicexperienceforspotify.pojo.Album;
import aviee.develop.music.myultimatemusicexperienceforspotify.pojo.Artists;
import aviee.develop.music.myultimatemusicexperienceforspotify.pojo.Items;
import aviee.develop.music.myultimatemusicexperienceforspotify.pojo.RandomItems;
import aviee.develop.music.myultimatemusicexperienceforspotify.pojo.RandomSong;
import aviee.develop.music.myultimatemusicexperienceforspotify.pojo.RandomTracks;
import aviee.develop.music.myultimatemusicexperienceforspotify.pojo.Song;
import aviee.develop.music.myultimatemusicexperienceforspotify.pojo.Track;
import aviee.develop.music.myultimatemusicexperienceforspotify.pojo.UserLibrary;

public class SongBaseAdapter extends BaseAdapter implements Filterable, Serializable {

    private static LayoutInflater inflater = null;
    Context context;
    ArrayList<Song> mData;
    ArrayList<Song> mSongFilterList;
    String TAG = "SongBaseAdapter";
    ValueFilter valueFilter;
    public String currentArtist;
    public WebView wv;

    //Constructor for MusicBaseAdapter passing in the context of the activity using the adapter and the arraylist holding the music objects
    public SongBaseAdapter(Context mainActivity, ArrayList<Song> songArrayList) {
        mData = songArrayList;
        mSongFilterList = mData;
        context = mainActivity;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }


    //Create a Holder class that will hold all of our layout objects
    public class Holder {
        TextView trackName;
        TextView artistName;
        TextView albumName;
        TextView tilda;
        ImageView dropDown;
    }

    @Override
    public View getView(int position, final View convertView, final ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;

        //Inflate the row view with layout created to store results
        rowView = inflater.inflate(aviee.develop.music.myultimatemusicexperienceforspotify.R.layout.song_adapter, parent, false);

        holder.trackName = (TextView) rowView.findViewById(aviee.develop.music.myultimatemusicexperienceforspotify.R.id.textViewSongName);
        holder.artistName = (TextView) rowView.findViewById(aviee.develop.music.myultimatemusicexperienceforspotify.R.id.textViewArtistName);
        holder.albumName = (TextView) rowView.findViewById(aviee.develop.music.myultimatemusicexperienceforspotify.R.id.textViewAlbumName);
        holder.dropDown = (ImageView) rowView.findViewById(aviee.develop.music.myultimatemusicexperienceforspotify.R.id.viewMusicVideo);
        holder.tilda = (TextView) rowView.findViewById(R.id.textView2);

        //Store the music object from the position within the ArrayList
        final Song songObject = mData.get(position);

        holder.trackName.setText(songObject.getTrackName());
        holder.artistName.setText(songObject.getArtist());
        holder.albumName.setText(songObject.getAlbum());
        holder.dropDown.setImageResource(R.drawable.baseline_more_vert_white_24);

        if (MainActivity.selectedPosition == position && MainActivity.currentSongPlaying != null &&
                MainActivity.currentSongPlaying == songObject) {
            holder.trackName.setTextColor(Color.parseColor("#90EE90"));
            holder.albumName.setTextColor(Color.parseColor("#90EE90"));
            holder.artistName.setTextColor(Color.parseColor("#90EE90"));
            holder.tilda.setTextColor(Color.parseColor("#90EE90"));
            holder.trackName.setTypeface(null, Typeface.BOLD_ITALIC);
        }


        holder.dropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                popup.getMenuInflater().inflate(aviee.develop.music.myultimatemusicexperienceforspotify.R.menu.popup_menu,
                        popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case aviee.develop.music.myultimatemusicexperienceforspotify.R.id.musicVideo:
                                Intent intent = new Intent(context, YoutubeActivity.class);
                                intent.putExtra("query", songObject.getArtist() + " - " + songObject.getTrackName() + " Music Video");
                                Log.i(TAG, songObject.getArtist() + " - " + songObject.getTrackName() + " Official Music Video");
                                context.startActivity(intent);
                                break;
                            case aviee.develop.music.myultimatemusicexperienceforspotify.R.id.randomSong:
                                currentArtist = songObject.getArtist();
                                Random random = new Random();
                                int offsetValue = random.nextInt(5000 - 0) + 0;
                                //Some url endpoint that you may have
                                String myUrl = "https://api.spotify.com/v1/search?q=" + songObject.getArtist() + "&type=track&market=US&limit=1&offset=" + Integer.toString(offsetValue);
                                //String to place our result in
                                String result;
                                //Instantiate new instance of our class
                                HttpGetRequest getRequest = new HttpGetRequest();
                                //Perform the doInBackground method, passing in our url
                                try {
                                    getRequest.execute(myUrl).get();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.action_alarms:
                                Toast.makeText(context, "Alarms are coming soon.", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });

            }
        });

        //Finally return rowView holding the layout for the music listview items
        return rowView;
    }

    public class HttpGetRequest extends AsyncTask<String, Void, Song> {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Song doInBackground(String... params) {
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

                connection.setRequestProperty("Authorization", "Bearer " + MainActivity.token);
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


                    RandomSong randomSong = objectMapper.readValue(responseBodyReader, RandomSong.class);
                    RandomTracks randomTracks = randomSong.getTracks();

                    int max = Integer.parseInt(randomTracks.getTotal());

                    Song song = new Song();
                    song.setGetTotalArtistTracks(randomTracks.getTotal());

                    for (RandomItems randomItems : randomSong.getTracks().getItems()) {
                        song.setAlbum(randomItems.getAlbum().getName());
                        Album album = randomItems.getAlbum();
                        song.setAlbumImage(album.getImages()[0].getUrl());
                        song.setReleaseDate(randomItems.getAlbum().getRelease_date());
                        StringBuilder artists = new StringBuilder();
                        ArrayList<String> arrayList = new ArrayList<>();
                        for (Artists artist : randomItems.getArtists()) {
                            artists.append(artist.getName() + ", ");
                            arrayList.add(artist.getUri());
                        }
                        final String allArtists = artists.toString().replaceAll(", $", "");
                        song.setArtist(allArtists);
                        song.setTrackValue(randomItems.getUri());
                        song.setTrackName(randomItems.getName());
                        song.setArtistUri(arrayList);

                    }
                    return song;
                } else {
                    return null;
                }
            } catch (IOException e) {
                Log.e(TAG, e.toString());

            }


            return null;
        }

        @Override
        protected void onPostExecute(Song song) {
            super.onPostExecute(song);
            if (song.getArtist() != null) {

                MainActivity.mSelectedTrackTitle.setText(song.getTrackName() + " ~ " + song.getArtist());
                Picasso.with(context).load(song.getAlbumImage()).into(MainActivity.mSelectedTrackImage);
                MainActivity.mPlayer.refreshCache();
                MainActivity.mPlayer.playUri(null, song.getTrackValue(), 0, 0);
                MainActivity.currentSongPlaying = song;
                return;
            }
            Random random = new Random();
            int offsetValue = random.nextInt(Integer.parseInt(song.getGetTotalArtistTracks()) - 0) + 0;

            String myUrl = "https://api.spotify.com/v1/search?q=" + currentArtist + "&type=track&market=US&limit=1&offset=" + Integer.toString(offsetValue);
            //String to place our result in
            String result;
            //Instantiate new instance of our class
            HttpGetRequest getRequest = new HttpGetRequest();
            //Perform the doInBackground method, passing in our url
            try {
                getRequest.execute(myUrl).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


        }
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                List<Song> filterList = new ArrayList<>();
                for (int i = 0; i < mSongFilterList.size(); i++) {
                    Song song = mSongFilterList.get(i);
                    if (song.getTrackName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filterList.add(song);
                        continue;
                    } else if (song.getAlbum().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filterList.add(song);
                        continue;
                    }
                    if (song.getArtist().toString().toLowerCase().contains(constraint.toString().toLowerCase()) && !constraint.equals(",")) {
                        filterList.add(song);
                        continue;
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mSongFilterList.size();
                results.values = mSongFilterList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mData = (ArrayList<Song>) results.values;
            notifyDataSetChanged();
        }

    }


    private class CustomeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1 == null || e2 == null) return false;
            if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1) return false;
            else {
                try { // right to left swipe .. go to next page
                    if (e1.getX() - e2.getX() > 100 && Math.abs(velocityX) > 800) {
                        //do your stuff
                        show_toast("swipe left");
                        return true;
                    } //left to right swipe .. go to prev page
                    else if (e2.getX() - e1.getX() > 100 && Math.abs(velocityX) > 800) {
                        //do your stuff
                        show_toast("swipe right");
                        return true;
                    } //bottom to top, go to next document
                    else if (e1.getY() - e2.getY() > 100 && Math.abs(velocityY) > 800
                            && wv.getScrollY() >= wv.getScale() * (wv.getContentHeight() - wv.getHeight())) {
                        //do your stuff
                        show_toast("swipe up");
                        return true;
                    } //top to bottom, go to prev document
                    else if (e2.getY() - e1.getY() > 100 && Math.abs(velocityY) > 800) {
                        //do your stuff
                        show_toast("swipe down");
                        return true;
                    }
                } catch (Exception e) { // nothing
                }
                return false;
            }
        }

        void show_toast(final String text) {
            Toast t = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            t.show();
        }
    }


    public class OnSwipeTouchListener implements View.OnTouchListener {
        private GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context c) {
            gestureDetector = new GestureDetector(c, new GestureListener());
        }

        public boolean onTouch(final View view, final MotionEvent motionEvent) {
            return gestureDetector.onTouchEvent(motionEvent);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            // Determines the fling velocity and then fires the appropriate swipe event accordingly
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                        }
                    } else {
                        if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffY > 0) {
                                onSwipeDown();
                            } else {
                                onSwipeUp();
                            }
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        public void onSwipeRight() {
            wv.goBack();
            show_toast("right");
        }

        public void onSwipeLeft() {
            wv.goForward();
            show_toast("left");

        }

        public void onSwipeUp() {
            show_toast("up");

        }

        public void onSwipeDown() {
            show_toast("down");

        }

        void show_toast(final String text) {
            Toast t = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            t.show();
        }
    }
}

