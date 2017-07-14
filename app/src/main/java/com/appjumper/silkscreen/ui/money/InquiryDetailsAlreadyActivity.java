package com.appjumper.silkscreen.ui.money;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.Avatar;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.bean.MyInquiry;
import com.appjumper.silkscreen.bean.MyInquiryDetailsResponse;
import com.appjumper.silkscreen.bean.Spec;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.home.CompanyDetailsActivity;
import com.appjumper.silkscreen.ui.home.adapter.GalleryAdapter;
import com.appjumper.silkscreen.ui.money.adapter.InquiryListViewAdapter;
import com.appjumper.silkscreen.ui.my.adapter.ViewOrderListViewAdapter;
import com.appjumper.silkscreen.view.MyListView;
import com.appjumper.silkscreen.view.MyRecyclerView;
import com.appjumper.silkscreen.view.ObservableScrollView;
import com.appjumper.silkscreen.view.SureOrCancelDialog;
import com.appjumper.silkscreen.view.phonegridview.GalleryActivity;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshBase;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshScrollView;

import org.apache.http.message.BasicNameValuePair;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016-11-18.
 * 询价详情已报价
 */
public class InquiryDetailsAlreadyActivity extends BaseActivity {
    @Bind(R.id.pull_refresh_scrollview)
    PullToRefreshScrollView mPullRefreshScrollView;
    @Bind(R.id.lv_offer)
    MyListView lvOffer;
    @Bind(R.id.layout)
    LinearLayout layout;
    @Bind(R.id.tv_remain)
    TextView tvRemain;
    @Bind(R.id.tv_offer_amount)//剩余时间
            TextView tvOfferAmount;
    @Bind(R.id.list_view)//产品信息
            MyListView listView;
    @Bind(R.id.recycler_view)//图纸信息
            MyRecyclerView myRecyclerView;
    @Bind(R.id.tv_number)//采购数量
            TextView tvNumber;
    @Bind(R.id.tv_date)//供货日期
            TextView tvDate;
    @Bind(R.id.tv_offer_type)//报价类型
            TextView tvOfferType;
    @Bind(R.id.tv_address)//收货地址
            TextView tvAddress;
    @Bind(R.id.tv_remark)//备注
            TextView tvRemark;
    @Bind(R.id.tv_contact)//联系人
            TextView tvContact;
    @Bind(R.id.tv_mobile)//手机号
            TextView tvMobile;
    @Bind(R.id.tv_offer_avg)//平均报价
            TextView tvOfferAvg;
    @Bind(R.id.tv_offer_number)//报价企业数量
            TextView tvOfferNumber;
    @Bind(R.id.ll_plans_info)//图纸信息外部布局
            LinearLayout llPlansInfo;
    @Bind(R.id.ll_remark)//备注外部布局
            LinearLayout llRemark;
    @Bind(R.id.tv_cancel)//取消询价
            TextView tvCancel;

    private String id;
    private static final int TIMER = 0x10;
    private String expiry_date;
    private String inquiry_id;
    private String count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_already);
        initBack();
        ButterKnife.bind(this);
        listerrefresh();
        id = getIntent().getStringExtra("id");
        initTitle(getIntent().getStringExtra("title") + "询价");
        initProgressDialog();
        progress.show();
        progress.setMessage("正在加载...");
        refresh();
        mPullRefreshScrollView.scrollTo(0, 0);
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
        private MyInquiryDetailsResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("id", id);
                data.put("uid", getUserID());
                response = JsonParser.getMyInquiryDetailsResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.INQUIRY_DETAILS));
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
    private Runnable submitRun = new Runnable() {
        private BaseResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("uid", getUserID());
                data.put("id", inquiry_id);
                response = JsonParser.getBaseResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.CANCEL));
            } catch (Exception e) {
                progress.dismiss();
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
            InquiryDetailsAlreadyActivity activity = (InquiryDetailsAlreadyActivity) reference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT://详情
                    MyInquiryDetailsResponse baseResponse = (MyInquiryDetailsResponse) msg.obj;
                    if (baseResponse.isSuccess()) {
                        layout.setVisibility(View.VISIBLE);
                        MyInquiry data = baseResponse.getData();
                        expiry_date = data.getExpiry_date();
                        initView(data);
                    } else {
                        showErrorToast(baseResponse.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_DATA_RIGHT://取消询价
                    BaseResponse base = (BaseResponse) msg.obj;
                    if (base.isSuccess()) {
                        showErrorToast("取消成功");
                        //refresh();
                        finish();
                    } else {
                        showErrorToast(base.getError_desc());
                    }
                    break;
                case TIMER://倒计时
                    updateTextView(HttpUtil.getTimeLong(expiry_date));
                    handler.sendEmptyMessageDelayed(TIMER, 1000);
                    break;
                case NETWORK_FAIL:
                    activity.showErrorToast();
                    break;
            }
        }
    }

    @OnClick({R.id.rl_more, R.id.tv_cancel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_more:
                start_Activity(this,OfferMoreActivity.class,new BasicNameValuePair("count",count),new BasicNameValuePair("id",id));
                break;
            case R.id.tv_cancel:
                SureOrCancelDialog followDialog = new SureOrCancelDialog(InquiryDetailsAlreadyActivity.this, "是否取消", new SureOrCancelDialog.SureButtonClick() {
                    @Override
                    public void onSureButtonClick() {
                        initProgressDialog();
                        progress.show();
                        progress.setMessage("正在取消...");
                        new Thread(submitRun).start();
                    }
                });
                followDialog.show();
                break;
            default:
                break;
        }
    }

    public void updateTextView(long times) {
        long timeMillis = System.currentTimeMillis();
        long times_remain = times - timeMillis;
        if (times_remain <= 0) {
            tvRemain.setVisibility(View.GONE);
            tvOfferAmount.setText("已截止");
            tvOfferAmount.setTextColor(getResources().getColor(R.color.red_color));
            return;
        }
        times_remain = times_remain / 1000;
        //秒钟
        long time_second = times_remain % 60;
        String str_second;
        if (time_second < 10) {
            str_second = "0" + time_second;
        } else {
            str_second = "" + time_second;
        }

        //分钟
        long time_minute = (times_remain % 3600) / 60;
        String str_minute;
        if (time_minute < 10) {
            str_minute = "0" + time_minute;
        } else {
            str_minute = "" + time_minute;
        }

        //小时
        long time_hour = times_remain / 3600;
        String str_hour;
        if (time_hour < 10) {
            str_hour = "0" + time_hour;
        } else {
            str_hour = "" + time_hour;
        }
        if (time_hour < 1) {
            tvOfferAmount.setText(str_minute + ":" + str_second);
        } else {
            tvOfferAmount.setText(str_hour + ":" + str_minute + ":" + str_second);
        }

    }

    /**
     * 隐藏Button
     */
    private void hideBottom() {
        RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) mPullRefreshScrollView.getLayoutParams();
        params.setMargins(0, 0, 0, dip(this, 0));
        mPullRefreshScrollView.setLayoutParams(params);
        tvCancel.setVisibility(View.GONE);
    }

    /**
     * 显示Button
     */
    private void showBottom() {
        RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) mPullRefreshScrollView.getLayoutParams();
        params.setMargins(0, 0, 0, dip(this, 46));
        mPullRefreshScrollView.setLayoutParams(params);
        tvCancel.setVisibility(View.VISIBLE);
    }

    private void initView(final MyInquiry data) {
        if (data.getStatus().equals("0")) {
            tvRemain.setVisibility(View.GONE);
            tvOfferAmount.setText("已取消");
            tvOfferAmount.setTextColor(getResources().getColor(R.color.red_color));
            hideBottom();
        } else {
            if (HttpUtil.getTimeLong(data.getExpiry_date()) <= System.currentTimeMillis()) {
                tvRemain.setVisibility(View.GONE);
                tvOfferAmount.setText("已截止");
                tvOfferAmount.setTextColor(getResources().getColor(R.color.red_color));
                hideBottom();
            } else {
                showBottom();
                handler.sendEmptyMessageAtTime(TIMER, 1000);
            }

        }
        inquiry_id = data.getId();
        tvOfferAvg.setText("￥" + data.getOffer_avg());
        count =data.getOffer_num();
        tvOfferNumber.setText(data.getOffer_num() + "家");
        if (data.getOffer_list() != null && data.getOffer_list().size() > 0) {
            lvOffer.setAdapter(new InquiryListViewAdapter(this, data.getOffer_list()));
            lvOffer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    start_Activity(InquiryDetailsAlreadyActivity.this, CompanyDetailsActivity.class, new BasicNameValuePair("from", "2"), new BasicNameValuePair("id", data.getOffer_list().get(i).getUser_id()));
                }
            });
        }


        //过滤空字段
        List<Spec> list = data.getSpec();
        Iterator<Spec> it = list.iterator();
        while (it.hasNext()) {
            Spec spec = it.next();
            if (TextUtils.isEmpty(spec.getValue().trim()))
                it.remove();
        }
        initSpecification(list);
        //initSpecification(data.getSpec());


        if (data.getImg_list().size() > 0) {
            initViewRecyclerView(data.getImg_list());
        } else {
            llPlansInfo.setVisibility(View.GONE);
        }
        tvNumber.setText(data.getNum() + "吨");
        tvDate.setText(data.getTime().substring(0, 10));

        ArrayList<String> typeList = new ArrayList<>();
        if (data.getFreight().equals("1")) {
            typeList.add("含运费");
        }
        if (data.getTax().equals("1")) {
            typeList.add("含税");
        }
        if (typeList.size() > 0) {
            String offerType = "";
            for (int i = 0; i < typeList.size(); i++) {
                offerType += "、" + typeList.get(i);
            }
            offerType = offerType.substring(1);
            tvOfferType.setText(offerType);
        }
        tvAddress.setText(data.getDistrict() + data.getAddress());
        if (data.getRemark() != null && !data.getRemark().equals("") && data.getRemark().length() > 0) {
            tvRemark.setText(data.getRemark());
        } else {
            llRemark.setVisibility(View.GONE);
        }
        tvContact.setText(data.getContacts());
        tvMobile.setText(data.getMobile());
    }

    /**
     * @param service_spec
     */
    private void initSpecification(List<Spec> service_spec) {
        /*final ArrayList<HashMap<String, Object>> mList = new ArrayList<>();
        List<String> item3 = new ArrayList<>();
        List<String> item1 = new ArrayList<>();
        for (int i = 0; i < service_spec.size(); i++) {
            item1.add(service_spec.get(i).getName());
            if (service_spec.get(i).getFieldinput().equals("radio")) {
                item3.add(service_spec.get(i).getValue());
            } else {
                item3.add(service_spec.get(i).getValue() + service_spec.get(i).getUnit());
            }
        }
        List<String> types = new ArrayList<>();
        types.add("规格");
        List<List<String>> items = new ArrayList<>();
        items.add(item1);
        List<List<String>> values = new ArrayList<>();
        values.add(item3);
        for (int j = 0; j < types.size(); j++) {
            HashMap<String, Object> selectProMap = new HashMap<>();
            selectProMap.put("type", types.get(j));
            selectProMap.put("label", items.get(j));
            selectProMap.put("value", values.get(j));
            mList.add(selectProMap);
        }
        InquirySpecificationAdapter adapter = new InquirySpecificationAdapter(this, mList);
        listView.setAdapter(adapter);*/

        listView.setDividerHeight(0);
        listView.setAdapter(new ViewOrderListViewAdapter(this, service_spec));
    }

    /**
     * @param img_list
     */
    private void initViewRecyclerView(final List<Avatar> img_list) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        myRecyclerView.setLayoutManager(linearLayoutManager);
        GalleryAdapter adapter = new GalleryAdapter(this, img_list);
        myRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickLitener(new GalleryAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(InquiryDetailsAlreadyActivity.this, GalleryActivity.class);
                ArrayList<String> urls = new ArrayList<>();
                for (Avatar string : img_list) {
                    urls.add(string.getOrigin());
                }
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, position);
                startActivity(intent);
            }
        });
    }
}
