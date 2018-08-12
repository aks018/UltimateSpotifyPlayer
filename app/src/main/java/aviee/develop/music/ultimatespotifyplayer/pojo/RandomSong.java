package aviee.develop.music.ultimatespotifyplayer.pojo;

public class RandomSong {
    private RandomTracks tracks;

    public RandomTracks getTracks() {
        return tracks;
    }

    public void setTracks(RandomTracks tracks) {
        this.tracks = tracks;
    }

    @Override
    public String toString() {
        return "ClassPojo [tracks = " + tracks + "]";
    }
}
