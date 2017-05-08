package com.appjumper.silkscreen.bean;

/**
 * Created by yc on 2016/12/8.
 * 个人认证
 */
public class PersonalAuthInfoResponse extends BaseResponse{
    private PersonalAuthInfo data;

    public PersonalAuthInfo getData() {
        return data;
    }

    public void setData(PersonalAuthInfo data) {
        this.data = data;
    }
}
