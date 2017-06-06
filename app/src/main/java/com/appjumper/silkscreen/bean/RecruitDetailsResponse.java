package com.appjumper.silkscreen.bean;

/**
 * Created by yc on 2016/11/21.
 * 设备详情
 */
public class RecruitDetailsResponse extends BaseResponse {
    private RecruitDetail data;

    public RecruitDetail getData() {
        return data;
    }

    public void setData(RecruitDetail data) {
        this.data = data;
    }
}
