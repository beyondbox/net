package com.appjumper.silkscreen.bean;

/**
 * Created by yc on 2016/12/2.
 * 行业新闻
 */
public class News {
    private String id;
    private String title;
    private String content;
    private Avatar img;
    private String update_time;
    private String create_time;
    private String url;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Avatar getImg() {
        return img;
    }

    public void setImg(Avatar img) {
        this.img = img;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
