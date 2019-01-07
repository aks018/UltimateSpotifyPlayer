package aviee.develop.music.myultimatemusicexperienceforspotify.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Song implements Serializable {
    String artist;
    String trackName;
    String trackValue;
    String album;
    String releaseDate;
    String albumImage;
    String getTotalArtistTracks;
    List<String> artistUri;
    String songLength;
    String albumID;


    public Song(){
        this.artist = "";
        this.trackName = "";
        this.trackValue ="";
        this.album = "";
        this.releaseDate ="";
        this.albumImage = "";
        this.getTotalArtistTracks = "";
        this.artistUri = new ArrayList<>();
        this.songLength = "";
        this.albumID = "";
    }

    public Song(String songURI, String artist, String trackName, String album, String releaseDate, String totalArtistTracks,
                String songLength, String albumID, String albumImage, List<String> artistUri){
        this.trackValue = songURI;
        this.artist = artist;
        this.trackName=trackName;
        this.album = album;
        this.releaseDate = releaseDate;
        this.getTotalArtistTracks = totalArtistTracks;
        this.songLength = songLength;
        this.artistUri = artistUri;
        this.albumImage = albumImage;
        this.albumID = albumID;

    }

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

    public List<String> getArtistUri() {
        return artistUri;
    }

    public void setArtistUri(List<String> artistUri) {
        this.artistUri = artistUri;
    }

    public String getSongLength() {
        return songLength;
    }

    public void setSongLength(String songLength) {
        this.songLength = songLength;
    }

    public String getAlbumID() {
        return albumID;
    }

    public void setAlbumID(String albumID) {
        this.albumID = albumID;
    }

    @Override
    public String toString() {
        return "Song{" +
                "artist='" + artist + '\'' +
                ", trackName='" + trackName + '\'' +
                ", trackValue='" + trackValue + '\'' +
                ", album='" + album + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", albumImage='" + albumImage + '\'' +
                ", getTotalArtistTracks='" + getTotalArtistTracks + '\'' +
                ", artistUri=" + artistUri +
                ", songLength='" + songLength + '\'' +
                ", albumID='" + albumID + '\'' +
                '}';
    }
}
