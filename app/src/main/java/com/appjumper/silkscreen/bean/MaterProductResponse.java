package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by yc on 2016/12/7.
 */
public class MaterProductResponse extends BaseResponse{
    private List<MaterProduct> data;

    public List<MaterProduct> getData() {
        return data;
    }

    public void setData(List<MaterProduct> data) {
        this.data = data;
    }
}
