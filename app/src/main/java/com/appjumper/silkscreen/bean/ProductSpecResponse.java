package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by yc on 2016/12/7.
 */
public class ProductSpecResponse extends BaseResponse{
    private List<Spec> data;

    public List<Spec> getData() {
        return data;
    }

    public void setData(List<Spec> data) {
        this.data = data;
    }
}
