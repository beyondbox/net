package com.appjumper.silkscreen.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 产品实体类，重新生成的，字段名与服务器一致
 * Created by Botx on 2017/4/27.
 */

public class ServiceProduct implements Serializable{


    /**
     * id : 14
     * name : 包塑丝
     * status : 1
     * create_time : 2016-12-20 11:31:46
     * update_time : 2016-12-20 11:31:46
     * is_custom : 1
     * custom_type : 10
     * is_machining : 1
     * machining_type : 18
     * is_goods : 0
     * goods_type : 0
     * alias : PVC包塑丝
     * initial : B
     * is_collection : 0
     * img : http://192.168.1.114/data/upload/admin/20170425/58fec2d043df0.jpg
     * product_type_name : null
     */

    private String id;
    private String name;
    private String status;
    private String create_time;
    private String update_time;
    private String is_custom;
    private String custom_type;
    private String is_machining;
    private String machining_type;
    private String is_goods;
    private String goods_type;
    private String alias;
    private String is_collection; //0未关注，1已关注
    private String img;
    private String product_type_name;

    @SerializedName("initial")
    private String section;

    private List<Spec> product_spec;

    //旧的字段
    private String product_type_id;
    private String spec;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getIs_custom() {
        return is_custom;
    }

    public void setIs_custom(String is_custom) {
        this.is_custom = is_custom;
    }

    public String getCustom_type() {
        return custom_type;
    }

    public void setCustom_type(String custom_type) {
        this.custom_type = custom_type;
    }

    public String getIs_machining() {
        return is_machining;
    }

    public void setIs_machining(String is_machining) {
        this.is_machining = is_machining;
    }

    public String getMachining_type() {
        return machining_type;
    }

    public void setMachining_type(String machining_type) {
        this.machining_type = machining_type;
    }

    public String getIs_goods() {
        return is_goods;
    }

    public void setIs_goods(String is_goods) {
        this.is_goods = is_goods;
    }

    public String getGoods_type() {
        return goods_type;
    }

    public void setGoods_type(String goods_type) {
        this.goods_type = goods_type;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }


    public String getIs_collection() {
        return is_collection;
    }

    public void setIs_collection(String is_collection) {
        this.is_collection = is_collection;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getProduct_type_name() {
        return product_type_name;
    }

    public void setProduct_type_name(String product_type_name) {
        this.product_type_name = product_type_name;
    }

    public List<Spec> getProduct_spec() {
        return product_spec;
    }

    public void setProduct_spec(List<Spec> product_spec) {
        this.product_spec = product_spec;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }






    public String getProduct_type_id() {
        return product_type_id;
    }

    public void setProduct_type_id(String product_type_id) {
        this.product_type_id = product_type_id;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }
}
