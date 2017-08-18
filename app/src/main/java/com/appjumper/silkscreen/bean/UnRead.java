package com.appjumper.silkscreen.bean;

import java.io.Serializable;

/**
 * 未读数
 * Created by Botx on 2017/5/15.
 */

public class UnRead implements Serializable{

    private int readNum; //我的询报价
    private int tenderNum; //招标信息
    private int tenderSelectNum; //投标信息
    private int expoNum; //展会信息
    private int newsNum; //行业新闻
    private int analysisNum; //走势分析文章
    private int collectionNum; //动态


    public int getReadNum() {
        return readNum;
    }

    public void setReadNum(int readNum) {
        this.readNum = readNum;
    }

    public int getTenderNum() {
        return tenderNum;
    }

    public void setTenderNum(int tenderNum) {
        this.tenderNum = tenderNum;
    }

    public int getExpoNum() {
        return expoNum;
    }

    public void setExpoNum(int expoNum) {
        this.expoNum = expoNum;
    }

    public int getNewsNum() {
        return newsNum;
    }

    public void setNewsNum(int newsNum) {
        this.newsNum = newsNum;
    }

    public int getAnalysisNum() {
        return analysisNum;
    }

    public void setAnalysisNum(int analysisNum) {
        this.analysisNum = analysisNum;
    }

    public int getCollectionNum() {
        return collectionNum;
    }

    public void setCollectionNum(int collectionNum) {
        this.collectionNum = collectionNum;
    }

    public int getTenderSelectNum() {
        return tenderSelectNum;
    }

    public void setTenderSelectNum(int tenderSelectNum) {
        this.tenderSelectNum = tenderSelectNum;
    }
}
