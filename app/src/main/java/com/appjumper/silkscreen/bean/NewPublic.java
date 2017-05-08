package com.appjumper.silkscreen.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yc on 2016/12/3.
 * 最新发布
 */
public class NewPublic implements Serializable {
    private String isad;
    private Avatar img;
    private String subtitle;
    private String id;
    private String enterprise_name;
    private String title;
    private String update_time;
    private String user_id;
    private String type;
    private String info_id;
    private String create_time;
    private List<Avatar> img_list;

    public List<Avatar> getImg_list() {
        return img_list;
    }

    public void setImg_list(List<Avatar> img_list) {
        this.img_list = img_list;
    }

    public String getIsad() {
        return isad;
    }

    public void setIsad(String isad) {
        this.isad = isad;
    }

    public Avatar getImg() {
        return img;
    }

    public void setImg(Avatar img) {
        this.img = img;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnterprise_name() {
        return enterprise_name;
    }

    public void setEnterprise_name(String enterprise_name) {
        this.enterprise_name = enterprise_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInfo_id() {
        return info_id;
    }

    public void setInfo_id(String info_id) {
        this.info_id = info_id;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
