package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by yc on 2016/12/6.
 *
 */
public class EnterpriseResponse extends BaseResponse{
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data extends PagerData{
        private List<Enterprise> items;

        public List<Enterprise> getItems() {
            return items;
        }

        public void setItems(List<Enterprise> items) {
            this.items = items;
        }
    }
}
