package com.example.avi.ultimatespotifyplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.avi.ultimatespotifyplayer.R;
import com.example.avi.ultimatespotifyplayer.pojo.Artists;
import com.example.avi.ultimatespotifyplayer.pojo.Song;

import java.util.ArrayList;

public class SongBaseAdapter extends BaseAdapter{

    private static LayoutInflater inflater=null;
    Context context;
    ArrayList<Song> songArrayList;

    //Constructor for MusicBaseAdapter passing in the context of the activity using the adapter and the arraylist holding the music objects
    public SongBaseAdapter(Context mainActivity, ArrayList<Song> songArrayList)
    {
        this.songArrayList = songArrayList;
        context = mainActivity;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return songArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return songArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //Create a Holder class that will hold all of our layout objects
    public class Holder
    {
        TextView trackName;
        TextView artistName;
        TextView albumName;
        ImageView albumCover;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;

        //Inflate the row view with layout created to store results
        rowView = inflater.inflate(R.layout.song_adapter, parent, false);

        holder.trackName = (TextView) rowView.findViewById(R.id.textViewSongName);
        holder.artistName = (TextView) rowView.findViewById(R.id.textViewArtistName);
        holder.albumName = (TextView) rowView.findViewById(R.id.textViewAlbumName);

        //Store the music object from the position within the arraylist
        Song songObject = songArrayList.get(position);

        holder.trackName.setText(songObject.getTrackName());
        StringBuilder artists = new StringBuilder();
        for(Artists artist : songObject.getArtist())
        {
            artists.append(artist.getName() + ", ");
        }
        holder.artistName.setText(artists.toString().replaceAll(", $", ""));
        holder.albumName.setText(songObject.getAlbum());

        //Finally return rowView holding the layout for the music listview items
        return rowView;
    }
}