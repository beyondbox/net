package com.appjumper.silkscreen.bean;

/**
 * 首页热门询价
 * Created by Botx on 2017/5/25.
 */

public class HotInquiry {


    /**
     * id : 66
     * type : 1
     * product_id : 19
     * productname : 不锈钢网
     * productimg : http://192.168.1.114/data/upload/admin/20170425/58fec2d043df0.jpg
     * shuliang : 1
     */

    private String id;
    private String type;
    private String product_id;
    private String productname;
    private String productimg;
    private String shuliang;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getProductimg() {
        return productimg;
    }

    public void setProductimg(String productimg) {
        this.productimg = productimg;
    }

    public String getShuliang() {
        return shuliang;
    }

    public void setShuliang(String shuliang) {
        this.shuliang = shuliang;
    }
}
