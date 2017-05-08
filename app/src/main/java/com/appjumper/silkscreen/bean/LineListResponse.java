package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by yc on 2016/11/16.
 * 物流线路列表
 */
public class LineListResponse extends BaseResponse{
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data extends PagerData{
        private List<LineList> items;

        public List<LineList> getItems() {
            return items;
        }

        public void setItems(List<LineList> items) {
            this.items = items;
        }
    }
}
