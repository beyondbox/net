package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by Administrator on 2016-12-05.
 */
public class EnterpriseCollectionResponse extends BaseResponse {
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data extends PagerData {
        private List<Enterprise> items;

        public List<Enterprise> getItems() {
            return items;
        }

        public void setItems(List<Enterprise> items) {
            this.items = items;
        }
    }
}
