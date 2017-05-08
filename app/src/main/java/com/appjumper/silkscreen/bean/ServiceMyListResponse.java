package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by yc on 2016/11/29.
 * 服务标签列表
 */
public class ServiceMyListResponse extends BaseResponse{
    private List<ServiceMyList> data;

    public List<ServiceMyList> getData() {
        return data;
    }

    public void setData(List<ServiceMyList> data) {
        this.data = data;
    }
}
