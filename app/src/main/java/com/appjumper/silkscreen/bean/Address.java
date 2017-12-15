package com.appjumper.silkscreen.bean;

import java.io.Serializable;

/**
 * 收货地址
 * Created by Botx on 2017/12/12.
 */

public class Address implements Serializable{

    private String id;
    private String name;
    private String mobile;
    private String address_type; //0货站   1地点
    private String address; //指定地点地址
    private String station_id = ""; //货站id

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStation_id() {
        return station_id;
    }

    public void setStation_id(String station_id) {
        this.station_id = station_id;
    }

    public String getAddress_type() {
        return address_type;
    }

    public void setAddress_type(String address_type) {
        this.address_type = address_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
