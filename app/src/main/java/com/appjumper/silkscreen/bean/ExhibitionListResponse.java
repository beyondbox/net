package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by yc on 2016/11/21.
 * 展会信息列表
 */
public class ExhibitionListResponse extends BaseResponse{
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data extends PagerData{
        private List<Exhibition> items;

        public List<Exhibition> getItems() {
            return items;
        }

        public void setItems(List<Exhibition> items) {
            this.items = items;
        }
    }
}
