package com.appjumper.silkscreen.bean;

/**
 * Created by yc on 2016/12/12.
 * 原材料 公司模型
 */
public class OfferList {
    private String material_name;
    private String material_id;
    private String offer_unit;
    private String id;
    private String company_id;
    private String offer_value;
    private String offer_value_tax;
    private String offer_time;
    private String company_name;

    public String getOffer_value_tax() {
        return offer_value_tax;
    }

    public void setOffer_value_tax(String offer_value_tax) {
        this.offer_value_tax = offer_value_tax;
    }

    public String getMaterial_name() {
        return material_name;
    }

    public void setMaterial_name(String material_name) {
        this.material_name = material_name;
    }

    public String getMaterial_id() {
        return material_id;
    }

    public void setMaterial_id(String material_id) {
        this.material_id = material_id;
    }

    public String getOffer_unit() {
        return offer_unit;
    }

    public void setOffer_unit(String offer_unit) {
        this.offer_unit = offer_unit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getOffer_value() {
        return offer_value;
    }

    public void setOffer_value(String offer_value) {
        this.offer_value = offer_value;
    }

    public String getOffer_time() {
        return offer_time;
    }

    public void setOffer_time(String offer_time) {
        this.offer_time = offer_time;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }
}
