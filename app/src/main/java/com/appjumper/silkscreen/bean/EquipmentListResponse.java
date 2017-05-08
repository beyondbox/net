package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by yc on 2016/11/21.
 * 设备列表
 */
public class EquipmentListResponse extends BaseResponse{
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data extends PagerData{
        private List<EquipmentList> items;

        public List<EquipmentList> getItems() {
            return items;
        }

        public void setItems(List<EquipmentList> items) {
            this.items = items;
        }
    }
}
