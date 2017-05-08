package com.appjumper.silkscreen.bean;

/**
 * Created by yc on 2016/12/2.
 *  * 企业认证信息
 */
public class AuthInfoResponse extends BaseResponse{
    private AuthInfo data;

    public AuthInfo getData() {
        return data;
    }

    public void setData(AuthInfo data) {
        this.data = data;
    }
}
