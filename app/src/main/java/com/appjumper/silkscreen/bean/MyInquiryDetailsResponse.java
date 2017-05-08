package com.appjumper.silkscreen.bean;

/**
 * Created by yc on 2016/11/29.
 * 企业详情
 */
public class MyInquiryDetailsResponse extends BaseResponse{
    private MyInquiry data;

    public MyInquiry getData() {
        return data;
    }

    public void setData(MyInquiry data) {
        this.data = data;
    }
}
