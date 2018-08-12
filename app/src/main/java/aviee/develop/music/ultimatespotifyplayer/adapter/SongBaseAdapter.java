package aviee.develop.music.ultimatespotifyplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import aviee.develop.music.ultimatespotifyplayer.MainActivity;
import aviee.develop.music.ultimatespotifyplayer.YoutubeActivity;
import aviee.develop.music.ultimatespotifyplayer.pojo.Album;
import aviee.develop.music.ultimatespotifyplayer.pojo.Artists;
import aviee.develop.music.ultimatespotifyplayer.pojo.RandomItems;
import aviee.develop.music.ultimatespotifyplayer.pojo.RandomSong;
import aviee.develop.music.ultimatespotifyplayer.pojo.RandomTracks;
import aviee.develop.music.ultimatespotifyplayer.pojo.Song;

public class SongBaseAdapter extends BaseAdapter implements Filterable {

    private static LayoutInflater inflater = null;
    Context context;
    ArrayList<Song> mData;
    ArrayList<Song> mSongFilterList;
    String TAG = "SongBaseAdapter";
    ValueFilter valueFilter;
    public String currentArtist;

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
        ImageView dropDown;
    }

    @Override
    public View getView(int position, final View convertView, final ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;

        //Inflate the row view with layout created to store results
        rowView = inflater.inflate(aviee.develop.music.ultimatespotifyplayer.R.layout.song_adapter, parent, false);

        holder.trackName = (TextView) rowView.findViewById(aviee.develop.music.ultimatespotifyplayer.R.id.textViewSongName);
        holder.artistName = (TextView) rowView.findViewById(aviee.develop.music.ultimatespotifyplayer.R.id.textViewArtistName);
        holder.albumName = (TextView) rowView.findViewById(aviee.develop.music.ultimatespotifyplayer.R.id.textViewAlbumName);
        holder.dropDown = (ImageView) rowView.findViewById(aviee.develop.music.ultimatespotifyplayer.R.id.viewMusicVideo);

        //Store the music object from the position within the arraylist
        final Song songObject = mData.get(position);

        holder.trackName.setText(songObject.getTrackName());
        holder.artistName.setText(songObject.getArtist());
        holder.albumName.setText(songObject.getAlbum());
        holder.dropDown.setImageResource(aviee.develop.music.ultimatespotifyplayer.R.drawable.ic_three_dots);

        if (MainActivity.selectedPosition == position && MainActivity.currentSongPlaying != null &&
                MainActivity.currentSongPlaying == songObject) {
            rowView.setBackgroundColor(Color.DKGRAY);
            holder.trackName.setTypeface(null, Typeface.BOLD_ITALIC);
        }


        holder.dropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                popup.getMenuInflater().inflate(aviee.develop.music.ultimatespotifyplayer.R.menu.popup_menu,
                        popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case aviee.develop.music.ultimatespotifyplayer.R.id.musicVideo:
                                Intent intent = new Intent(context, YoutubeActivity.class);
                                intent.putExtra("query", songObject.getArtist() + " - " + songObject.getTrackName() + " Music Video");
                                context.startActivity(intent);
                                break;
                            case aviee.develop.music.ultimatespotifyplayer.R.id.randomSong:
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
                Log.e("MainActivity", e.toString());
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
}

