package com.appjumper.silkscreen.bean;


import com.appjumper.silkscreen.util.PinyinUtil;

import java.util.List;

/**
 * Created by yc on 2016/10/18.
 * 地址
 */
public class AreaBeanResponse extends BaseResponse{
    private List<AreaBean> data;

    public List<AreaBean> getData() {
        return data;
    }

    public void setData(List<AreaBean> data) {
        this.data = data;
    }
}
