package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by yc on 2016/11/21.
 * 招聘列表
 */
public class RecruitListResponse extends BaseResponse {
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data extends PagerData {
        private List<RecruitList> items;

        public List<RecruitList> getItems() {
            return items;
        }

        public void setItems(List<RecruitList> items) {
            this.items = items;
        }
    }
}
