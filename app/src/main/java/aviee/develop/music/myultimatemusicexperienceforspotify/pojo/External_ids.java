package aviee.develop.music.myultimatemusicexperienceforspotify.pojo;

public class External_ids
{
    private String isrc;

    public String getIsrc ()
    {
        return isrc;
    }

    public void setIsrc (String isrc)
    {
        this.isrc = isrc;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [isrc = "+isrc+"]";
    }
}
