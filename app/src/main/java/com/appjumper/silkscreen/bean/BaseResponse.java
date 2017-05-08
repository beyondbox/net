package com.appjumper.silkscreen.bean;

/**
 * Created by yc on 16/10/6.
 */
public class BaseResponse {
    private int error_code;
    private  String error_desc;


    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getError_desc() {
        return error_desc;
    }

    public void setError_desc(String error_desc) {
        this.error_desc = error_desc;
    }

    public boolean isSuccess(){
        return error_code == 0;
    }
}
