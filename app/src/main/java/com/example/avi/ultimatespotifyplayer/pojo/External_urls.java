package com.example.avi.ultimatespotifyplayer.pojo;

public class External_urls
{
    private String spotify;

    public String getSpotify ()
    {
        return spotify;
    }

    public void setSpotify (String spotify)
    {
        this.spotify = spotify;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [spotify = "+spotify+"]";
    }
}
