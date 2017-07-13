package com.appjumper.silkscreen.ui.home.stock;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.Avatar;
import com.appjumper.silkscreen.bean.Product;
import com.appjumper.silkscreen.bean.ProductDetailsResponse;
import com.appjumper.silkscreen.bean.ServiceProduct;
import com.appjumper.silkscreen.bean.Spec;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.home.CompanyDetailsActivity;
import com.appjumper.silkscreen.ui.home.adapter.PropertyAdapter;
import com.appjumper.silkscreen.ui.home.adapter.SpecGridAdapter;
import com.appjumper.silkscreen.ui.home.adapter.SpecSelectGridAdapter;
import com.appjumper.silkscreen.ui.home.adapter.StockSpecListViewAdapter;
import com.appjumper.silkscreen.ui.inquiry.InquiryStockActivity;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.appjumper.silkscreen.util.PicassoRoundTransform;
import com.appjumper.silkscreen.util.ShareUtil;
import com.appjumper.silkscreen.view.MyGridView;
import com.appjumper.silkscreen.view.MyListView;
import com.appjumper.silkscreen.view.ObservableScrollView;
import com.appjumper.silkscreen.view.SelectPicPopupWindow;
import com.appjumper.silkscreen.view.banner.CycleView2Pager;
import com.appjumper.silkscreen.view.banner.ViewFactory;
import com.appjumper.silkscreen.view.phonegridview.GalleryActivity;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshBase;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshScrollView;
import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 现货产品详情
 * Created by Botx on 2017/4/14.
 */

public class StockDetailActivity extends BaseActivity {

    public static StockDetailActivity instance = null;

    @Bind(R.id.pull_refresh_scrollview)
    PullToRefreshScrollView mPullRefreshScrollView;

    @Bind(R.id.list_view)
    MyListView listView;

    @Bind(R.id.tv_remark)
    TextView tv_remark;

    @Bind(R.id.iv_logo)//公司logo
            ImageView iv_logo;

    @Bind(R.id.tv_company_name)//公司名称
            TextView tv_company_name;

    @Bind(R.id.img_auth_status)//个人认证
            ImageView img_auth_status;

    @Bind(R.id.img_enterprise_auth_status)//企
            ImageView img_enterprise_auth_status;

    @Bind(R.id.img_enterprise_productivity_auth_status)//力
            ImageView img_enterprise_productivity_auth_status;

    @Bind(R.id.tv_address)//地址
            TextView tv_address;

    @Bind(R.id.lLaytSpec)
    LinearLayout lLaytSpec;
    @Bind(R.id.tv_inquiry)
    TextView tv_inquiry;

    private CycleView2Pager cycleViewPager;
    private String id;
    private List<Avatar> imglist;
    private String eid;
    private Product product;

    private List<List<Spec>> multiSpecList = new ArrayList<>(); //多规格数据集合
    private int selectedIndex = 0; //选择的规格下标
    private PopupWindow popup; //选择规格对话框





    private void initView(Product data) {
        product = data;
        eid = data.getEnterprise_id();
        imglist = data.getImg_list();
        tv_remark.setText(data.getRemark());
        tv_company_name.setText(data.getEnterprise_name());
        if (data.getEnterprise_logo() != null && !data.getEnterprise_logo().getSmall().equals("")) {
            Picasso.with(this).load(data.getEnterprise_logo().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.icon_logo_image61).error(R.mipmap.icon_logo_image61).into(iv_logo);
        }

        if (data.getAuth_status() != null && data.getAuth_status().equals("2")) {
            img_auth_status.setVisibility(View.VISIBLE);
        } else {
            img_auth_status.setVisibility(View.GONE);
        }

        if (data.getEnterprise_auth_status() != null && data.getEnterprise_auth_status().equals("2")) {
            img_enterprise_auth_status.setVisibility(View.VISIBLE);
        } else {
            img_enterprise_auth_status.setVisibility(View.GONE);
        }

        if (data.getEnterprise_productivity_auth_status() != null && data.getEnterprise_productivity_auth_status().equals("2")) {
            img_enterprise_productivity_auth_status.setVisibility(View.VISIBLE);
        } else {
            img_enterprise_productivity_auth_status.setVisibility(View.GONE);
        }
        tv_address.setText(data.getEnterprise_address());
        initViewPager(data.getImg_list());

        parseMultiSpec();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
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

        if (multiSpecList.size() > 1)
            initSepcSelectPopup();
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
        List<Spec> tempList = new ArrayList<>();
        for (int j = 0; j < specList.size(); j++) {
            Spec spec = specList.get(j);
            if (!TextUtils.isEmpty(spec.getValue().trim())) {
                tempList.add(spec);
            }
        }

        //渲染具体规格
        LinearLayout llSpec = (LinearLayout) specView.findViewById(R.id.llSpec);
        //刀片刺绳用listview，其余产品用gridview
        if (product.getProduct_id().equals("27"))
            llSpec.addView(getListView(tempList));
        else
            llSpec.addView(getGridView(tempList));


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


    /**
     * 初始化规格选择对话框
     */
    private void initSepcSelectPopup() {
        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_stock_spec_select, null);
        popup = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        ImageView imgViProduct = (ImageView) contentView.findViewById(R.id.imgViProduct);
        TextView txtName = (TextView) contentView.findViewById(R.id.txtName);
        ImageView imgViClose = (ImageView) contentView.findViewById(R.id.imgViClose);
        final GridView gridView = (GridView) contentView.findViewById(R.id.gridSpec);
        final TextView txtSpecDesc = (TextView) contentView.findViewById(R.id.txtSpecDesc);

        txtName.setText(product.getProduct_name());
        txtSpecDesc.setText(getSpecDesc(multiSpecList.get(0)));
        selectedIndex = 0;
        Picasso.with(context)
                .load(product.getImg_list().get(0).getSmall())
                .placeholder(R.mipmap.img_error)
                .error(R.mipmap.img_error)
                .into(imgViProduct);

        imgViClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });

        List<String> menuList = new ArrayList<>();
        for (int i = 0; i < multiSpecList.size(); i++) {
            menuList.add("规格" + (i + 1));
        }

        final SpecSelectGridAdapter adapter = new SpecSelectGridAdapter(context, menuList);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedIndex = position;
                adapter.changeSelected(position);
                gridView.smoothScrollToPosition(position);
                txtSpecDesc.setText(getSpecDesc(multiSpecList.get(position)));
            }
        });

        TextView txtConfirm = (TextView) contentView.findViewById(R.id.txtConfirm);
        txtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToInquiry();
            }
        });

        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                AppTool.setBackgroundAlpha(context, 1.0f);
            }
        });

        popup.setAnimationStyle(R.style.PopupAnimBottom);
        popup.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
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

    @OnClick({R.id.tv_inquiry, R.id.rl_company, R.id.iv_share})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_inquiry://询价
                if (checkLogined()) {
                    if (multiSpecList.size() > 1) {
                        popup.showAtLocation(tv_inquiry, Gravity.BOTTOM, 0, 0);
                        AppTool.setBackgroundAlpha(context, 0.5f);
                    } else {
                        goToInquiry();
                    }
                }
                break;
            case R.id.rl_company:
                start_Activity(context, CompanyDetailsActivity.class, new BasicNameValuePair("from", "2"), new BasicNameValuePair("id", eid));
                break;
            case R.id.iv_share:
                ShareUtil.intShare2(this, v, product.getRemark(), product.getProduct_name()+product.getService_type_name(), product.getUrl(),product.getImg_list().get(0).getSmall());
                break;
            default:
                break;
        }
    }



    /**
     * 跳转到询价界面
     */
    private void goToInquiry() {
        ServiceProduct serviceProduct = new ServiceProduct();
        serviceProduct.setName(product.getProduct_name());
        serviceProduct.setId(product.getProduct_id());
        serviceProduct.setRemark(product.getRemark());

        if (multiSpecList.size() > 0)
            serviceProduct.setProduct_spec(multiSpecList.get(selectedIndex));
        else
            serviceProduct.setProduct_spec(new ArrayList<Spec>());

        Intent intent = new Intent(context, InquiryStockActivity.class);
        intent.putExtra("type", product.getType());
        intent.putExtra("eid", product.getEnterprise_id());
        intent.putExtra(Const.KEY_OBJECT, serviceProduct);

        startActivity(intent);
    }


    /**
     * 生成规格描述
     * @return
     */
    private String getSpecDesc(List<Spec> specList) {
        String str = "";

        for (int i = 0; i < specList.size(); i++) {
            Spec spec = specList.get(i);
            if (TextUtils.isEmpty(spec.getValue()))
                continue;
            if (spec.getValue().matches("[hH][tT]{2}[pP]://[\\s\\S]+\\.[jJ][pP][gG]"))
                continue;

            str += spec.getName() + spec.getValue();
            if (i != specList.size() - 1)
                str += ", ";
        }

        return str;
    }


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

                        if (!getUserID().equals(data.getUser_id())) {
                            CommonApi.addLiveness(data.getUser_id(), 20);
                        } else {
                            tv_inquiry.setVisibility(View.GONE);
                        }
                    } else {
                        showErrorToast(response.getError_desc());
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
    }


















    /**
     * 规格弹窗 （没用着）
     *
     * @param v
     */
    private void initViewPopupWindow(final View v) {
        //实例化SelectPicPopupWindow
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View mMenuView = inflater.inflate(R.layout.dialog_specifications_detail_page, null);
        ImageView ivPhoto = (ImageView) mMenuView.findViewById(R.id.iv_photo);
        TextView tvName = (TextView) mMenuView.findViewById(R.id.tv_name);
        ImageView ivDismiss = (ImageView) mMenuView.findViewById(R.id.iv_dismiss);
        ListView lvProperty = (ListView) mMenuView.findViewById(R.id.lv_property);
        final TextView tvStyle = (TextView) mMenuView.findViewById(R.id.tv_style);
        Button btnConfirm = (Button) mMenuView.findViewById(R.id.btn_confirm);
        final SelectPicPopupWindow menuWindow = new SelectPicPopupWindow(context, mMenuView);
        menuWindow.setAnimationStyle(R.style.BottomPopupAnimation);

        tvName.setText("石笼网");
        Picasso.with(context).load(R.mipmap.ic_launcher).into(ivPhoto);
        //假数据
        final ArrayList<HashMap<String, Object>> mList = new ArrayList<>();
        List<String> item1 = new ArrayList<>();
        item1.add("规格一：丝径 55 网孔 44");
        item1.add("规格二：丝径 54 网孔 43");
        item1.add("规格三：丝径 56 网孔 43");
        item1.add("规格三：丝径 56 网孔 43");
        List<String> item2 = new ArrayList<>();
        item2.add("现货");
        item2.add("订做");
        item2.add("加工");
        List<String> types = new ArrayList<>();
        types.add("规格");
        types.add("服务");
        List<List<String>> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        final long[] arr = new long[mList.size()];
        final Set<String> titles = new HashSet<>();
        final List<String> mTitles = new ArrayList<>();

        for (int j = 0; j < types.size(); j++) {
            HashMap<String, Object> selectProMap = new HashMap<>();
            selectProMap.put("type", types.get(j));
            selectProMap.put("label", items.get(j));
            mTitles.add(types.get(j));
            mList.add(selectProMap);

        }
        tvStyle.setText("请选择规格和服务");
        PropertyAdapter propertyAdapter = new PropertyAdapter(handler, this, mList);
        propertyAdapter.setCallbackListener(new PropertyAdapter.CallbackListener() {
            @Override
            public void onCallbackListener(String type, int i, int position) {
                titles.add((String) mList.get(position).get("type"));
//                long[] arr2 = new long[arr.length];
//                for (int r = 0; r < arr.length; r++) {
//                    arr2[r] = arr[r];
//                }
                mTitles.removeAll(titles);
                if (mTitles.size() > 0) {
                    StringBuffer buff = new StringBuffer();
                    for (int k = 0; k < mTitles.size(); k++) {
                        buff.append(mTitles.get(k));
                    }
                    tvStyle.setText("请选择" + buff.toString());
                } else {
                    tvStyle.setText("已选择规格和服务");
                }
//                Arrays.sort(arr2);
//                StringBuffer buffer = new StringBuffer();
//                for (int j = 0; j < arr2.length; j++) {
//                    buffer.append(arr2[j] + "_");
//                }
//                String key = buffer.toString();
//                key = key.substring(0, key.length() - 1);
            }
        });

        lvProperty.setAdapter(propertyAdapter);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTitles.size() > 0) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < mTitles.size(); i++) {
                        builder.append(mTitles.get(i));
                    }
                    showErrorToast("请选择" + builder.toString());
                    return;
                }
            }

        });
        ivDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuWindow.dismiss();
            }
        });
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_layout_specifications).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        menuWindow.dismiss();

                    }
                }
                return true;
            }
        });
        backgroundAlpha(0.4f);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        menuWindow.setBackgroundDrawable(dw);
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        menuWindow.setOnDismissListener(new popupDismissListener());
        menuWindow.showAtLocation(v, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
    }

    class popupDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

}
