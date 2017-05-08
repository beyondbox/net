package com.appjumper.silkscreen.bean;

/**
 * Created by yc on 2016/11/11.
 * 用户资料
 */
public class UserResponse extends BaseResponse{
    private User data;

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }
}
