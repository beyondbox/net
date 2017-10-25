package com.appjumper.silkscreen.bean;

import java.io.Serializable;

/**
 * 求购报价
 * Created by Botx on 2017/10/19.
 */

public class AskBuyOffer implements Serializable{


    /**
     * id : 84
     * inquiry_id : 1
     * user_id : 57
     * money : 0
     * create_time : 2017-10-18 16:26:49
     * status : 1
     * is_read : 0
     * user_read : 0
     * offer_type : 1
     * offer_user_type : 0
     * price_unit :
     * offer_content :
     * offer_status :
     * mobile : 15081180805
     * user_nicename :
     * avatar :
     * name :
     * auth_status : 0
     * enterprise_name :
     * enterprise_logo :
     * enterprise_auth_status :
     * enterprise_productivity_auth_status :
     */

    private String id;
    private String inquiry_id;
    private String user_id;
    private String money;
    private String create_time;
    private String status;
    private String is_read;
    private String user_read;
    private String offer_type;
    private String offer_user_type;
    private String price_unit;
    private String offer_content;
    private String offer_status; //0-尚未处理；1-已付定金；2-全额支付；3-交易结束
    private String mobile;
    private String user_nicename;
    private String avatar;
    private String name;
    private String auth_status;
    private String enterprise_name;
    private String enterprise_logo;
    private String enterprise_auth_status;
    private String enterprise_productivity_auth_status;
    private String product_img;
    private String purchase_user_nicename;
    private String purchase_user_mobile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInquiry_id() {
        return inquiry_id;
    }

    public void setInquiry_id(String inquiry_id) {
        this.inquiry_id = inquiry_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIs_read() {
        return is_read;
    }

    public void setIs_read(String is_read) {
        this.is_read = is_read;
    }

    public String getUser_read() {
        return user_read;
    }

    public void setUser_read(String user_read) {
        this.user_read = user_read;
    }

    public String getOffer_type() {
        return offer_type;
    }

    public void setOffer_type(String offer_type) {
        this.offer_type = offer_type;
    }

    public String getOffer_user_type() {
        return offer_user_type;
    }

    public void setOffer_user_type(String offer_user_type) {
        this.offer_user_type = offer_user_type;
    }

    public String getPrice_unit() {
        return price_unit;
    }

    public void setPrice_unit(String price_unit) {
        this.price_unit = price_unit;
    }

    public String getOffer_content() {
        return offer_content;
    }

    public void setOffer_content(String offer_content) {
        this.offer_content = offer_content;
    }

    public String getOffer_status() {
        return offer_status;
    }

    public void setOffer_status(String offer_status) {
        this.offer_status = offer_status;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUser_nicename() {
        return user_nicename;
    }

    public void setUser_nicename(String user_nicename) {
        this.user_nicename = user_nicename;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuth_status() {
        return auth_status;
    }

    public void setAuth_status(String auth_status) {
        this.auth_status = auth_status;
    }

    public String getEnterprise_name() {
        return enterprise_name;
    }

    public void setEnterprise_name(String enterprise_name) {
        this.enterprise_name = enterprise_name;
    }

    public String getEnterprise_logo() {
        return enterprise_logo;
    }

    public void setEnterprise_logo(String enterprise_logo) {
        this.enterprise_logo = enterprise_logo;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getProduct_img() {
        return product_img;
    }

    public void setProduct_img(String product_img) {
        this.product_img = product_img;
    }

    public String getPurchase_user_nicename() {
        return purchase_user_nicename;
    }

    public void setPurchase_user_nicename(String purchase_user_nicename) {
        this.purchase_user_nicename = purchase_user_nicename;
    }

    public String getPurchase_user_mobile() {
        return purchase_user_mobile;
    }

    public void setPurchase_user_mobile(String purchase_user_mobile) {
        this.purchase_user_mobile = purchase_user_mobile;
    }
}
