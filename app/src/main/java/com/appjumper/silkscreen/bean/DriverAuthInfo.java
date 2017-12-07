package com.appjumper.silkscreen.bean;

import java.io.Serializable;

/**
 * 司机认证（接口用）
 * Created by Botx on 2017/12/6.
 */

public class DriverAuthInfo implements Serializable{


    /**
     * id : 88
     * user_login :
     * user_pass : ###8d49cdf33838c36feaf567cd6b3115fd
     * user_nicename : jack
     * user_email :
     * user_url :
     * avatar : http://115.28.148.207/data/upload//avatar/2017-11-13/5a08f43480d5e200_200.jpg
     * sex : 0
     * birthday : null
     * signature : null
     * last_login_ip : 106.114.249.213
     * last_login_time : 2017-12-06 09:31:38
     * create_time : 2017-05-20 17:07:48
     * user_activation_key :
     * user_status : 1
     * score : 286
     * user_type : 2
     * coin : 0
     * mobile : 18633883634
     * sort : 50
     * is_home_show : 0
     * name : 薄
     * idcard :
     * idcard_img :
     * idcard_img_back :
     * auth_status : 2
     * liveness_count : 327
     * check_code : null
     * source_type : 0
     * release_num : 10
     * is_goods : 0
     * openid : null
     * is_wechat : 0
     * inviter : null
     * driver_status : 1
     * driver_img : http://115.28.148.207/data/upload/2017-12-06/5a27ba531177e.jpg
     * driver_img_back : http://115.28.148.207/data/upload/2017-12-06/5a27ba6e90b20.jpg
     * driving_license_img : http://115.28.148.207/data/upload/2017-12-06/5a27ba8aa01a8.jpg
     * driving_license_img_back : http://115.28.148.207/data/upload/2017-12-06/5a27ba8f6a5bb.jpg
     * driver_car_img : http://115.28.148.207/data/upload/2017-12-06/5a27ba947b432.jpg
     * driver_lng : 114.50642
     * driver_lat : 38.039314
     * driver_car_status : 1
     * driver_update_place_date : 0000-00-00 00:00:00
     * driver_order_num : 0
     * distance : 0.0
     * is_car_product : 0
     * is_vender : 1
     * car_num : 0
     * is_purchase : 0
     * is_car_product_message : 0
     * is_fast_examine : 0
     * is_purchase_app : 1
     * auth_status_reason : null
     * driver_status_reason : null
     */

    private String id;
    private String mobile;
    private String name;
    private String driver_img;
    private String driver_img_back;
    private String driving_license_img;
    private String driving_license_img_back;
    private String driver_car_img;
    private CarInfo carinfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDriver_img() {
        return driver_img;
    }

    public void setDriver_img(String driver_img) {
        this.driver_img = driver_img;
    }

    public String getDriver_img_back() {
        return driver_img_back;
    }

    public void setDriver_img_back(String driver_img_back) {
        this.driver_img_back = driver_img_back;
    }

    public String getDriving_license_img() {
        return driving_license_img;
    }

    public void setDriving_license_img(String driving_license_img) {
        this.driving_license_img = driving_license_img;
    }

    public String getDriving_license_img_back() {
        return driving_license_img_back;
    }

    public void setDriving_license_img_back(String driving_license_img_back) {
        this.driving_license_img_back = driving_license_img_back;
    }

    public String getDriver_car_img() {
        return driver_car_img;
    }

    public void setDriver_car_img(String driver_car_img) {
        this.driver_car_img = driver_car_img;
    }

    public CarInfo getCarinfo() {
        return carinfo;
    }

    public void setCarinfo(CarInfo carinfo) {
        this.carinfo = carinfo;
    }
}
