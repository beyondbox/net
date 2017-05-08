package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by yc on 2016/12/6.
 * 热门搜索
 */
public class KeyWorksResponse extends BaseResponse{
    List<KeyWorks> data;

    public List<KeyWorks> getData() {
        return data;
    }

    public void setData(List<KeyWorks> data) {
        this.data = data;
    }
}
