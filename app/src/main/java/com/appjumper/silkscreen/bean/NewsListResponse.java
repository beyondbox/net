package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by yc on 2016/11/21.
 * 行业新闻列表
 */
public class NewsListResponse extends BaseResponse{
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data extends PagerData{
        private List<News> items;

        public List<News> getItems() {
            return items;
        }

        public void setItems(List<News> items) {
            this.items = items;
        }
    }
}
