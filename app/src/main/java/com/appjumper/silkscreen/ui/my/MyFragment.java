package com.appjumper.silkscreen.ui.my;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.bean.UserResponse;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.WebViewActivity;
import com.appjumper.silkscreen.ui.home.CompanyDetailsActivity;
import com.appjumper.silkscreen.ui.home.stockshop.ReleaseGoodsSelectActivity;
import com.appjumper.silkscreen.ui.money.MessageActivity;
import com.appjumper.silkscreen.ui.my.deliver.DeliverOrderListActivity;
import com.appjumper.silkscreen.ui.my.driver.DriverOrderListActivity;
import com.appjumper.silkscreen.ui.my.enterprise.CertifyManageActivity;
import com.appjumper.silkscreen.ui.my.enterprise.EnterpriseCreateActivity;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.ShareUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;



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

    @Bind(R.id.llDeliver)
    LinearLayout llDeliver;
    @Bind(R.id.llDriver)
    LinearLayout llDriver;

    @Bind(R.id.txtDriverState)
    TextView txtDriverState;



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


            /*if (user.getDriver_status().equals(Const.AUTH_SUCCESS + "")) {
                llDriver.setVisibility(View.VISIBLE);
                llDeliver.setVisibility(View.GONE);
                txtDriverState.setText(user.getDriver_car_status().equals("0") ? "更改为运输中" : "更改为空闲");
            } else {
                llDriver.setVisibility(View.GONE);
                llDeliver.setVisibility(View.VISIBLE);
            }*/

            txtDriverState.setText(user.getDriver_car_status().equals("0") ? "更改为运输中" : "更改为空闲");

        } else {
            txtCompanyName.setVisibility(View.GONE);
            tv_name.setText("请登录／注册");
            img_head.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.img_error_head));
            llReleaseStockGoods.setVisibility(View.GONE);
        }
    }


    @Override
    protected void initData() {
        /*if (getUser() != null) {
            new Thread(new UserInfoRun()).start();
        } else {
            initView();
        }*/

        initView();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getUser() != null) {
            new Thread(new UserInfoRun()).start();
        }
    }



    @OnClick({R.id.llCompany, R.id.rl_user, R.id.rl_share, R.id.rl_system_setting, R.id.rlHelp, R.id.txtMoreDeliver, R.id.txtMoreDriver,
            R.id.rl_feedback, R.id.ll_certify, R.id.rl_point, R.id.rl_my_release, R.id.rlMyDeal, R.id.rlReleaseStockGoods, R.id.txtDriverState})
    public void onClick(View v) {
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
                //start_Activity(context, DriverAuthThirdActivity.class);
                break;
            case R.id.rl_user://用户信息
                if (checkLogined()) {
                    start_Activity(getActivity(), PersonalActivity.class);
                }
                break;
            case R.id.rl_system_setting://系统设置
                start_Activity(getActivity(), SystemSettingActivity.class);
                //test();
                //start_Activity(context, InquiryCiShengActivity.class);
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
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT://用户信息
                    UserResponse userResponse = (UserResponse) msg.obj;
                    if (userResponse.isSuccess()) {
                        User user = userResponse.getData();
                        getMyApplication().getMyUserManager()
                                .storeUserInfo(user);
                        if (isViewCreated && isDataInited)
                            initView();
                    } else {
                        showErrorToast(userResponse.getError_desc());
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
                if (isDetached())
                    return;

                progress.dismiss();
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
