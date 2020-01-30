package com.samer.wear.activity;

import java.io.Serializable;

public class Note implements Serializable {
    private String title = "";
    private String id    = "";
    private String lat   = "";
    private String longi = "";

    public Note(String id, String title, String lat, String longi) {
        this.title = title;
        this.id = id;
        this.lat = lat;
        this.longi = longi;
    }

    public Note(String id, String title){
        this.id = id;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }
}
