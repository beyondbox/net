package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by yc on 2016/11/30.
 * 产品列表
 */
public class ProductResponse extends BaseResponse{
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data extends PagerData{
        private List<Product> items;

        public List<Product> getItems() {
            return items;
        }

        public void setItems(List<Product> items) {
            this.items = items;
        }
    }


}
