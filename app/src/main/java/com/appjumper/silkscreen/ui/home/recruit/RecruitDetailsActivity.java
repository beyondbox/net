package com.appjumper.silkscreen.ui.home.recruit;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.bean.RecruitDetail;
import com.appjumper.silkscreen.bean.RecruitDetailsResponse;
import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.MapviewActivity;
import com.appjumper.silkscreen.ui.common.WebViewActivity;
import com.appjumper.silkscreen.ui.home.CompanyDetailsActivity;
import com.appjumper.silkscreen.ui.home.adapter.RecruitDetailAdapter;
import com.appjumper.silkscreen.util.PicassoRoundTransform;
import com.appjumper.silkscreen.view.MyListView;
import com.appjumper.silkscreen.view.ObservableScrollView;
import com.appjumper.silkscreen.view.SureOrCancelDialog;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshBase;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshScrollView;
import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yc on 2016/11/7.
 * 招聘详情
 */
public class RecruitDetailsActivity extends BaseActivity {


    @Bind(R.id.pull_refresh_scrollview)
    PullToRefreshScrollView mPullRefreshScrollView;
    @Bind(R.id.layout)
    LinearLayout layout;
    @Bind(R.id.list_view)
    MyListView listView;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_remark)
    TextView tvRemark;
    @Bind(R.id.tv_date)
    TextView tvDate;
    @Bind(R.id.tv_salary)//薪水
            TextView tvSalary;
    @Bind(R.id.tv_education)//学历
            TextView tvEducation;
    @Bind(R.id.tv_work_experience)//工作经历
            TextView tvWorkExperience;
    @Bind(R.id.tv_gender)//性别
            TextView tvGender;
    @Bind(R.id.tv_job_form)//职位类型
            TextView tvJobForm;

    @Bind(R.id.rl_company)
    LinearLayout rlCompany;
    @Bind(R.id.iv_logo)//公司图片
            ImageView ivLogo;
    @Bind(R.id.tv_company_name)//公司名称
            TextView tvCompanyName;
    @Bind(R.id.tv_address)//公司地址
            TextView tv_address;
    @Bind(R.id.user_auth_status)//个人认证
            ImageView user_auth_status;
    @Bind(R.id.tv_auth_status)//个人认证（企业上的）
            ImageView tv_auth_status;
    @Bind(R.id.tv_enterprise_auth_status)//企
            ImageView tv_enterprise_auth_status;
    @Bind(R.id.tv_enterprise_productivity_auth_status)//力
            ImageView tv_enterprise_productivity_auth_status;
    @Bind(R.id.rl_user)
    RelativeLayout rlUser;
    @Bind(R.id.iv_img)//个人头像
            ImageView iv_img;

    @Bind(R.id.tv_name)//个人姓名
            TextView tv_name;

    @Bind(R.id.tv_mobile)//个人手机号
            TextView tv_mobile;

    private String id;
    private String mobile;
    private String url;
    private String eid;//企业id
    private Enterprise enterprise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit_details);
        ButterKnife.bind(this);
        initBack();
        id = getIntent().getStringExtra("id");
        listerrefresh();
        initProgressDialog();
        progress.show();
        progress.setMessage("正在加载...");
        refresh();
    }

    private void initView(final RecruitDetail data) {
        tvTitle.setText(data.getName());
        tvRemark.setText(data.getResponsibilities());
        tvSalary.setText("￥" + data.getSalary()+"元/月");
        tvEducation.setText(data.getEducation());
        tvGender.setText(data.getGender());
        tvJobForm.setText(data.getRemark());
        tvWorkExperience.setText(data.getExperience() + "年");
        tvDate.setText(data.getCreate_time().substring(5, 16));
        listView.setAdapter(new RecruitDetailAdapter(this, data.getRecommend()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                start_Activity(RecruitDetailsActivity.this, RecruitDetailsActivity.class, new BasicNameValuePair("id", data.getRecommend().get(position).getId()));
            }
        });
        if (data.getEnterprise() != null) {
            enterprise = data.getEnterprise();
            eid = enterprise.getEnterprise_id();
            rlCompany.setVisibility(View.VISIBLE);
            mobile = enterprise.getEnterprise_tel();
            url = enterprise.getEnterprise_website();
            Picasso.with(this).load(enterprise.getEnterprise_logo().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.icon_logo_image61).error(R.mipmap.icon_logo_image61).into(ivLogo);
            tv_address.setText(enterprise.getEnterprise_address());
            tvCompanyName.setText(enterprise.getEnterprise_name());
            if (enterprise.getUser_auth_status() != null && enterprise.getUser_auth_status().equals("2")) {
                tv_auth_status.setVisibility(View.VISIBLE);
            } else {
                tv_auth_status.setVisibility(View.GONE);
            }
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
            rlUser.setVisibility(View.GONE);
        } else {
            User user = data.getUser();
            if (user != null) {
                rlCompany.setVisibility(View.GONE);
                rlUser.setVisibility(View.VISIBLE);
                tv_name.setText(user.getUser_nicename());
                tv_mobile.setText(user.getMobile());
                mobile = user.getMobile();
                if (user.getAvatar() != null && !user.getAvatar().getSmall().equals("")) {
                    Picasso.with(this).load(user.getAvatar().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.img_error_head).error(R.mipmap.img_error_head).into(iv_img);
                }

                if (user.getAuth_status() != null && user.getAuth_status().equals("2")) {
                    user_auth_status.setVisibility(View.VISIBLE);
                } else {
                    user_auth_status.setVisibility(View.GONE);
                }
            }

        }
    }

    private void refresh() {
        mPullRefreshScrollView.setRefreshing();
        new Thread(detailsRun).start();
    }

    private void listerrefresh() {
        mPullRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ObservableScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ObservableScrollView> refreshView) {
                new Thread(detailsRun).start();
            }
        });
    }

    //详情
    private Runnable detailsRun = new Runnable() {
        private RecruitDetailsResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("id", id);
                data.put("uid", getUserID());
                response = JsonParser.getRecruitDetailsResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.RECRUIT_DETAILS));
            } catch (Exception e) {
                progress.dismiss();
                e.printStackTrace();
            }
            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        NETWORK_SUCCESS_PAGER_RIGHT, response));
            } else {
                handler.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    };
    private MyHandler handler = new MyHandler(this);

    private class MyHandler extends Handler {
        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            if (isDestroyed())
                return;

            mPullRefreshScrollView.onRefreshComplete();
            if (progress != null) {
                progress.dismiss();
            }
            RecruitDetailsActivity activity = (RecruitDetailsActivity) reference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT://详情
                    RecruitDetailsResponse baseResponse = (RecruitDetailsResponse) msg.obj;
                    if (baseResponse.isSuccess()) {
                        layout.setVisibility(View.VISIBLE);
                        RecruitDetail data = baseResponse.getData();
                        initView(data);

                        if (!getUserID().equals(data.getUser_id()))
                            CommonApi.addLiveness(data.getUser_id(), 20);
                    } else {
                        showErrorToast(baseResponse.getError_desc());
                    }
                    break;
                case NETWORK_FAIL:
                    activity.showErrorToast();
                    break;
            }
        }
    }


    @OnClick({R.id.tv_contact, R.id.rl_company, R.id.ll_phone, R.id.ll_url, R.id.ll_location, R.id.rl_user})
    public void onClick(View v) {
        SureOrCancelDialog followDialog = new SureOrCancelDialog(this, "拨打电话？", new SureOrCancelDialog.SureButtonClick() {
            @Override
            public void onSureButtonClick() {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobile));//跳转到拨号界面，同时传递电话号码
                startActivity(dialIntent);
            }
        });
        switch (v.getId()) {
            case R.id.tv_contact://马上联系

                followDialog.show();
                break;
            case R.id.rl_company:
                start_Activity(RecruitDetailsActivity.this, CompanyDetailsActivity.class, new BasicNameValuePair("from", "2"), new BasicNameValuePair("id", eid));
                break;
            case R.id.ll_phone:
                followDialog.show();
                break;
            case R.id.ll_url:
                start_Activity(RecruitDetailsActivity.this, WebViewActivity.class, new BasicNameValuePair("url", url), new BasicNameValuePair("title", tvCompanyName.getText().toString()));
                break;
            case R.id.rl_user:
                followDialog.show();
                break;
            case R.id.ll_location:
                start_Activity(this, MapviewActivity.class, new BasicNameValuePair("address", enterprise.getEnterprise_address()), new BasicNameValuePair("lng", enterprise.getLng())
                        , new BasicNameValuePair("lat", enterprise.getLat()), new BasicNameValuePair("name", enterprise.getEnterprise_name()));
                break;
            default:
                break;
        }
    }

}
