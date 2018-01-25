package com.appjumper.silkscreen.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 求购
 * Created by Botx on 2017/10/19.
 */

public class AskBuy implements Serializable{


    /**
     * id : 3
     * img :
     * user_nicename :
     * imgs : 4705,4706
     * consult_num : 0
     * offer_num : 0
     * expiry_date : 2017-10-18 17:18:00
     * create_time : 2017-10-18 17:18:16
     * purchase_status : 0
     * product_id : 14
     * product_name : 包塑丝
     * purchase_content : state
     * user_id : 54
     * update_time : 2017-10-18 17:18:16
     * examine_status : 0
     * adviser_id :
     * adviser_mobile :
     * mobile : 15233217800
     * nickname : 33333
     * avatar : avatar
     * name : 赵策
     * auth_status : 2
     * liveness_count : 1313
     * enterprise_id : 54
     * enterprise_name : EnterpriseName
     * enterprise_logo : http://115.28.148.207/data/upload/2017-09-07/59b0f8dda4a2a.jpg
     * enterprise_address : 图一
     * enterprise_auth_status : 2
     * enterprise_productivity_auth_status : 2
     * img_list : [{"img_id":"4705","width":"750","height":"498","origin":"http://115.28.148.207/data/upload/2017-10-18/59e71c56efbbf.jpg","small":"http://115.28.148.207/data/upload/2017-10-18/59e71c56efbbf200_200.jpg"},{"img_id":"4706","width":"750","height":"500","origin":"http://115.28.148.207/data/upload/2017-10-18/59e71c56f0cda.jpg","small":"http://115.28.148.207/data/upload/2017-10-18/59e71c56f0cda200_200.jpg"}]
     */

    private String id;
    private String img;
    private String user_nicename;
    private String imgs;
    private String consult_num;
    private String offer_num;
    private String expiry_date;
    private String create_time;
    private String purchase_status;
    private String product_id;
    private String product_name;
    private String purchase_content;
    private String user_id;
    private String update_time;
    private String examine_status;
    private String adviser_id;
    private String adviser_mobile;
    private String mobile;
    private String nickname;
    private String avatar;
    private String name;
    private String auth_status;
    private String liveness_count;
    private String enterprise_id;
    private String enterprise_name;
    private String enterprise_logo;
    private String enterprise_address;
    private String enterprise_auth_status;
    private String enterprise_productivity_auth_status;
    private String adviser_avatar;
    private String adviser_nicename;
    private String examine_refusal_reason;
    private String offer_num_read;
    private List<Avatar> img_list;
    private List<AskBuyOffer> offer_list;

    private String order_id;
    private String purchase_num;
    private String pay_type;
    private String pay_money;
    private String offer_money;
    private String purchase_unit;
    private String surplus_money;
    private String pruchase_type;
    private String address;
    private String remarks;
    private String purchase_id;
    private String product_img;
    private String siwangjia_short; //是否由丝网+进行短途运输（0-是，默认；1-否）
    private String offer_name;
    private String offer_mobile;
    private String weight;
    private String weight_unit;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUser_nicename() {
        return user_nicename;
    }

    public void setUser_nicename(String user_nicename) {
        this.user_nicename = user_nicename;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public String getConsult_num() {
        return consult_num;
    }

    public void setConsult_num(String consult_num) {
        this.consult_num = consult_num;
    }

    public String getOffer_num() {
        return offer_num;
    }

    public void setOffer_num(String offer_num) {
        this.offer_num = offer_num;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getPurchase_status() {
        return purchase_status;
    }

    public void setPurchase_status(String purchase_status) {
        this.purchase_status = purchase_status;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getPurchase_content() {
        return purchase_content;
    }

    public void setPurchase_content(String purchase_content) {
        this.purchase_content = purchase_content;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getExamine_status() {
        return examine_status;
    }

    public void setExamine_status(String examine_status) {
        this.examine_status = examine_status;
    }

    public String getAdviser_id() {
        return adviser_id;
    }

    public void setAdviser_id(String adviser_id) {
        this.adviser_id = adviser_id;
    }

    public String getAdviser_mobile() {
        return adviser_mobile;
    }

    public void setAdviser_mobile(String adviser_mobile) {
        this.adviser_mobile = adviser_mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public String getLiveness_count() {
        return liveness_count;
    }

    public void setLiveness_count(String liveness_count) {
        this.liveness_count = liveness_count;
    }

    public String getEnterprise_id() {
        return enterprise_id;
    }

    public void setEnterprise_id(String enterprise_id) {
        this.enterprise_id = enterprise_id;
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

    public String getEnterprise_address() {
        return enterprise_address;
    }

    public void setEnterprise_address(String enterprise_address) {
        this.enterprise_address = enterprise_address;
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

    public List<Avatar> getImg_list() {
        return img_list;
    }

    public void setImg_list(List<Avatar> img_list) {
        this.img_list = img_list;
    }

    public List<AskBuyOffer> getOffer_list() {
        return offer_list;
    }

    public void setOffer_list(List<AskBuyOffer> offer_list) {
        this.offer_list = offer_list;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAdviser_avatar() {
        return adviser_avatar;
    }

    public void setAdviser_avatar(String adviser_avatar) {
        this.adviser_avatar = adviser_avatar;
    }

    public String getAdviser_nicename() {
        return adviser_nicename;
    }

    public void setAdviser_nicename(String adviser_nicename) {
        this.adviser_nicename = adviser_nicename;
    }

    public String getPurchase_num() {
        return purchase_num;
    }

    public void setPurchase_num(String purchase_num) {
        this.purchase_num = purchase_num;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getPay_money() {
        return pay_money;
    }

    public void setPay_money(String pay_money) {
        this.pay_money = pay_money;
    }

    public String getOffer_money() {
        return offer_money;
    }

    public void setOffer_money(String offer_money) {
        this.offer_money = offer_money;
    }

    public String getPurchase_unit() {
        return purchase_unit;
    }

    public void setPurchase_unit(String purchase_unit) {
        this.purchase_unit = purchase_unit;
    }

    public String getSurplus_money() {
        return surplus_money;
    }

    public void setSurplus_money(String surplus_money) {
        this.surplus_money = surplus_money;
    }

    public String getPruchase_type() {
        return pruchase_type;
    }

    public void setPruchase_type(String pruchase_type) {
        this.pruchase_type = pruchase_type;
    }

    public String getExamine_refusal_reason() {
        return examine_refusal_reason;
    }

    public void setExamine_refusal_reason(String examine_refusal_reason) {
        this.examine_refusal_reason = examine_refusal_reason;
    }

    public String getOffer_num_read() {
        return offer_num_read;
    }

    public void setOffer_num_read(String offer_num_read) {
        this.offer_num_read = offer_num_read;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getPurchase_id() {
        return purchase_id;
    }

    public void setPurchase_id(String purchase_id) {
        this.purchase_id = purchase_id;
    }

    public String getProduct_img() {
        return product_img;
    }

    public void setProduct_img(String product_img) {
        this.product_img = product_img;
    }

    public String getSiwangjia_short() {
        return siwangjia_short;
    }

    public void setSiwangjia_short(String siwangjia_short) {
        this.siwangjia_short = siwangjia_short;
    }

    public String getOffer_name() {
        return offer_name;
    }

    public void setOffer_name(String offer_name) {
        this.offer_name = offer_name;
    }

    public String getOffer_mobile() {
        return offer_mobile;
    }

    public void setOffer_mobile(String offer_mobile) {
        this.offer_mobile = offer_mobile;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWeight_unit() {
        return weight_unit;
    }

    public void setWeight_unit(String weight_unit) {
        this.weight_unit = weight_unit;
    }
}
