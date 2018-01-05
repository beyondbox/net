package com.appjumper.silkscreen.ui.my.askbuy;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.AskBuy;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.my.adapter.AskBuyOrderListAdapter;
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
 * Created by Botx on 2017/12/14.
 */

public class AskBuyOrderListFragment extends BaseFragment {

    @Bind(R.id.ptrLayt)
    PtrClassicFrameLayout ptrLayt;
    @Bind(R.id.recyclerData)
    RecyclerView recyclerData;

    private List<AskBuy> dataList;
    private AskBuyOrderListAdapter adapter;
    private AskBuy item; //当前操作的条目

    private int page = 1;
    private int pageSize = 30;
    private int totalSize;

    private String type = "";
    private Dialog payDialog;
    private String payType;

    private String pushId;
    private int pushType;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_askbuy_order_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        type = bundle.getString(Const.KEY_TYPE);
        pushId = bundle.getString("id", "");
        pushType = bundle.getInt(Const.KEY_PUSH_TYPE, 0);

        initRecyclerView();
        initRefreshLayout();
        initProgressDialog(false, null);
        initDialog();

        ptrLayt.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrLayt.autoRefresh();
            }
        }, 50);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isDataInited) {
            page = 1;
            getData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isDataInited && getUserVisibleHint()) {
            page = 1;
            getData();
        }
    }


    private void initDialog() {
        payDialog = new Dialog(context, R.style.CustomDialog);
        View contentView = LayoutInflater.from(context).inflate(R.layout.popup_payed_confirm, null);
        payDialog.setContentView(contentView);

        TextView btn0 = (TextView) contentView.findViewById(R.id.btn0);
        TextView btn1 = (TextView) contentView.findViewById(R.id.btn1);
        TextView btn2 = (TextView) contentView.findViewById(R.id.btn2);
        TextView btn3 = (TextView) contentView.findViewById(R.id.btn3);

        PayTypeClickListener listener = new PayTypeClickListener();
        btn0.setOnClickListener(listener);
        btn1.setOnClickListener(listener);
        btn2.setOnClickListener(listener);
        btn3.setOnClickListener(listener);

        Display display = context.getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams params = payDialog.getWindow().getAttributes();
        params.width = (int) (display.getWidth() * 0.7);
        payDialog.getWindow().setAttributes(params);
    }

    private class PayTypeClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn0:
                    payType = "公户";
                    break;
                case R.id.btn1:
                    payType = "工行卡";
                    break;
                case R.id.btn2:
                    payType = "微信帐户";
                    break;
                case R.id.btn3:
                    payType = "支付宝帐户";
                    break;
            }

            payDialog.dismiss();
            payConfirm();
        }
    }


    private void initRecyclerView() {
        dataList = new ArrayList<>();

        adapter = new AskBuyOrderListAdapter(R.layout.item_recycler_askbuy_order, dataList);
        recyclerData.setLayoutManager(new LinearLayoutManager(context));
        adapter.bindToRecyclerView(recyclerData);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                AskBuy askBuy = dataList.get(position);
                if (askBuy.getExamine_status().equals(Const.ASKBUY_ORDER_PAYING + "")) {
                    start_Activity(context, AskBuyOrderPayActivity.class, new BasicNameValuePair("id", askBuy.getId()));
                } else {
                    start_Activity(context, AskBuyOrderDetailActivity.class, new BasicNameValuePair("id", askBuy.getId()));
                }
            }
        });

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
                item = dataList.get(position);
                Intent intent = null;
                switch (view.getId()) {
                    case R.id.txtHandle0:
                        if (item.getExamine_status().equals(Const.ASKBUY_ORDER_AUDITING + "")) {
                            new SureOrCancelDialog(context, "提示", "确定要取消该订单吗？", "确定", "取消", new SureOrCancelDialog.SureButtonClick() {
                                @Override
                                public void onSureButtonClick() {
                                    cancelOrder(position);
                                }
                            }).show();
                        } else {
                            new SureOrCancelDialog(context, "提示", "确定要删除该订单吗？", "确定", "取消", new SureOrCancelDialog.SureButtonClick() {
                                @Override
                                public void onSureButtonClick() {
                                    deleteOrder(position);
                                }
                            }).show();
                        }
                        break;
                    case R.id.txtHandle1:
                        new SureOrCancelDialog(context, "提示", "确定要取消该订单吗？", "确定", "取消", new SureOrCancelDialog.SureButtonClick() {
                            @Override
                            public void onSureButtonClick() {
                                cancelOrder(position);
                            }
                        }).show();
                        break;
                    case R.id.txtHandle2:
                        start_Activity(context, AskBuyOrderPayActivity.class, new BasicNameValuePair("id", item.getId()));
                        break;
                    case R.id.txtHandle3:
                        payDialog.show();
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
        RequestParams params = MyHttpClient.getApiParam("purchase", "my_purchase_order_list");
        params.put("page", page);
        params.put("pagesize", pageSize);
        params.put("uid", getUserID());
        params.put("examine_status", type);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        List<AskBuy> list = GsonUtil.getEntityList(dataObj.getJSONArray("items").toString(), AskBuy.class);
                        totalSize = dataObj.optInt("total");

                        if (page == 1) {
                            dataList.clear();
                            //recyclerData.smoothScrollToPosition(0);
                        }
                        dataList.addAll(list);
                        adapter.notifyDataSetChanged();

                        if (dataList.size() < totalSize)
                            adapter.setEnableLoadMore(true);

                        //处理推送
                        if (page == 1) {
                            if (TextUtils.isEmpty(type)) {
                                if (!TextUtils.isEmpty(pushId)) {
                                    switch (pushType) {
                                        case Const.PUSH_ASKBUY_ORDER_PASS:
                                            start_Activity(context, AskBuyOrderPayActivity.class, new BasicNameValuePair("id", pushId));
                                            break;
                                        case Const.PUSH_ASKBUY_RECEIPT_REFUSE:
                                            start_Activity(context, AskBuyOrderPayActivity.class, new BasicNameValuePair("id", pushId));
                                            break;
                                        default:
                                            start_Activity(context, AskBuyOrderDetailActivity.class, new BasicNameValuePair("id", pushId));
                                            break;
                                    }
                                    pushId = "";
                                }
                            }
                        }

                    } else {
                        showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (!isViewCreated) return;
                showFailTips(getResources().getString(R.string.requst_fail));
                if (page > 1)
                    page--;
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (!isViewCreated) return;

                ptrLayt.refreshComplete();
                adapter.loadMoreComplete();
                if (totalSize == dataList.size())
                    adapter.loadMoreEnd();

                adapter.setEmptyView(R.layout.layout_empty_view_common);
            }
        });
    }


    /**
     * 取消订单
     */
    private void cancelOrder(final int position) {
        RequestParams params = MyHttpClient.getApiParam("purchase", "cancel_purchase_order");
        params.put("uid", getUserID());
        params.put("purchase_id", item.getPurchase_id());

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
                        dataList.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, dataList.size() - position);
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
     * 删除订单
     */
    private void deleteOrder(final int position) {
        RequestParams params = MyHttpClient.getApiParam("purchase", "hide_purchase_order");
        params.put("uid", getUserID());
        params.put("purchase_id", item.getPurchase_id());

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
                        dataList.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, dataList.size() - position);
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
     * 确认支付
     */
    private void payConfirm() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "confirm_purchase_order");
        params.put("uid", getUserID());
        params.put("pay_type", payType);
        params.put("purchase_id", item.getPurchase_id());

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
                        showErrorToast("确认付款成功");
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

}
