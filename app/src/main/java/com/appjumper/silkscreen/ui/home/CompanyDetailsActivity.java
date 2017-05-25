package com.appjumper.silkscreen.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.Avatar;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.bean.EnterpriseDetailsResponse;
import com.appjumper.silkscreen.bean.LineList;
import com.appjumper.silkscreen.bean.NewPublic;
import com.appjumper.silkscreen.bean.Product;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.common.MapviewActivity;
import com.appjumper.silkscreen.ui.common.WebViewActivity;
import com.appjumper.silkscreen.ui.home.adapter.CompanyProcessListViewAdapter;
import com.appjumper.silkscreen.ui.home.adapter.GalleryAdapter;
import com.appjumper.silkscreen.ui.home.adapter.HomeListview2Adapter;
import com.appjumper.silkscreen.ui.home.equipment.EquipmentDetailsActivity;
import com.appjumper.silkscreen.ui.home.logistics.LogisticsDetailsActivity;
import com.appjumper.silkscreen.ui.home.logistics.TruckDetailsActivity;
import com.appjumper.silkscreen.ui.home.process.ProcessingDetailsActivity;
import com.appjumper.silkscreen.ui.home.recruit.RecruitDetailsActivity;
import com.appjumper.silkscreen.ui.home.workshop.WorkshopDetailsActivity;
import com.appjumper.silkscreen.ui.my.adapter.MyLogisticsListviewAdapter;
import com.appjumper.silkscreen.ui.my.enterprise.EnterpriseCreateActivity;
import com.appjumper.silkscreen.ui.my.enterprise.MyLogisticsDetailsActivity;
import com.appjumper.silkscreen.ui.my.enterprise.ViewOrderActivity;
import com.appjumper.silkscreen.util.CircleTransform;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.util.ShareUtil;
import com.appjumper.silkscreen.view.MyListView;
import com.appjumper.silkscreen.view.MyRecyclerView;
import com.appjumper.silkscreen.view.ObservableScrollView;
import com.appjumper.silkscreen.view.SureOrCancelDialog;
import com.appjumper.silkscreen.view.phonegridview.GalleryActivity;
import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016-11-15.
 * 企业详情
 */
public class CompanyDetailsActivity extends BaseActivity implements ObservableScrollView.ScrollViewListener {
    @Bind(R.id.title_layout)
    LinearLayout title_layout;

    @Bind(R.id.scrollView)
    ObservableScrollView scrollView;
    @Bind(R.id.layout)
    LinearLayout layout;

    private int height;

    @Bind(R.id.recycler_view)
    MyRecyclerView myRecyclerView;

    @Bind(R.id.rg_tab)
    RadioGroup rgTab;

    @Bind(R.id.list_view)//公司服务
            MyListView listView;

    @Bind(R.id.tv_url)//公司网址
            TextView tvUrl;


    @Bind(R.id.tv_phone)//电话号码
            TextView tvPhone;

    @Bind(R.id.lv_release_history)//发布历史
            MyListView lvReleaseHistory;

    @Bind(R.id.iv_collect)//收藏
            ImageView ivCollect;

    @Bind(R.id.iv_share)//分享
            ImageView ivShare;

    @Bind(R.id.tv_edit)//编辑
            TextView tvEdit;

    @Bind(R.id.iv_enterprise_logo)//公司logo
            ImageView iv_enterprise_logo;

    @Bind(R.id.tv_company_name_details)//公司名称
            TextView tv_company_name;

    @Bind(R.id.tv_auth_status) //个人认证
    ImageView tv_auth_status;

    @Bind(R.id.tv_enterprise_auth_status)//企
            ImageView tv_enterprise_auth_status;

    @Bind(R.id.tv_enterprise_productivity_auth_status)//力
            ImageView tv_enterprise_productivity_auth_status;

    @Bind(R.id.tv_enterprise_reg_date)//注册时间
            TextView tv_enterprise_reg_date;

    @Bind(R.id.tv_enterprise_reg_money)//注册资金
            TextView tv_enterprise_reg_money;

    @Bind(R.id.tv_enterprise_area)//厂房面积
            TextView tv_enterprise_area;

    @Bind(R.id.tv_enterprise_staff_num)//员工人数
            TextView tv_enterprise_staff_num;

    @Bind(R.id.tv_enterprise_machine_num)//机器数量
            TextView tv_enterprise_machine_num;

    @Bind(R.id.tv_enterprise_capacity_num)//机器数量
            TextView tv_enterprise_capacity_num;

    @Bind(R.id.tv_enterprise_intro)//企业简介
            TextView tv_enterprise_intro;

    @Bind(R.id.tv_enterprise_contacts)//联系人
            TextView tv_enterprise_contacts;

    @Bind(R.id.tv_enterprise_qq)//QQ
            TextView tv_enterprise_qq;

    @Bind(R.id.tv_enterprise_address)//地址
            TextView tv_enterprise_address;

    private String from;//from 1代表从我的见面跳转，2从其他界面跳转
    private String id;
    private String uid = "";
    private Enterprise data;
    private String enterprise_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_details);
        ButterKnife.bind(this);
        initBack();
        height = dip(this, 120);
        scrollView.setScrollViewListener(this);

        from = getIntent().getStringExtra("from");
        id = getIntent().getStringExtra("id");
        if (from.equals("1")) {
            ivCollect.setVisibility(View.GONE);
            ivShare.setVisibility(View.GONE);
            tvEdit.setVisibility(View.VISIBLE);
        } else if (from.equals("2")) {
            ivCollect.setVisibility(View.VISIBLE);
            ivShare.setVisibility(View.VISIBLE);
            tvEdit.setVisibility(View.GONE);
            if (getUser() != null) {
                uid = getUserID();
            }
        }

        initProgressDialog();
        progress.show();
        progress.setMessage("正在加载...");
        new Thread(new CompanyDetailRun()).start();
    }

    private void initView(Enterprise enterprise) {
        if (getUser() != null) {
            if (getUser().getEnterprise() != null) {
                if (getUser().getEnterprise().getEnterprise_id().equals(enterprise.getEnterprise_id())) {
                    ivCollect.setVisibility(View.GONE);
                    ivShare.setVisibility(View.GONE);
                    tvEdit.setVisibility(View.VISIBLE);
                } else {
                    if (enterprise.getCollection().equals("1")) {
                        ivCollect.setImageResource(R.mipmap.icon_collect_selected);
                    } else {
                        ivCollect.setImageResource(R.mipmap.icon_collect);
                    }
                }

            } else {
                if (enterprise.getCollection().equals("1")) {
                    ivCollect.setImageResource(R.mipmap.icon_collect_selected);
                } else {
                    ivCollect.setImageResource(R.mipmap.icon_collect);
                }
            }

        } else {
            ivCollect.setImageResource(R.mipmap.icon_collect);
        }
        data = enterprise;
        enterprise_id = enterprise.getEnterprise_id();
        tv_company_name.setText(enterprise.getEnterprise_name());
        tv_enterprise_reg_date.setText(enterprise.getEnterprise_reg_date());
        tv_enterprise_reg_money.setText(enterprise.getEnterprise_reg_money()+"万元");
        tv_enterprise_area.setText(enterprise.getEnterprise_area()+"m²");
        tv_enterprise_staff_num.setText(enterprise.getEnterprise_staff_num()+"人");
        tv_enterprise_machine_num.setText(enterprise.getEnterprise_machine_num());
        tv_enterprise_capacity_num.setText(enterprise.getEnterprise_capacity_num());
        tv_enterprise_intro.setText(enterprise.getEnterprise_intro());

        tv_enterprise_contacts.setText(enterprise.getEnterprise_contacts());
        tv_enterprise_qq.setText(enterprise.getEnterprise_qq());
        tvUrl.setText(enterprise.getEnterprise_website());
        tvPhone.setText(enterprise.getEnterprise_tel());
        tv_enterprise_address.setText(enterprise.getEnterprise_address());

        if (enterprise.getEnterprise_logo() != null && !enterprise.getEnterprise_logo().getSmall().equals("")) {
            Picasso.with(this).load(enterprise.getEnterprise_logo().getSmall()).placeholder(R.mipmap.icon_logo_image61).error(R.mipmap.icon_logo_image61).transform(new CircleTransform()).into(iv_enterprise_logo);
        }

        /*List<String> list = enterprise.getUser_auth_status();
        if (list != null && list.size() > 0) {
            if (list.get(0).equals("2"))
                tv_auth_status.setVisibility(View.VISIBLE);
            else
                tv_auth_status.setVisibility(View.GONE);
        } else {
            tv_auth_status.setVisibility(View.GONE);
        }*/

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
        initViewRecyclerView(enterprise.getImg_list());
    }

    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (y <= 0) {
            title_layout.setBackgroundColor(Color.argb((int) 0, 66, 133, 245));
        } else if (y > 0 && y <= height) {
            float scale = (float) y / height;
            float alpha = (255 * scale);
            title_layout.setBackgroundColor(Color.argb((int) alpha, 66, 133, 245));
        } else {
            title_layout.setBackgroundColor(Color.argb((int) 255, 66, 133, 245));
        }
    }


    //企业详情
    private class CompanyDetailRun implements Runnable {
        private EnterpriseDetailsResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("id", id);
                data.put("uid", uid);
                response = JsonParser.getEnterpriseDetailsResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.ENTERPRISEDETAILS));
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

    //企业收藏
    private Runnable run = new Runnable() {

        public void run() {
            BaseResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("uid", getUserID());
                data.put("enterprise_id", enterprise_id);
                response = JsonParser.getBaseResponse(HttpUtil.postMsg(HttpUtil.getData(data), Url.ADD_COLLECTION));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        NETWORK_SUCCESS_DATA_RIGHT, response));
            } else {
                handler.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    };
    private MyHandler handler = new MyHandler();

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            progress.dismiss();
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT://企业详情
                    EnterpriseDetailsResponse detailsResponse = (EnterpriseDetailsResponse) msg.obj;
                    if (detailsResponse.isSuccess()) {
                        initView(detailsResponse.getData());
                        initAdapter();
                        initReleaseHistory();
                    } else {
                        showErrorToast(detailsResponse.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_DATA_RIGHT:
                    BaseResponse base = (BaseResponse) msg.obj;
                    if (base.isSuccess()) {
                        ivCollect.setImageResource(R.mipmap.icon_collect_selected);
                        showSuccessTips("收藏成功");
                    } else {
                        showErrorToast(base.getError_desc());
                    }
                    break;
                case NETWORK_FAIL:
                    showErrorToast();
                    break;
                default:
                    showErrorToast();
                    break;
            }
        }
    }

    ;


    /**
     * 画廊
     *
     * @param img_list
     */
    private void initViewRecyclerView(final List<Avatar> img_list) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        myRecyclerView.setLayoutManager(linearLayoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.view_padding);
        int margin = getResources().getDimensionPixelSize(R.dimen.header_footer_top_bottom_padding);
        myRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels, margin, margin, img_list));
        GalleryAdapter adapter = new GalleryAdapter(this, img_list);
        myRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickLitener(new GalleryAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(CompanyDetailsActivity.this, GalleryActivity.class);
                ArrayList<String> urls = new ArrayList<String>();
                for (Avatar string : img_list) {
                    urls.add(string.getOrigin());
                }
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, position);
                startActivity(intent);
            }
        });
    }

    /**
     * 设置外间距
     */
    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private final List<Avatar> img_list;
        private int space;
        private int left;
        private int right;

        public SpacesItemDecoration(int space, int left, int right, List<Avatar> img_list) {
            this.space = space;
            this.left = left;
            this.right = right;
            this.img_list = img_list;

        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildPosition(view) == 0) {
                outRect.left = left;
                outRect.right = space;
            } else if (parent.getChildPosition(view) == (img_list.size() - 1)) {
                outRect.right = right;
            } else {
                outRect.right = space;
            }
        }
    }

    /**
     * 切换adapter
     */
    private void initAdapter() {
        final List<Product> list = data.getService_jiagong();
        CompanyProcessListViewAdapter adapter = new CompanyProcessListViewAdapter(CompanyDetailsActivity.this, list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                start_Activity(CompanyDetailsActivity.this, ViewOrderActivity.class, new BasicNameValuePair("title", list.get(i).getProduct_name() + list.get(i).getService_type_name()), new BasicNameValuePair("id", list.get(i).getId()));
            }
        });
        listView.setAdapter(adapter);
        rgTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                BaseAdapter adapter = null;
                switch (checkedId) {
                    case R.id.rd_process://加工
                        final List<Product> list = data.getService_jiagong();
                        adapter = new CompanyProcessListViewAdapter(CompanyDetailsActivity.this, list);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                if (getUser() != null) {
                                    if (getUser().getEnterprise() != null) {
                                        if (data.getEnterprise_id().equals(getUser().getEnterprise().getEnterprise_id())) {
                                            start_Activity(CompanyDetailsActivity.this, ViewOrderActivity.class, new BasicNameValuePair("title", list.get(i).getProduct_name() + list.get(i).getService_type_name()), new BasicNameValuePair("id", list.get(i).getId()));
                                        } else {
                                            start_Activity(CompanyDetailsActivity.this, ProcessingDetailsActivity.class, new BasicNameValuePair("title", list.get(i).getProduct_name() + list.get(i).getService_type_name()), new BasicNameValuePair("id", list.get(i).getId()));
                                        }
                                    } else {
                                        start_Activity(CompanyDetailsActivity.this, ProcessingDetailsActivity.class, new BasicNameValuePair("title", list.get(i).getProduct_name() + list.get(i).getService_type_name()), new BasicNameValuePair("id", list.get(i).getId()));
                                    }

                                } else {
                                    start_Activity(CompanyDetailsActivity.this, ProcessingDetailsActivity.class, new BasicNameValuePair("title", list.get(i).getProduct_name() + list.get(i).getService_type_name()), new BasicNameValuePair("id", list.get(i).getId()));
                                }
                            }
                        });
                        break;
                    case R.id.rd_order://订做
                        final List<Product> list2 = data.getService_dingzuo();
                        adapter = new CompanyProcessListViewAdapter(CompanyDetailsActivity.this, list2);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                if (getUser() != null) {
                                    if (getUser().getEnterprise() != null) {
                                        if (data.getEnterprise_id().equals(getUser().getEnterprise().getEnterprise_id())) {
                                            start_Activity(CompanyDetailsActivity.this, ViewOrderActivity.class, new BasicNameValuePair("title", list2.get(i).getProduct_name() + list2.get(i).getService_type_name()), new BasicNameValuePair("id", list2.get(i).getId()));
                                        } else {
                                            start_Activity(CompanyDetailsActivity.this, ProcessingDetailsActivity.class, new BasicNameValuePair("title", list2.get(i).getProduct_name() + list2.get(i).getService_type_name()), new BasicNameValuePair("id", list2.get(i).getId()));
                                        }
                                    } else {
                                        start_Activity(CompanyDetailsActivity.this, ProcessingDetailsActivity.class, new BasicNameValuePair("title", list2.get(i).getProduct_name() + list2.get(i).getService_type_name()), new BasicNameValuePair("id", list2.get(i).getId()));
                                    }

                                } else {
                                    start_Activity(CompanyDetailsActivity.this, ProcessingDetailsActivity.class, new BasicNameValuePair("title", list2.get(i).getProduct_name() + list2.get(i).getService_type_name()), new BasicNameValuePair("id", list2.get(i).getId()));
                                }
                            }
                        });
                        break;
                    case R.id.rd_stock://现货
                        final List<Product> list3 = data.getService_xianhuo();
                        adapter = new CompanyProcessListViewAdapter(CompanyDetailsActivity.this, list3);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                if (getUser() != null) {
                                    if (getUser().getEnterprise() != null) {
                                        if (data.getEnterprise_id().equals(getUser().getEnterprise().getEnterprise_id())) {
                                            start_Activity(CompanyDetailsActivity.this, ViewOrderActivity.class, new BasicNameValuePair("title", list3.get(i).getProduct_name() + list3.get(i).getService_type_name()), new BasicNameValuePair("id", list3.get(i).getId()));
                                        } else {
                                            start_Activity(CompanyDetailsActivity.this, ProcessingDetailsActivity.class, new BasicNameValuePair("title", list3.get(i).getProduct_name() + list3.get(i).getService_type_name()), new BasicNameValuePair("id", list3.get(i).getId()));
                                        }
                                    } else {
                                        start_Activity(CompanyDetailsActivity.this, ProcessingDetailsActivity.class, new BasicNameValuePair("title", list3.get(i).getProduct_name() + list3.get(i).getService_type_name()), new BasicNameValuePair("id", list3.get(i).getId()));
                                    }

                                } else {
                                    start_Activity(CompanyDetailsActivity.this, ProcessingDetailsActivity.class, new BasicNameValuePair("title", list3.get(i).getProduct_name() + list3.get(i).getService_type_name()), new BasicNameValuePair("id", list3.get(i).getId()));
                                }
                            }
                        });
                        break;
                    case R.id.rd_logistics://物流
                        final List<LineList> list4 = data.getLine();
                        adapter = new MyLogisticsListviewAdapter(CompanyDetailsActivity.this, list4);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                if (getUser() != null) {
                                    if (getUser().getEnterprise() != null) {
                                        if (data.getEnterprise_id().equals(getUser().getEnterprise().getEnterprise_id())) {
                                            start_Activity(CompanyDetailsActivity.this, MyLogisticsDetailsActivity.class, new BasicNameValuePair("id", list4.get(i).getId()));
                                        } else {
                                            start_Activity(CompanyDetailsActivity.this, LogisticsDetailsActivity.class, new BasicNameValuePair("id", list4.get(i).getId()), new BasicNameValuePair("type", "2"));
                                        }
                                    } else {
                                        start_Activity(CompanyDetailsActivity.this, LogisticsDetailsActivity.class, new BasicNameValuePair("id", list4.get(i).getId()), new BasicNameValuePair("type", "2"));
                                    }
                                } else {
                                    start_Activity(CompanyDetailsActivity.this, LogisticsDetailsActivity.class, new BasicNameValuePair("id", list4.get(i).getId()), new BasicNameValuePair("type", "2"));
                                }


                            }
                        });
                        break;
                    default:
                        break;
                }
                listView.setAdapter(adapter);
            }
        });
    }

    @OnClick({R.id.rl_url, R.id.rl_phone, R.id.rl_location, R.id.tv_inquiry, R.id.iv_collect, R.id.tv_edit,R.id.iv_share})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_url://网址
                start_Activity(CompanyDetailsActivity.this, WebViewActivity.class, new BasicNameValuePair("url", tvUrl.getText().toString()), new BasicNameValuePair("title", tv_company_name.getText().toString()));
                break;
            case R.id.rl_phone://电话
                SureOrCancelDialog followDialog = new SureOrCancelDialog(this, "拨打电话？", new SureOrCancelDialog.SureButtonClick() {
                    @Override
                    public void onSureButtonClick() {
                        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tvPhone.getText().toString()));//跳转到拨号界面，同时传递电话号码
                        startActivity(dialIntent);
                    }
                });
                followDialog.show();
                break;
            case R.id.rl_location://地址
                start_Activity(this, MapviewActivity.class, new BasicNameValuePair("address", data.getEnterprise_address()), new BasicNameValuePair("lng", data.getLng())
                        , new BasicNameValuePair("lat", data.getLat()),new BasicNameValuePair("name",data.getEnterprise_name()));
                break;
            case R.id.tv_inquiry://询价
                break;
            case R.id.iv_collect://收藏
                if (getUser() == null) {
                    showErrorToast("请您先登录");
                    return;
                }
                if (data.getCollection().equals("1")) {
                    showErrorToast("您已收藏过该企业");
                    return;
                }
                new Thread(run).start();
                break;
            case R.id.tv_edit://编辑
                Intent intent = new Intent(this, EnterpriseCreateActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("enterprise", data);
                intent.putExtra("type", "1");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.iv_share:
                ShareUtil.intShare2(this, v, data.getEnterprise_intro(), data.getEnterprise_name(), data.getUrl(),data.getEnterprise_logo().getSmall());
                break;
            default:
                break;
        }
    }

    /**
     * 发布历史
     */
    private void initReleaseHistory() {
        final List<NewPublic> newPublic = data.getNewpublic();
        if (newPublic != null && newPublic.size() > 0) {
            lvReleaseHistory.setAdapter(new HomeListview2Adapter(this, newPublic));
            lvReleaseHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    NewPublic item = newPublic.get(i);
                    if (!checkLogined())
                        return;

                    switch (item.getType()) {
                        case "1"://定做
                            start_Activity(CompanyDetailsActivity.this, ProcessingDetailsActivity.class, new BasicNameValuePair("title", item.getTitle() + item.getSubtitle()), new BasicNameValuePair("id", item.getInfo_id()));
                            break;
                        case "2"://加工
                            start_Activity(CompanyDetailsActivity.this, ProcessingDetailsActivity.class, new BasicNameValuePair("title", item.getTitle() + item.getSubtitle()), new BasicNameValuePair("id", item.getInfo_id()));
                            break;
                        case "3"://现货
                            start_Activity(CompanyDetailsActivity.this, ProcessingDetailsActivity.class, new BasicNameValuePair("title", item.getTitle() + item.getSubtitle()), new BasicNameValuePair("id", item.getInfo_id()));
                            break;
                        case "4"://物流
                            start_Activity(CompanyDetailsActivity.this, LogisticsDetailsActivity.class, new BasicNameValuePair("id", item.getInfo_id()), new BasicNameValuePair("type", "1"));
                            break;
                        case "5"://设备
                            start_Activity(CompanyDetailsActivity.this, EquipmentDetailsActivity.class, new BasicNameValuePair("id", item.getInfo_id()));
                            break;
                        case "6"://找车
                            start_Activity(CompanyDetailsActivity.this, TruckDetailsActivity.class, new BasicNameValuePair("title", "详情"), new BasicNameValuePair("id", item.getInfo_id()));
                            break;
                        case "7"://招聘
                            start_Activity(CompanyDetailsActivity.this, RecruitDetailsActivity.class, new BasicNameValuePair("id", item.getInfo_id()));
                            break;
                        case "8"://厂房
                            start_Activity(CompanyDetailsActivity.this, WorkshopDetailsActivity.class, new BasicNameValuePair("id", item.getInfo_id()));
                            break;


                    }
                }
            });
        }

//        ReleaseHistoryListViewAdapter adapter = new ReleaseHistoryListViewAdapter(this);
//        lvReleaseHistory.setAdapter(new HomeListview2Adapter(this, newPublic));

    }
}