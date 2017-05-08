package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by yc on 2016/11/21.
 * 招标信息列表
 */
public class TenderListResponse extends BaseResponse{
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data extends PagerData{
        private List<Tender> items;

        public List<Tender> getItems() {
            return items;
        }

        public void setItems(List<Tender> items) {
            this.items = items;
        }
    }
}
