package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by yc on 2016/11/24.
 * 我的询价列表
 */
public class MyInquiryResponse extends BaseResponse{
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data extends PagerData{
        private List<MyInquiry> items;

        public List<MyInquiry> getItems() {
            return items;
        }

        public void setItems(List<MyInquiry> items) {
            this.items = items;
        }
    }
}
