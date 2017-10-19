package com.appjumper.silkscreen.bean;

import java.io.Serializable;

/**
 * Created by yc on 2016/10/20.
 */
public class Avatar implements Serializable{
    private String small;
    private String width;
    private String origin;
    private String height;
    private String img_id;

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getImg_id() {
        return img_id;
    }

    public void setImg_id(String img_id) {
        this.img_id = img_id;
    }
}
