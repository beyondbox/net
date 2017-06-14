package com.appjumper.silkscreen.ui.home.equipment;

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
import com.appjumper.silkscreen.bean.EquipmentDetailsResponse;
import com.appjumper.silkscreen.bean.EquipmentList;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.home.CompanyDetailsActivity;
import com.appjumper.silkscreen.ui.home.adapter.EquipmentDetailsListviewAdapter;
import com.appjumper.silkscreen.ui.home.adapter.EquipmentListviewAdapter;
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
 * Created by Administrator on 2016-11-18.
 * 设备详情
 */
public class EquipmentDetailsActivity extends BaseActivity {
    @Bind(R.id.pull_refresh_scrollview)
    PullToRefreshScrollView mPullRefreshScrollView;

    @Bind(R.id.list_view_up)
    MyListView listViewUp;

    @Bind(R.id.list_view_down)
    MyListView listViewDown;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_remark)
    TextView tvRemark;
    @Bind(R.id.tv_date)
    TextView tvDate;

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
        setContentView(R.layout.activity_equipment_details);
        initBack();
        ButterKnife.bind(this);

        listerrefresh();
        id = getIntent().getStringExtra("id");
        initProgressDialog();
        progress.show();
        progress.setMessage("正在加载...");
        refresh();
        mPullRefreshScrollView.scrollTo(0, 0);
    }

    private void initView(final EquipmentList data) {
        mobile = data.getMobile();
        tvTitle.setText(data.getTitle());
        tvRemark.setText(data.getRemark());
        tvDate.setText(data.getCreate_time().substring(5, 16));
        listViewUp.setAdapter(new EquipmentDetailsListviewAdapter(this, data.getItems()));
        listViewDown.setAdapter(new EquipmentListviewAdapter(this, data.getRecommend()));
        listViewDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                start_Activity(EquipmentDetailsActivity.this, EquipmentDetailsActivity.class, new BasicNameValuePair("id", data.getRecommend().get(position).getId()));
            }
        });
        if (data.getEnterprise_id() != null) {
            eid = data.getEnterprise_id();
            rlCompany.setVisibility(View.VISIBLE);
            Picasso.with(this).load(data.getEnterprise_logo().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.icon_logo_image61).error(R.mipmap.icon_logo_image61).into(ivLogo);
            tvCompanyName.setText(data.getEnterprise_name());
            tv_address.setText(data.getEnterprise_address());
            if (data.getAuth_status() != null && data.getAuth_status().equals("2")) {
                tv_auth_status.setVisibility(View.VISIBLE);
            } else {
                tv_auth_status.setVisibility(View.GONE);
            }
            if (data.getEnterprise_auth_status() != null && data.getEnterprise_auth_status().equals("2")) {
                tv_enterprise_auth_status.setVisibility(View.VISIBLE);
            } else {
                tv_enterprise_auth_status.setVisibility(View.GONE);
            }
            if (data.getEnterprise_productivity_auth_status() != null && data.getEnterprise_productivity_auth_status().equals("2")) {
                tv_enterprise_productivity_auth_status.setVisibility(View.VISIBLE);
            } else {
                tv_enterprise_productivity_auth_status.setVisibility(View.GONE);
            }
            rlUser.setVisibility(View.GONE);
        } else {
            rlCompany.setVisibility(View.GONE);
            rlUser.setVisibility(View.VISIBLE);
            tv_name.setText(data.getNicename());
            tv_mobile.setText(data.getMobile());
            if (data.getAvatar() != null && !data.getAvatar().getSmall().equals("")) {
                Picasso.with(this).load(data.getAvatar().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.img_error_head).error(R.mipmap.img_error_head).into(iv_img);
            }

            if (data.getAuth_status() != null && data.getAuth_status().equals("2")) {
                user_auth_status.setVisibility(View.VISIBLE);
            } else {
                user_auth_status.setVisibility(View.GONE);
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
        private EquipmentDetailsResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("id", id);
                data.put("uid", getUserID());
                response = JsonParser.getEquipmentDetailsResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.EQUIPMENT_DETAILS));
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
            EquipmentDetailsActivity activity = (EquipmentDetailsActivity) reference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT://详情
                    EquipmentDetailsResponse baseResponse = (EquipmentDetailsResponse) msg.obj;
                    if (baseResponse.isSuccess()) {
                        EquipmentList data = baseResponse.getData();
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


    @OnClick({R.id.tv_contact, R.id.rl_user, R.id.rl_company})
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
            case R.id.rl_user://个人
                followDialog.show();
                break;
            case R.id.rl_company:
                start_Activity(EquipmentDetailsActivity.this, CompanyDetailsActivity.class, new BasicNameValuePair("from", "2"), new BasicNameValuePair("id", eid));
                break;
            default:
                break;
        }
    }
}
