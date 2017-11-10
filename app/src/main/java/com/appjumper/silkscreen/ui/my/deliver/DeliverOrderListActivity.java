package com.appjumper.silkscreen.ui.my.deliver;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.Freight;
import com.appjumper.silkscreen.bean.FreightOffer;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.home.logistics.ReleaseFreightActivity;
import com.appjumper.silkscreen.ui.my.adapter.ChooseDriverDialogAdapter;
import com.appjumper.silkscreen.ui.my.adapter.DeliverOrderListAdapter;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.SureOrCancelDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 发货厂家--订单列表
 * Created by Botx on 2017/10/27.
 */

public class DeliverOrderListActivity extends BaseActivity {

    @Bind(R.id.ptrLayt)
    PtrClassicFrameLayout ptrLayt;
    @Bind(R.id.recyclerData)
    RecyclerView recyclerData;

    private List<Freight> dataList;
    private DeliverOrderListAdapter adapter;
    private Freight item; //当前操作的订单

    private int page = 1;
    private int pageSize = 30;
    private int totalSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliver_orderlist);
        ButterKnife.bind(context);

        initTitle("订单列表");
        initBack();
        initProgressDialog(false, null);
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

    private void initRecyclerView() {
        dataList = new ArrayList<>();

        adapter = new DeliverOrderListAdapter(R.layout.item_recycler_freight_order_list, dataList);
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
                    case Const.FREIGHT_AUDITING: //审核中
                        intent = new Intent(context, AuditingDeliverActivity.class);
                        break;
                    case Const.FREIGHT_AUDIT_REFUSE: //审核不通过
                        intent = new Intent(context, AuditRefuseActivity.class);
                        break;
                    case Const.FREIGHT_AUDIT_PASS: //司机报价中
                        intent = new Intent(context, ChooseDriverActivity.class);
                        break;
                    case Const.FREIGHT_DRIVER_PAYING: //等待司机支付
                        intent = new Intent(context, WaitDriverPayActivity.class);
                        break;
                    case Const.FREIGHT_GOTO_LOAD: //司机正在赶来
                        intent = new Intent(context, DriverComingActivity.class);
                        break;
                    case Const.FREIGHT_LOADING: //装货中
                        intent = new Intent(context, LoadingDeliverActivity.class);
                        break;
                    case Const.FREIGHT_TRANSPORTING: //运输途中
                        intent = new Intent(context, TransportingDeliverActivity.class);
                        break;
                    case Const.FREIGHT_TRANSPORT_FINISH: //运输完成
                        intent = new Intent(context, TransportFinishDeliverActivity.class);
                        break;
                    case Const.FREIGHT_ORDER_FINISH: //订单完成
                        intent = new Intent(context, OrderFinishDeliverActivity.class);
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
                        AppTool.dial(context, item.getConfirm_driver_mobile());
                        break;
                    case R.id.txtHandle1: //第二个按钮
                        switch (state) {
                            case Const.FREIGHT_AUDIT_REFUSE:
                                start_Activity(context, ReleaseFreightActivity.class);
                                break;
                            case Const.FREIGHT_TRANSPORTING:
                                start_Activity(context, TransportingDeliverActivity.class, new BasicNameValuePair("id", item.getId()));
                                break;
                            default:
                                AppTool.dial(context, Const.SERVICE_PHONE_FREIGHT);
                                break;
                        }
                        break;
                    case R.id.txtHandle2: //第三个按钮
                        switch (state) {
                            case Const.FREIGHT_AUDIT_PASS:
                                if (item.getOffer_num().equals("0"))
                                    showErrorToast("暂无司机报价");
                                else
                                    getOrderDetail(item.getId());
                                break;
                            case Const.FREIGHT_GOTO_LOAD:
                                new SureOrCancelDialog(context, "提示", "是否确认司机到达？", "确定", "取消", new SureOrCancelDialog.SureButtonClick() {
                                    @Override
                                    public void onSureButtonClick() {
                                        confirmDriverArrived();
                                    }
                                }).show();
                                break;
                            case Const.FREIGHT_LOADING:
                                start_Activity(context, LoadingDeliverActivity.class, new BasicNameValuePair("id", item.getId()));
                                break;
                            case Const.FREIGHT_TRANSPORTING:
                                new SureOrCancelDialog(context, "提示", "是否确认送达？", "确定", "取消", new SureOrCancelDialog.SureButtonClick() {
                                    @Override
                                    public void onSureButtonClick() {
                                        confirmArrived();
                                    }
                                }).show();
                                break;
                            case Const.FREIGHT_TRANSPORT_FINISH:
                                start_Activity(context, TransportFinishDeliverActivity.class, new BasicNameValuePair("id", item.getId()));
                                break;
                            case Const.FREIGHT_ORDER_FINISH:
                                start_Activity(context, ReleaseFreightActivity.class);
                                break;
                            default:
                                AppTool.dial(context, Const.SERVICE_PHONE_FREIGHT);
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


    /**
     * 获取数据
     */
    private void getData() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "car_product_list");
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
     * 获取订单详情
     */
    private void getOrderDetail(String id) {
        RequestParams params = MyHttpClient.getApiParam("purchase", "details_car_product");
        params.put("id", id);

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
                        Freight data = GsonUtil.getEntity(jsonObj.getJSONObject("data").toString(), Freight.class);
                        showChooseDriverDialog(data.getOffer_list());
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
     * 选择司机对话框
     */
    private void showChooseDriverDialog(final List<FreightOffer> offerList) {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_choose_driver, null);
        dialog.setView(view);

        ListView lvRecord = (ListView) view.findViewById(R.id.lvRecord);
        TextView txtConfirm = (TextView) view.findViewById(R.id.txtConfirm);
        final ChooseDriverDialogAdapter recordAdapter = new ChooseDriverDialogAdapter(context, offerList);
        lvRecord.setDividerHeight(0);
        lvRecord.setAdapter(recordAdapter);

        lvRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                recordAdapter.changeSelected(i);
            }
        });

        txtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                getDriverState(offerList.get(recordAdapter.selectedPosition));
            }
        });

        dialog.show();
    }


    /**
     * 获取司机状态
     */
    private void getDriverState(final FreightOffer selectedOffer) {
        RequestParams params = MyHttpClient.getApiParam("purchase", "select_driver");
        params.put("driver_id", selectedOffer.getUser_id());
        params.put("offer_id", selectedOffer.getId());

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
                        chooseDriver(selectedOffer);
                    } else {
                        progress.dismiss();
                        showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
                    }
                } catch (JSONException e) {
                    progress.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progress.dismiss();
                showFailTips(getResources().getString(R.string.requst_fail));
            }
        });
    }


    /**
     * 确认选择司机
     */
    private void chooseDriver(FreightOffer selectedOffer) {
        RequestParams params = MyHttpClient.getApiParam("purchase", "confirm_driver");
        params.put("driver_id", selectedOffer.getUser_id());
        params.put("offer_id", selectedOffer.getId());
        params.put("car_product_id", item.getId());
        params.put("driver_offer", selectedOffer.getMoney());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        Intent intent = new Intent(context, WaitDriverPayActivity.class);
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
     * 确认司机到达
     */
    private void confirmDriverArrived() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "driver_enterprise_date");
        params.put("car_product_id", item.getId());
        params.put("driver_id", item.getConfirm_driver_id());
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
                        Intent intent = new Intent(context, LoadingDeliverActivity.class);
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
     * 确认送达
     */
    private void confirmArrived() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "enterprise_confirm_arrive");
        params.put("car_product_id", item.getId());
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


}
