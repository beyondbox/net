package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by yc on 2016/11/29.
 * 产品类型列表
 */
public class ProductTypeResponse extends BaseResponse{
    private List<ProductType> data;

    public List<ProductType> getData() {
        return data;
    }

    public void setData(List<ProductType> data) {
        this.data = data;
    }
}
