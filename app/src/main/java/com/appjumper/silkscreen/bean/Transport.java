package com.appjumper.silkscreen.bean;

/**
 * 空车配货--运输情况
 * Created by Botx on 2017/11/10.
 */

public class Transport {


    /**
     * id : 59
     * user_id : 88
     * create_time : 2017-11-10 10:02:59
     * content : 厂家确认司机到达
     * car_product_id : 120
     */

    private String id;
    private String user_id;
    private String create_time;
    private String content;
    private String car_product_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCar_product_id() {
        return car_product_id;
    }

    public void setCar_product_id(String car_product_id) {
        this.car_product_id = car_product_id;
    }
}
