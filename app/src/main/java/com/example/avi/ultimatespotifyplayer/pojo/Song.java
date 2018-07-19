package com.example.avi.ultimatespotifyplayer.pojo;

public class Song {
    Artists[] artist;
    String trackName;
    String trackValue;
    String album;
    String releaseDate;

    public Artists[] getArtist() {
        return artist;
    }

    public void setArtist(Artists[] artist) {
        this.artist = artist;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getTrackValue() {
        return trackValue;
    }

    public void setTrackValue(String trackValue) {
        this.trackValue = trackValue;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
