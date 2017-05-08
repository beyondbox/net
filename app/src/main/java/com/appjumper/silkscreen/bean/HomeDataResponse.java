package com.appjumper.silkscreen.bean;

/**
 * Created by yc on 2016/12/1.
 * 首页
 */
public class HomeDataResponse extends BaseResponse{
    private HomeData data;

    public HomeData getData() {
        return data;
    }

    public void setData(HomeData data) {
        this.data = data;
    }
}
