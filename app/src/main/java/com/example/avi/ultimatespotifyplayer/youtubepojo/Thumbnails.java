package com.example.avi.ultimatespotifyplayer.youtubepojo;

public class Thumbnails {
    private Default aDefault;

    private High high;

    private Medium medium;

    public Default getDefault() {
        return aDefault;
    }

    public void setDefault(Default aDefault) {
        this.aDefault = aDefault;
    }

    public High getHigh() {
        return high;
    }

    public void setHigh(High high) {
        this.high = high;
    }

    public Medium getMedium() {
        return medium;
    }

    public void setMedium(Medium medium) {
        this.medium = medium;
    }

    @Override
    public String toString() {
        return "ClassPojo [default = " + aDefault + ", high = " + high + ", medium = " + medium + "]";
    }
}
