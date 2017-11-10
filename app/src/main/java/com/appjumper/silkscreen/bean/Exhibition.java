package com.appjumper.silkscreen.bean;

import java.io.Serializable;

/**
 * Created by yc on 2016/12/2.
 * 展会列表
 */
public class Exhibition implements Serializable{
    private String id;
    private String title;
    private String location;
    private String time;
    private Avatar img;
    private String distance;
    private String url;
    private boolean is_read = true;
    private String create_time;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Avatar getImg() {
        return img;
    }

    public void setImg(Avatar img) {
        this.img = img;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public boolean is_read() {
        return is_read;
    }

    public void setIs_read(boolean is_read) {
        this.is_read = is_read;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
