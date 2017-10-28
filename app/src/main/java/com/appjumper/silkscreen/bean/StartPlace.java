package com.appjumper.silkscreen.bean;

/**
 * 起始地
 * Created by Botx on 2017/10/25.
 */

public class StartPlace {
    private String id;
    private String from_name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    @Override
    public String toString() {
        return from_name;
    }
}
