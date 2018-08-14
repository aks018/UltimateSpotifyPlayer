package aviee.develop.music.ultimatespotifyplayer.pojo;

import java.io.Serializable;
import java.util.ArrayList;

public class Song implements Serializable {
    String artist;
    String trackName;
    String trackValue;
    String album;
    String releaseDate;
    String albumImage;
    String getTotalArtistTracks;
    ArrayList<String> artistUri;
    String songLength;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
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

    public String getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(String albumImage) {
        this.albumImage = albumImage;
    }

    public String getGetTotalArtistTracks() {
        return getTotalArtistTracks;
    }

    public void setGetTotalArtistTracks(String getTotalArtistTracks) {
        this.getTotalArtistTracks = getTotalArtistTracks;
    }

    public ArrayList<String> getArtistUri() {
        return artistUri;
    }

    public void setArtistUri(ArrayList<String> artistUri) {
        this.artistUri = artistUri;
    }

    public String getSongLength() {
        return songLength;
    }

    public void setSongLength(String songLength) {
        this.songLength = songLength;
    }
}
