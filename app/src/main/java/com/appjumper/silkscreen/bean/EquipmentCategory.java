package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by Administrator on 2016-12-01.
 */
public class EquipmentCategory {
    private String id;
    private String name;
    private List<EquipmentCategory> sublist;

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

    public List<EquipmentCategory> getSublist() {
        return sublist;
    }

    public void setSublist(List<EquipmentCategory> sublist) {
        this.sublist = sublist;
    }
}
