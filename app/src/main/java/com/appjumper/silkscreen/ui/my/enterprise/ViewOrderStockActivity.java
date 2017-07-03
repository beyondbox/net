package com.appjumper.silkscreen.ui.my.enterprise;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.Avatar;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.bean.Product;
import com.appjumper.silkscreen.bean.ProductDetailsResponse;
import com.appjumper.silkscreen.bean.Spec;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.home.adapter.SpecGridAdapter;
import com.appjumper.silkscreen.ui.home.adapter.StockSpecListViewAdapter;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.appjumper.silkscreen.view.MyGridView;
import com.appjumper.silkscreen.view.MyListView;
import com.appjumper.silkscreen.view.ObservableScrollView;
import com.appjumper.silkscreen.view.SureOrCancelDialog;
import com.appjumper.silkscreen.view.banner.CycleView2Pager;
import com.appjumper.silkscreen.view.banner.ViewFactory;
import com.appjumper.silkscreen.view.phonegridview.GalleryActivity;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshBase;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshScrollView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 现货产品详情
 * Created by Botx on 2017/4/14.
 */

public class ViewOrderStockActivity extends BaseActivity {

    public static ViewOrderStockActivity instance = null;

    @Bind(R.id.pull_refresh_scrollview)
    PullToRefreshScrollView mPullRefreshScrollView;

    @Bind(R.id.list_view)
    MyListView listView;

    @Bind(R.id.tv_remark)
    TextView tv_remark;

    @Bind(R.id.lLaytSpec)
    LinearLayout lLaytSpec;

    private CycleView2Pager cycleViewPager;
    private String id;
    private List<Avatar> imglist;
    private String eid;
    private Product product;

    private List<List<Spec>> multiSpecList = new ArrayList<>(); //多规格数据集合



    private void initView(Product data) {
        product = data;
        eid = data.getEnterprise_id();
        imglist = data.getImg_list();
        tv_remark.setText(data.getRemark());
        initViewPager(data.getImg_list());

        parseMultiSpec();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_stock);
        ButterKnife.bind(this);
        instance = this;
        initBack();
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        initTitle(getIntent().getStringExtra("title"));
        initProgressDialog();
        progress.show();
        progress.setMessage("正在加载...");
        new Thread(run).start();
        refresh();
    }



    /**
     * 解析多规格数据
     */
    private void parseMultiSpec() {
        int count = Integer.valueOf(product.getSpec_num());
        List<Spec> list = product.getService_spec();

        if (list.size() == 0)
            return;

        for (int i = 1; i < count + 1; i++) {
            List<Spec> temp = new ArrayList<>();
            for (Spec spec : list) {
                if (spec.getSpec_num().equals(i + ""))
                    temp.add(spec);
            }

            multiSpecList.add(temp);
        }

        for (int i = 0; i < multiSpecList.size(); i++) {
            setOneSpec(multiSpecList.get(i), i);
        }
    }



    /**
     * 渲染单规格数据
     */
    private void setOneSpec(List<Spec> specList, int i) {
        if (specList.size() == 0)
            return;

        View specView = LayoutInflater.from(context).inflate(R.layout.layout_spec_detail, null);

        //规格编号
        TextView txtSpecName = (TextView) specView.findViewById(R.id.txtSpecName);
        if (multiSpecList.size() == 1 && i == 0)
            txtSpecName.setText("规格");
        else
            txtSpecName.setText("规格" + (i + 1));


        //设置右上角存量显示
        TextView txtCunLiang = (TextView) specView.findViewById(R.id.txtCunLiang);
        for (Spec spec : specList) {
            if (spec.getFieldname().equals("cunliang")) {
                if (!TextUtils.isEmpty(spec.getValue())) {
                    txtCunLiang.setText("存量" + spec.getValue() + spec.getUnit());
                    txtCunLiang.setVisibility(View.VISIBLE);
                }
                break;
            }
        }

        //过滤空字段
        /*List<Spec> tempList = new ArrayList<>();
        for (int j = 0; j < specList.size(); j++) {
            Spec spec = specList.get(j);
            if (!TextUtils.isEmpty(spec.getValue().trim())) {
                tempList.add(spec);
            }
        }*/

        //渲染具体规格
        LinearLayout llSpec = (LinearLayout) specView.findViewById(R.id.llSpec);
        //刀片刺绳用listview，其余产品用gridview
        if (product.getProduct_id().equals("27"))
            llSpec.addView(getListView(specList));
        else
            llSpec.addView(getGridView(specList));


        lLaytSpec.addView(specView);
    }


    /**
     * 生成规格数据的GridView
     */
    private GridView getGridView(List<Spec> specList) {
        MyGridView gridView = new MyGridView(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        gridView.setLayoutParams(params);
        int padding = DisplayUtil.dip2px(context, 12);
        int paddingLeft = DisplayUtil.dip2px(context, 14);
        gridView.setPadding(paddingLeft, padding, padding, padding);
        gridView.setNumColumns(2);
        gridView.setHorizontalSpacing(DisplayUtil.dip2px(context, 10));
        gridView.setVerticalSpacing(DisplayUtil.dip2px(context, 12));

        gridView.setAdapter(new SpecGridAdapter(context, specList));

        return gridView;
    }


    /**
     * 生成规格数据的ListView
     */
    private ListView getListView(List<Spec> specList) {
        MyListView listView = new MyListView(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        listView.setLayoutParams(params);
        int padding = DisplayUtil.dip2px(context, 12);
        int paddingLeft = DisplayUtil.dip2px(context, 17);
        listView.setPadding(paddingLeft, padding, padding, padding);
        listView.setDividerHeight(0);

        listView.setAdapter(new StockSpecListViewAdapter(context, specList));

        return listView;
    }


    private void refresh() {
        mPullRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ObservableScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ObservableScrollView> refreshView) {
                mPullRefreshScrollView.onRefreshComplete();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ObservableScrollView> refreshView) {
                mPullRefreshScrollView.onRefreshComplete();
            }
        });
    }



    @OnClick({R.id.tv_delete})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_delete://删除
                SureOrCancelDialog followDialog = new SureOrCancelDialog(this, "是否删除服务", new SureOrCancelDialog.SureButtonClick() {
                    @Override
                    public void onSureButtonClick() {
                        new Thread(deleteRun).start();
                    }
                });
                followDialog.show();
                break;
            default:
                break;
        }
    }


    private Runnable deleteRun = new Runnable() {

        public void run() {
            BaseResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("id", id);
                data.put("uid", getUserID());
                response = JsonParser.getBaseResponse(HttpUtil.postMsg(HttpUtil.getData(data), Url.SERVICEDELETE));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        4, response));
            } else {
                handler.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    };


    private Runnable run = new Runnable() {

        public void run() {
            ProductDetailsResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("g", "api");
                data.put("m", "service");
                data.put("a", "details");


                data.put("id", id);
                data.put("uid", getUserID());

                //response = JsonParser.getProductDetailsResponse(HttpUtil.postMsg(HttpUtil.getData(data), Url.SERVICEDETAILS));
                response = JsonParser.getProductDetailsResponse(HttpUtil.getMsg(Url.HOST + "?" + HttpUtil.getData(data)));
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
            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_RIGHT:
                    ProductDetailsResponse response = (ProductDetailsResponse) msg.obj;
                    if (response.isSuccess()) {
                        Product data = response.getData();
                        initView(data);

                        if (!getUserID().equals(data.getUser_id()))
                            CommonApi.addLiveness(data.getUser_id(), 20);
                    } else {
                        showErrorToast(response.getError_desc());
                    }
                    break;
                case 4://删除
                    BaseResponse baseresponse = (BaseResponse) msg.obj;
                    if(baseresponse.isSuccess()){
                        showErrorToast("删除成功");
                        setResult(Const.RESULT_CODE_NEED_REFRESH);
                        finish();
                    }else{
                        showErrorToast(baseresponse.getError_desc());
                    }
                    break;
                default:
                    showErrorToast();
                    break;
            }
        }
    }


    /**
     * 轮播图
     */
    private List<ImageView> views = new ArrayList<ImageView>();

    private void initViewPager(List<Avatar> list) {
        if (views != null) {
            views.clear();
        }
        cycleViewPager = (CycleView2Pager) getFragmentManager()
                .findFragmentById(R.id.fragment_cycle_viewpager_content);

        // 将最后一个ImageView添加进来
        views.add(ViewFactory.getImageView(this, list.get(list.size() - 1).getSmall()));
        for (int i = 0; i < list.size(); i++) {
            views.add(ViewFactory.getImageView(this, list.get(i).getOrigin()));
        }
        // 将第一个ImageView添加进来
        views.add(ViewFactory.getImageView(this, list.get(0).getOrigin()));

        // 设置循环，在调用setData方法前调用
        cycleViewPager.setCycle(true);

        // 在加载数据前设置是否循环
        cycleViewPager.setData(views, list, mAdCycleViewListener);
        //设置轮播
        cycleViewPager.setWheel(true);

        // 设置轮播时间，默认5000ms
        cycleViewPager.setTime(2000);
        //设置圆点指示图标组居中显示，默认靠右
        cycleViewPager.setIndicatorCenter();
    }

    private CycleView2Pager.ImageCycleViewListener mAdCycleViewListener = new CycleView2Pager.ImageCycleViewListener() {

        @Override
        public void onImageClick(Avatar info, int position, View imageView) {
            if (cycleViewPager.isCycle()) {
                position = position - 1;
                Intent intent = new Intent(context, GalleryActivity.class);
                ArrayList<String> urls = new ArrayList<String>();
                for (Avatar string : imglist) {
                    urls.add(string.getOrigin());
                }
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, position);
                startActivity(intent);
            }

        }

    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
        if (progress != null)
            progress.dismiss();
    }


}
