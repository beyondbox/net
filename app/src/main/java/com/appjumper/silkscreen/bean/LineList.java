package com.appjumper.silkscreen.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by yc on 2016/11/16.
 * 物流线路列表
 */
public class LineList implements Serializable{
    private String car_load;
    private String expiry_date;
    private String enterprise_name;
    private String status;
    private String user_id;
    private String create_time;
    private String car_model;
    private Avatar enterprise_logo;
    private String type;//1是企业 2个人
    private String car_space;
    @SerializedName(value = "id", alternate = {"station_id"})
    private String id;
    private String date;
    private String user_nicename;
    private String enterprise_auth_status;
    private String to;
    private String mobile;
    private String enterprise_productivity_auth_status;
    private String from;
    private Avatar avatar;
    private String update_time;
    private String passby_name;
    private String passby;
    private String car_width;
    private String remark;
    private String car_length;
    private String car_height;
    private String number;
    private String weight;
    private String name;
    private String from_distance;
    private String distance;
    private String auth_status;
    private boolean is_read = true;
    private String line_type;
    @SerializedName(value = "official_name", alternate = {"station_name"})
    private String official_name;
    private String official_mobile;
    @SerializedName(value = "official_address", alternate = {"station_address"})
    private String official_address;

    private String product_name;
    private String productspec;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getFrom_distance() {
        return from_distance;
    }

    public void setFrom_distance(String from_distance) {
        this.from_distance = from_distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCar_load() {
        return car_load;
    }

    public void setCar_load(String car_load) {
        this.car_load = car_load;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getEnterprise_name() {
        return enterprise_name;
    }

    public void setEnterprise_name(String enterprise_name) {
        this.enterprise_name = enterprise_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getCar_model() {
        return car_model;
    }

    public void setCar_model(String car_model) {
        this.car_model = car_model;
    }

    public Avatar getEnterprise_logo() {
        return enterprise_logo;
    }

    public void setEnterprise_logo(Avatar enterprise_logo) {
        this.enterprise_logo = enterprise_logo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCar_space() {
        return car_space;
    }

    public void setCar_space(String car_space) {
        this.car_space = car_space;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser_nicename() {
        return user_nicename;
    }

    public void setUser_nicename(String user_nicename) {
        this.user_nicename = user_nicename;
    }

    public String getEnterprise_auth_status() {
        return enterprise_auth_status;
    }

    public void setEnterprise_auth_status(String enterprise_auth_status) {
        this.enterprise_auth_status = enterprise_auth_status;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEnterprise_productivity_auth_status() {
        return enterprise_productivity_auth_status;
    }

    public void setEnterprise_productivity_auth_status(String enterprise_productivity_auth_status) {
        this.enterprise_productivity_auth_status = enterprise_productivity_auth_status;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getPassby_name() {
        return passby_name;
    }

    public void setPassby_name(String passby_name) {
        this.passby_name = passby_name;
    }

    public String getPassby() {
        return passby;
    }

    public void setPassby(String passby) {
        this.passby = passby;
    }

    public String getCar_width() {
        return car_width;
    }

    public void setCar_width(String car_width) {
        this.car_width = car_width;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCar_length() {
        return car_length;
    }

    public void setCar_length(String car_length) {
        this.car_length = car_length;
    }

    public String getCar_height() {
        return car_height;
    }

    public void setCar_height(String car_height) {
        this.car_height = car_height;
    }

    public String getAuth_status() {
        return auth_status;
    }

    public void setAuth_status(String auth_status) {
        this.auth_status = auth_status;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProductspec() {
        return productspec;
    }

    public void setProductspec(String productspec) {
        this.productspec = productspec;
    }

    public boolean is_read() {
        return is_read;
    }

    public void setIs_read(boolean is_read) {
        this.is_read = is_read;
    }

    public String getLine_type() {
        return line_type;
    }

    public void setLine_type(String line_type) {
        this.line_type = line_type;
    }

    public String getOfficial_name() {
        return official_name;
    }

    public void setOfficial_name(String official_name) {
        this.official_name = official_name;
    }

    public String getOfficial_mobile() {
        return official_mobile;
    }

    public void setOfficial_mobile(String official_mobile) {
        this.official_mobile = official_mobile;
    }

    public String getOfficial_address() {
        return official_address;
    }

    public void setOfficial_address(String official_address) {
        this.official_address = official_address;
    }
}
