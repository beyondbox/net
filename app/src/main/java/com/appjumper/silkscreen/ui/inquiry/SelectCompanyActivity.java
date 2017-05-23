package com.appjumper.silkscreen.ui.inquiry;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.bean.EnterpriseSelect;
import com.appjumper.silkscreen.bean.EnterpriseSelectResponse;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.inquiry.adapter.SelectCompanyListViewAdapter;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.view.MyListView;
import com.appjumper.silkscreen.view.ObservableScrollView;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshBase;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshScrollView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yc on 2016/11/7.
 * 物流详情
 */
public class SelectCompanyActivity extends BaseActivity {


    @Bind(R.id.pull_refresh_scrollview)
    PullToRefreshScrollView mPullRefreshScrollView;
    @Bind(R.id.layout)
    LinearLayout layout;
    /**
     * 认证公司
     */
    @Bind(R.id.list_view_one)
    MyListView listViewOne;
    @Bind(R.id.ll_one)
    LinearLayout llOne;
    @Bind(R.id.iv_one)
    ImageView ivOne;
    @Bind(R.id.cb_one)
    CheckBox cbOne;
    private SelectCompanyListViewAdapter adapterOne;
    /**
     * 未认证公司
     */
    @Bind(R.id.list_view_two)
    MyListView listViewTwo;
    @Bind(R.id.ll_two)
    LinearLayout llTwo;
    @Bind(R.id.iv_two)
    ImageView ivTwo;
    @Bind(R.id.cb_two)
    CheckBox cbTwo;
    private SelectCompanyListViewAdapter adapterTwo;
    private String type;
    private ArrayList<Enterprise> resultList = new ArrayList<>();
    private List<Enterprise> auth;
    private List<Enterprise> noauth;
    private String productType;
    private String product_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_company);
        ButterKnife.bind(this);
        initBack();
        initTitle("选择公司");
        type = getIntent().getStringExtra("type");
        productType = getIntent().getStringExtra("product_type");
        product_id = getIntent().getStringExtra("product_id");
        initRightButton("完成", new RightButtonListener() {
            @Override
            public void click() {
                long[] choicesOne = getListCheckedItemIds(listViewOne);
                long[] choicesTwo = getListCheckedItemIds(listViewTwo);
                if (choicesOne.length < 1 && choicesTwo.length < 1) {
                    showErrorToast("请至少选择一家公司！");
                    return;
                }
                for (int i = 0; i < choicesOne.length; i++) {
                    resultList.add(auth.get((int) choicesOne[i]));
                }
                for (int i = 0; i < choicesTwo.length; i++) {
                    resultList.add(noauth.get((int) choicesTwo[i]));
                }
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", resultList);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();

            }
        });
        listerrefresh();
        initProgressDialog();
        progress.show();
        progress.setMessage("正在加载...");

        refresh();
        mPullRefreshScrollView.scrollTo(0, 0);
        listViewOne.setFocusable(false);
        listViewTwo.setFocusable(false);
    }

    private void refresh() {
        mPullRefreshScrollView.setRefreshing();
        new Thread(run).start();
    }

    private void listerrefresh() {
        mPullRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ObservableScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ObservableScrollView> refreshView) {
                new Thread(run).start();
            }
        });
    }

    //详情
    private Runnable run = new Runnable() {
        private EnterpriseSelectResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("type", type);
//                data.put("product_type", productType);
                data.put("product_id", product_id);
                response = JsonParser.getEnterpriseSelectResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.INQUIRY_ENTERPRISE));
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
            SelectCompanyActivity activity = (SelectCompanyActivity) reference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT://详情
                    EnterpriseSelectResponse baseResponse = (EnterpriseSelectResponse) msg.obj;
                    if (baseResponse.isSuccess()) {
                        layout.setVisibility(View.VISIBLE);
                        EnterpriseSelect data = baseResponse.getData();
                        if (data != null) {
                            if (data.getAuth() != null && data.getAuth().size() > 0) {
                                auth = data.getAuth();
                                initOne(auth);
                            }
                            if (data.getNoauth() != null && data.getNoauth().size() > 0) {
                                noauth = data.getNoauth();
                                initTwo(noauth);
                            }
                        }
                    } else {
                        showErrorToast(baseResponse.getError_desc());
                    }
                    break;
                case NETWORK_FAIL:
                    activity.showErrorToast();
                    break;
            }
        }
    }

    /**
     * 认证公司
     */
    private void initOne(final List<Enterprise> auth) {
        cbOne.setText("认证公司（"+auth.size()+")");
        listViewOne.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        adapterOne = new SelectCompanyListViewAdapter(this, listViewOne, auth);
        listViewOne.setAdapter(adapterOne);
        listViewOne.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapterOne.notifyDataSetChanged();
            }
        });
    }

    /**
     * 未认证公司
     */
    private void initTwo(List<Enterprise> noauth) {
        cbTwo.setText("未认证公司（"+noauth.size()+")");
        listViewTwo.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        adapterTwo = new SelectCompanyListViewAdapter(this, listViewTwo, noauth);
        listViewTwo.setAdapter(adapterTwo);
        listViewTwo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapterTwo.notifyDataSetChanged();
            }
        });
    }


    @OnClick({R.id.rl_one, R.id.rl_two, R.id.cb_one, R.id.cb_two})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_one:
                if (llOne.getVisibility() == View.GONE) {
                    llOne.setVisibility(View.VISIBLE);
                    ivOne.setImageResource(R.mipmap.icon_arrows_up_01);
                } else if (llOne.getVisibility() == View.VISIBLE) {
                    llOne.setVisibility(View.GONE);
                    ivOne.setImageResource(R.mipmap.icon_arrows_down_01);
                }
                break;
            case R.id.rl_two:
                if (llTwo.getVisibility() == View.GONE) {
                    llTwo.setVisibility(View.VISIBLE);
                    ivTwo.setImageResource(R.mipmap.icon_arrows_up_01);
                } else if (llTwo.getVisibility() == View.VISIBLE) {
                    llTwo.setVisibility(View.GONE);
                    ivTwo.setImageResource(R.mipmap.icon_arrows_down_01);
                }
                break;
            case R.id.cb_one:
                if (cbOne.isChecked()) {
                    for (int i = 0; i < adapterOne.getCount(); i++) {
                        listViewOne.setItemChecked(i, true);
                    }
                } else {
                    for (int i = 0; i < adapterOne.getCount(); i++) {
                        listViewOne.setItemChecked(i, false);
                    }
                }
                break;
            case R.id.cb_two:
                if (cbTwo.isChecked()) {
                    for (int i = 0; i < adapterTwo.getCount(); i++) {
                        listViewTwo.setItemChecked(i, true);
                    }
                } else {
                    for (int i = 0; i < adapterTwo.getCount(); i++) {
                        listViewTwo.setItemChecked(i, false);
                    }
                }
                break;
            default:
                break;
        }
    }

    public long[] getListCheckedItemIds(ListView listView) {

        long[] ids = new long[listView.getCount()];//getCount()即获取到ListView所包含的item总个数
        //定义用户选中Item的总个数
        int checkedTotal = 0;
        for (int i = 0; i < listView.getCount(); i++) {
            //如果这个Item是被选中的
            if (listView.isItemChecked(i)) {
                ids[checkedTotal] = i;
                checkedTotal++;
            }
        }

        if (checkedTotal < listView.getCount()) {
            //定义选中的Item的ID数组
            long[] selectedIds = new long[checkedTotal];
            //数组复制 ids
            System.arraycopy(ids, 0, selectedIds, 0, checkedTotal);
            return selectedIds;
        } else {
            //用户将所有的Item都选了
            return ids;
        }
    }
}
