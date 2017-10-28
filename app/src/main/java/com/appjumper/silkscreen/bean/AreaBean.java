package com.appjumper.silkscreen.bean;


import java.io.Serializable;

/**
 * Created by yc on 2016/10/18.
 */
public class AreaBean implements Comparable<AreaBean>, Serializable {
    private String id;
    private String shortname;
    private String merger_name;
    private String first;
    private String lng;
    private String lat;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getMerger_name() {
        return merger_name;
    }

    public void setMerger_name(String merger_name) {
        this.merger_name = merger_name;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    @Override
    public int compareTo(AreaBean another) {
        return this.getFirst().compareTo(another.getFirst());
    }
}
