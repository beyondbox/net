package com.appjumper.silkscreen.bean;

import java.io.Serializable;

/**
 * Created by yc on 2016/12/8.
 * 个人认证
 */
public class PersonalAuthInfo implements Serializable{
    private Avatar idcard_img;
    private Avatar idcard_img_back;
    private String idcard;
    private String name;
    private String auth_status_name;
    private String auth_status;

    public Avatar getIdcard_img() {
        return idcard_img;
    }

    public void setIdcard_img(Avatar idcard_img) {
        this.idcard_img = idcard_img;
    }

    public Avatar getIdcard_img_back() {
        return idcard_img_back;
    }

    public void setIdcard_img_back(Avatar idcard_img_back) {
        this.idcard_img_back = idcard_img_back;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuth_status_name() {
        return auth_status_name;
    }

    public void setAuth_status_name(String auth_status_name) {
        this.auth_status_name = auth_status_name;
    }

    public String getAuth_status() {
        return auth_status;
    }

    public void setAuth_status(String auth_status) {
        this.auth_status = auth_status;
    }
}
