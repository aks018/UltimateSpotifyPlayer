package com.example.avi.ultimatespotifyplayer.pojo;

public class Album
{
    private String id;

    private Artists[] artists;

    private External_urls external_urls;

    private String name;

    private String release_date;

    private Images[] images;

    private String release_date_precision;

    private String type;

    private String album_type;

    private String uri;

    private String href;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public Artists[] getArtists ()
    {
        return artists;
    }

    public void setArtists (Artists[] artists)
    {
        this.artists = artists;
    }

    public External_urls getExternal_urls ()
    {
        return external_urls;
    }

    public void setExternal_urls (External_urls external_urls)
    {
        this.external_urls = external_urls;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getRelease_date ()
    {
        return release_date;
    }

    public void setRelease_date (String release_date)
    {
        this.release_date = release_date;
    }

    public Images[] getImages ()
    {
        return images;
    }

    public void setImages (Images[] images)
    {
        this.images = images;
    }

    public String getRelease_date_precision ()
    {
        return release_date_precision;
    }

    public void setRelease_date_precision (String release_date_precision)
    {
        this.release_date_precision = release_date_precision;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public String getAlbum_type ()
    {
        return album_type;
    }

    public void setAlbum_type (String album_type)
    {
        this.album_type = album_type;
    }

    public String getUri ()
    {
        return uri;
    }

    public void setUri (String uri)
    {
        this.uri = uri;
    }

    public String getHref ()
    {
        return href;
    }

    public void setHref (String href)
    {
        this.href = href;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", artists = "+artists+", external_urls = "+external_urls+", name = "+name+", release_date = "+release_date+", images = "+images+", release_date_precision = "+release_date_precision+", type = "+type+", album_type = "+album_type+", uri = "+uri+", href = "+href+"]";
    }
}
