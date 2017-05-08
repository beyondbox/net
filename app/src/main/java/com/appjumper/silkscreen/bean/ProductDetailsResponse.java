package com.appjumper.silkscreen.bean;


/**
 * Created by yc on 2016/11/30.
 * 服务详情接口
 */
public class ProductDetailsResponse extends BaseResponse{
    private Product data;

    public Product getData() {
        return data;
    }

    public void setData(Product data) {
        this.data = data;
    }
}
