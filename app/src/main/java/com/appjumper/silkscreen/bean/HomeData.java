package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by yc on 2016/12/1.
 * 首页
 */
public class HomeData {
    private List<HomeBanner> banner;
    private String checkin;//是否签到
    private String score;//积分
    private List<Notice> notice;//公告
    private List<Enterprise> recommend;//推荐
    private List<HomeBanner> ad;//广告
    private List<NewPublic> newpublic;//最新发布

    public List<NewPublic> getNewpublic() {
        return newpublic;
    }

    public void setNewpublic(List<NewPublic> newpublic) {
        this.newpublic = newpublic;
    }

    public List<HomeBanner> getBanner() {
        return banner;
    }

    public void setBanner(List<HomeBanner> banner) {
        this.banner = banner;
    }

    public String getCheckin() {
        return checkin;
    }

    public void setCheckin(String checkin) {
        this.checkin = checkin;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public List<Notice> getNotice() {
        return notice;
    }

    public void setNotice(List<Notice> notice) {
        this.notice = notice;
    }

    public List<Enterprise> getRecommend() {
        return recommend;
    }

    public void setRecommend(List<Enterprise> recommend) {
        this.recommend = recommend;
    }

    public List<HomeBanner> getAd() {
        return ad;
    }

    public void setAd(List<HomeBanner> ad) {
        this.ad = ad;
    }
}
