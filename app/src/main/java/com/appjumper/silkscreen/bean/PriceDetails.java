package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by yc on 2016/12/12.
 * 原材详情
 */
public class PriceDetails {
    private String avg;//均价
    private String avg_diff;//涨跌值
    private String count;//报价公司数
    private List<OfferList> offer_list;//报价公司
    private List<Float> avg_list;//图标每日均价 共7天
    private String space_money = "50"; //纵坐标取整数的倍数

    public String getAvg() {
        return avg;
    }

    public void setAvg(String avg) {
        this.avg = avg;
    }

    public String getAvg_diff() {
        return avg_diff;
    }

    public void setAvg_diff(String avg_diff) {
        this.avg_diff = avg_diff;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<OfferList> getOffer_list() {
        return offer_list;
    }

    public void setOffer_list(List<OfferList> offer_list) {
        this.offer_list = offer_list;
    }

    public List<Float> getAvg_list() {
        return avg_list;
    }

    public void setAvg_list(List<Float> avg_list) {
        this.avg_list = avg_list;
    }

    public String getSpace_money() {
        return space_money;
    }

    public void setSpace_money(String space_money) {
        this.space_money = space_money;
    }
}
