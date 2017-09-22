package com.appjumper.silkscreen.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yc on 2016/11/30.
 * 产品
 */
public class Product implements Serializable{
    private List<Spec> service_spec;
    private String enterprise_name;
    private String spec;
    private String product_type;
    private String status;
    private String user_id;
    private String create_time;
    private String nickname;
    private Avatar enterprise_logo;
    private String lng;
    private String product_name;
    private String product_type_name;
    private String type;
    private String imgs;
    private String id;
    private String product_id;
    private String enterprise_auth_status;
    private String service_type_name;
    private String mobile;
    private String enterprise_productivity_auth_status;
    private String lat;
    private String update_time;
    private Avatar avatar;
    private String remark;
    private String distance;
    private String product_alias;
    private String enterprise_address;
    private String enterprise_id;
    private String url;
    private List<Avatar> img_list;
    private String auth_status;

    private String img_require;
    private boolean is_read = true;
    private String spec_num;
    private String description;
    private String img;

    @SerializedName("initial")
    private String section;







    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEnterprise_id() {
        return enterprise_id;
    }

    public void setEnterprise_id(String enterprise_id) {
        this.enterprise_id = enterprise_id;
    }

    public String getEnterprise_address() {
        return enterprise_address;
    }

    public void setEnterprise_address(String enterprise_address) {
        this.enterprise_address = enterprise_address;
    }

    public String getProduct_alias() {
        return product_alias;
    }

    public void setProduct_alias(String product_alias) {
        this.product_alias = product_alias;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public List<Avatar> getImg_list() {
        return img_list;
    }

    public void setImg_list(List<Avatar> img_list) {
        this.img_list = img_list;
    }

    public List<Spec> getService_spec() {
        return service_spec;
    }

    public void setService_spec(List<Spec> service_spec) {
        this.service_spec = service_spec;
    }

    public String getEnterprise_name() {
        return enterprise_name;
    }

    public void setEnterprise_name(String enterprise_name) {
        this.enterprise_name = enterprise_name;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Avatar getEnterprise_logo() {
        return enterprise_logo;
    }

    public void setEnterprise_logo(Avatar enterprise_logo) {
        this.enterprise_logo = enterprise_logo;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_type_name() {
        return product_type_name;
    }

    public void setProduct_type_name(String product_type_name) {
        this.product_type_name = product_type_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getEnterprise_auth_status() {
        return enterprise_auth_status;
    }

    public void setEnterprise_auth_status(String enterprise_auth_status) {
        this.enterprise_auth_status = enterprise_auth_status;
    }

    public String getService_type_name() {
        return service_type_name;
    }

    public void setService_type_name(String service_type_name) {
        this.service_type_name = service_type_name;
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

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getImg_require() {
        return img_require;
    }

    public void setImg_require(String img_require) {
        this.img_require = img_require;
    }

    public String getAuth_status() {
        return auth_status;
    }

    public void setAuth_status(String auth_status) {
        this.auth_status = auth_status;
    }

    public boolean is_read() {
        return is_read;
    }

    public void setIs_read(boolean is_read) {
        this.is_read = is_read;
    }

    public String getSpec_num() {
        return spec_num;
    }

    public void setSpec_num(String spec_num) {
        this.spec_num = spec_num;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
