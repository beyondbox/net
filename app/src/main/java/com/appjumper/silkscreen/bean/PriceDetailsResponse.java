package com.appjumper.silkscreen.bean;

/**
 * Created by yc on 2016/12/12.
 * 原材详情
 */
public class PriceDetailsResponse extends BaseResponse{
    private PriceDetails data;

    public PriceDetails getData() {
        return data;
    }

    public void setData(PriceDetails data) {
        this.data = data;
    }
}
