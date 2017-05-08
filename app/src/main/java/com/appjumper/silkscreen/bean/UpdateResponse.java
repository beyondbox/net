package com.appjumper.silkscreen.bean;

/**
 * Created by yc on 2016/12/15.
 * 版本更新
 */
public class UpdateResponse extends BaseResponse{
    private Update data;

    public Update getData() {
        return data;
    }

    public void setData(Update data) {
        this.data = data;
    }
}
