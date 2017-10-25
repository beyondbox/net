package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by yc on 2016/11/24.
 * 求购报价列表
 */
public class AskBuyOfferResponse extends BaseResponse{
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data extends PagerData{
        private List<AskBuyOffer> items;

        public List<AskBuyOffer> getItems() {
            return items;
        }

        public void setItems(List<AskBuyOffer> items) {
            this.items = items;
        }
    }
}
