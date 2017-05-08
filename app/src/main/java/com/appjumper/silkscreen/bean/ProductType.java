package com.appjumper.silkscreen.bean;

/**
 * Created by yc on 2016/11/29.
 * 产品类型列表
 */
public class ProductType {
    private String id;
    private String name;
    private String type;
    private String type_name;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }
}
