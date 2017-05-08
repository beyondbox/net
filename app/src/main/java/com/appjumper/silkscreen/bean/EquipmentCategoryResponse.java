package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by Administrator on 2016-12-01.
 */
public class EquipmentCategoryResponse extends BaseResponse {
    private List<EquipmentCategory> data;

    public List<EquipmentCategory> getData() {
        return data;
    }

    public void setData(List<EquipmentCategory> data) {
        this.data = data;
    }
}
