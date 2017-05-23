package com.appjumper.silkscreen.ui.money;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.Avatar;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.bean.MyInquiry;
import com.appjumper.silkscreen.bean.MyInquiryDetailsResponse;
import com.appjumper.silkscreen.bean.Spec;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.home.adapter.GalleryAdapter;
import com.appjumper.silkscreen.ui.money.adapter.InquirySpecificationAdapter;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.view.MyListView;
import com.appjumper.silkscreen.view.MyRecyclerView;
import com.appjumper.silkscreen.view.ObservableScrollView;
import com.appjumper.silkscreen.view.SureOrCancelEditDialog;
import com.appjumper.silkscreen.view.phonegridview.GalleryActivity;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshBase;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshScrollView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-11-18.
 * 报价详情
 */
public class OfferDetailsActivity extends BaseActivity {
    @Bind(R.id.pull_refresh_scrollview)
    PullToRefreshScrollView mPullRefreshScrollView;
    @Bind(R.id.layout)
    LinearLayout layout;
    @Bind(R.id.tv_remain)
    TextView tvRemain;
    @Bind(R.id.tv_offer)//报价
            TextView tvOffer;
    @Bind(R.id.ll_my_offer)
    LinearLayout myOffer;
    @Bind(R.id.tv_product_name)//产品名字
            TextView tvProductName;
    @Bind(R.id.tv_offer_amount)//报价金额
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
    @Bind(R.id.ll_plans_info)//图纸信息外部布局
            LinearLayout llPlansInfo;
    @Bind(R.id.ll_remark)//备注外部布局
            LinearLayout llRemark;

    private String id;
    private String money;
    private String inquiry_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
        initBack();
        ButterKnife.bind(this);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        listerrefresh();
        initProgressDialog();
        progress.show();
        progress.setMessage("正在加载...");
        refresh();
        mPullRefreshScrollView.scrollTo(0, 0);
    }


    /**
     * 隐藏Button
     */
    private void hideBottom() {
        RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) mPullRefreshScrollView.getLayoutParams();
        params.setMargins(0, 0, 0, dip(this, 0));
        mPullRefreshScrollView.setLayoutParams(params);
        tvOffer.setVisibility(View.GONE);
    }

    /**
     * 显示Button
     */
    private void showBottom() {
        RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) mPullRefreshScrollView.getLayoutParams();
        params.setMargins(0, 0, 0, dip(this, 46));
        mPullRefreshScrollView.setLayoutParams(params);
        tvOffer.setVisibility(View.VISIBLE);
        tvOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SureOrCancelEditDialog followDialog = new SureOrCancelEditDialog(OfferDetailsActivity.this, "报价金额", new SureOrCancelEditDialog.SureButtonClick() {
                    @Override
                    public void onSureButtonClick(String amount) {
                        money = amount;
                        initProgressDialog();
                        progress.show();
                        progress.setMessage("正在提交...");
                        new Thread(submitRun).start();
                    }
                });
                followDialog.show();
            }
        });
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
                data.put("uid", getUserID());
                data.put("id", id);
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
                data.put("money", money);
                data.put("inquiry_id", inquiry_id);
                response = JsonParser.getBaseResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.OFFER));
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
            OfferDetailsActivity activity = (OfferDetailsActivity) reference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT://详情
                    MyInquiryDetailsResponse baseResponse = (MyInquiryDetailsResponse) msg.obj;
                    if (baseResponse.isSuccess()) {
                        layout.setVisibility(View.VISIBLE);
                        MyInquiry data = baseResponse.getData();
                        initView(data);
                    } else {
                        showErrorToast(baseResponse.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_DATA_RIGHT://立即报价
                    BaseResponse base = (BaseResponse) msg.obj;
                    if (base.isSuccess()) {
                        showSuccessTips("提交成功");
                        CommonApi.addLiveness(getUserID(), 4);
                    } else {
                        showErrorToast(base.getError_desc());
                    }
                    break;
                case NETWORK_FAIL:
                    activity.showErrorToast();
                    break;
            }
        }
    }

    private void initView(MyInquiry data) {
        tvProductName.setText(data.getProduct_name());
        if (data.getMy_offer() != null) {
            hideBottom();
            tvOfferAmount.setText("￥" + data.getMy_offer().getMoney());
        } else {
            if (data.getStatus().equals("0")) {
                hideBottom();
                myOffer.setVisibility(View.VISIBLE);
                tvRemain.setVisibility(View.GONE);
                tvOfferAmount.setText("已取消");
                tvOfferAmount.setTextColor(getResources().getColor(R.color.red_color));
            } else {
                if (HttpUtil.getTimeLong(data.getExpiry_date()) - System.currentTimeMillis() <= 0) {
                    hideBottom();
                    myOffer.setVisibility(View.VISIBLE);
                    tvRemain.setVisibility(View.GONE);
                    tvOfferAmount.setText("已截止");
                    tvOfferAmount.setTextColor(getResources().getColor(R.color.red_color));
                } else {
                    showBottom();
                    myOffer.setVisibility(View.GONE);
                }
            }

        }
        inquiry_id = data.getId();
        initSpecification(data.getSpec());
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
        final ArrayList<HashMap<String, Object>> mList = new ArrayList<>();
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
        listView.setAdapter(adapter);
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
                Intent intent = new Intent(OfferDetailsActivity.this, GalleryActivity.class);
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
