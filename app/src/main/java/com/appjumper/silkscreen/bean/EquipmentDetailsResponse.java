package com.appjumper.silkscreen.bean;

/**
 * Created by yc on 2016/11/21.
 * 设备详情
 */
public class EquipmentDetailsResponse extends BaseResponse {
    private EquipmentList data;

    public EquipmentList getData() {
        return data;
    }

    public void setData(EquipmentList data) {
        this.data = data;
    }
}
