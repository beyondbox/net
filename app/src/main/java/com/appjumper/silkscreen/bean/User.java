package com.appjumper.silkscreen.bean;

/**
 * Created by yc on 16/10/6.
 *  用户模型
 */
public class User {
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
}
