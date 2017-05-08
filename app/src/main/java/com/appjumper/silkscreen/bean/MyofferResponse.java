package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by yc on 2016/11/24.
 * 我的报价列表
 */
public class MyofferResponse extends BaseResponse{
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data extends PagerData{
        private List<Myoffer> items;

        public List<Myoffer> getItems() {
            return items;
        }

        public void setItems(List<Myoffer> items) {
            this.items = items;
        }
    }
}
