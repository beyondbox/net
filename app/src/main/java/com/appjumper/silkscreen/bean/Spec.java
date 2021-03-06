package com.appjumper.silkscreen.bean;

import java.io.Serializable;

/**
 * Created by yc on 2016/11/24.
 * 规格
 */
public class Spec implements Serializable{
    private String unit;
    private String fieldname;
    private String product_id;
    private String update_time;
    private String status;
    private String id;
    private String inquiry_price_id;
    private String datatype;
    private String value;
    private String fieldinput;
    private String require;
    private String name;
    private String create_time;

    private String unit_default;
    private String min_value;
    private String max_value;
    private String spec_num;




    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInquiry_price_id() {
        return inquiry_price_id;
    }

    public void setInquiry_price_id(String inquiry_price_id) {
        this.inquiry_price_id = inquiry_price_id;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFieldinput() {
        return fieldinput;
    }

    public void setFieldinput(String fieldinput) {
        this.fieldinput = fieldinput;
    }

    public String getRequire() {
        return require;
    }

    public void setRequire(String require) {
        this.require = require;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUnit_default() {
        return unit_default;
    }

    public void setUnit_default(String unit_default) {
        this.unit_default = unit_default;
    }

    public String getMin_value() {
        return min_value;
    }

    public void setMin_value(String min_value) {
        this.min_value = min_value;
    }

    public String getMax_value() {
        return max_value;
    }

    public void setMax_value(String max_value) {
        this.max_value = max_value;
    }

    public String getSpec_num() {
        return spec_num;
    }

    public void setSpec_num(String spec_num) {
        this.spec_num = spec_num;
    }
}
