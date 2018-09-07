package aviee.develop.music.myultimatesongexperienceforspotify.pojo;

public class Items
{
    private String added_at;

    private Track track;

    public String getAdded_at ()
    {
        return added_at;
    }

    public void setAdded_at (String added_at)
    {
        this.added_at = added_at;
    }

    public Track getTrack ()
    {
        return track;
    }

    public void setTrack (Track track)
    {
        this.track = track;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [added_at = "+added_at+", track = "+track+"]";
    }
}