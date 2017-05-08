package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by yc on 2016/12/6.
 * 最新发布
 */
public class NewPublicResponse extends BaseResponse{
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data extends PagerData{
        private List<NewPublic> items;

        public List<NewPublic> getItems() {
            return items;
        }

        public void setItems(List<NewPublic> items) {
            this.items = items;
        }
    }
}
