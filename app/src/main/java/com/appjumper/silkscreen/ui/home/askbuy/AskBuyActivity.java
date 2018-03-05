package com.appjumper.silkscreen.ui.home.askbuy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.base.MyApplication;
import com.appjumper.silkscreen.bean.AskBuy;
import com.appjumper.silkscreen.bean.HotInquiry;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.MainActivity;
import com.appjumper.silkscreen.ui.common.ProductSelectActivity;
import com.appjumper.silkscreen.ui.home.adapter.AskBuyFilterAdapter;
import com.appjumper.silkscreen.ui.home.adapter.AskBuyListAdapter;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.DisplayUtil;
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
 * 求购公共列表
 * Created by Botx on 2018/1/11.
 */

public class AskBuyActivity extends BaseActivity {

    @Bind(R.id.ptrLayt)
    PtrClassicFrameLayout ptrLayt;
    @Bind(R.id.recyclerData)
    RecyclerView recyclerData;
    @Bind(R.id.llAll)
    LinearLayout llAll;
    @Bind(R.id.markAll)
    View markAll;
    @Bind(R.id.recyclerFilter)
    RecyclerView recyclerFilter;
    @Bind(R.id.llFilter)
    LinearLayout llFilter;
    @Bind(R.id.imgViCall)
    ImageView imgViCall;

    private List<AskBuy> dataList;
    private AskBuyListAdapter adapter;

    private List<HotInquiry> filterList;
    private AskBuyFilterAdapter filterAdapter;

    private int page = 1;
    private int pageSize = 30;
    private int totalPage;
    private String productId = "";

    private int lastX;
    private int lastY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_askbuy);
        ButterKnife.bind(context);

        registerBroadcastReceiver();
        initTitle("求购");
        initBack();
        initRightButton("管理", new RightButtonListener() {
            @Override
            public void click() {
                if (checkLogined())
                    start_Activity(context, AskBuyManageActivity.class);
            }
        });

        initRecyclerView();
        initRefreshLayout();
        ptrLayt.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrLayt.autoRefresh();
            }
        }, 80);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setHoverButton();
            }
        }, 200);
    }


    private void initRecyclerView() {
        dataList = new ArrayList<>();
        filterList = new ArrayList<>();

        adapter = new AskBuyListAdapter(R.layout.item_recycler_ask_buy_list, dataList);
        recyclerData.setLayoutManager(new LinearLayoutManager(context));
        adapter.bindToRecyclerView(recyclerData);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                start_Activity(context, AskBuyDetailActivity.class, new BasicNameValuePair("id", dataList.get(position).getId()));
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.txtOffer:
                        if (checkLogined()) {
                            if (!MyApplication.appContext.checkMobile(context)) return;
                            Intent intent = new Intent(context, ReleaseOfferActivity.class);
                            intent.putExtra(Const.KEY_OBJECT, dataList.get(position));
                            startActivity(intent);
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

        //设置顶部筛选
        filterAdapter = new AskBuyFilterAdapter(R.layout.item_recycler_askbuy_filter, filterList);
        recyclerFilter.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        filterAdapter.bindToRecyclerView(recyclerFilter);
        filterAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                filterAdapter.changeSelected(position);
                recyclerFilter.smoothScrollToPosition(position);
                llAll.setSelected(false);
                markAll.setVisibility(View.INVISIBLE);
                productId = filterList.get(position).getProduct_id();
                ptrLayt.autoRefresh();
            }
        });
        llAll.setSelected(true);
    }


    /**
     * 设置下拉刷新
     */
    private void initRefreshLayout() {
        ptrLayt.disableWhenHorizontalMove(true);
        ptrLayt.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getFilter();
                page = 1;
                getData();
            }
        });
    }


    /**
     * 设置联系客服悬浮按钮
     */
    private void setHoverButton() {
        final int width = imgViCall.getWidth();
        final int height = imgViCall.getHeight();

        int padding = DisplayUtil.dip2px(context, 5);
        final int boundaryL = padding;
        final int boundaryR = getWindowManager().getDefaultDisplay().getWidth() - padding;
        final int boundaryT = padding;
        final int boundaryB = llFilter.getHeight() + ptrLayt.getHeight();

        imgViCall.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        imgViCall.setClickable(false);
                        int dx =(int)event.getRawX() - lastX;
                        int dy =(int)event.getRawY() - lastY;

                        int left = imgViCall.getLeft() + dx;
                        int top = imgViCall.getTop() + dy;
                        int right = imgViCall.getRight() + dx;
                        int bottom = imgViCall.getBottom() + dy;
                        if(left < boundaryL){
                            left = boundaryL;
                            right = left + width;
                        }
                        if(right > boundaryR){
                            right = boundaryR;
                            left = right - width;
                        }
                        if(top < boundaryT){
                            top = boundaryT;
                            bottom = top + height;
                        }
                        if(bottom > boundaryB){
                            bottom = boundaryB;
                            top = bottom - height;
                        }
                        imgViCall.layout(left, top, right, bottom);
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(DisplayUtil.dip2px(context, 90), RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(imgViCall.getLeft(), imgViCall.getTop(), 0, 0);
                        imgViCall.setLayoutParams(params);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                imgViCall.setClickable(true);
                            }
                        }, 20);
                        break;
                    default:
                        break;
                }

                return false;
            }
        });
    }


    /**
     * 获取顶部筛选数据
     */
    private void getFilter() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "purchase_product_list");
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        List<HotInquiry> list = GsonUtil.getEntityList(jsonObj.getJSONArray("data").toString(), HotInquiry.class);
                        filterList.clear();
                        filterList.addAll(list);
                        filterAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }


    /**
     * 获取数据
     */
    private void getData() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "new_purchase_list");
        params.put("product_id", productId);
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
                        List<AskBuy> list = GsonUtil.getEntityList(dataObj.getJSONObject("items").getJSONArray("purchase").toString(), AskBuy.class);
                        totalPage = dataObj.optInt("totalpage");

                        if (page == 1) {
                            dataList.clear();
                            if (recyclerData != null) recyclerData.smoothScrollToPosition(0);
                        }
                        dataList.addAll(list);

                        if (page == totalPage || totalPage == 0) {
                            List<AskBuy> endList = GsonUtil.getEntityList(dataObj.getJSONObject("items").getJSONArray("purchase_expiry").toString(), AskBuy.class);
                            dataList.addAll(endList);
                        }

                        adapter.notifyDataSetChanged();

                        if (page < totalPage)
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
                if (isDestroyed()) return;

                ptrLayt.refreshComplete();
                adapter.loadMoreComplete();
                if (totalPage == page)
                    adapter.loadMoreEnd();

                adapter.setEmptyView(R.layout.layout_empty_view_common);
            }
        });
    }


    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_ADD_READ_NUM);
        filter.addAction(Const.ACTION_RELEASE_SUCCESS);
        registerReceiver(myReceiver, filter);
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Const.ACTION_ADD_READ_NUM)) {
                String askId = intent.getStringExtra("id");
                for (int i = 0; i < dataList.size(); i++) {
                    AskBuy askBuy = dataList.get(i);
                    if (askId.equals(askBuy.getId())) {
                        dataList.get(i).setConsult_num((Integer.valueOf(askBuy.getConsult_num()) + 1) + "");
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            } else if (action.equals(Const.ACTION_RELEASE_SUCCESS)) {
                getFilter();
                page = 1;
                getData();
            }
        }
    };


    @OnClick({R.id.llAll, R.id.imgViCall, R.id.txtRelease})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.llAll: //全部
                llAll.setSelected(true);
                markAll.setVisibility(View.VISIBLE);
                filterAdapter.changeSelected(-1);
                productId = "";
                ptrLayt.autoRefresh();
                break;
            case R.id.imgViCall: //联系客服
                AppTool.dial(context, Const.SERVICE_PHONE_ASKBUY);
                break;
            case R.id.txtRelease: //发布求购
                if (checkLogined()) {
                    if (!MyApplication.appContext.checkMobile(context)) return;
                    intent = new Intent(context, ProductSelectActivity.class);
                    intent.putExtra(Const.KEY_SERVICE_TYPE, Const.SERVICE_TYPE_PRODUCT_ALL);
                    intent.putExtra(Const.KEY_MOTION, ProductSelectActivity.MOTION_RELEASE_ASKBUY);
                    startActivity(intent);
                }
                break;
        }
    }


    @Override
    public void finish() {
        super.finish();
        if (MainActivity.instance == null) {
            start_Activity(context, MainActivity.class);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

}
