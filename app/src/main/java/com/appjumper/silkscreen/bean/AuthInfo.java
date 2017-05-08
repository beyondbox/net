package com.appjumper.silkscreen.bean;

/**
 * Created by yc on 2016/12/2.
 * 企业认证信息
 */
public class AuthInfo {
    private String enterprise_auth_status;
    private String enterprise_productivity_auth_status;
    private String enterprise_productivity_auth_status_name;
    private String enterprise_auth_status_name;
    private String enterprise_productivity_auth_url;
    private Avatar enterprise_legal_img;
    private Avatar enterprise_licence_img;
    private Avatar enterprise_legal_img_back;

    public String getEnterprise_productivity_auth_url() {
        return enterprise_productivity_auth_url;
    }

    public void setEnterprise_productivity_auth_url(String enterprise_productivity_auth_url) {
        this.enterprise_productivity_auth_url = enterprise_productivity_auth_url;
    }

    public String getEnterprise_auth_status() {
        return enterprise_auth_status;
    }

    public void setEnterprise_auth_status(String enterprise_auth_status) {
        this.enterprise_auth_status = enterprise_auth_status;
    }

    public String getEnterprise_productivity_auth_status() {
        return enterprise_productivity_auth_status;
    }

    public void setEnterprise_productivity_auth_status(String enterprise_productivity_auth_status) {
        this.enterprise_productivity_auth_status = enterprise_productivity_auth_status;
    }

    public String getEnterprise_productivity_auth_status_name() {
        return enterprise_productivity_auth_status_name;
    }

    public void setEnterprise_productivity_auth_status_name(String enterprise_productivity_auth_status_name) {
        this.enterprise_productivity_auth_status_name = enterprise_productivity_auth_status_name;
    }

    public String getEnterprise_auth_status_name() {
        return enterprise_auth_status_name;
    }

    public void setEnterprise_auth_status_name(String enterprise_auth_status_name) {
        this.enterprise_auth_status_name = enterprise_auth_status_name;
    }

    public Avatar getEnterprise_legal_img() {
        return enterprise_legal_img;
    }

    public void setEnterprise_legal_img(Avatar enterprise_legal_img) {
        this.enterprise_legal_img = enterprise_legal_img;
    }

    public Avatar getEnterprise_licence_img() {
        return enterprise_licence_img;
    }

    public void setEnterprise_licence_img(Avatar enterprise_licence_img) {
        this.enterprise_licence_img = enterprise_licence_img;
    }

    public Avatar getEnterprise_legal_img_back() {
        return enterprise_legal_img_back;
    }

    public void setEnterprise_legal_img_back(Avatar enterprise_legal_img_back) {
        this.enterprise_legal_img_back = enterprise_legal_img_back;
    }
}
