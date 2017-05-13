package com.appjumper.silkscreen.ui.my;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.bean.UserResponse;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.WebViewActivity;
import com.appjumper.silkscreen.ui.home.CompanyDetailsActivity;
import com.appjumper.silkscreen.ui.my.enterprise.AuthenticationAdministrationActivity;
import com.appjumper.silkscreen.ui.my.enterprise.EnterpriseCreateActivity;
import com.appjumper.silkscreen.ui.my.enterprise.ServiceAdministrationActivity;
import com.appjumper.silkscreen.util.PicassoRoundTransform;
import com.appjumper.silkscreen.util.ShareUtil;
import com.squareup.picasso.Picasso;

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

    @Bind(R.id.iv_enterprise_logo)//企业头像
            ImageView iv_enterprise_logo;

    @Bind(R.id.tv_enterprise_name)//企业名称
            TextView tv_enterprise_name;

    @Bind(R.id.tv_enterprise_auth_status)//企
            ImageView tv_enterprise_auth_status;

    @Bind(R.id.tv_enterprise_productivity_auth_status)//力
            ImageView tv_enterprise_productivity_auth_status;

    @Bind(R.id.rl_enterprise_create)//已创建企业显示布局
            LinearLayout rl_enterprise_create;

    @Bind(R.id.rl_enterprise)//未创建企业显示布局
            RelativeLayout rl_enterprise;

    @Bind(R.id.tv_name)//用户姓名
            TextView tv_name;

    @Bind(R.id.tv_mobile)//用户手机号
            TextView tv_mobile;

    @Bind(R.id.tv_score)//积分
            TextView tv_score;

    @Bind(R.id.tv_personal_certificate)//个人认证状态
            TextView tv_personal_certificate;

    private Enterprise enterprise;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_my, null);
        ButterKnife.bind(this, view);
        return view;
    }


    private void initView() {
        User user = getUser();
        if (user != null) {
            enterprise = user.getEnterprise();
            if (enterprise != null) {
                rl_enterprise.setVisibility(View.GONE);
                rl_enterprise_create.setVisibility(View.VISIBLE);
                if (enterprise.getEnterprise_logo() != null) {
                    Picasso.with(getContext()).load(enterprise.getEnterprise_logo().getSmall()).placeholder(R.mipmap.icon_logo_image61).error(R.mipmap.icon_logo_image61).transform(new PicassoRoundTransform()).into(iv_enterprise_logo);
                }
                tv_enterprise_name.setText(enterprise.getEnterprise_name());

                if (enterprise.getEnterprise_auth_status() != null && enterprise.getEnterprise_auth_status().equals("2")) {
                    tv_enterprise_auth_status.setVisibility(View.VISIBLE);
                } else {
                    tv_enterprise_auth_status.setVisibility(View.GONE);
                }
                if (enterprise.getEnterprise_productivity_auth_status() != null && enterprise.getEnterprise_productivity_auth_status().equals("2")) {
                    tv_enterprise_productivity_auth_status.setVisibility(View.VISIBLE);
                } else {
                    tv_enterprise_productivity_auth_status.setVisibility(View.GONE);
                }
            } else {
                rl_enterprise.setVisibility(View.VISIBLE);
                rl_enterprise_create.setVisibility(View.GONE);
            }

            String authStatus = user.getAuth_status();
            switch (authStatus) {
                case "0":
                    tv_personal_certificate.setText("未认证");
                    break;
                case "1":
                    tv_personal_certificate.setText("认证中");
                    break;
                case "2":
                    tv_personal_certificate.setText("认证成功");
                    break;
                case "3":
                    tv_personal_certificate.setText("认证失败");
                    break;
            }
            tv_mobile.setVisibility(View.VISIBLE);
            tv_name.setText(user.getUser_nicename());
            tv_score.setText(user.getScore());
            tv_mobile.setText("手机号：" + user.getMobile());
            if (user.getAvatar() != null) {
                Picasso.with(getContext()).load(user.getAvatar().getSmall()).placeholder(R.mipmap.img_error_head).error(R.mipmap.img_error_head).transform(new PicassoRoundTransform()).into(img_head);
            }
        } else {
            rl_enterprise.setVisibility(View.VISIBLE);
            rl_enterprise_create.setVisibility(View.GONE);
            tv_name.setText("请登录／注册");
            tv_score.setText("");
            tv_mobile.setVisibility(View.GONE);
            tv_personal_certificate.setText("");
            img_head.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.img_error_head));
        }
    }


    @Override
    protected void initData() {
        if (getUser() != null) {
            new Thread(new UserInfoRun()).start();
        } else {
            initView();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getUser() != null) {
            new Thread(new UserInfoRun()).start();
        } else {
            initView();
        }
    }



    @OnClick({R.id.rl_user, R.id.rl_share, R.id.rl_system_setting, R.id.rl_enterprise,
            R.id.rl_feedback, R.id.ll_view, R.id.ll_service, R.id.rl_point, R.id.ll_company,
            R.id.rl_collect, R.id.rl_personal_certificate, R.id.tv_help})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_help://帮助
                start_Activity(getActivity(), WebViewActivity.class,new BasicNameValuePair("title","帮助"),new BasicNameValuePair("url",Url.IP+"/index.php?g=portal&m=page&a=index&id=3"));
                break;
            case R.id.rl_share://分享
                ShareUtil.intShare(getActivity(), v, "构建丝网新生态，打造丝网行业的信息服务平台，在这里有你想要知道的所有丝网行业相关信息", "丝网+", "http://siwangjia.kuaizhan.com");
                break;
            case R.id.rl_personal_certificate://个人认证
                if (checkLogined()) {
                    start_Activity(getActivity(), PersonalAuthenticationActivity.class);
                }
                break;
            case R.id.rl_feedback://意见反馈
                start_Activity(getActivity(), FeedbackActivity.class);
                //start_Activity(context, ReleaseCiShengActivity.class);
                break;
            case R.id.rl_enterprise://企业信息
                if (checkLogined()) {
                    start_Activity(getActivity(), EnterpriseCreateActivity.class, new BasicNameValuePair("type", "0"));
                }
                break;
            case R.id.rl_user://用户信息
                if (checkLogined()) {
                    start_Activity(getActivity(), PersonalActivity.class);
                }
                break;
            case R.id.rl_system_setting://系统设置
                start_Activity(getActivity(), SystemSettingActivity.class);
                //start_Activity(context, InquiryCiShengActivity.class);
                break;
            case R.id.ll_view://查看公司认证
                if (checkLogined()) {
                    start_Activity(getActivity(), AuthenticationAdministrationActivity.class);
                }
                break;
            case R.id.ll_service://服务管理
                if (checkLogined()) {
                    start_Activity(getActivity(), ServiceAdministrationActivity.class);
                }
                break;
            case R.id.rl_point://我的积分
                if (checkLogined()) {
                    start_Activity(getActivity(), MyPointActivity.class);
                }
                break;
            case R.id.ll_company://公司详情
                start_Activity(getActivity(), CompanyDetailsActivity.class, new BasicNameValuePair("from", "1"), new BasicNameValuePair("id", enterprise.getEnterprise_id()));
                break;
            case R.id.rl_collect://企业收藏
                if (checkLogined()) {
                    start_Activity(getActivity(), EnterpriseCollectionActivity.class);
                }
                break;
            default:
                break;
        }
    }

    //修改
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

    ;
    private MyHandler handler = new MyHandler();

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT://修改性别
                    UserResponse userResponse = (UserResponse) msg.obj;
                    if (userResponse.isSuccess()) {
                        User user = userResponse.getData();
                        getMyApplication().getMyUserManager()
                                .storeUserInfo(user);
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
}
