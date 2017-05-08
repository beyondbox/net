package com.appjumper.silkscreen.util;

/**
 * 常量
 * Created by Botx on 2017/3/23.
 */

public class Const {

    /**
     * 选择物流地点
     */
    public static final int REQUEST_CODE_SELECT_LOGISTICS = 2000;
    /**
     * 选择产品
     */
    public static final int REQUEST_CODE_SELECT_PRODUCT = 2001;
    /**
     * 选择设备
     */
    public static final int REQUEST_CODE_SELECT_DEVICE = 2002;
    /**
     * 选择职位
     */
    public static final int REQUEST_CODE_SELECT_POST = 2003;
    /**
     * 选择出租厂房的地点
     */
    public static final int REQUEST_CODE_SELECT_PLANT = 2004;
    /**
     * 发布加工
     */
    public static final int REQUEST_CODE_RELEASE_PROCESS = 2005;
    /**
     * 发布订做
     */
    public static final int REQUEST_CODE_RELEASE_ORDER = 2006;
    /**
     * 发布现货
     */
    public static final int REQUEST_CODE_RELEASE_STOCK = 2007;
    /**
     * 订做询价
     */
    public static final int REQUEST_CODE_INQUIRY_ORDER = 2008;
    /**
     * 现货询价
     */
    public static final int REQUEST_CODE_INQUIRY_STOCK = 2009;
    /**
     * 加工询价
     */
    public static final int REQUEST_CODE_INQUIRY_PROCESS = 2010;





    /**
     * 注册成功
     */
    public static final int RESULT_CODE_REGISTER_SUCCESS = 1000;
    /**
     * 需要刷新数据
     */
    public static final int RESULT_CODE_NEED_REFRESH = 1001;



    /**
     * 我的关注刷新——原材料
     */
    public static final String ACTION_ATTENTION_MATER_REFRESH = "com.appjumper.silkscreen.ATTENTION_MATER_REFRESH";
    /**
     * 管理关注的产品
     */
    public static final String ACTION_ATTENT_PRODUCT_MANAGE = "com.appjumper.silkscreen.ATTENT_PRODUCT_MANAGE";
    /**
     * 搜索结果刷新
     */
    public static final String ACTION_SEARCHING_REFRESH = "com.appjumper.silkscreen.SEARCHING_REFRESH";



    public static final int HTTP_STATE_SUCCESS = 0;



    public static final int SERVICE_TYPE_ORDER = 1; //订做
    public static final int SERVICE_TYPE_PROCESS = 2; //加工
    public static final int SERVICE_TYPE_STOCK = 3; //现货
    public static final int SERVICE_TYPE_LOGISTICS = 4; //物流线路


    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_SERVICE_TYPE = "serviceType";
    public static final String KEY_IS_MULTI_MODE = "isMultiMode";
    public static final String KEY_IS_FILTER_MODE = "isFilterMode";
    public static final String KEY_ERROR_CODE = "error_code";
    public static final String KEY_ACTION = "action";
    public static final String KEY_OBJECT = "object";

}
