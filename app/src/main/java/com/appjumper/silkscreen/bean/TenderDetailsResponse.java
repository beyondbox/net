package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by yc on 2016/11/21.
 * 招投标信息详情
 */
public class TenderDetailsResponse extends BaseResponse{
    private Tender data;

    public Tender getData() {
        return data;
    }

    public void setData(Tender data) {
        this.data = data;
    }
}
