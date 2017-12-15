package com.appjumper.silkscreen.ui.my;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.bean.Freight;
import com.appjumper.silkscreen.bean.FreightOffer;
import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.bean.UserResponse;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.WebViewActivity;
import com.appjumper.silkscreen.ui.home.CompanyDetailsActivity;
import com.appjumper.silkscreen.ui.home.stockshop.ReleaseGoodsSelectActivity;
import com.appjumper.silkscreen.ui.money.MessageActivity;
import com.appjumper.silkscreen.ui.my.adapter.MyDeliverAdapter;
import com.appjumper.silkscreen.ui.my.adapter.MyDriverAdapter;
import com.appjumper.silkscreen.ui.my.askbuy.AskBuyOrderListActivity;
import com.appjumper.silkscreen.ui.my.audit.AuditMenuActivity;
import com.appjumper.silkscreen.ui.my.deliver.AuditRefuseActivity;
import com.appjumper.silkscreen.ui.my.deliver.AuditingDeliverActivity;
import com.appjumper.silkscreen.ui.my.deliver.ChooseDriverActivity;
import com.appjumper.silkscreen.ui.my.deliver.DeliverOrderListActivity;
import com.appjumper.silkscreen.ui.my.deliver.DriverComingActivity;
import com.appjumper.silkscreen.ui.my.deliver.LoadingDeliverActivity;
import com.appjumper.silkscreen.ui.my.deliver.OrderFinishDeliverActivity;
import com.appjumper.silkscreen.ui.my.deliver.TransportFinishDeliverActivity;
import com.appjumper.silkscreen.ui.my.deliver.TransportingDeliverActivity;
import com.appjumper.silkscreen.ui.my.deliver.WaitDriverPayActivity;
import com.appjumper.silkscreen.ui.my.driver.DriverOrderListActivity;
import com.appjumper.silkscreen.ui.my.driver.DriverPayActivity;
import com.appjumper.silkscreen.ui.my.driver.GoToDeliverActivity;
import com.appjumper.silkscreen.ui.my.driver.LoadingDriverActivity;
import com.appjumper.silkscreen.ui.my.driver.OfferedActivity;
import com.appjumper.silkscreen.ui.my.driver.OrderFinishDriverActivity;
import com.appjumper.silkscreen.ui.my.driver.ReceiveInquiryActivity;
import com.appjumper.silkscreen.ui.my.driver.TransportFinishDriverActivity;
import com.appjumper.silkscreen.ui.my.driver.TransportingDriverActivity;
import com.appjumper.silkscreen.ui.my.enterprise.CertifyManageActivity;
import com.appjumper.silkscreen.ui.my.enterprise.EnterpriseCreateActivity;
import com.appjumper.silkscreen.util.CommonUtil;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.ShareUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import q.rorbin.badgeview.QBadgeView;


/**
 * Created by yc on 2016/11/7.
 * 我的
 */
public class MyFragment extends BaseFragment {
    @Bind(R.id.img_head)//用户头像
            ImageView img_head;

    @Bind(R.id.tv_name)//用户姓名
            TextView tv_name;

    @Bind(R.id.txtCompanyName)
    TextView txtCompanyName;
    @Bind(R.id.llReleaseStockGoods)
    LinearLayout llReleaseStockGoods;

    @Bind(R.id.llAudit)
    LinearLayout llAudit;
    @Bind(R.id.rlAudit)
    RelativeLayout rlAudit;

    @Bind(R.id.llDeliver)
    LinearLayout llDeliver;
    @Bind(R.id.llDriver)
    LinearLayout llDriver;

    @Bind(R.id.txtDriverState)
    TextView txtDriverState;

    @Bind(R.id.txtDeliverNum)
    TextView txtDeliverNum;
    @Bind(R.id.recyclerDeliver)
    RecyclerView recyclerDeliver;

    @Bind(R.id.txtDriverNum)
    TextView txtDriverNum;
    @Bind(R.id.recyclerDriver)
    RecyclerView recyclerDriver;
    @Bind(R.id.llChangeState)
    LinearLayout llChangeState;

    @Bind(R.id.llAskBuy)
    LinearLayout llAskBuy;
    @Bind(R.id.unReadAudit)
    TextView unReadAudit;
    @Bind(R.id.unReadPay)
    TextView unReadPay;
    @Bind(R.id.unReadFinishing)
    TextView unReadFinishing;
    @Bind(R.id.unReadFinish)
    TextView unReadFinish;

    private List<Freight> deliverList;
    private MyDeliverAdapter deliverAdapter;
    private List<Freight> driverList;
    private MyDriverAdapter driverAdapter;

    private QBadgeView badgeAudit; //快速审核小红点



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_my2, null);
        ButterKnife.bind(this, view);
        initProgressDialog();
        return view;
    }


    private void initView() {
        User user = getUser();
        if (user != null) {
            if (user.getAvatar() != null) {
                Picasso.with(getContext()).load(user.getAvatar().getSmall()).placeholder(R.mipmap.img_error_head).error(R.mipmap.img_error_head)
                        .into(img_head);
            }

            if (TextUtils.isEmpty(user.getUser_nicename()))
                tv_name.setText("未设置昵称");
            else
                tv_name.setText(user.getUser_nicename());

            txtCompanyName.setVisibility(View.VISIBLE);
            Enterprise enterprise = user.getEnterprise();
            if (enterprise != null)
                txtCompanyName.setText(enterprise.getEnterprise_name());
            else
                txtCompanyName.setText("完善企业信息");

            if (user.getIs_goods().equals("1"))
                llReleaseStockGoods.setVisibility(View.VISIBLE);
            else
                llReleaseStockGoods.setVisibility(View.GONE);

            if (user.getIs_fast_examine().equals("1")) {
                llAudit.setVisibility(View.VISIBLE);
                getUnreadAudit();
            } else {
                llAudit.setVisibility(View.GONE);
            }

            if (user.getIs_purchase_order().equals("1")) {
                llAskBuy.setVisibility(View.VISIBLE);
                getUnreadAskBuy();
            } else {
                llAskBuy.setVisibility(View.GONE);
            }

            if (user.getDriver_status().equals(Const.AUTH_SUCCESS + "")) { //司机
                llDriver.setVisibility(View.VISIBLE);
                llDeliver.setVisibility(View.GONE);
                txtDriverState.setText(user.getDriver_car_status().equals("0") ? "更改为运输中" : "更改为空闲");
                getDriverOrder();
            } else {
                llDriver.setVisibility(View.GONE);
                if (user.getIs_vender().equals("1")) { //发货厂家
                    llDeliver.setVisibility(View.VISIBLE);
                    getDeliverOrder();
                } else {
                    llDeliver.setVisibility(View.GONE);
                }
            }

        } else {
            txtCompanyName.setVisibility(View.GONE);
            tv_name.setText("请登录／注册");
            img_head.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.img_error_head));
        }
    }


    @Override
    protected void initData() {
        /*if (getUser() != null) {
            new Thread(new UserInfoRun()).start();
        } else {
            initView();
        }*/

        initOrderView();
        initView();
        initUnread();
        if (getUser() != null) {
            new Thread(new UserInfoRun()).start();
        }
    }


    private void initOrderView() {
        /**
         * 发货厂家订单
         */
        deliverList = new ArrayList<>();

        deliverAdapter = new MyDeliverAdapter(R.layout.item_recycler_freight_my, deliverList);
        recyclerDeliver.setLayoutManager(new LinearLayoutManager(context));
        deliverAdapter.bindToRecyclerView(recyclerDeliver);
        deliverAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        deliverAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Freight freight = deliverList.get(position);
                int state = Integer.valueOf(freight.getExamine_status());
                Intent intent = null;
                switch (state) {
                    case Const.FREIGHT_AUDITING: //审核中
                        intent = new Intent(context, AuditingDeliverActivity.class);
                        break;
                    case Const.FREIGHT_AUDIT_REFUSE: //审核不通过
                        intent = new Intent(context, AuditRefuseActivity.class);
                        break;
                    case Const.FREIGHT_AUDIT_PASS: //司机报价中
                        intent = new Intent(context, ChooseDriverActivity.class);
                        break;
                    case Const.FREIGHT_DRIVER_PAYING: //等待司机支付
                        intent = new Intent(context, WaitDriverPayActivity.class);
                        break;
                    case Const.FREIGHT_GOTO_LOAD: //司机正在赶来
                        intent = new Intent(context, DriverComingActivity.class);
                        break;
                    case Const.FREIGHT_LOADING: //装货中
                        intent = new Intent(context, LoadingDeliverActivity.class);
                        break;
                    case Const.FREIGHT_TRANSPORTING: //运输途中
                        intent = new Intent(context, TransportingDeliverActivity.class);
                        break;
                    case Const.FREIGHT_TRANSPORT_FINISH: //运输完成
                        intent = new Intent(context, TransportFinishDeliverActivity.class);
                        break;
                    case Const.FREIGHT_ORDER_FINISH: //订单完成
                        intent = new Intent(context, OrderFinishDeliverActivity.class);
                        break;
                }

                if (intent != null) {
                    intent.putExtra("id", freight.getId());
                    startActivity(intent);
                }
            }
        });


        /**
         * 司机订单
         */
        driverList = new ArrayList<>();

        driverAdapter = new MyDriverAdapter(R.layout.item_recycler_freight_my, driverList);
        recyclerDriver.setLayoutManager(new LinearLayoutManager(context));
        driverAdapter.bindToRecyclerView(recyclerDriver);
        driverAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        driverAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Freight freight = driverList.get(position);
                int state = Integer.valueOf(freight.getExamine_status());
                Intent intent = null;
                switch (state) {
                    case Const.FREIGHT_AUDIT_PASS: // 收到询价或者已报价
                        List<FreightOffer> offerList = freight.getOffer_list();
                        if (offerList != null && offerList.size() > 0)
                            intent = new Intent(context, OfferedActivity.class);
                        else
                            intent = new Intent(context, ReceiveInquiryActivity.class);
                        break;
                    case Const.FREIGHT_DRIVER_PAYING: //等待支付
                        intent = new Intent(context, DriverPayActivity.class);
                        break;
                    case Const.FREIGHT_GOTO_LOAD: //前往厂家
                        intent = new Intent(context, GoToDeliverActivity.class);
                        break;
                    case Const.FREIGHT_LOADING: //装货中
                        intent = new Intent(context, LoadingDriverActivity.class);
                        break;
                    case Const.FREIGHT_TRANSPORTING: //运输途中
                        intent = new Intent(context, TransportingDriverActivity.class);
                        break;
                    case Const.FREIGHT_TRANSPORT_FINISH: //运输完成
                        intent = new Intent(context, TransportFinishDriverActivity.class);
                        break;
                    case Const.FREIGHT_ORDER_FINISH: //订单完成
                        intent = new Intent(context, OrderFinishDriverActivity.class);
                        break;
                }

                if (intent != null) {
                    intent.putExtra("id", freight.getId());
                    startActivity(intent);
                }
            }
        });
    }


    private void initUnread() {
        badgeAudit = new QBadgeView(context);
        badgeAudit.bindTarget(rlAudit).setBadgePadding(4.3f, true).setBadgeGravity(Gravity.START | Gravity.TOP).setGravityOffset(6, 7, true);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getUser() != null) {
            new Thread(new UserInfoRun()).start();
        }
    }



    @OnClick({R.id.llCompany, R.id.rl_user, R.id.rl_share, R.id.rl_system_setting, R.id.rlHelp, R.id.txtMoreDeliver, R.id.txtMoreDriver, R.id.rlAudit,
            R.id.rl_feedback, R.id.ll_certify, R.id.rl_point, R.id.rl_my_release, R.id.rlMyDeal, R.id.rlReleaseStockGoods, R.id.txtDriverState, R.id.txtUpdateLocation,
            R.id.txtMoreAskBuy, R.id.rlAuditing, R.id.rlPaying, R.id.rlFinishing, R.id.rlFinish})
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.llCompany: //企业信息
                if (checkLogined()) {
                    if (getUser().getEnterprise() != null)
                        start_Activity(getActivity(), CompanyDetailsActivity.class, new BasicNameValuePair("from", "1"), new BasicNameValuePair("id", getUser().getEnterprise().getEnterprise_id()));
                    else
                        start_Activity(getActivity(), EnterpriseCreateActivity.class, new BasicNameValuePair("type", "0"));
                }
                break;
            case R.id.rlHelp://帮助
                start_Activity(getActivity(), WebViewActivity.class,new BasicNameValuePair("title","帮助"),new BasicNameValuePair("url",Url.IP+"/index.php?g=portal&m=page&a=index&id=3"));
                break;
            case R.id.rl_share://分享
                ShareUtil.intShare(getActivity(), v, "构建丝网新生态，打造丝网行业的信息服务平台，在这里有你想要知道的所有丝网行业相关信息", "丝网+", Const.SHARE_APP_URL);
                break;
            case R.id.rl_feedback://意见反馈
                start_Activity(getActivity(), FeedbackActivity.class);
                //start_Activity(context, MapChooseActivity.class);
                break;
            case R.id.rl_user://用户信息
                if (checkLogined()) {
                    start_Activity(getActivity(), PersonalActivity.class);
                }
                break;
            case R.id.rl_system_setting://系统设置
                start_Activity(getActivity(), SystemSettingActivity.class);
                break;
            case R.id.ll_certify://查看认证
                if (checkLogined()) {
                    //start_Activity(getActivity(), AuthenticationAdministrationActivity.class);
                    start_Activity(context, CertifyManageActivity.class);
                }
                break;
            case R.id.rl_point://我的积分
                if (checkLogined()) {
                    start_Activity(getActivity(), MyPointActivity.class);
                }
                break;
            case R.id.rl_my_release: //我的发布
                if (checkLogined())
                    start_Activity(context, MyReleaseActivity.class);
                break;
            case R.id.rlMyDeal: //我的询报价
                start_Activity(getActivity(), MessageActivity.class);
                break;
            case R.id.rlReleaseStockGoods: //发布现货商品
                start_Activity(context, ReleaseGoodsSelectActivity.class);
                break;
            case R.id.txtMoreDeliver: //发货厂家订单列表
                start_Activity(context, DeliverOrderListActivity.class);
                break;
            case R.id.txtMoreDriver: //司机订单列表
                start_Activity(context, DriverOrderListActivity.class);
                break;
            case R.id.txtDriverState: //更改司机状态
                changeDriverState();
                break;
            case R.id.txtUpdateLocation: //更新位置
                CommonUtil.getLocation(new AMapLocationListener() {
                    @Override
                    public void onLocationChanged(AMapLocation aMapLocation) {
                        if (aMapLocation.getErrorCode() == 0) {
                            updateLocation(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                        } else {
                            showErrorToast("定位失败: " + aMapLocation.getErrorInfo());
                        }
                    }
                });
                break;
            case R.id.rlAudit: //快速审核
                start_Activity(context, AuditMenuActivity.class);
                break;
            case R.id.txtMoreAskBuy: //求购订单列表
                start_Activity(context, AskBuyOrderListActivity.class);
                break;
            case R.id.rlAuditing:
                intent = new Intent(context, AskBuyOrderListActivity.class);
                intent.putExtra(Const.KEY_POSITION, 1);
                startActivity(intent);
                break;
            case R.id.rlPaying:
                intent = new Intent(context, AskBuyOrderListActivity.class);
                intent.putExtra(Const.KEY_POSITION, 2);
                startActivity(intent);
                break;
            case R.id.rlFinishing:
                intent = new Intent(context, AskBuyOrderListActivity.class);
                intent.putExtra(Const.KEY_POSITION, 3);
                startActivity(intent);
                break;
            case R.id.rlFinish:
                intent = new Intent(context, AskBuyOrderListActivity.class);
                intent.putExtra(Const.KEY_POSITION, 4);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    //用户信息
    private class UserInfoRun implements Runnable {
        private UserResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("g", "api");
                data.put("m", "user");
                data.put("a", "info");

                data.put("uid", getUserID());

                //response = JsonParser.getUserResponse(HttpUtil.postMsg(HttpUtil.getData(data), Url.USERINFO));
                response = JsonParser.getUserResponse(HttpUtil.getMsg(Url.HOST + "?" + HttpUtil.getData(data)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        NETWORK_SUCCESS_PAGER_RIGHT, response));
            } else {
                handler.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    }



    private MyHandler handler = new MyHandler();

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (isDetached()) return;

            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT://用户信息
                    UserResponse userResponse = (UserResponse) msg.obj;
                    if (userResponse.isSuccess()) {
                        User user = userResponse.getData();
                        getMyApplication().getMyUserManager().storeUserInfo(user);

                        if (user.getDriver_status().equals(Const.AUTH_SUCCESS + "")) {
                            double lat = Double.valueOf(getLat());
                            double lng = Double.valueOf(getLng());
                            if (lat != 0 || lng != 0)
                                updateLocationSilent(getLat(), getLng());
                        }

                        if (isViewCreated && isDataInited)
                            initView();
                    }
                    break;
                case NETWORK_SUCCESS_DATA_ERROR:
                    //showErrorToast();
                    break;
                default:
                    //showErrorToast();
                    break;
            }
        }
    };


    /**
     * 获取发货厂家订单
     */
    private void getDeliverOrder() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "car_product_list");
        params.put("page", 1);
        params.put("pagesize", 1);
        params.put("uid", getUserID());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        List<Freight> list = GsonUtil.getEntityList(dataObj.getJSONArray("items").toString(), Freight.class);
                        deliverList.clear();
                        deliverList.addAll(list);
                        deliverAdapter.notifyDataSetChanged();
                        if (!isViewCreated) return;
                        txtDeliverNum.setText("我的发货订单（" + dataObj.getString("total") + "）");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }


    /**
     * 获取司机订单
     */
    private void getDriverOrder() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "car_driver_list");
        params.put("page", 1);
        params.put("pagesize", 1);
        params.put("uid", getUserID());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        List<Freight> list = GsonUtil.getEntityList(dataObj.getJSONArray("items").toString(), Freight.class);
                        driverList.clear();
                        driverList.addAll(list);
                        driverAdapter.notifyDataSetChanged();
                        if (!isViewCreated) return;
                        txtDriverNum.setText("我的运输订单（" + dataObj.getString("total") + "）");

                        if (getUser().getDriver_car_status().equals("0")) {
                            recyclerDriver.setVisibility(View.VISIBLE);
                            llChangeState.setVisibility(View.GONE);
                        } else {
                            if (driverList.size() == 0) {
                                txtDriverNum.setText("暂无订单");
                                recyclerDriver.setVisibility(View.GONE);
                                llChangeState.setVisibility(View.VISIBLE);
                            } else {
                                int orderState = Integer.valueOf(driverList.get(0).getExamine_status());
                                if (orderState == Const.FREIGHT_INVALID || orderState == Const.FREIGHT_ORDER_FINISH) {
                                    txtDriverNum.setText("暂无订单");
                                    recyclerDriver.setVisibility(View.GONE);
                                    llChangeState.setVisibility(View.VISIBLE);
                                } else {
                                    recyclerDriver.setVisibility(View.VISIBLE);
                                    llChangeState.setVisibility(View.GONE);
                                }
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }


    /**
     * 更改司机状态
     */
    private void changeDriverState() {
        RequestParams params = MyHttpClient.getApiParam("user", "driver_car_status");
        params.put("uid", getUserID());
        params.put("driver_car_status", getUser().getDriver_car_status().equals("0") ? 1 : 0);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        new Thread(new UserInfoRun()).start();
                        showErrorToast("更改状态成功");
                    } else {
                        showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (!isViewCreated) return;

                progress.dismiss();
            }
        });
    }


    /**
     * 更新位置
     */
    private void updateLocation(double lat, double lng) {
        RequestParams params = MyHttpClient.getApiParam("user", "driver_position");
        params.put("uid", getUserID());
        params.put("driver_lat", lat + "");
        params.put("driver_lng", lng + "");

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        showErrorToast("更新位置成功");
                    } else {
                        showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }
        });
    }


    /**
     * 更新位置，静默模式
     */
    private void updateLocationSilent(String lat, String lng) {
        RequestParams params = MyHttpClient.getApiParam("user", "driver_position");
        params.put("uid", getUserID());
        params.put("driver_lat", lat);
        params.put("driver_lng", lng);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }


    /**
     * 获取快速审核未处理数
     */
    private void getUnreadAudit() {
        RequestParams params = MyHttpClient.getApiParam("collection", "fast_examine_num");
        params.put("uid", getUserID());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        int numFreight = dataObj.optInt("car_product_num");
                        int numAskbuy = dataObj.optInt("purchase_num");
                        int numPerson = dataObj.optInt("auth_status_num");
                        int numDriver = dataObj.optInt("driver_status_num");

                        if (numFreight > 0 || numAskbuy > 0 || numPerson > 0 || numDriver > 0)
                            badgeAudit.setBadgeNumber(-1);
                        else
                            badgeAudit.setBadgeNumber(0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }


    /**
     * 获取求购订单未处理数
     */
    private void getUnreadAskBuy() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "my_purchase_order_num");
        params.put("uid", getUserID());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        int auditNum = dataObj.optInt("wait_examine");
                        int payNum = dataObj.optInt("wait_pay");
                        int finishingNum = dataObj.optInt("wait_confirm");

                        unReadAudit.setText(auditNum + "");
                        unReadPay.setText(payNum + "");
                        unReadFinishing.setText(finishingNum + "");

                        unReadAudit.setVisibility(auditNum > 0 ? View.VISIBLE : View.GONE);
                        unReadPay.setVisibility(payNum > 0 ? View.VISIBLE : View.GONE);
                        unReadFinishing.setVisibility(finishingNum > 0 ? View.VISIBLE : View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }


    /**
     * 测试接口
     */
    private void test() {
        RequestParams params = MyHttpClient.getApiParam("Analysis", "edit_analysis_post");
        params.remove("g");
        params.put("g", "Admin");
        params.put("id", 8);

        MyHttpClient.getInstance().post(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

}
