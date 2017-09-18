package com.appjumper.silkscreen.ui.home.workshop;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.Avatar;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.bean.EquipmentDetailsResponse;
import com.appjumper.silkscreen.bean.EquipmentList;
import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.home.CompanyDetailsActivity;
import com.appjumper.silkscreen.ui.home.adapter.GalleryAdapter;
import com.appjumper.silkscreen.ui.home.adapter.WorkshopListViewAdapter;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.PicassoRoundTransform;
import com.appjumper.silkscreen.view.MyLinearLayoutManger;
import com.appjumper.silkscreen.view.MyListView;
import com.appjumper.silkscreen.view.MyRecyclerView;
import com.appjumper.silkscreen.view.ObservableScrollView;
import com.appjumper.silkscreen.view.SureOrCancelDialog;
import com.appjumper.silkscreen.view.phonegridview.GalleryActivity;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshBase;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshScrollView;
import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016-11-18.
 * 设备详情
 */
public class WorkshopDetailsActivity extends BaseActivity {
    @Bind(R.id.pull_refresh_scrollview)
    PullToRefreshScrollView mPullRefreshScrollView;

    @Bind(R.id.list_view_down)
    MyListView listView;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_remark)
    TextView tvRemark;
    @Bind(R.id.tv_date)
    TextView tvDate;
    @Bind(R.id.tv_form)//形式
            TextView tvForm;
    @Bind(R.id.tv_area)//面积
            TextView tvArea;
    @Bind(R.id.tv_detail_address)//详细地址
            TextView tvDetailAddress;
    @Bind(R.id.tv_position)//地段
            TextView tvPosition;
    @Bind(R.id.tv_price)
    TextView tvPrice;
    @Bind(R.id.recycler_view)
    MyRecyclerView myRecyclerView;

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
    LinearLayout rlUser;
    @Bind(R.id.iv_img)//个人头像
            ImageView iv_img;

    @Bind(R.id.tv_name)//个人姓名
            TextView tv_name;

    @Bind(R.id.tv_mobile)//个人手机号
            TextView tv_mobile;

    @Bind(R.id.txtMark)
    TextView txtMark;
    @Bind(R.id.llRecommend)
    LinearLayout llRecommend;



    private String id;
    private String eid;//企业id
    private String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrokshop_details);
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
        if (!TextUtils.isEmpty(data.getWorkshop_type())) {
            int infoType = Integer.valueOf(data.getWorkshop_type());
            switch (infoType) {
                case Const.INFO_TYPE_PER:
                    txtMark.setText("个人");
                    txtMark.setBackgroundResource(R.drawable.shape_mark_person_bg);
                    break;
                case Const.INFO_TYPE_COM:
                    txtMark.setText("企业");
                    txtMark.setBackgroundResource(R.drawable.shape_mark_enterprise_bg);
                    break;
                case Const.INFO_TYPE_OFFICIAL:
                    txtMark.setText("官方");
                    txtMark.setBackgroundResource(R.drawable.shape_mark_official_bg);
                    break;
                default:
                    break;
            }
        }

        tvTitle.setText(data.getTitle() + "/" + data.getPosition());
        tvRemark.setText(data.getRemark());
        tvDate.setText(data.getCreate_time().substring(5, 16));
        tvForm.setText(data.getLease_mode());
        tvArea.setText(data.getArea() + "m²");
        tvPosition.setText(data.getPosition());
        tvPrice.setText(data.getPrice() + "元/年");

        tvDetailAddress.setText(data.getAddress());
        listView.setAdapter(new WorkshopListViewAdapter(this, data.getRecommend()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                start_Activity(WorkshopDetailsActivity.this,WorkshopDetailsActivity.class,new BasicNameValuePair("id",data.getRecommend().get(position).getId()));
            }
        });
        MyLinearLayoutManger linearLayoutManager = new MyLinearLayoutManger(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        myRecyclerView.setLayoutManager(linearLayoutManager);
        if (data.getImg_list() != null && data.getImg_list().size() > 0) {
            GalleryAdapter adapter = new GalleryAdapter(this, data.getImg_list());
            myRecyclerView.setAdapter(adapter);
            adapter.setOnItemClickLitener(new GalleryAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(WorkshopDetailsActivity.this, GalleryActivity.class);
                    ArrayList<String> urls = new ArrayList<String>();
                    for (Avatar string : data.getImg_list()) {
                        urls.add(string.getOrigin());
                    }
                    intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
                    intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, position);
                    startActivity(intent);
                }
            });
        }
        if (data.getEnterprise() != null) {
            Enterprise enterprise = data.getEnterprise();
            eid = data.getEnterprise().getEnterprise_id();
            mobile = enterprise.getEnterprise_tel();
            rlCompany.setVisibility(View.VISIBLE);
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
                mobile = user.getMobile();
                tv_mobile.setText(user.getMobile());
                if (user.getAvatar() != null && !TextUtils.isEmpty(user.getAvatar().getSmall())) {
                    Picasso.with(this).load(user.getAvatar().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.img_error_head).error(R.mipmap.img_error_head).into(iv_img);
                }

                if (user.getAuth_status() != null && user.getAuth_status().equals("2")) {
                    user_auth_status.setVisibility(View.VISIBLE);
                } else {
                    user_auth_status.setVisibility(View.GONE);
                }
            }
        }


        if (!TextUtils.isEmpty(data.getWorkshop_type())) {
            int infoType = Integer.valueOf(data.getWorkshop_type());
            if (infoType == Const.INFO_TYPE_OFFICIAL) {
                rlCompany.setVisibility(View.GONE);
                rlUser.setVisibility(View.GONE);
                mobile = data.getOfficial_mobile();
            }
        }


        List<EquipmentList> recommendList = data.getRecommend();
        if (recommendList.size() == 0)
            llRecommend.setVisibility(View.GONE);
        else
            llRecommend.setVisibility(View.VISIBLE);

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
                        HttpUtil.getData(data), Url.WORKSHOP_DETAILS));
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
            WorkshopDetailsActivity activity = (WorkshopDetailsActivity) reference.get();
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
                start_Activity(WorkshopDetailsActivity.this, CompanyDetailsActivity.class, new BasicNameValuePair("from", "2"), new BasicNameValuePair("id", eid));
                break;
            case R.id.rl_user:
                followDialog.show();
                break;
            default:
                break;
        }
    }
}
