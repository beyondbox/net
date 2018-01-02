package com.appjumper.silkscreen.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yc on 2016/11/22.
 * 企业信息
 */
public class Enterprise implements Serializable{

    private String enterprise_capacity_num;
    private String enterprise_name;
    private String enterprise_area;
    private List<Avatar> img_list;
    private String enterprise_tel;
    private String enterprise_reg_date;
    private String enterprise_reg_money;
    private String status;
    private String enterprise_legal_img;
    private String enterprise_legal_img_back;
    private String create_time;
    private Avatar enterprise_logo;
    private String enterprise_imgs;
    private String enterprise_contacts;
    private String enterprise_machine_num;
    private String enterprise_website;
    private String enterprise_licence_img;
    private String enterprise_qq;
    private String enterprise_address;
    private String enterprise_intro;
    private String enterprise_auth_status; //企业认证
    private String enterprise_id;
    private String enterprise_staff_num;
    private String enterprise_productivity_auth_status; //企业生产力认证
    private String update_time;
    private String distance;
    private String jiagong;
    private String dingzuo;
    private String xianhuo;
    private String line_num;
    private String enterprise_mobile;
    private String collection;
    private String url;
    private String lng;
    private String lat;
    private List<Product> service_xianhuo;
    private List<Product> service_dingzuo;
    private List<Product> service_jiagong;
    private List<LineList> line;
    private List<NewPublic> newpublic;
    private String auth_status;
    private String enterprise_corporate_representative;
    private String enterprise_corporate;
    private String enterprise_registration_mark;

    private String user_auth_status; //个人认证



    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public List<NewPublic> getNewpublic() {
        return newpublic;
    }

    public void setNewpublic(List<NewPublic> newpublic) {
        this.newpublic = newpublic;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getJiagong() {
        return jiagong;
    }

    public void setJiagong(String jiagong) {
        this.jiagong = jiagong;
    }

    public String getDingzuo() {
        return dingzuo;
    }

    public void setDingzuo(String dingzuo) {
        this.dingzuo = dingzuo;
    }

    public String getXianhuo() {
        return xianhuo;
    }

    public void setXianhuo(String xianhuo) {
        this.xianhuo = xianhuo;
    }

    public String getLine_num() {
        return line_num;
    }

    public void setLine_num(String line_num) {
        this.line_num = line_num;
    }

    public List<Product> getService_xianhuo() {
        return service_xianhuo;
    }

    public void setService_xianhuo(List<Product> service_xianhuo) {
        this.service_xianhuo = service_xianhuo;
    }

    public List<Product> getService_dingzuo() {
        return service_dingzuo;
    }

    public void setService_dingzuo(List<Product> service_dingzuo) {
        this.service_dingzuo = service_dingzuo;
    }

    public List<Product> getService_jiagong() {
        return service_jiagong;
    }

    public void setService_jiagong(List<Product> service_jiagong) {
        this.service_jiagong = service_jiagong;
    }

    public List<LineList> getLine() {
        return line;
    }

    public void setLine(List<LineList> line) {
        this.line = line;
    }

    public List<Avatar> getImg_list() {
        return img_list;
    }

    public void setImg_list(List<Avatar> img_list) {
        this.img_list = img_list;
    }

    public String getEnterprise_reg_money() {
        return enterprise_reg_money;
    }

    public void setEnterprise_reg_money(String enterprise_reg_money) {
        this.enterprise_reg_money = enterprise_reg_money;
    }

    public String getEnterprise_capacity_num() {
        return enterprise_capacity_num;
    }

    public void setEnterprise_capacity_num(String enterprise_capacity_num) {
        this.enterprise_capacity_num = enterprise_capacity_num;
    }

    public String getEnterprise_name() {
        return enterprise_name;
    }

    public void setEnterprise_name(String enterprise_name) {
        this.enterprise_name = enterprise_name;
    }

    public String getEnterprise_area() {
        return enterprise_area;
    }

    public void setEnterprise_area(String enterprise_area) {
        this.enterprise_area = enterprise_area;
    }

    public String getEnterprise_tel() {
        return enterprise_tel;
    }

    public void setEnterprise_tel(String enterprise_tel) {
        this.enterprise_tel = enterprise_tel;
    }

    public String getEnterprise_reg_date() {
        return enterprise_reg_date;
    }

    public void setEnterprise_reg_date(String enterprise_reg_date) {
        this.enterprise_reg_date = enterprise_reg_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEnterprise_legal_img() {
        return enterprise_legal_img;
    }

    public void setEnterprise_legal_img(String enterprise_legal_img) {
        this.enterprise_legal_img = enterprise_legal_img;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public Avatar getEnterprise_logo() {
        return enterprise_logo;
    }

    public void setEnterprise_logo(Avatar enterprise_logo) {
        this.enterprise_logo = enterprise_logo;
    }

    public String getEnterprise_imgs() {
        return enterprise_imgs;
    }

    public void setEnterprise_imgs(String enterprise_imgs) {
        this.enterprise_imgs = enterprise_imgs;
    }

    public String getEnterprise_contacts() {
        return enterprise_contacts;
    }

    public void setEnterprise_contacts(String enterprise_contacts) {
        this.enterprise_contacts = enterprise_contacts;
    }

    public String getEnterprise_machine_num() {
        return enterprise_machine_num;
    }

    public void setEnterprise_machine_num(String enterprise_machine_num) {
        this.enterprise_machine_num = enterprise_machine_num;
    }

    public String getEnterprise_website() {
        return enterprise_website;
    }

    public void setEnterprise_website(String enterprise_website) {
        this.enterprise_website = enterprise_website;
    }

    public String getEnterprise_licence_img() {
        return enterprise_licence_img;
    }

    public void setEnterprise_licence_img(String enterprise_licence_img) {
        this.enterprise_licence_img = enterprise_licence_img;
    }

    public String getEnterprise_qq() {
        return enterprise_qq;
    }

    public void setEnterprise_qq(String enterprise_qq) {
        this.enterprise_qq = enterprise_qq;
    }

    public String getEnterprise_address() {
        return enterprise_address;
    }

    public void setEnterprise_address(String enterprise_address) {
        this.enterprise_address = enterprise_address;
    }

    public String getEnterprise_intro() {
        return enterprise_intro;
    }

    public void setEnterprise_intro(String enterprise_intro) {
        this.enterprise_intro = enterprise_intro;
    }

    public String getEnterprise_auth_status() {
        return enterprise_auth_status;
    }

    public void setEnterprise_auth_status(String enterprise_auth_status) {
        this.enterprise_auth_status = enterprise_auth_status;
    }

    public String getEnterprise_id() {
        return enterprise_id;
    }

    public void setEnterprise_id(String enterprise_id) {
        this.enterprise_id = enterprise_id;
    }

    public String getEnterprise_staff_num() {
        return enterprise_staff_num;
    }

    public void setEnterprise_staff_num(String enterprise_staff_num) {
        this.enterprise_staff_num = enterprise_staff_num;
    }

    public String getEnterprise_productivity_auth_status() {
        return enterprise_productivity_auth_status;
    }

    public void setEnterprise_productivity_auth_status(String enterprise_productivity_auth_status) {
        this.enterprise_productivity_auth_status = enterprise_productivity_auth_status;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getEnterprise_mobile() {
        return enterprise_mobile;
    }

    public void setEnterprise_mobile(String enterprise_mobile) {
        this.enterprise_mobile = enterprise_mobile;
    }

    public String getAuth_status() {
        return auth_status;
    }

    public void setAuth_status(String auth_status) {
        this.auth_status = auth_status;
    }


    public String getUser_auth_status() {
        return user_auth_status;
    }

    public void setUser_auth_status(String user_auth_status) {
        this.user_auth_status = user_auth_status;
    }

    public String getEnterprise_legal_img_back() {
        return enterprise_legal_img_back;
    }

    public void setEnterprise_legal_img_back(String enterprise_legal_img_back) {
        this.enterprise_legal_img_back = enterprise_legal_img_back;
    }

    public String getEnterprise_corporate_representative() {
        return enterprise_corporate_representative;
    }

    public void setEnterprise_corporate_representative(String enterprise_corporate_representative) {
        this.enterprise_corporate_representative = enterprise_corporate_representative;
    }

    public String getEnterprise_corporate() {
        return enterprise_corporate;
    }

    public void setEnterprise_corporate(String enterprise_corporate) {
        this.enterprise_corporate = enterprise_corporate;
    }

    public String getEnterprise_registration_mark() {
        return enterprise_registration_mark;
    }

    public void setEnterprise_registration_mark(String enterprise_registration_mark) {
        this.enterprise_registration_mark = enterprise_registration_mark;
    }
}
