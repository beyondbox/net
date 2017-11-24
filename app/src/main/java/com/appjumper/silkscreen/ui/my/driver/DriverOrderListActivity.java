package com.appjumper.silkscreen.ui.my.driver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Freight;
import com.appjumper.silkscreen.bean.FreightOffer;
import com.appjumper.silkscreen.bean.ImageResponse;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.MainActivity;
import com.appjumper.silkscreen.ui.common.MultiSelectPhotoActivity;
import com.appjumper.silkscreen.ui.my.adapter.DriverOrderListAdapter;
import com.appjumper.silkscreen.util.AlipayHelper;
import com.appjumper.silkscreen.util.AmapTTSController;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Applibrary;
import com.appjumper.silkscreen.util.CommonUtil;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.ImageUtil;
import com.appjumper.silkscreen.view.SureOrCancelDialog;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 司机--订单列表
 * Created by Botx on 2017/10/27.
 */

public class DriverOrderListActivity extends MultiSelectPhotoActivity implements INaviInfoCallback {

    @Bind(R.id.ptrLayt)
    PtrClassicFrameLayout ptrLayt;
    @Bind(R.id.recyclerData)
    RecyclerView recyclerData;

    private List<Freight> dataList;
    private DriverOrderListAdapter adapter;
    private Freight item; //当前操作的订单

    private AlertDialog arriveDialog;
    private SureOrCancelDialog gpsDialog;
    private AmapTTSController amapTTSController;
    private ImageView imgViUpload;
    private TextView txtConfirm;
    private File tempImageFile;
    private String arriveImgUrl = "";

    private PopupWindow popupPay;
    private TextView txtConfirmPay;
    private TextView txtAccount;

    private String pushId;
    private int pushType;

    private int page = 1;
    private int pageSize = 30;
    private int totalSize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliver_orderlist);
        ButterKnife.bind(context);

        initTitle("订单列表");
        initBack();
        initProgressDialog(false, null);
        initDialog();
        registerBroadcastReceiver();

        amapTTSController = AmapTTSController.getInstance(getApplicationContext());
        amapTTSController.init();
        setCropSingleImage(false);
        setSingleImage(true);
        setCropTaskPhoto(false);

        Intent intent = getIntent();
        if (intent.hasExtra("id")) {
            pushType = intent.getIntExtra(Const.KEY_TYPE, 0);
            pushId = intent.getStringExtra("id");
        }

        initRecyclerView();
        initRefreshLayout();
        ptrLayt.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrLayt.autoRefresh();
            }
        }, 50);
    }


    @Override
    protected void onStart() {
        super.onStart();
        page = 1;
        getData();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        /*if (intent.hasExtra("id")) {
            pushId = intent.getStringExtra("id");
            ptrLayt.autoRefresh();
        }*/
    }


    private void initRecyclerView() {
        dataList = new ArrayList<>();

        adapter = new DriverOrderListAdapter(R.layout.item_recycler_freight_order_list, dataList);
        recyclerData.setLayoutManager(new LinearLayoutManager(context));
        adapter.bindToRecyclerView(recyclerData);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Freight freight = dataList.get(position);
                int state = Integer.valueOf(freight.getExamine_status());
                Intent intent = null;
                switch (state) {
                    case Const.FREIGHT_AUDIT_PASS: // 收到询价或者已报价
                        List<FreightOffer> offerList = freight.getOffer_list();
                        if (offerList != null && offerList.size() > 0)
                            intent = new Intent(context, OfferedActivity.class);
                        else
                            intent = new Intent(context, ReceiveInquiryActivity.class);
                        break;
                    case Const.FREIGHT_DRIVER_PAYING: //等待支付
                        intent = new Intent(context, DriverPayActivity.class);
                        break;
                    case Const.FREIGHT_GOTO_LOAD: //前往厂家
                        intent = new Intent(context, GoToDeliverActivity.class);
                        break;
                    case Const.FREIGHT_LOADING: //装货中
                        intent = new Intent(context, LoadingDriverActivity.class);
                        break;
                    case Const.FREIGHT_TRANSPORTING: //运输途中
                        intent = new Intent(context, TransportingDriverActivity.class);
                        break;
                    case Const.FREIGHT_TRANSPORT_FINISH: //运输完成
                        intent = new Intent(context, TransportFinishDriverActivity.class);
                        break;
                    case Const.FREIGHT_ORDER_FINISH: //订单完成
                        intent = new Intent(context, OrderFinishDriverActivity.class);
                        break;
                }

                if (intent != null) {
                    intent.putExtra("id", freight.getId());
                    startActivity(intent);
                }
            }
        });


        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                item = dataList.get(position);
                int state = Integer.valueOf(item.getExamine_status());
                switch (view.getId()) {
                    case R.id.txtHandle0: //第一个按钮
                        if (item.getCar_product_type().equals(Const.INFO_TYPE_OFFICIAL + ""))
                            AppTool.dial(context, item.getEnterprise_mobile());
                        else
                            AppTool.dial(context, item.getMobile());
                        break;
                    case R.id.txtHandle1: //第二个按钮
                        switch (state) {
                            case Const.FREIGHT_AUDIT_PASS:
                                new SureOrCancelDialog(context, "提示", "确定要忽略该订单吗？", "确定", "取消", new SureOrCancelDialog.SureButtonClick() {
                                    @Override
                                    public void onSureButtonClick() {
                                        ignoreOrder();
                                    }
                                }).show();
                                break;
                            case Const.FREIGHT_DRIVER_PAYING:
                                new SureOrCancelDialog(context, "提示", "确定要放弃该订单吗？", "确定", "取消", new SureOrCancelDialog.SureButtonClick() {
                                    @Override
                                    public void onSureButtonClick() {
                                        giveupOrder();
                                    }
                                }).show();
                                break;
                            case Const.FREIGHT_TRANSPORTING:
                                CommonUtil.getLocation(new AMapLocationListener() {
                                    @Override
                                    public void onLocationChanged(AMapLocation aMapLocation) {
                                        if (aMapLocation.getErrorCode() == 0) {
                                            updateLocation(aMapLocation.getLatitude(), aMapLocation.getLongitude(), aMapLocation.getAddress());
                                        } else {
                                            showErrorToast("定位失败: " + aMapLocation.getErrorInfo());
                                        }
                                    }
                                });
                                break;
                            case Const.FREIGHT_TRANSPORT_FINISH:
                                AppTool.dial(context, Const.SERVICE_PHONE_FREIGHT);
                                break;
                            default:
                                if (item.getCar_product_type().equals(Const.INFO_TYPE_OFFICIAL + ""))
                                    AppTool.dial(context, item.getEnterprise_mobile());
                                else
                                    AppTool.dial(context, item.getMobile());
                                break;
                        }
                        break;
                    case R.id.txtHandle2: //第三个按钮
                        switch (state) {
                            case Const.FREIGHT_AUDIT_PASS:
                                List<FreightOffer> offerList = item.getOffer_list();
                                if (offerList != null && offerList.size() > 0)
                                    showErrorToast("您已报价");
                                else
                                    showOfferDialog();
                                break;
                            case Const.FREIGHT_DRIVER_PAYING:
                                long endTime = HttpUtil.getTimeLong(item.getExpiry_driver_pay_date());
                                if (System.currentTimeMillis() < endTime) {
                                    popupPay.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                                    AppTool.setBackgroundAlpha(context, 0.4f);
                                }
                                break;
                            case Const.FREIGHT_GOTO_LOAD:
                                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                                if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
                                    gpsDialog.show();
                                    return;
                                }
                                LatLng latLng = new LatLng(Double.valueOf(item.getConsignor_lat()), Double.valueOf(item.getConsignor_lng()));
                                AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), new AmapNaviParams(null, null, new Poi(item.getConsignor_place(), latLng, ""), AmapNaviType.DRIVER), DriverOrderListActivity.this);
                                break;
                            case Const.FREIGHT_LOADING:
                                AppTool.dial(context, Const.SERVICE_PHONE_FREIGHT);
                                break;
                            case Const.FREIGHT_TRANSPORTING:
                                arriveDialog.show();
                                break;
                            case Const.FREIGHT_TRANSPORT_FINISH:
                                new SureOrCancelDialog(context, "提示", "是否确认收到运费？", "确定", "取消", new SureOrCancelDialog.SureButtonClick() {
                                    @Override
                                    public void onSureButtonClick() {
                                        confirmPayment();
                                    }
                                }).show();
                                break;
                        }
                        break;
                }
            }
        });


        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                getData();
            }
        }, recyclerData);

        adapter.setEnableLoadMore(false);
    }


    /**
     * 设置下拉刷新
     */
    private void initRefreshLayout() {
        ptrLayt.disableWhenHorizontalMove(true);
        ptrLayt.setLastUpdateTimeRelateObject(this);
        ptrLayt.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                page = 1;
                getData();
            }
        });
    }


    @Override
    protected void requestImage(String[] path) {
        String origPath = path[0];

        if (tempImageFile != null) {
            if (tempImageFile.exists())
                tempImageFile.delete();
        }
        tempImageFile = new File(Applibrary.IMAGE_CACHE_DIR, System.currentTimeMillis() + ".jpg");
        boolean result = ImageUtil.saveBitmap(ImageUtil.compressImage(origPath, 1920, 1080), 80, tempImageFile);
        if (result) {
            progress.show();
            new Thread(new UpdateStringRun(tempImageFile.getPath())).start();
        }
    }



    /**
     * 获取数据
     */
    private void getData() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "car_driver_list");
        params.put("page", page);
        params.put("pagesize", pageSize);
        params.put("uid", getUserID());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        List<Freight> list = GsonUtil.getEntityList(dataObj.getJSONArray("items").toString(), Freight.class);
                        totalSize = dataObj.optInt("total");

                        if (page == 1) {
                            dataList.clear();
                            //recyclerData.smoothScrollToPosition(0);
                        }
                        dataList.addAll(list);
                        adapter.notifyDataSetChanged();

                        if (dataList.size() < totalSize)
                            adapter.setEnableLoadMore(true);

                        //处理推送，跳转到详情
                        if (page == 1) {
                            if (!TextUtils.isEmpty(pushId)) {
                                Intent intent = null;
                                switch (pushType) {
                                    case Const.PUSH_FREIGHT_NEW_INQUIRY://有新的询价
                                        intent = new Intent(context, ReceiveInquiryActivity.class);
                                        break;
                                    case Const.PUSH_FREIGHT_CHOOSED://司机被采纳
                                        intent = new Intent(context, DriverPayActivity.class);
                                        break;
                                }

                                intent.putExtra("id", pushId);
                                startActivity(intent);
                                pushId = "";
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (isDestroyed()) return;
                showFailTips(getResources().getString(R.string.requst_fail));
                if (page > 1)
                    page--;
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (isDestroyed())
                    return;

                ptrLayt.refreshComplete();
                adapter.loadMoreComplete();
                if (totalSize == dataList.size())
                    adapter.loadMoreEnd();

                adapter.setEmptyView(R.layout.layout_empty_view_common);
            }
        });
    }


    /**
     * 忽略订单
     */
    private void ignoreOrder() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "driver_ignore_order");
        params.put("id", item.getId());
        params.put("uid", getUserID());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        showErrorToast("忽略订单成功");
                        page = 1;
                        getData();
                    } else {
                        showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }


    /**
     * 放弃订单
     */
    private void giveupOrder() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "give_up_order");
        params.put("id", item.getId());
        params.put("offer_id", item.getOffer_list().get(0).getId());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        showErrorToast("放弃订单成功");
                        page = 1;
                        getData();
                    } else {
                        showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }


    /**
     * 报价对话框
     */
    private void showOfferDialog() {
        final AlertDialog offerDialog = new AlertDialog.Builder(context).create();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_freight_offer, null);
        offerDialog.setView(view);

        final EditText edtTxtPrice = (EditText) view.findViewById(R.id.edtTxtPrice);
        TextView txtPlace = (TextView) view.findViewById(R.id.txtTitle);
        TextView txtSubmitOffer = (TextView) view.findViewById(R.id.txtOffer);

        txtPlace.setText(item.getFrom_name() + " - " + item.getTo_name());
        txtSubmitOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String money = edtTxtPrice.getText().toString().trim();
                if (TextUtils.isEmpty(money)) {
                    showErrorToast("请输入报价金额");
                    return;
                }
                offerDialog.dismiss();
                offer(money);
            }
        });

        offerDialog.show();
    }


    /**
     * 报价
     */
    private void offer(String money) {
        RequestParams params = MyHttpClient.getApiParam("purchase", "offer_driver");
        params.put("order_id", item.getOrder_id());
        params.put("uid", getUserID());
        params.put("car_product_id", item.getId());
        params.put("money", money);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        Intent intent = new Intent(context, OfferedActivity.class);
                        intent.putExtra("id", item.getId());
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
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }


    /**
     * 获取订单信息
     */
    private void getOrderInfo() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "driver_pay");
        params.put("uid", getUserID());
        params.put("car_product_id", item.getId());
        params.put("pay_money", 0.01);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        String orderInfo = jsonObj.getString("data");
                        AlipayHelper alipayHelper = new AlipayHelper(context, item.getId());
                        alipayHelper.payV2(orderInfo);
                    } else {
                        showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (isDestroyed())
                    return;

                progress.dismiss();
            }
        });
    }


    /**
     * 初始化对话框
     */
    private void initDialog() {
        arriveDialog = new AlertDialog.Builder(context).create();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_arrived, null);
        arriveDialog.setView(view);

        imgViUpload = (ImageView) view.findViewById(R.id.imgViUpload);
        txtConfirm = (TextView) view.findViewById(R.id.txtConfirm);
        imgViUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWindowSelectList(view);
            }
        });
        txtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(arriveImgUrl)) {
                    showErrorToast("请上传货主签字照片");
                    return;
                }
                arriveDialog.dismiss();
                confirmArrived();
            }
        });


        gpsDialog = new SureOrCancelDialog(context,
                "提示",
                "您尚未开启GPS，点击确定前往设置",
                "确定",
                "取消",
                new SureOrCancelDialog.SureButtonClick() {
                    @Override
                    public void onSureButtonClick() {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });



        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_pay, null);
        popupPay = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        txtAccount = (TextView) contentView.findViewById(R.id.txtAccount);
        txtConfirmPay = (TextView) contentView.findViewById(R.id.txtConfirm);

        String account = getUser().getMobile();
        txtAccount.setText(account.substring(0, 3) + "***" + account.substring(account.length() - 4, account.length()));
        txtConfirmPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupPay.dismiss();
                getOrderInfo();
            }
        });

        popupPay.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                AppTool.setBackgroundAlpha(context, 1.0f);
            }
        });

        popupPay.setAnimationStyle(R.style.PopupAnimBottom);
        popupPay.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupPay.setOutsideTouchable(true);
        popupPay.setFocusable(true);
    }


    public class UpdateStringRun implements Runnable {
        private File upLoadBitmapFile;
        public UpdateStringRun(String newPicturePath) {
            this.upLoadBitmapFile = new File(newPicturePath);
        }

        @Override
        public void run() {
            ImageResponse retMap = null;
            try {
                String url = Url.UPLOADIMAGE;
                retMap = JsonParser.getImageResponse(HttpUtil.uploadFile(url, upLoadBitmapFile));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (retMap != null) {
                handler.sendMessage(handler.obtainMessage(3, retMap));
            } else {
                handler.sendMessage(handler.obtainMessage(NETWORK_SUCCESS_DATA_ERROR));
            }
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isDestroyed())
                return;

            progress.dismiss();
            switch (msg.what) {
                case 3://上传图片
                    ImageResponse imgResponse = (ImageResponse) msg.obj;
                    if (imgResponse.isSuccess()) {
                        arriveImgUrl = imgResponse.getData().get(0).getOrigin();
                        Glide.with(context).load(arriveImgUrl).placeholder(R.mipmap.img_error).into(imgViUpload);
                    } else {
                        showErrorToast(imgResponse.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_DATA_ERROR:
                    showErrorToast("网络超时，请稍候");
                    break;
            }
        }
    };


    /**
     * 确认送达
     */
    private void confirmArrived() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "driver_confirm_arrive");
        params.put("car_product_id", item.getId());
        params.put("uid", getUserID());
        params.put("confirm_arrive_img", arriveImgUrl);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        showErrorToast("提交成功，请等待官方确认");
                    } else {
                        showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }


    /**
     * 确认收到运费
     */
    private void confirmPayment() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "driver_confirm_payment");
        params.put("car_product_id", item.getId());
        params.put("uid", getUserID());
        params.put("car_offer_id", item.getOffer_list().get(0).getId());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        Intent intent = new Intent(context, OrderFinishDriverActivity.class);
                        intent.putExtra("id", item.getId());
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
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }

    /**
     * 更新位置
     */
    private void updateLocation(double lat, double lng, String address) {
        RequestParams params = MyHttpClient.getApiParam("purchase", "driver_position");
        params.put("uid", getUserID());
        params.put("driver_lat", lat + "");
        params.put("driver_lng", lng + "");
        params.put("place_name", address);
        params.put("car_product_id", item.getId());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        showErrorToast("更新成功");
                    } else {
                        showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }
        });
    }



    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_PAY_SUCCESS);
        filter.addAction(Const.ACTION_PAY_FAIL);
        registerReceiver(myReceiver, filter);
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Const.ACTION_PAY_SUCCESS)) {
                showErrorToast("支付成功");
                Intent intent2 = new Intent(context, GoToDeliverActivity.class);
                intent2.putExtra("id", item.getId());
                startActivity(intent2);
            } else if (action.equals(Const.ACTION_PAY_FAIL)) {
                showErrorToast("支付失败");
            }
        }
    };




    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onGetNavigationText(String s) {
        amapTTSController.onGetNavigationText(s);
    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onArriveDestination(boolean b) {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onStopSpeaking() {
        amapTTSController.stopSpeaking();
    }


    @Override
    public void finish() {
        super.finish();
        if (MainActivity.instance == null)
            startActivity(new Intent(context, MainActivity.class));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        amapTTSController.destroy();
        unregisterReceiver(myReceiver);
        adapter.cancelAllTimers();
        if (tempImageFile != null) {
            if (tempImageFile.exists())
                tempImageFile.delete();
        }
    }

}
