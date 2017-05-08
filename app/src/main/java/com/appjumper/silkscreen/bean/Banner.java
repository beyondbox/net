package com.appjumper.silkscreen.bean;

import java.io.Serializable;

/**
 * Created by yc on 2016/10/26.
 */
public class Banner implements Serializable{
    private String ntypeid;
    private String id;
    private String bannerpath;
    private String ntype;
    private String width;
    private String height;

    public String getNtypeid() {
        return ntypeid;
    }

    public void setNtypeid(String ntypeid) {
        this.ntypeid = ntypeid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBannerpath() {
        return bannerpath;
    }

    public void setBannerpath(String bannerpath) {
        this.bannerpath = bannerpath;
    }

    public String getNtype() {
        return ntype;
    }

    public void setNtype(String ntype) {
        this.ntype = ntype;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
}
