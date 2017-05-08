package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by yc on 2016/12/3.
 * 积分记录
 */
public class IntegralListResponse extends BaseResponse{
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data extends PagerData{
        private List<IntegralList> items;

        public List<IntegralList> getItems() {
            return items;
        }

        public void setItems(List<IntegralList> items) {
            this.items = items;
        }
    }
}
