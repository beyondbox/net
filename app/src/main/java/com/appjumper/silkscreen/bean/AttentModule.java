package com.appjumper.silkscreen.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 关注的版块
 * Created by Botx on 2017/5/2.
 */

public class AttentModule {

    @SerializedName(value = "card_id", alternate = {"job_id", "equipment_id", "id"})
    private String id;
    @SerializedName(value = "shortname", alternate = {"name"})
    private String name;

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
}
