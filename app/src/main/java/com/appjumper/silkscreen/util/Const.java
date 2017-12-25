package com.appjumper.silkscreen.util;

/**
 * 常量
 * Created by Botx on 2017/3/23.
 */

public class Const {

    //app分享链接
    public static String SHARE_APP_URL = "https://siwangjia.kuaizhan.com/";
    //求购分享链接
    public static String SHARE_ASKBUY_URL = "http://www.siwangjia.com/data/upload/web/want_buy.html";

    //空车配货版块客服电话
    public static final String SERVICE_PHONE_FREIGHT = "18531881288";
    //求购版块客服电话
    public static final String SERVICE_PHONE_ASKBUY = "18531881113";





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
     * 请求权限
     */
    public static final int REQUEST_CODE_PERMISSION = 2011;
    /**
     * 地图选点
     */
    public static final int REQUEST_CODE_MAP_CHOOSE = 2012;
    /**
     * 选择收货地址
     */
    public static final int REQUEST_CODE_SELECT_ADDRESS = 2013;
    /**
     * 选择货站
     */
    public static final int REQUEST_CODE_SELECT_STATION = 2014;





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
     * 添加找车的货物
     */
    public static final String ACTION_ADD_PRODUCT = "com.appjumper.silkscreen.ADD_PRODUCT";
    /**
     * 搜索结果刷新
     */
    public static final String ACTION_SEARCHING_REFRESH = "com.appjumper.silkscreen.SEARCHING_REFRESH";
    /**
     * 未读消息数刷新
     */
    public static final String ACTION_UNREAD_REFRESH = "com.appjumper.silkscreen.UNREAD_REFRESH";
    /**
     * 登录成功
     */
    public static final String ACTION_LOGIN_SUCCESS = "com.appjumper.silkscreen.LOGIN_SUCCESS";
    /**
     * 动态刷新
     */
    public static final String ACTION_DYNAMIC_REFRESH = "com.appjumper.silkscreen.DYNAMIC_REFRESH";
    /**
     * 原材料走势图详情跳转
     */
    public static final String ACTION_CHART_DETAIL = "com.appjumper.silkscreen.CHART_DETAIL";
    /**
     * 发布信息成功
     */
    public static final String ACTION_RELEASE_SUCCESS = "com.appjumper.silkscreen.RELEASE_SUCCESS";
    /**
     * 增加浏览次数
     */
    public static final String ACTION_ADD_READ_NUM = "com.appjumper.silkscreen.ADD_READ_NUM";
    /**
     * 数据刷新
     */
    public static final String ACTION_REFRESH = "com.appjumper.silkscreen.REFRESH";
    /**
     * 求购列表
     */
    public static final String ACTION_ASKBUY_LIST = "com.appjumper.silkscreen.ASKBUY_LIST";
    /**
     * 支付成功
     */
    public static final String ACTION_PAY_SUCCESS = "com.appjumper.silkscreen.PAY_SUCCESS";
    /**
     * 支付失败
     */
    public static final String ACTION_PAY_FAIL = "com.appjumper.silkscreen.PAY_FAIL";




    public static final int HTTP_STATE_SUCCESS = 0;



    public static final int SERVICE_TYPE_ORDER = 1; //订做
    public static final int SERVICE_TYPE_PROCESS = 2; //加工
    public static final int SERVICE_TYPE_STOCK = 3; //现货
    public static final int SERVICE_TYPE_LOGISTICS = 4; //物流-货站
    public static final int SERVICE_TYPE_LOGISTICS_PER = 5; //物流-个人
    public static final int SERVICE_TYPE_LOGISTICS_CAR = 6; //物流-找车
    public static final int SERVICE_TYPE_WORKSHOP = 7; //厂房
    public static final int SERVICE_TYPE_DEVICE = 8; //设备
    public static final int SERVICE_TYPE_JOB = 9; //招聘


    public static final int AUTH_NOT_YET = 0; //未认证
    public static final int AUTH_ING = 1; //认证中
    public static final int AUTH_SUCCESS = 2; //认证通过
    public static final int AUTH_FAIL = 3; //认证失败


    public static final int INFO_TYPE_PER = 1; //信息类型-个人
    public static final int INFO_TYPE_COM = 2; //信息类型-企业
    public static final int INFO_TYPE_OFFICIAL = 3; //信息类型-官方


    public static final String ADDRESS_LEVEL_PROVINCE = "0"; //地址+省级
    public static final String ADDRESS_LEVEL_CITY = "1"; //地址-市级
    public static final String ADDRESS_LEVEL_COUNTY = "2"; //地址-县级


    public static final int OFFER_NOT_YET = 0; //求购报价-未报价
    public static final int OFFER_DEAL = 1; //求购报价-已交易
    public static final int OFFER_OFFERED = 2; //求购报价-报价结束


    public static final int ASKBUY_AUDITING = 0; //求购-审核中
    public static final int ASKBUY_OFFERING = 1; //求购-报价中
    public static final int ASKBUY_REFUSE = 2; //求购-审核拒绝
    public static final int ASKBUY_PAYED_SUB = 3; //求购-已付订金
    public static final int ASKBUY_PAYED_ALL = 4; //求购-已付全额
    public static final int ASKBUY_SEND_OUT = 5; //求购-确认发货
    public static final int ASKBUY_RECEIPT_APPLY = 6; //求购-报价方申请收款
    public static final int ASKBUY_RECEIPT_ACCEPT = 7; //求购-报价方收款审核通过


    public static final int ASKBUY_ORDER_AUDITING = 0; //求购订单-审核中
    public static final int ASKBUY_ORDER_PAYING = 1; //求购订单-待付款
    public static final int ASKBUY_ORDER_REFUSE = 2; //求购订单-审核拒绝
    public static final int ASKBUY_ORDER_RECEIPTING = 3; //求购订单-待收款
    public static final int ASKBUY_ORDER_FINISH = 4; //求购订单-交易完成


    public static final String PAY_TYPE_ALL = "0"; //支付方式-全额
    public static final String PAY_TYPE_SUB = "1"; //支付方式-订金


    public static final int PUSH_NEW_INQUIRY = 1; //推送-有新的询价
    public static final int PUSH_NEW_OFFER = 2; //推送-有新的报价
    public static final int PUSH_FREIGHT_NEW_INQUIRY = 3; //推送-空车配货-司机收到询价
    public static final int PUSH_ASKBUY_CHOOSE_OFFER = 4; //推送-求购-选择报价
    public static final int PUSH_ASKBUY_CHOOSED = 5; //推送-求购-报价被采纳
    public static final int PUSH_FREIGHT_NEW_OFFER = 6; //推送-空车配货-有司机给报价
    public static final int PUSH_FREIGHT_CHOOSED = 7; //推送-空车配货-司机被采纳
    public static final int PUSH_FREIGHT_DRIVER_PAYED = 8; //推送-空车配货-司机已支付
    public static final int PUSH_FREIGHT_DRIVER_ARRIVED = 9; //推送-空车配货-司机已送达
    public static final int PUSH_ASKBUY_PASS_TO_OFFER = 10; //推送-求购信息审核通过，推送给报价用户
    public static final int PUSH_AUDIT_ASKBUY = 11; //推送-快速审核-求购
    public static final int PUSH_AUDIT_FREIGHT = 12; //推送-快速审核-空车配货
    public static final int PUSH_AUDIT_PERSON = 13; //推送-快速审核-个人认证
    public static final int PUSH_AUDIT_DRIVER = 14; //推送-快速审核-司机认证
    public static final int PUSH_ASKBUY_ORDER_PASS = 15; //推送-求购订单审核通过
    public static final int PUSH_ASKBUY_PASS = 16; //推送-求购信息审核通过
    public static final int PUSH_ASKBUY_ORDER_REFUSE = 17; //推送-求购订单审核失败
    public static final int PUSH_ASKBUY_ORDER_REFUSE_TO_OFFER = 18; //推送-求购订单审核失败-推给报价用户请谨慎报价
    public static final int PUSH_ASKBUY_RECEIPT_REFUSE = 19; //推送-求购收款审核失败
    public static final int PUSH_AUDIT_ASKBUY_ORDER = 20; //推送-快速审核-求购订单
    public static final int PUSH_AUDIT_ASKBUY_RECEIPT = 21; //推送-快速审核-求购收款
    public static final int PUSH_NEW_OFFER_WIRE_ROD = 22; //推送-有新的盘条报价


    public static final int FREIGHT_AUDITING = 0; //空车配货-审核中
    public static final int FREIGHT_AUDIT_PASS = 1; //空车配货-审核通过
    public static final int FREIGHT_AUDIT_REFUSE = 2; //空车配货-审核拒绝
    public static final int FREIGHT_DRIVER_PAYING = 3; //空车配货-等待司机支付
    public static final int FREIGHT_GOTO_LOAD = 4; //空车配货-司机前往厂家
    public static final int FREIGHT_LOADING = 5; //空车配货-装货中
    public static final int FREIGHT_TRANSPORTING = 6; //空车配货-运输中
    public static final int FREIGHT_TRANSPORT_FINISH = 7; //空车配货-运输完成
    public static final int FREIGHT_ORDER_FINISH = 8; //空车配货-订单完成
    public static final int FREIGHT_APPLY_AHEAD_CHARGE = 9; //空车配货-司机申请运费垫付
    public static final int FREIGHT_INVALID = 10; //空车配货-订单已失效


    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_SERVICE_TYPE = "serviceType";
    public static final String KEY_IS_MULTI_MODE = "isMultiMode";
    public static final String KEY_IS_FILTER_MODE = "isFilterMode";
    public static final String KEY_ERROR_CODE = "error_code";
    public static final String KEY_ERROR_DESC = "error_desc";
    public static final String KEY_ACTION = "action";
    public static final String KEY_OBJECT = "object";
    public static final String KEY_TOTAL = "total";
    public static final String KEY_PRODUCT_LIST = "productList";
    public static final String KEY_HAS_CHANGED = "hasChanged";
    public static final String KEY_MOTION = "motion";
    public static final String KEY_REGISTER_ID = "registerID";
    public static final String KEY_NAME = "name";
    public static final String KEY_PID = "pid";
    public static final String KEY_IS_STOCK_SHOP = "isStockShop";
    public static final String KEY_BITMAP = "bitmap";
    public static final String KEY_QUALITY = "quality";
    public static final String KEY_ADDRESS_LEVEL = "addressLevel";
    public static final String KEY_TYPE = "type";
    public static final String KEY_IS_READ_MODE = "isReadMode";
    public static final String KEY_POI = "poi";
    public static final String KEY_LATLNG = "latLng";
    public static final String KEY_POSITION = "position";
    public static final String KEY_IS_SELECT_MODE = "isSelectMode";
    public static final String KEY_UNIT = "unit";
    public static final String KEY_VERSION_CODE = "version_code";
    public static final String KEY_PUSH_TYPE = "pushType";



    public static final String FILE_PROVIDER = "com.appjumper.silkscreen.fileprovider";
}
