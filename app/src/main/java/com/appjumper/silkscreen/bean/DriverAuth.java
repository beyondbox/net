package com.appjumper.silkscreen.bean;

import java.io.Serializable;

/**
 * 司机认证(本地用)
 * Created by Botx on 2017/10/24.
 */

public class DriverAuth implements Serializable{

    private String name;
    private String ID;
    private String imgIDCard;
    private String imgIDCardBack;
    private String imgDriver; //驾驶证
    private String imgDriverBack;
    private String imgDriving; //行驶证
    private String imgDrivingBack;
    private String imgGroup; //合照

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getImgIDCard() {
        return imgIDCard;
    }

    public void setImgIDCard(String imgIDCard) {
        this.imgIDCard = imgIDCard;
    }

    public String getImgIDCardBack() {
        return imgIDCardBack;
    }

    public void setImgIDCardBack(String imgIDCardBack) {
        this.imgIDCardBack = imgIDCardBack;
    }

    public String getImgDriver() {
        return imgDriver;
    }

    public void setImgDriver(String imgDriver) {
        this.imgDriver = imgDriver;
    }

    public String getImgDriverBack() {
        return imgDriverBack;
    }

    public void setImgDriverBack(String imgDriverBack) {
        this.imgDriverBack = imgDriverBack;
    }

    public String getImgDriving() {
        return imgDriving;
    }

    public void setImgDriving(String imgDriving) {
        this.imgDriving = imgDriving;
    }

    public String getImgDrivingBack() {
        return imgDrivingBack;
    }

    public void setImgDrivingBack(String imgDrivingBack) {
        this.imgDrivingBack = imgDrivingBack;
    }

    public String getImgGroup() {
        return imgGroup;
    }

    public void setImgGroup(String imgGroup) {
        this.imgGroup = imgGroup;
    }
}
