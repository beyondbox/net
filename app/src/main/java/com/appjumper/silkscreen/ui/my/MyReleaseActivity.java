package com.appjumper.silkscreen.ui.my;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.bean.MyRelease;
import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.PlusActivity;
import com.appjumper.silkscreen.ui.home.equipment.EquipmentDetailsActivity;
import com.appjumper.silkscreen.ui.home.logistics.LogisticsDetailsActivity;
import com.appjumper.silkscreen.ui.home.logistics.TruckDetailsActivity;
import com.appjumper.silkscreen.ui.home.process.ProcessingDetailsActivity;
import com.appjumper.silkscreen.ui.home.recruit.RecruitDetailsActivity;
import com.appjumper.silkscreen.ui.home.stock.StockDetailActivity;
import com.appjumper.silkscreen.ui.home.workshop.WorkshopDetailsActivity;
import com.appjumper.silkscreen.ui.my.adapter.MyReleaseAdapter;
import com.appjumper.silkscreen.ui.my.askbuy.AskBuyEditActivity;
import com.appjumper.silkscreen.ui.my.askbuy.AskBuyOrderDetailActivity;
import com.appjumper.silkscreen.ui.my.askbuy.ChooseOfferActivity;
import com.appjumper.silkscreen.ui.my.enterprise.AddServiceCompleteActivity;
import com.appjumper.silkscreen.ui.my.enterprise.EnterpriseAuthFirstepActivity;
import com.appjumper.silkscreen.ui.my.enterprise.EnterpriseCreateActivity;
import com.appjumper.silkscreen.ui.my.enterprise.ProductivityAuthenticationActivity;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.MyAlertDialog;
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
 * 我的发布
 * Created by Botx on 2017/7/31.
 */

public class MyReleaseActivity extends BaseActivity {

    @Bind(R.id.ptrLayt)
    PtrClassicFrameLayout ptrLayt;
    @Bind(R.id.recyclerData)
    RecyclerView recyclerData;
    @Bind(R.id.txtHint)
    TextView txtHint;

    private List<MyRelease> dataList;
    private MyReleaseAdapter adapter;

    private int page = 1;
    private int pageSize = 20;
    private int totalSize;

    private SureOrCancelDialog certifyDialog;
    private MyAlertDialog deleteDialog;
    private int deleteIndex = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_release);
        ButterKnife.bind(context);
        registerBroadcastReceiver();

        initTitle("我的发布");
        initBack();
        initRightButton("发布信息", new RightButtonListener() {
            @Override
            public void click() {
                Intent intent = new Intent(context, PlusActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.bottom_to_top, R.anim.no_change);
            }
        });

        initRecyclerView();
        initRefreshLayout();
        initDeleteDialog();
        initCertifyDialog();

        if (AddServiceCompleteActivity.instance != null)
            AddServiceCompleteActivity.instance.finish();

        ptrLayt.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrLayt.autoRefresh();
            }
        }, 50);
    }


    private void initRecyclerView() {
        dataList = new ArrayList<>();

        adapter = new MyReleaseAdapter(R.layout.item_recycler_my_release, dataList);
        recyclerData.setLayoutManager(new LinearLayoutManager(context));
        adapter.bindToRecyclerView(recyclerData);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MyRelease item = dataList.get(position);
                switch (item.getType()) {
                    case "1"://定做
                        start_Activity(context, ProcessingDetailsActivity.class, new BasicNameValuePair("title", item.getTitle() + item.getSubtitle()), new BasicNameValuePair("id", item.getInfo_id()));
                        break;
                    case "2"://加工
                        start_Activity(context, ProcessingDetailsActivity.class, new BasicNameValuePair("title", item.getTitle() + item.getSubtitle()), new BasicNameValuePair("id", item.getInfo_id()));
                        break;
                    case "3"://现货
                        start_Activity(context, StockDetailActivity.class, new BasicNameValuePair("title", item.getTitle() + item.getSubtitle()), new BasicNameValuePair("id", item.getInfo_id()));
                        break;
                    case "4"://物流
                        start_Activity(context, LogisticsDetailsActivity.class, new BasicNameValuePair("id", item.getInfo_id()), new BasicNameValuePair("type", "1"));
                        break;
                    case "5"://设备
                        start_Activity(context, EquipmentDetailsActivity.class, new BasicNameValuePair("id", item.getInfo_id()));
                        break;
                    case "6"://找车
                        start_Activity(context, TruckDetailsActivity.class, new BasicNameValuePair("title", "详情"), new BasicNameValuePair("id", item.getInfo_id()));
                        break;
                    case "7"://招聘
                        start_Activity(context, RecruitDetailsActivity.class, new BasicNameValuePair("id", item.getInfo_id()));
                        break;
                    case "8"://厂房
                        start_Activity(context, WorkshopDetailsActivity.class, new BasicNameValuePair("id", item.getInfo_id()));
                        break;
                }
            }
        });

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                MyRelease item = dataList.get(position);
                switch (view.getId()) {
                    case R.id.txtRefresh: //刷新排名或者编辑
                        if (item.getType().equals("9")) {
                            Intent intent = new Intent(context, AskBuyEditActivity.class);
                            intent.putExtra(Const.KEY_OBJECT, item);
                            startActivity(intent);
                        } else {
                            refreshRank(item);
                        }
                        break;
                    case R.id.txtDelete: //删除
                        deleteIndex = position;
                        deleteDialog.show();
                        break;
                    case R.id.txtHandle: //求购信息的处理
                        int state = Integer.valueOf(item.getExamine_status());
                        Intent intent = null;
                        switch (state) {
                            case Const.ASKBUY_OFFERING: //报价中，查看报价
                                intent = new Intent(context, ChooseOfferActivity.class);
                                intent.putExtra("id", item.getInfo_id());
                                break;
                            case Const.ASKBUY_PAYED_SUB: //已付订金，查看订单
                                intent = new Intent(context, AskBuyOrderDetailActivity.class);
                                intent.putExtra("id", item.getInfo_id());
                                break;
                            case Const.ASKBUY_PAYED_ALL: //已付全额，查看订单
                                intent = new Intent(context, AskBuyOrderDetailActivity.class);
                                intent.putExtra(Const.KEY_IS_READ_MODE, true);
                                intent.putExtra("id", item.getInfo_id());
                                break;
                            default: //其余都是查看订单
                                intent = new Intent(context, AskBuyOrderDetailActivity.class);
                                intent.putExtra(Const.KEY_IS_READ_MODE, true);
                                intent.putExtra("id", item.getInfo_id());
                                break;
                        }
                        if (intent != null)
                            startActivity(intent);
                        break;
                    default:
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
     * 刷新排名
     */
    private void refreshRank(MyRelease item) {
        int count = Integer.valueOf(item.getRenovate_count());
        if (count < 5) {
            Enterprise enterprise = getUser().getEnterprise();
            if (count == 3) {
                if (enterprise != null && enterprise.getEnterprise_productivity_auth_status().equals("2"))
                    showRefreshDialog(item, 2);
                else
                    certifyDialog.show();
            } else {
                int leftCount;
                if (enterprise != null && enterprise.getEnterprise_productivity_auth_status().equals("2"))
                    leftCount = 5 - count;
                else
                    leftCount = 3 - count;

                showRefreshDialog(item, leftCount);
            }
        } else {
            showErrorToast("该信息今日刷新5次，已达上限！");
        }
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
     * 初始化删除对话框
     */
    private void initDeleteDialog() {
        MyAlertDialog.Builder builder = new MyAlertDialog.Builder(context);
        builder.setMessage("确定要删除吗？信息删除后将不能在平台展示！")
                .setIcon(R.mipmap.delete)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyRelease item = dataList.get(deleteIndex);
                        delete(item.getId(), item.getInfo_id(), item.getType());
                    }
                });

        deleteDialog = builder.create();
    }


    /**
     * 初始化认证对话框
     */
    private void initCertifyDialog() {
        certifyDialog = new SureOrCancelDialog(context, "刷新次数达到上限", "该信息今日刷新3次，已达上限。请完成企业认证获取更多刷新机会", "去认证", "取消",
                new SureOrCancelDialog.SureButtonClick() {
                    @Override
                    public void onSureButtonClick() {
                        User user = getUser();
                        if (user.getEnterprise() == null) {
                            start_Activity(context, EnterpriseCreateActivity.class, new BasicNameValuePair("type", "0"));
                            return;
                        }
                        if (!user.getEnterprise().getEnterprise_auth_status().equals("2")) {
                            start_Activity(context, EnterpriseAuthFirstepActivity.class);
                            return;
                        }
                        if (!user.getEnterprise().getEnterprise_productivity_auth_status().equals("2")) {
                            start_Activity(context, ProductivityAuthenticationActivity.class);
                            return;
                        }
                    }
                });
    }


    /**
     * 弹出刷新提示框
     */
    private void showRefreshDialog(final MyRelease item, int leftCount) {
        SureOrCancelDialog refreshDialog = new SureOrCancelDialog(context,
                "提示",
                "本次刷新将消耗10积分，该信息今日已刷新" + item.getRenovate_count() + "次，剩余" + leftCount + "次刷新机会",
                "确定",
                "取消",
                new SureOrCancelDialog.SureButtonClick() {
                    @Override
                    public void onSureButtonClick() {
                        refresh(item.getInfo_id(), item.getType(), item.getId());
                    }
                });

        refreshDialog.show();
    }


    /**
     * 获取列表数据
     */
    private void getData() {
        RequestParams params = MyHttpClient.getApiParam("enterprise", "myRelease");
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
                        List<MyRelease> list = GsonUtil.getEntityList(dataObj.getJSONArray("newpublic").toString(), MyRelease.class);
                        totalSize = dataObj.optInt("total");

                        if (page == 1) {
                            dataList.clear();
                            //recyclerData.smoothScrollToPosition(0);
                            /*if (list.size() > 0) {
                                adapter.removeAllHeaderView();
                                adapter.addHeaderView(LayoutInflater.from(context).inflate(R.layout.layout_header_view_my_release, null));
                            }*/
                        }
                        dataList.addAll(list);
                        adapter.notifyDataSetChanged();

                        if (dataList.size() < totalSize)
                            adapter.setEnableLoadMore(true);

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

                if (dataList.size() == 0)
                    txtHint.setVisibility(View.INVISIBLE);
                else
                    txtHint.setVisibility(View.VISIBLE);
            }
        });
    }


    /**
     * 刷新排名接口
     */
    private void refresh(String info_id, String info_type, String id) {
        RequestParams params = MyHttpClient.getApiParam("user", "clearIntegral");
        params.put("uid", getUserID());
        params.put("info_id", info_id);
        params.put("info_type", info_type);
        params.put("id", id);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        showErrorToast("刷新成功");
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
        });
    }


    /**
     * 删除
     */
    private void delete(String id, String info_id, String type) {
        RequestParams params = MyHttpClient.getApiParam("service", "myDelete");
        params.put("uid", getUserID());
        params.put("id", id);
        params.put("info_id", info_id);
        params.put("type", type);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        dataList.remove(deleteIndex);
                        adapter.notifyItemRemoved(deleteIndex);
                        adapter.notifyItemRangeChanged(deleteIndex, dataList.size() - deleteIndex);
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
        filter.addAction(Const.ACTION_RELEASE_SUCCESS);
        registerReceiver(myReceiver, filter);
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Const.ACTION_RELEASE_SUCCESS)) {
                ptrLayt.autoRefresh();
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

}
