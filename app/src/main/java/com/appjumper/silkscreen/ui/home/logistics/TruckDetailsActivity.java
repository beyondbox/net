package com.appjumper.silkscreen.ui.home.logistics;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.bean.LineDetails;
import com.appjumper.silkscreen.bean.LineDetailsResponse;
import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.home.CompanyDetailsActivity;
import com.appjumper.silkscreen.ui.home.adapter.TruckListviewAdapter;
import com.appjumper.silkscreen.util.CircleTransform;
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
 * 找车详情
 */
public class TruckDetailsActivity extends BaseActivity {


    @Bind(R.id.pull_refresh_scrollview)
    PullToRefreshScrollView mPullRefreshScrollView;
    @Bind(R.id.list_view)//相关推荐
            MyListView listView;
    @Bind(R.id.tv_start)//起点
            TextView tv_start;

    @Bind(R.id.tv_end)//终点
            TextView tv_end;
    @Bind(R.id.tv_passby)//距离信息
            TextView tv_passby;
    @Bind(R.id.tv_date)//装货时间
            TextView tvDate;
    @Bind(R.id.tv_car_model)//车型
            TextView tvCarModel;
    @Bind(R.id.tv_number)//数量
            TextView tvNumber;
    @Bind(R.id.tv_weight)//载重
            TextView tvWeigth;
    @Bind(R.id.tv_goods_name)//货物名称
            TextView tvGoodsName;
    @Bind(R.id.tv_remark)//备注
            TextView tvRemark;
    @Bind(R.id.rl_company)
    RelativeLayout rlCompany;
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
    private String eid;//企业id
    private String mobile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_details);
        ButterKnife.bind(this);
        initBack();

        listerrefresh();
        id = getIntent().getStringExtra("id");
        initProgressDialog();
        progress.show();
        progress.setMessage("正在加载...");
        refresh();

        mPullRefreshScrollView.scrollTo(0, 0);
        listView.setFocusable(false);
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

    //初始化信息
    private void initView(final LineDetails data) {
        tv_start.setText(data.getFrom());
        tv_end.setText(data.getTo());
        tv_passby.setText("距离起点" + data.getFrom_distance() + "km，运货里程" + data.getDistance() + "km");
        tvCarModel.setText(data.getCar_model());
        tvWeigth.setText(data.getWeight()+"KG");
        tvRemark.setText(data.getRemark());
        tvGoodsName.setText(data.getProduct_name());
        tvNumber.setText(data.getNumber() + data.getProductspec());
        Enterprise enterprise = data.getEnterprise();
        if (enterprise != null) {
            eid = data.getEnterprise().getEnterprise_id();
            mobile = enterprise.getEnterprise_tel();
            rlCompany.setVisibility(View.VISIBLE);
            Picasso.with(this).load(enterprise.getEnterprise_logo().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.img_error).error(R.mipmap.img_error).into(ivLogo);
            tvCompanyName.setText(enterprise.getEnterprise_name());
            tv_address.setText(enterprise.getEnterprise_address());
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
            mobile = user.getMobile();
            rlCompany.setVisibility(View.GONE);
            rlUser.setVisibility(View.VISIBLE);
            if(user!=null){
                tv_name.setText(user.getUser_nicename());
                tv_mobile.setText(user.getMobile());
                if(user.getAvatar() != null){
                    Picasso.with(this).load(user.getAvatar().getSmall()).transform(new CircleTransform()).placeholder(R.mipmap.img_error_head).error(R.mipmap.img_error_head).into(iv_img);
                }

                if (user.getAuth_status() != null && user.getAuth_status().equals("2")) {
                    user_auth_status.setVisibility(View.VISIBLE);
                } else {
                    user_auth_status.setVisibility(View.GONE);
                }
            }
        }
        TruckListviewAdapter adapter = new TruckListviewAdapter(this, data.getRecommend());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                start_Activity(context, TruckDetailsActivity.class, new BasicNameValuePair("id", data.getRecommend().get(position).getId()));
            }
        });
    }

    //详情
    private Runnable detailsRun = new Runnable() {
        private LineDetailsResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("id", id);
                data.put("uid", getUserID());
                response = JsonParser.getLineDetailsResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.TRUCK_DETAILS));
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
            TruckDetailsActivity activity = (TruckDetailsActivity) reference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT://详情
                    LineDetailsResponse baseResponse = (LineDetailsResponse) msg.obj;
                    if (baseResponse.isSuccess()) {
                        LineDetails data = baseResponse.getData();
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

    @OnClick({R.id.tv_contact, R.id.rl_company, R.id.rl_user})
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
                start_Activity(TruckDetailsActivity.this, CompanyDetailsActivity.class, new BasicNameValuePair("from", "2"), new BasicNameValuePair("id", eid));
                break;
            case R.id.rl_user:
                followDialog.show();
                break;
            default:
                break;
        }
    }

}
