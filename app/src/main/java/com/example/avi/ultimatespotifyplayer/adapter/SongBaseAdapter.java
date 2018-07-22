package com.example.avi.ultimatespotifyplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avi.ultimatespotifyplayer.MainActivity;
import com.example.avi.ultimatespotifyplayer.R;
import com.example.avi.ultimatespotifyplayer.YoutubeActivity;
import com.example.avi.ultimatespotifyplayer.pojo.Artists;
import com.example.avi.ultimatespotifyplayer.pojo.Song;

import java.util.ArrayList;
import java.util.List;

public class SongBaseAdapter extends BaseAdapter implements Filterable {

    private static LayoutInflater inflater = null;
    Context context;
    ArrayList<Song> mData;
    ArrayList<Song> mSongFilterList;
    String TAG = "SongBaseAdapter";
    ValueFilter valueFilter;


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
        ImageView youtubeVideo;
    }

    @Override
    public View getView(int position, final View convertView, final ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;

        //Inflate the row view with layout created to store results
        rowView = inflater.inflate(R.layout.song_adapter, parent, false);

        holder.trackName = (TextView) rowView.findViewById(R.id.textViewSongName);
        holder.artistName = (TextView) rowView.findViewById(R.id.textViewArtistName);
        holder.albumName = (TextView) rowView.findViewById(R.id.textViewAlbumName);
        holder.youtubeVideo = (ImageView) rowView.findViewById(R.id.viewMusicVideo);


        //Store the music object from the position within the arraylist
        final Song songObject = mData.get(position);

        holder.trackName.setText(songObject.getTrackName());
        StringBuilder artists = new StringBuilder();
        for (Artists artist : songObject.getArtist()) {
            artists.append(artist.getName() + ", ");
        }
        holder.artistName.setText(artists.toString().replaceAll(", $", ""));
        holder.albumName.setText(songObject.getAlbum());
        holder.youtubeVideo.setImageResource(R.drawable.baseline_arrow_forward_ios_24);

        holder.youtubeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "artist: " + songObject.getArtist());
                Log.d(TAG, "track: " + songObject.getTrackName());
                Toast.makeText(parent.getContext(), "button clicked: " + songObject.getTrackName(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, YoutubeActivity.class);
                context.startActivity(intent);

            }
        });

        //Finally return rowView holding the layout for the music listview items
        return rowView;
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
                    StringBuilder artists = new StringBuilder();
                    for (Artists artist : song.getArtist()) {
                        artists.append(artist.getName() + ", ");
                    }
                    if (artists.toString().toLowerCase().contains(constraint.toString().toLowerCase()) && !constraint.equals(",")) {
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

