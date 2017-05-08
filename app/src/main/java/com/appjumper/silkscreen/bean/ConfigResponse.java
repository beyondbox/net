package com.appjumper.silkscreen.bean;

/**
 * Created by yc on 2016/12/15.
 * 通用
 */
public class ConfigResponse extends BaseResponse{
    private Config data;

    public Config getData() {
        return data;
    }

    public void setData(Config data) {
        this.data = data;
    }
}
