package com.appjumper.silkscreen.bean;

/**
 * 单位
 * Created by Botx on 2018/1/23.
 */

public class Unit {


    /**
     * id : 2
     * name : kg/套
     * create_time : 2018-01-22 13:19:49
     * update_time : 2018-01-22 13:19:49
     * status : 1
     */

    private String id;
    private String name;
    private String create_time;
    private String update_time;
    private String status;
    private String weight_unit;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWeight_unit() {
        return weight_unit;
    }

    public void setWeight_unit(String weight_unit) {
        this.weight_unit = weight_unit;
    }

    @Override
    public String toString() {
        return name;
    }
}
