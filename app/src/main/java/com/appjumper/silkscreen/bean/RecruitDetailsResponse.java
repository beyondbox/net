package com.appjumper.silkscreen.bean;

/**
 * Created by yc on 2016/11/21.
 * 设备详情
 */
public class RecruitDetailsResponse extends BaseResponse {
    private RecruitList data;

    public RecruitList getData() {
        return data;
    }

    public void setData(RecruitList data) {
        this.data = data;
    }
}
