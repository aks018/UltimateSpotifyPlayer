package com.example.avi.ultimatespotifyplayer.youtubepojo;

public class RetrieveResults
{
    private String regionCode;

    private String etag;

    private YoutubeItems[] items;

    private PageInfo pageInfo;

    private String nextPageToken;

    private String kind;

    public String getRegionCode ()
    {
        return regionCode;
    }

    public void setRegionCode (String regionCode)
    {
        this.regionCode = regionCode;
    }

    public String getEtag ()
    {
        return etag;
    }

    public void setEtag (String etag)
    {
        this.etag = etag;
    }

    public YoutubeItems[] getItems ()
    {
        return items;
    }

    public void setItems (YoutubeItems[] items)
    {
        this.items = items;
    }

    public PageInfo getPageInfo ()
    {
        return pageInfo;
    }

    public void setPageInfo (PageInfo pageInfo)
    {
        this.pageInfo = pageInfo;
    }

    public String getNextPageToken ()
    {
        return nextPageToken;
    }

    public void setNextPageToken (String nextPageToken)
    {
        this.nextPageToken = nextPageToken;
    }

    public String getKind ()
    {
        return kind;
    }

    public void setKind (String kind)
    {
        this.kind = kind;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [regionCode = "+regionCode+", etag = "+etag+", items = "+items+", pageInfo = "+pageInfo+", nextPageToken = "+nextPageToken+", kind = "+kind+"]";
    }
}