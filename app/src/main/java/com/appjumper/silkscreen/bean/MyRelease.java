package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * 我的发布
 * Created by Botx on 2017/8/1.
 */

public class MyRelease {

    /**
     * id : 528
     * title : 金刚网
     * subtitle : 订做
     * enterprise_name : 优加网络科技
     * img : {"origin":"http://115.28.148.207/data/upload/2017-06-08/5938a07fc41bf.jpg","width":"","height":"","small":"http://115.28.148.207/data/upload/2017-06-08/5938a07fc41bf.jpg"}
     * imgs : 2046
     * type : 1
     * isad : 0
     * user_id : 83
     * info_id : 372
     * create_time : 2017-07-11 10:17:24
     * update_time : 2017-07-11 10:17:24
     * renovate_time : null
     * expiry_date : null
     * img_list : [{"img_id":"2046","width":"639","height":"1136","origin":"http://115.28.148.207/data/upload/2017-07-11/596435345cca0.png","small":"http://115.28.148.207/data/upload/2017-07-11/596435345cca0200_200.png"}]
     */

    private String id;
    private String title;
    private String subtitle;
    private String enterprise_name;
    private Avatar img;
    private String imgs;
    private String type;
    private String isad;
    private String user_id;
    private String info_id;
    private String create_time;
    private String update_time;
    private String renovate_time;
    private String expiry_date;
    private List<Avatar> img_list;

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

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getEnterprise_name() {
        return enterprise_name;
    }

    public void setEnterprise_name(String enterprise_name) {
        this.enterprise_name = enterprise_name;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsad() {
        return isad;
    }

    public void setIsad(String isad) {
        this.isad = isad;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public Avatar getImg() {
        return img;
    }

    public void setImg(Avatar img) {
        this.img = img;
    }

    public List<Avatar> getImg_list() {
        return img_list;
    }

    public void setImg_list(List<Avatar> img_list) {
        this.img_list = img_list;
    }

    public String getRenovate_time() {
        return renovate_time;
    }

    public void setRenovate_time(String renovate_time) {
        this.renovate_time = renovate_time;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }
}
