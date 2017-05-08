package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by yc on 2016/11/22.
 * 物流详情
 */
public class LineDetails extends LineList{
    private Enterprise enterprise;//企业信息
    private User user;//个人
    private List<LineList> recommend;//推荐

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<LineList> getRecommend() {
        return recommend;
    }

    public void setRecommend(List<LineList> recommend) {
        this.recommend = recommend;
    }
}
