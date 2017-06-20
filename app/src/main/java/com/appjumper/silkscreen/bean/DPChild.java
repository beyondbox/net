package com.appjumper.silkscreen.bean;

/**
 * 刀片刺绳规格中的内容
 * Created by Botx on 2017/6/19.
 */

public class DPChild {

    private String key;
    private String value;
    private String name;

    public DPChild(String key, String value, String name) {
        this.key = key;
        this.value = value;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
