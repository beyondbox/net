package com.appjumper.silkscreen.bean;

/**
 * 刀片刺绳规格分组
 * Created by Botx on 2017/6/19.
 */

public class DPGroup {

    private boolean isChecked;
    private String name;

    public DPGroup(boolean isChecked, String name) {
        this.isChecked = isChecked;
        this.name = name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public String getName() {
        return name;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void setName(String name) {
        this.name = name;
    }
}
