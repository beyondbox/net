package com.appjumper.silkscreen.bean;

import java.io.Serializable;

/**
 * Created by yc on 16/10/6.
 *  用户模型
 */
public class User implements Serializable{
    private String id;
    private String last_login_ip;
    private String user_pass;
    private String user_url;
    private String sex;
    private String user_activation_key;
    private String user_login;
    private String user_type;
    private String coin;
    private String mobile;
    private Avatar avatar;
    private String signature;
    private String user_nicename;
    private String user_email;
    private String birthday;
    private String create_time;
    private String last_login_time;
    private String user_status;
    private String score;
    private String auth_status;
    private Enterprise enterprise;
    private String is_goods;
    private String is_wechat; //微信用户是否绑定了手机号 0未绑定 1绑定
    private String openid;
    private String driver_status;
    private String driver_car_status;
    private String is_vender; //是否空车配货发货厂家 0不是  1是
    private String is_purchase; //求购短信推送开关  0推送 1不推送
    private String is_car_product_message; //空车配货短信推送开关  0推送 1不推送
    private String is_fast_examine; //是否有快速审核权限 0没有  1有
    private String idcard;
    private String idcard_img;
    private String idcard_img_back;
    private String name;
    private String is_purchase_order; //求购-是否显示我的采购订单：0-不显示；1-显示
    private String admin_purchase; //求购管理员帐号0-否；1-是
    private String admin_car_product; //空车配货管理员帐号0-否；1-是
    private String admin_purchase_add; //求购官方手机号0-否；1-是


    private String driver_img;
    private String driver_img_back;
    private String driving_license_img;
    private String driving_license_img_back;
    private String driver_car_img;
    private CarInfo carinfo;


    public String getAuth_status() {
        return auth_status;
    }

    public void setAuth_status(String auth_status) {
        this.auth_status = auth_status;
    }

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLast_login_ip() {
        return last_login_ip;
    }

    public void setLast_login_ip(String last_login_ip) {
        this.last_login_ip = last_login_ip;
    }

    public String getUser_pass() {
        return user_pass;
    }

    public void setUser_pass(String user_pass) {
        this.user_pass = user_pass;
    }

    public String getUser_url() {
        return user_url;
    }

    public void setUser_url(String user_url) {
        this.user_url = user_url;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUser_activation_key() {
        return user_activation_key;
    }

    public void setUser_activation_key(String user_activation_key) {
        this.user_activation_key = user_activation_key;
    }

    public String getUser_login() {
        return user_login;
    }

    public void setUser_login(String user_login) {
        this.user_login = user_login;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getUser_nicename() {
        return user_nicename;
    }

    public void setUser_nicename(String user_nicename) {
        this.user_nicename = user_nicename;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(String last_login_time) {
        this.last_login_time = last_login_time;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getIs_goods() {
        return is_goods;
    }

    public void setIs_goods(String is_goods) {
        this.is_goods = is_goods;
    }

    public String getIs_wechat() {
        return is_wechat;
    }

    public void setIs_wechat(String is_wechat) {
        this.is_wechat = is_wechat;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getDriver_status() {
        return driver_status;
    }

    public void setDriver_status(String driver_status) {
        this.driver_status = driver_status;
    }

    public String getDriver_car_status() {
        return driver_car_status;
    }

    public void setDriver_car_status(String driver_car_status) {
        this.driver_car_status = driver_car_status;
    }

    public String getIs_vender() {
        return is_vender;
    }

    public void setIs_vender(String is_vender) {
        this.is_vender = is_vender;
    }

    public String getIs_purchase() {
        return is_purchase;
    }

    public void setIs_purchase(String is_purchase) {
        this.is_purchase = is_purchase;
    }

    public String getIs_car_product_message() {
        return is_car_product_message;
    }

    public void setIs_car_product_message(String is_car_product_message) {
        this.is_car_product_message = is_car_product_message;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getIdcard_img() {
        return idcard_img;
    }

    public void setIdcard_img(String idcard_img) {
        this.idcard_img = idcard_img;
    }

    public String getIdcard_img_back() {
        return idcard_img_back;
    }

    public void setIdcard_img_back(String idcard_img_back) {
        this.idcard_img_back = idcard_img_back;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIs_fast_examine() {
        return is_fast_examine;
    }

    public void setIs_fast_examine(String is_fast_examine) {
        this.is_fast_examine = is_fast_examine;
    }

    public String getIs_purchase_order() {
        return is_purchase_order;
    }

    public void setIs_purchase_order(String is_purchase_order) {
        this.is_purchase_order = is_purchase_order;
    }

    public String getDriver_img() {
        return driver_img;
    }

    public void setDriver_img(String driver_img) {
        this.driver_img = driver_img;
    }

    public String getDriver_img_back() {
        return driver_img_back;
    }

    public void setDriver_img_back(String driver_img_back) {
        this.driver_img_back = driver_img_back;
    }

    public String getDriving_license_img() {
        return driving_license_img;
    }

    public void setDriving_license_img(String driving_license_img) {
        this.driving_license_img = driving_license_img;
    }

    public String getDriving_license_img_back() {
        return driving_license_img_back;
    }

    public void setDriving_license_img_back(String driving_license_img_back) {
        this.driving_license_img_back = driving_license_img_back;
    }

    public String getDriver_car_img() {
        return driver_car_img;
    }

    public void setDriver_car_img(String driver_car_img) {
        this.driver_car_img = driver_car_img;
    }

    public CarInfo getCarinfo() {
        return carinfo;
    }

    public void setCarinfo(CarInfo carinfo) {
        this.carinfo = carinfo;
    }

    public String getAdmin_purchase() {
        return admin_purchase;
    }

    public void setAdmin_purchase(String admin_purchase) {
        this.admin_purchase = admin_purchase;
    }

    public String getAdmin_car_product() {
        return admin_car_product;
    }

    public void setAdmin_car_product(String admin_car_product) {
        this.admin_car_product = admin_car_product;
    }

    public String getAdmin_purchase_add() {
        return admin_purchase_add;
    }

    public void setAdmin_purchase_add(String admin_purchase_add) {
        this.admin_purchase_add = admin_purchase_add;
    }
}
