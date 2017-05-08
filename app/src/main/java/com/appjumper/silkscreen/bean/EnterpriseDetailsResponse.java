package com.appjumper.silkscreen.bean;

/**
 * Created by yc on 2016/11/29.
 * 企业详情
 */
public class EnterpriseDetailsResponse extends BaseResponse{
    private Enterprise data;

    public Enterprise getData() {
        return data;
    }

    public void setData(Enterprise data) {
        this.data = data;
    }
}
