package com.appjumper.silkscreen.ui.money;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.MyInquiry;
import com.appjumper.silkscreen.bean.MyInquiryResponse;
import com.appjumper.silkscreen.bean.Myoffer;
import com.appjumper.silkscreen.bean.MyofferResponse;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.MainActivity;
import com.appjumper.silkscreen.ui.money.adapter.InquiryAdapter;
import com.appjumper.silkscreen.ui.money.adapter.OfferAdapter;
import com.appjumper.silkscreen.view.MyAlertDialog;
import com.appjumper.silkscreen.view.pulltorefresh.PagedListView;
import com.appjumper.silkscreen.view.pulltorefresh.PullToRefreshBase;
import com.appjumper.silkscreen.view.pulltorefresh.PullToRefreshPagedListView;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.appjumper.silkscreen.R.id.map;

/**
 * 消息
 * Created by Botx on 2017/3/21.
 */

public class MessageActivity extends BaseActivity {

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        initBack();
        initTitle("消息");

        FragmentTransaction ftr = getSupportFragmentManager().beginTransaction();
        ftr.add(R.id.laytContent, new MoneyFragment());
        //ftr.replace(R.id.lLaytContent, new MoneyFragment());
        ftr.commit();
    }*/




    @Bind(R.id.listview)
    PullToRefreshPagedListView pullToRefreshView;

    @Bind(R.id.rg_tab)
    RadioGroup rg;

    private PagedListView listView;
    private View mEmptyLayout;
    private View myEmptyLayout;
    private String pagesize = "30";
    private int pageNumber = 1;

    private String type = "1";//我的报价或者我的询价
    private InquiryAdapter inquiryAdapter;
    private List<MyInquiry> inquiryList;
    private OfferAdapter offerAdapter;
    private String inquiryId = ""; //询价id

    private MyAlertDialog invalidDialog;

    private List<Myoffer> filterOfferList = new ArrayList<>(); //过滤后的我的报价数据


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);

        initBack();
        initTitle("消息");
        initInvalidDialog();

        mEmptyLayout = LayoutInflater.from(context).inflate(R.layout.pull_listitem_empty_padding, null);
        pullToRefreshView.setEmptyView(mEmptyLayout);
        listView = pullToRefreshView.getRefreshableView();
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectRadioBtn();
            }
        });

        initListener();

        Intent intent = getIntent();
        if (intent.hasExtra("id")) {
            inquiryId = intent.getStringExtra("id");
            rg.check(R.id.rd1);
        } else {
            if (getUser() != null) {
                refresh();
                if (!getMyApplication().getMyUserManager().getInvalidInquiryOption())
                    invalidDialog.show();
            }
        }

    }


    private void refresh() {
        pullToRefreshView.setRefreshing();
        new Thread(run).start();
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (getUser() == null) {
            if (inquiryList != null) {
                inquiryList.clear();
                inquiryAdapter.notifyDataSetChanged();
            }
            if (filterOfferList.size() > 0) {
                filterOfferList.clear();
                offerAdapter.notifyDataSetChanged();
            }
            mEmptyLayout = LayoutInflater.from(context).inflate(R.layout.activity_money_login, null);
            pullToRefreshView.setEmptyView(mEmptyLayout);
            TextView tvlogin = (TextView) mEmptyLayout.findViewById(R.id.tv_login);
            tvlogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkLogined();
                }
            });
        } else {
            pullToRefreshView.setEmptyView(myEmptyLayout);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getUser() != null) {
            new Thread(run).start();
        }
    }


    /**
     * 监听
     */
    public void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - 1;

                switch (type) {
                    case "1":
                        if (filterOfferList != null && filterOfferList.size() > 0) {
                            start_Activity(context, OfferDetailsActivity.class, new BasicNameValuePair("id", filterOfferList.get(position).getId()));
                        }
                        break;
                    case "2":
                        if (inquiryList.get(position).getOffer_num() != null && !inquiryList.get(position).getOffer_num().equals("") && Integer.parseInt(inquiryList.get(position).getOffer_num()) > 0) {
                            start_Activity(context, InquiryDetailsAlreadyActivity.class, new BasicNameValuePair("id", inquiryList.get(position).getId()), new BasicNameValuePair("title", inquiryList.get(position).getProduct_name()));
                        } else {
                            start_Activity(context, InquiryDetailsNoneActivity.class, new BasicNameValuePair("id", inquiryList.get(position).getId()), new BasicNameValuePair("title", inquiryList.get(position).getProduct_name()));
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        listView.setOnLoadMoreListener(new PagedListView.OnLoadMoreListener() {
            @Override
            public void onLoadMoreItems() {
                if (getUser() != null) {
                    new Thread(pageRun).start();
                } else {
                    pullToRefreshView.onRefreshComplete();
                }

            }
        });
        pullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
                if (getUser() != null) {
                    new Thread(run).start();
                } else {
                    pullToRefreshView.onRefreshComplete();
                }
            }

            @Override
            public void onPullUpToRefresh() {

            }
        });
    }

    private Runnable run = new Runnable() {

        public void run() {
            if (type.equals("1")) {
                MyofferResponse response = null;
                try {
                    HashMap<String, String> data = new HashMap<String, String>();
                    data.put("g", "api");
                    data.put("m", "inquiry");
                    data.put("a", "my_offer");

                    data.put("pagesize", pagesize);
                    data.put("page", "1");
                    data.put("uid", getUserID());
                    response = JsonParser.getMyofferResponse(HttpUtil.getMsg(Url.HOST + "?" + HttpUtil.getData(data)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response != null) {
                    handler.sendMessage(handler.obtainMessage(
                            3, response));
                } else {
                    handler.sendEmptyMessage(NETWORK_FAIL);
                }
            } else {
                MyInquiryResponse response = null;
                try {
                    HashMap<String, String> data = new HashMap<String, String>();
                    data.put("g", "api");
                    data.put("m", "inquiry");
                    data.put("a", "my_inquiry");

                    data.put("pagesize", pagesize);
                    data.put("page", "1");
                    data.put("uid", getUserID());
                    response = JsonParser.getMyInquiryResponse(HttpUtil.getMsg(Url.HOST + "?" + HttpUtil.getData(data)));
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

        }
    };

    private Runnable pageRun = new Runnable() {
        @SuppressWarnings("unchecked")
        public void run() {
            if (type.equals("1")) {
                MyofferResponse response = null;
                try {
                    HashMap<String, String> data = new HashMap<String, String>();
                    data.put("pagesize", pagesize);
                    data.put("page", "" + pageNumber);
                    data.put("uid", getUserID());
                    response = JsonParser.getMyofferResponse(HttpUtil.getMsg(Url.MYOFFER + "?" + HttpUtil.getData(data)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response != null) {
                    handler.sendMessage(handler.obtainMessage(4, response));
                } else {
                    handler.sendEmptyMessage(NETWORK_FAIL);
                }
            } else {
                MyInquiryResponse response = null;
                try {
                    HashMap<String, String> data = new HashMap<String, String>();
                    data.put("pagesize", pagesize);
                    data.put("page", "" + pageNumber);
                    data.put("uid", getUserID());
                    response = JsonParser.getMyInquiryResponse(HttpUtil.getMsg(Url.MYINQUIRY + "?" + HttpUtil.getData(data)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response != null) {
                    handler.sendMessage(handler.obtainMessage(NETWORK_SUCCESS_PAGER_RIGHT, response));
                } else {
                    handler.sendEmptyMessage(NETWORK_FAIL);
                }
            }
        }
    };

    public MyHandler handler = new MyHandler();

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (isDestroyed())
                return;

            pullToRefreshView.onRefreshComplete();
            myEmptyLayout = LayoutInflater.from(context).inflate(R.layout.pull_listitem_empty_padding, null);
            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_RIGHT:
                    MyInquiryResponse response = (MyInquiryResponse) msg.obj;
                    if (response.isSuccess()) {
                        inquiryList = response.getData().getItems();
                        inquiryAdapter = new InquiryAdapter(context, inquiryList);
                        listView.onFinishLoading(response.getData().hasMore());
                        listView.setAdapter(inquiryAdapter);
                        pageNumber = 2;
                        pullToRefreshView.setEmptyView(inquiryList.isEmpty() ? myEmptyLayout : null);

                        //跳转到对应的询价详情（点击通知栏推送过来的）
                        if (!TextUtils.isEmpty(inquiryId)) {
                            for (MyInquiry myInquiry : inquiryList) {
                                if (myInquiry.getId().equals(inquiryId)) {
                                    start_Activity(context, InquiryDetailsAlreadyActivity.class, new BasicNameValuePair("id", inquiryId), new BasicNameValuePair("title", myInquiry.getProduct_name()));
                                    break;
                                }
                            }
                            inquiryId = "";
                        }
                    } else {
                        listView.onFinishLoading(false);
                        showErrorToast(response.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_PAGER_RIGHT:
                    MyInquiryResponse pageResponse = (MyInquiryResponse) msg.obj;
                    if (pageResponse.isSuccess()) {
                        List<MyInquiry> tempList = pageResponse.getData()
                                .getItems();
                        inquiryList.addAll(tempList);
                        inquiryAdapter.notifyDataSetChanged();
                        listView.onFinishLoading(pageResponse.getData().hasMore());
                        pageNumber++;
                    } else {
                        listView.onFinishLoading(false);
                        showErrorToast(pageResponse.getError_desc());
                    }
                    break;
                case 3:
                    MyofferResponse offerresponse = (MyofferResponse) msg.obj;
                    if (offerresponse.isSuccess()) {
                        List<Myoffer> list = offerresponse.getData().getItems();
                        myOfferFilter(list, true);
                        offerAdapter = new OfferAdapter(context, filterOfferList);
                        listView.setAdapter(offerAdapter);
                        listView.onFinishLoading(offerresponse.getData().hasMore());
                        pageNumber = 2;
                        pullToRefreshView.setEmptyView(filterOfferList.isEmpty() ? myEmptyLayout : null);

                    } else {
                        listView.onFinishLoading(false);
                        showErrorToast(offerresponse.getError_desc());
                    }
                    break;
                case 4:
                    MyofferResponse offerpageResponse = (MyofferResponse) msg.obj;
                    if (offerpageResponse.isSuccess()) {
                        List<Myoffer> tempList = offerpageResponse.getData().getItems();
                        myOfferFilter(tempList, false);
                        offerAdapter.notifyDataSetChanged();
                        listView.onFinishLoading(offerpageResponse.getData().hasMore());
                        pageNumber++;
                    } else {
                        listView.onFinishLoading(false);
                        showErrorToast(offerpageResponse.getError_desc());
                    }
                    break;
                default:
                    pullToRefreshView.setEmptyView(mEmptyLayout);
//                    showErrorToast();
                    listView.onFinishLoading(false);
                    break;
            }
        }
    }


    private void selectRadioBtn() {
        RadioButton radioButton = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
        switch (radioButton.getId()) {
            case R.id.rd0:
                type = "1";
                if (getUser() != null) {
                    pageNumber = 1;
                    refresh();
                } else {
                    pullToRefreshView.onRefreshComplete();
                }

                break;
            case R.id.rd1:
                type = "2";
                if (getUser() != null) {
                    pageNumber = 1;
                    refresh();
                } else {
                    pullToRefreshView.onRefreshComplete();
                }

                break;
        }
    }


    /**
     * 初始化删除无效记录对话框
     */
    private void initInvalidDialog() {
        MyAlertDialog.Builder builder = new MyAlertDialog.Builder(context);
        builder.setMessage("是否删除已失效的询价记录？")
                .setIcon(R.mipmap.delete)
                .setNegativeButton("否", null)
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getMyApplication().getMyUserManager().setInvalidInquiryOption(true);
                        refresh();
                    }
                });

        invalidDialog = builder.create();
    }



    /**
     * 过滤我的报价
     */
    private void myOfferFilter(List<Myoffer> list, boolean isFirstPage) {

        if (getMyApplication().getMyUserManager().getInvalidInquiryOption()) {
            Iterator<Myoffer> it = list.iterator();
            while (it.hasNext()) {
                Myoffer myoffer = it.next();
                if (myoffer.getStatus().equals("0"))
                    it.remove();
            }
        }

        if (isFirstPage)
            filterOfferList.clear();
        filterOfferList.addAll(list);
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
        if (inquiryAdapter != null) {
            inquiryAdapter.cancelAllTimers();
        }
        if (offerAdapter != null) {
            offerAdapter.cancelAllTimers();
        }
    }
}
