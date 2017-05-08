package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by yc on 2016/11/26.
 * 图片
 */
public class ImageResponse extends BaseResponse{
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public class Data extends Avatar{
        private String img_id;

        public String getImg_id() {
            return img_id;
        }

        public void setImg_id(String img_id) {
            this.img_id = img_id;
        }
    }
}
