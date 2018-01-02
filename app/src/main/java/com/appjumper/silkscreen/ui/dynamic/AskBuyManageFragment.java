package com.appjumper.silkscreen.ui.dynamic;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.AskBuy;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.dynamic.adapter.AskBuyManageAdapter;
import com.appjumper.silkscreen.ui.my.askbuy.AskBuyEditActivity;
import com.appjumper.silkscreen.ui.my.askbuy.AskBuyOrderListActivity;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.DisplayUtil;
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
import butterknife.OnClick;

/**
 * 求购管理
 * Created by Botx on 2017/10/17.
 */

public class AskBuyManageFragment extends BaseFragment {

    @Bind(R.id.ptrLayt)
    PtrClassicFrameLayout ptrLayt;
    @Bind(R.id.recyclerData)
    RecyclerView recyclerData;
    @Bind(R.id.btn1)
    TextView btn1;

    private List<AskBuy> dataList;
    private AskBuyManageAdapter adapter;
    private AskBuy item; //当前操作的条目

    private int page = 1;
    private int pageSize = 30;
    private int totalSize;

    private PopupWindow popupDelete;
    private String pushId;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_askbuy_manage, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) pushId = bundle.getString("id");

        initRecyclerView();
        initRefreshLayout();
        initProgressDialog(false, null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initDialog();
            }
        }, 200);

        ptrLayt.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrLayt.autoRefresh();
            }
        }, 50);
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
        View contentView = LayoutInflater.from(context).inflate(R.layout.popup_delete_batch, null);
        popupDelete = new PopupWindow(contentView, btn1.getWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView btn2 = (TextView) contentView.findViewById(R.id.btn2);
        TextView btn3 = (TextView) contentView.findViewById(R.id.btn3);
        TextView btn4 = (TextView) contentView.findViewById(R.id.btn4);

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBatch(0);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBatch(1);
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupDelete.dismiss();
            }
        });

        popupDelete.setAnimationStyle(R.style.PopupAnimBottom);
        popupDelete.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupDelete.setOutsideTouchable(true);
        popupDelete.setFocusable(true);
    }


    private void initRecyclerView() {
        dataList = new ArrayList<>();

        adapter = new AskBuyManageAdapter(R.layout.item_recycler_askbuy_manage, dataList);
        recyclerData.setLayoutManager(new LinearLayoutManager(context));
        adapter.bindToRecyclerView(recyclerData);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                start_Activity(context, AskBuyManageDetailActivity.class, new BasicNameValuePair("id", dataList.get(position).getId()));
            }
        });

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
                item = dataList.get(position);
                Intent intent = null;
                switch (view.getId()) {
                    case R.id.txtHandle:
                        int status = Integer.valueOf(item.getExamine_status());
                        switch (status) {
                            case Const.ASKBUY_AUDITING: //审核中
                                new SureOrCancelDialog(context, "提示", "确定要取消该求购信息吗？", "确定", "取消", new SureOrCancelDialog.SureButtonClick() {
                                    @Override
                                    public void onSureButtonClick() {
                                        deleteInfo(position);
                                    }
                                }).show();
                                break;
                            case Const.ASKBUY_REFUSE: //审核失败
                                intent = new Intent(context, AskBuyEditActivity.class);
                                intent.putExtra("id", item.getId());
                                startActivity(intent);
                                break;
                            case Const.ASKBUY_OFFERING: //报价中和报价结束
                                long expiryTime = AppTool.getTimeMs(item.getExpiry_date(), "yy-MM-dd HH:mm:ss");
                                if (System.currentTimeMillis() < expiryTime) {
                                    start_Activity(context, AskBuyManageDetailActivity.class, new BasicNameValuePair("id", item.getId()));
                                } else {
                                    start_Activity(context, AskBuyEditActivity.class, new BasicNameValuePair("id", item.getId()));
                                }
                                break;
                        }
                        break;
                    case R.id.txtHandle1: //报价结束删除信息
                        new SureOrCancelDialog(context, "提示", "确定要删除该求购信息吗？", "确定", "取消", new SureOrCancelDialog.SureButtonClick() {
                            @Override
                            public void onSureButtonClick() {
                                deleteInfo(position);
                            }
                        }).show();
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
        RequestParams params = MyHttpClient.getApiParam("purchase", "my_purchase_list");
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
                            if (!TextUtils.isEmpty(pushId)) {
                                start_Activity(context, AskBuyManageDetailActivity.class, new BasicNameValuePair("id", pushId));
                                pushId = "";
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
     * 删除求购
     */
    private void deleteInfo(final int position) {
        RequestParams params = MyHttpClient.getApiParam("purchase", "cancel_purchase");
        params.put("purchase_id", item.getId());
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
     * 批量删除
     */
    private void deleteBatch(int type) {
        RequestParams params = MyHttpClient.getApiParam("purchase", "cancel_batch_purchase");
        params.put("type", type);
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
                        ptrLayt.autoRefresh();
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


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (getView() != null) {
            getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
        }
    }


    @OnClick({R.id.btn0, R.id.btn1})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn0:
                start_Activity(context, AskBuyOrderListActivity.class);
                break;
            case R.id.btn1:
                if (dataList.size() == 0) return;
                popupDelete.showAtLocation(btn1, Gravity.BOTTOM | Gravity.RIGHT, DisplayUtil.dip2px(context, 5), btn1.getHeight() + DisplayUtil.dip2px(context, 7));
                break;
        }
    }

}
