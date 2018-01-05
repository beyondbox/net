package com.appjumper.silkscreen.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.appjumper.silkscreen.bean.ServiceProduct;
import com.appjumper.silkscreen.bean.Spec;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.adapter.ProductListAdapter;
import com.appjumper.silkscreen.ui.dynamic.ReleaseAskBuyActivity;
import com.appjumper.silkscreen.ui.inquiry.InquirySpecificationActivity;
import com.appjumper.silkscreen.ui.my.enterprise.AddServiceCompleteActivity;
import com.appjumper.silkscreen.ui.my.enterprise.SpecificationActivity;
import com.appjumper.silkscreen.ui.my.enterprise.SpecificationStockActivity;
import com.appjumper.silkscreen.ui.spec.InquiryDaoPianActivity;
import com.appjumper.silkscreen.ui.spec.InquiryHuLanActivity;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.MProgressDialog;
import com.appjumper.silkscreen.util.SPUtil;
import com.appjumper.silkscreen.view.IndexSideBar;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 产品选择列表
 * Created by Botx on 2017/3/17.
 */

public class ProductSelectActivity extends BaseActivity {

    public static ProductSelectActivity instance = null;

    @Bind(R.id.txtHint)
    TextView txtHint;
    @Bind(R.id.lvData)
    ListView lvData;
    @Bind(R.id.sideBar)
    IndexSideBar sideBar;
    @Bind(R.id.lLaytConfirm)
    LinearLayout lLaytConfirm;
    @Bind(R.id.txtNoSelect)
    TextView txtNoSelect;

    /**
     * 添加服务
     */
    public static final int MOTION_RELEASE_SERVICE = 3001;
    /**
     * 发布询价
     */
    public static final int MOTION_RELEASE_INQUIRY = 3002;
    /**
     * 发布求购
     */
    public static final int MOTION_RELEASE_ASKBUY = 3003;


    private List<ServiceProduct> productList;
    private ProductListAdapter productAdapter;

    private List<String> actualSectionList; //数据中实际存在的首字母

    private boolean isMultiMode = false; //多选模式
    private boolean isFilterMode = false; //筛选模式
    private boolean isStockShop = false; //选择现货商城的商品
    private int serviceType; //产品类型
    private String action = "";

    private int motion = 0; //标记是添加服务还是发布询价


    private Handler hintHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isDestroyed())
                return;

            switch (msg.what) {
                case IndexSideBar.WHAT_HIDE_HINT:
                    txtHint.setVisibility(View.GONE);
                    break;
                case IndexSideBar.WHAT_SHOW_HINT:
                    String hintStr = (String) msg.obj;
                    txtHint.setText(hintStr);
                    txtHint.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    private String[] b = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_select);
        ButterKnife.bind(this);
        instance = this;
        initProgressDialog();

        Intent intent = getIntent();
        isMultiMode = intent.getBooleanExtra(Const.KEY_IS_MULTI_MODE, false);
        isFilterMode = intent.getBooleanExtra(Const.KEY_IS_FILTER_MODE, false);
        isStockShop = intent.getBooleanExtra(Const.KEY_IS_STOCK_SHOP, false);
        serviceType = intent.getIntExtra(Const.KEY_SERVICE_TYPE, 0);
        motion = intent.getIntExtra(Const.KEY_MOTION, 0);
        if (intent.hasExtra(Const.KEY_ACTION))
            action = intent.getStringExtra(Const.KEY_ACTION);

        if (isFilterMode) {
            txtNoSelect.setVisibility(View.VISIBLE);
        } else {
            txtNoSelect.setVisibility(View.GONE);
        }

        switch (serviceType) {
            case Const.SERVICE_TYPE_ORDER:
                initTitle("订做产品选择");
                break;
            case Const.SERVICE_TYPE_PROCESS:
                initTitle("加工产品选择");
                break;
            case Const.SERVICE_TYPE_STOCK:
                if (action.equals(Const.ACTION_ADD_PRODUCT))
                    initTitle("产品选择");
                else
                    initTitle("现货产品选择");
                break;
            default:
                initTitle("产品选择");
                break;
        }

        initListView();
        getData();

        sideBar.setHintHandler(hintHandler);
        sideBar.setOnTouchingLetterChangedListener(new IndexSideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = productAdapter.getPositionForSection(s.toUpperCase().charAt(0));
                lvData.setSelection(position);
            }
        });

        if (AddServiceCompleteActivity.instance != null)
            AddServiceCompleteActivity.instance.finish();
    }



    private void initListView() {
        productList = new ArrayList<>();
        actualSectionList = new ArrayList<>();
        productAdapter = new ProductListAdapter(context, productList, action);
        if (isMultiMode) {
            productAdapter.setMultiMode(true);
            lLaytConfirm.setVisibility(View.VISIBLE);
        }

        lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isMultiMode) {
                    if (motion == 0) {
                        Intent data = new Intent();
                        data.putExtra(Const.KEY_OBJECT, productList.get(position));
                        setResult(0, data);
                        finish();
                    } else {
                        handleSpecialNeeds(productList.get(position));
                    }
                }
            }
        });

        productAdapter.setOnWhichClickListener(new MyBaseAdapter.OnWhichClickListener() {
            @Override
            public void onWhichClick(View view, int position, int tag) {
                switch (view.getId()) {
                    case R.id.chkSelect:
                        if (action.equals(Const.ACTION_ATTENT_PRODUCT_MANAGE)) {
                            if (((CheckBox)view).isChecked())
                                productList.get(position).setIs_collection("1");
                            else
                                productList.get(position).setIs_collection("0");
                        } else if (action.equals(Const.ACTION_ADD_PRODUCT)) {
                            if (((CheckBox)view).isChecked())
                                productList.get(position).setIs_car("1");
                            else
                                productList.get(position).setIs_car("0");
                        }

                        productAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }
        });

        lvData.setAdapter(productAdapter);

        if (isMultiMode) {
            progress.show();
        } else {
            //加载缓存
            String cache = "";
            if (isStockShop)
                cache = SPUtil.getString(null, Const.KEY_PRODUCT_LIST, "");
            else
                cache = SPUtil.getString(null, Const.KEY_PRODUCT_LIST + serviceType, "");

            if (!TextUtils.isEmpty(cache)) {
                try {
                    parseJson(new JSONObject(cache));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                progress.show();
            }
        }

    }



    /**
     * 处理添加服务和发布询价的跳转
     * 添加服务或者发布询价时要求不关闭当前列表界面，so....
     */
    private void handleSpecialNeeds(ServiceProduct product) {
        Intent intent = null;

        switch (motion) {
            case MOTION_RELEASE_SERVICE: //添加服务
                productCheck(product);
                break;

            case MOTION_RELEASE_INQUIRY: //发布询价
                if (product.getId().equals("104"))
                    intent = new Intent(context, InquiryHuLanActivity.class);
                else if (product.getId().equals("27"))
                    intent = new Intent(context, InquiryDaoPianActivity.class);
                else
                    intent = new Intent(context, InquirySpecificationActivity.class);
                intent.putExtra("identity", "3");
                intent.putExtra("service", product);
                intent.putExtra("type", serviceType + "");
                startActivity(intent);
                break;

            case MOTION_RELEASE_ASKBUY: //发布求购
                intent = new Intent(context, ReleaseAskBuyActivity.class);
                intent.putExtra(Const.KEY_OBJECT, product);
                startActivity(intent);
                break;
            default:
                break;
        }

    }




    /**
     * 获取产品数据
     */
    private void getData() {
        RequestParams params = MyHttpClient.getApiParam("collection", "productByServiceType");
        params.put("uid", getUserID());
        params.put("service_type", serviceType);
        if (isStockShop)
            params.put("type", 1);
        else if (isFilterMode)
            params.put("type", 2);
        else
            params.put("type", 0);


        if (motion == MOTION_RELEASE_ASKBUY)
            params.put("purchase", 1);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                //progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");

                        if (!isDestroyed())
                            parseJson(dataObj);

                        //数据缓存到本地
                        if (isStockShop)
                            SPUtil.putString(null, Const.KEY_PRODUCT_LIST, dataObj.toString());
                        else
                            SPUtil.putString(null, Const.KEY_PRODUCT_LIST + serviceType, dataObj.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (!isDestroyed())
                    showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (!isDestroyed())
                    progress.dismiss();
            }
        });
    }


    /**
     * json数据解析
     */
    private void parseJson(JSONObject dataObj) {
        actualSectionList.clear();
        productList.clear();

        Iterator<String> it = dataObj.keys();

        while (it.hasNext()) {
            String section = it.next();
            actualSectionList.add(section);
            try {
                JSONArray jsonArr = dataObj.getJSONArray(section);
                productList.addAll(GsonUtil.getEntityList(jsonArr.toString(), ServiceProduct.class));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        sideBar.setActualCitySections(actualSectionList);
        productAdapter.notifyDataSetChanged();
    }



    /**
     * 检查是否已发布过该产品
     * @param product
     */
    private void productCheck(final ServiceProduct product) {
        final MProgressDialog progress2 = new MProgressDialog(context, false);

        RequestParams params = MyHttpClient.getApiParam("service", "oneCheck");
        params.put("uid", getUserID());
        params.put("type", serviceType);
        params.put("product_id", product.getId());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress2.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        Intent intent = null;
                        product.setProduct_spec(new ArrayList<Spec>());

                        if (serviceType == Const.SERVICE_TYPE_STOCK) {
                            /*if (product.getId().equals("104"))
                                intent = new Intent(context, ReleaseHuLanStockActivity.class);
                            else if (product.getId().equals("27"))
                                intent = new Intent(context, ReleaseDaoPianStockActivity.class);
                            else*/
                                intent = new Intent(context, SpecificationStockActivity.class);
                        } else {
                            /*if (product.getId().equals("104"))
                                intent = new Intent(context, ReleaseHuLanActivity.class);
                            else if (product.getId().equals("27"))
                                intent = new Intent(context, ReleaseDaoPianActivity.class);
                            else*/
                                intent = new Intent(context, SpecificationActivity.class);
                        }
                        intent.putExtra("service", product);
                        intent.putExtra("type", serviceType + "");

                        startActivity(intent);

                    } else {
                        showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showErrorToast(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress2.dismiss();
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;

        if (isMultiMode) {
            ArrayList<ServiceProduct> list = (ArrayList<ServiceProduct>) data.getSerializableExtra("list");
            for (ServiceProduct product : list) {
                for (ServiceProduct product1 : productList) {
                    if (product.getId().equals(product1.getId())) {
                        if (action.equals(Const.ACTION_ATTENT_PRODUCT_MANAGE)) {
                            product1.setIs_collection(product.getIs_collection());
                        } else if (action.equals(Const.ACTION_ADD_PRODUCT)) {
                            product1.setIs_car(product.getIs_car());
                        }
                        break;
                    }
                }
            }

            productAdapter.notifyDataSetChanged();
        } else {
            if (motion == 0) {
                setResult(0, data);
                finish();
            } else {
                handleSpecialNeeds((ServiceProduct) data.getSerializableExtra(Const.KEY_OBJECT));
            }
        }

    }



    @OnClick({R.id.back, R.id.txtConfirm, R.id.txtNoSelect, R.id.imgViSearch})
    public void onClick(View view) {
        Intent data = new Intent();
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.txtNoSelect: //全部产品
                ServiceProduct pro = new ServiceProduct();
                pro.setId("");
                pro.setName("全部产品");
                pro.setProduct_spec(new ArrayList<Spec>());
                data.putExtra(Const.KEY_OBJECT, pro);
                setResult(0, data);
                finish();
                break;
            case R.id.txtConfirm: //完成
                ArrayList<ServiceProduct> list = new ArrayList<>();
                for (ServiceProduct product : productList) {
                    if (action.equals(Const.ACTION_ATTENT_PRODUCT_MANAGE)) {
                        if (product.getIs_collection().equals("1"))
                            list.add(product);
                    } else if (action.equals(Const.ACTION_ADD_PRODUCT)) {
                        if (product.getIs_car().equals("1"))
                            list.add(product);
                    }
                }

                data.putExtra("list", list);
                setResult(0, data);
                finish();
                break;
            case R.id.imgViSearch: //搜索
                if (productList.size() == 0)
                    return;
                Intent intent = new Intent(context, ProductSearchActivity.class);
                intent.putExtra("list", (Serializable)productList);
                intent.putExtra(Const.KEY_IS_MULTI_MODE, isMultiMode);
                intent.putExtra(Const.KEY_ACTION, action);
                startActivityForResult(intent, 0);
                break;
            default:
                break;
        }
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }


}
