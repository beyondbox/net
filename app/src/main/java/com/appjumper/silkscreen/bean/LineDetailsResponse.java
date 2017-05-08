package com.appjumper.silkscreen.bean;

/**
 * Created by yc on 2016/11/22.
 * 物流详情
 */
public class LineDetailsResponse extends BaseResponse{
  private LineDetails data;

    public LineDetails getData() {
        return data;
    }

    public void setData(LineDetails data) {
        this.data = data;
    }
}
