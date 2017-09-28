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
import com.appjumper.silkscreen.ui.my.enterprise.CertifyManageActivity;
import com.appjumper.silkscreen.ui.my.enterprise.EnterpriseCreateActivity;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.ShareUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;

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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_my2, null);
        ButterKnife.bind(this, view);
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



    @OnClick({R.id.llCompany, R.id.rl_user, R.id.rl_share, R.id.rl_system_setting, R.id.rlHelp,
            R.id.rl_feedback, R.id.ll_certify, R.id.rl_point, R.id.rl_my_release, R.id.rlMyDeal, R.id.rlReleaseStockGoods})
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
                //start_Activity(context, InquiryHuLanActivity.class);
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
                data.put("uid", getUserID());
                response = JsonParser.getUserResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.USERINFO));
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
