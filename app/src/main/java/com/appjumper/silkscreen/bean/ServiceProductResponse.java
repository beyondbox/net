package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by yc on 2016/11/29.
 * 产品列表
 */
public class ServiceProductResponse extends BaseResponse{
    private List<ServiceProduct> data;

    public List<ServiceProduct> getData() {
        return data;
    }

    public void setData(List<ServiceProduct> data) {
        this.data = data;
    }
}
