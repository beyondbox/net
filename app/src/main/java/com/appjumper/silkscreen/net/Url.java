package com.appjumper.silkscreen.net;

/**
 * Created by Administrator on 2016/6/29.
 */
public class Url {
    //http请求时候的md5加密用的秘钥，开发者（包括android，ios，php）请自行统一修改，以确保app网络安全
    public static final String SIG_KEY = "zuoapp.la";


    //服务器地址
    public static final String IP = "http://115.28.148.207";  //正式
    //public static final String IP = "http://192.168.1.114"; //测试
    public static final String HTTP_URL = IP + "/api/";

    public static final String HOST = IP + "/index.php";

    //获取验证码
    public static final String VERIFYCODE = HTTP_URL + "user/verifycode";
    //修改个人资料
    public static final String USEREDIT = HTTP_URL + "user/edit";
    //上传图片
    public static final String UPLOADIMAGE = HTTP_URL + "upload/image";
    //上传用户头像
    public static final String UPLOADAVATAR = HTTP_URL + "upload/avatar";
    //获取个人资料
    public static final String USERINFO = HTTP_URL + "user/info";
    //找回密码接口
    public static final String REPASSWORD = HTTP_URL + "user/repassword";
    //验证手机号
    public static final String AUTHCODE = HTTP_URL + "user/auth_mobile";
    //注册
    public static final String REGISTER = HTTP_URL + "user/register";
    //登录
    public static final String LOGIN = HTTP_URL + "user/login";
    //物流线路列表（货站／个人）
    public static final String LINELIST = HTTP_URL + "line/line_list";
    //物流线路列表（找车）
    public static final String TRUCK_LINELIST = HTTP_URL + "findcar/findcar_list";
    //找车详情
    public static final String TRUCK_DETAILS= HTTP_URL + "findcar/details";
    //找车发布
    public static final String TRUCK_RELEASE= HTTP_URL + "findcar/add";
    //物流信息发布
    public static final String LINEADD = HTTP_URL + "line/add";
    //物流信息详情
    public static final String LINEDETAILS = HTTP_URL + "line/details";
    //设备列表接口
    public static final String EQUIPMENTLIST = HTTP_URL + "equipment/equipment_list";
    //所有城市接口
    public static final String CITYLIST = HTTP_URL + "area/city_list";
    //所有下级位置接口
    public static final String SUB = HTTP_URL + "area/sub";
    //我的询价接口
    public static final String MYINQUIRY = HTTP_URL + "inquiry/my_inquiry";
    //我的报价接口
    public static final String MYOFFER = HTTP_URL + "inquiry/my_offer";
    //创建企业
    public static final String ENTERPRISEADD = HTTP_URL + "enterprise/edit";
    //编辑企业接口
    public static final String ENTERPRISEA_EDIT = HTTP_URL + "enterprise/add";
    //企业详情接口
    public static final String ENTERPRISEDETAILS = HTTP_URL + "enterprise/details";
    //企业认证
    public static final String ENTERPRISEAUTH = HTTP_URL + "enterprise/auth";
    //服务列表标签
    public static final String SERVICEMYLIST = HTTP_URL + "service/my_list";
    //产品类型列表接口
    public static final String SERVICEPRODUCTTYPE = HTTP_URL + "service/product_type";
    //产品列表接口
    public static final String SERVICEPRODUCT = HTTP_URL + "service/product";
    //设备发布接口
    public static final String EQUIPMENT_RELEASE = HTTP_URL + "equipment/add";
    //设备分类列表
    public static final String EQUIPMENT_CATEGORY = HTTP_URL + "equipment/equipment_type";
    //设备详情
    public static final String EQUIPMENT_DETAILS = HTTP_URL + "equipment/details";
    //厂房列表接口
    public static final String WORKSHOP_LIST = HTTP_URL + "workshop/workshop_list";
    //厂房详情
    public static final String WORKSHOP_DETAILS = HTTP_URL + "workshop/details";
    //厂房发布接口
    public static final String WORKSHOP_RELEASE = HTTP_URL + "workshop/add";
    //招聘列表
    public static final String RECRUIT_LIST= HTTP_URL + "recruit/recruit_list";
    //招聘发布
    public static final String RECRUIT_RELEASE= HTTP_URL + "recruit/add";
    //招聘详情
    public static final String RECRUIT_DETAILS= HTTP_URL + "recruit/details";
    //职位列表
    public static final String JOB_TYPE= HTTP_URL + "recruit/job_type";
    //添加服务
    public static final String SERVICEADD = HTTP_URL + "service/add";
    //服务列表接口
    public static final String SERVICELIST = HTTP_URL + "service/service_list";
    //服务列表物流接口
    public static final String SERVICEMY = HTTP_URL + "service/my";
    //服务详情接口
    public static final String SERVICEDETAILS = HTTP_URL + "service/details";
    //首页
    public static final String HOMEDATA = HTTP_URL + "home/data";
    //招标信息列表
    public static final String TENDERLIST = HTTP_URL + "tender/tender_list";
    //招标信息详情
    public static final String TENDERDETAILS = HTTP_URL + "tender/details";
    //中标信息列表
    public static final String SELECTLIST = HTTP_URL + "tender/select_list";
    //中标信息详情
    public static final String SELECTDETAILS = HTTP_URL + "tender/select_details";
    //展会列表
    public static final String EXPOLIST = HTTP_URL + "expo/expo_list";
    //行业信息列表
    public static final String NEWSLIST = HTTP_URL + "news/news_list";
    //企业认证信息接口
    public static final String AUTHINFO = HTTP_URL + "enterprise/auth_info";
    //生产力认证
    public static final String PRODUCTVITYAUTH = HTTP_URL + "enterprise/productivity_auth";
    //用户签到接口
    public static final String CHECKIN = HTTP_URL + "user/checkin";
    //积分记录接口
    public static final String INTEGRALLIST = HTTP_URL + "user/integral_list";
    //询价
    public static final String INQUIRY_ADD   = HTTP_URL + "inquiry/add";
    //企业收藏
    public static final String ENTERPRISE_COLLECTION  = HTTP_URL + "collection/my";
    //添加企业收藏
    public static final String ADD_COLLECTION  = HTTP_URL + "collection/add";
    //删除企业收藏
    public static final String DELETE_COLLECTION  = HTTP_URL + "collection/delete";
    //删除服务
    public static final String SERVICEDELETE = HTTP_URL + "service/delete";
    //意见反馈
    public static final String FEEDBACK  = HTTP_URL + "feedback/add";
    //询价详情
    public static final String INQUIRY_DETAILS  = HTTP_URL + "inquiry/details";
    //询价详情更多
    public static final String INQUIRY_MORE = HTTP_URL + "inquiry/more_offer";
    //报价接口
    public static final String OFFER  = HTTP_URL + "inquiry/offer";
    //报价接口
    public static final String CANCEL  = HTTP_URL + "inquiry/cancel";
    //热门搜索
    public static final String HOMEKEYWORKS = HTTP_URL + "home/keyworks";
    //企业列表
    public static final String ENTERPRISELIST = HTTP_URL + "enterprise/enterprise_list";
    //最新发布
    public static final String NEWPUBLIC = HTTP_URL + "home/newpublic";
    //所有产品
    public static final String PRICEALLPROFUVT = HTTP_URL + "price/allproduct";
    //现货筛选规格-包含值
    public static final String PRODUCTSPECVALUE = HTTP_URL + "service/product_spec_value";
    //获取个人认证信息
    public static final String USERAUTHINFO = HTTP_URL + "user/auth_info";
    //个人信息认证
    public static final String USERAUTH = HTTP_URL + "user/auth";
    //产品的价格详情
    public static final String PRICEDETAILS = HTTP_URL + "price/details";
    //询价-企业列表
    public static final String INQUIRY_ENTERPRISE = HTTP_URL + "inquiry/enterprise_list";
    //动态更多
    public static final String PRICE_MORE = HTTP_URL + "price/more";
    //版本更新
    public static final String UPDATE = HTTP_URL + "update";
    //帮助
    public static final String CONFIG = HTTP_URL + "config";

}
