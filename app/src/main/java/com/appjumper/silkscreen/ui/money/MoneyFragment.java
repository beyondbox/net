package com.appjumper.silkscreen.ui.money;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.MyInquiry;
import com.appjumper.silkscreen.bean.MyInquiryResponse;
import com.appjumper.silkscreen.bean.Myoffer;
import com.appjumper.silkscreen.bean.MyofferResponse;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.ui.money.adapter.InquiryAdapter;
import com.appjumper.silkscreen.ui.money.adapter.OfferAdapter;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.view.pulltorefresh.PagedListView;
import com.appjumper.silkscreen.view.pulltorefresh.PullToRefreshBase;
import com.appjumper.silkscreen.view.pulltorefresh.PullToRefreshPagedListView;

import org.apache.http.message.BasicNameValuePair;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yc on 2016/11/7.
 * 询报价
 */
public class MoneyFragment extends BaseFragment {
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
    private List<Myoffer> offerList;
    private OfferAdapter offerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_money, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEmptyLayout = LayoutInflater.from(getContext()).inflate(R.layout.pull_listitem_empty_padding, null);
        pullToRefreshView.setEmptyView(mEmptyLayout);
        listView = pullToRefreshView.getRefreshableView();
        listView.onFinishLoading(false);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectRadioBtn();
            }
        });
        initListener();
        if (getUser() != null) {
            refresh();
        }
    }

    private void refresh() {
        pullToRefreshView.setRefreshing();
        new Thread(run).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUser() != null) {
            refresh();
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getUser() == null) {
                if (inquiryList != null) {
                    inquiryList.clear();
                    inquiryAdapter.notifyDataSetChanged();
                }
                if (offerList != null) {
                    offerList.clear();
                    offerAdapter.notifyDataSetChanged();
                }
                mEmptyLayout = LayoutInflater.from(getContext()).inflate(R.layout.activity_money_login, null);
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
                        if (offerList != null && offerList.size() > 0) {
                            start_Activity(getActivity(), OfferDetailsActivity.class, new BasicNameValuePair("id", offerList.get(position).getId()));
                        }
                        break;
                    case "2":
                        if (inquiryList.get(position).getOffer_num() != null && !inquiryList.get(position).getOffer_num().equals("") && Integer.parseInt(inquiryList.get(position).getOffer_num()) > 0) {
                            start_Activity(getActivity(), InquiryDetailsAlreadyActivity.class, new BasicNameValuePair("id", inquiryList.get(position).getId()), new BasicNameValuePair("title", inquiryList.get(position).getProduct_name()));
                        } else {
                            start_Activity(getActivity(), InquiryDetailsNoneActivity.class, new BasicNameValuePair("id", inquiryList.get(position).getId()), new BasicNameValuePair("title", inquiryList.get(position).getProduct_name()));
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
                    data.put("pagesize", pagesize);
                    data.put("page", "1");
                    data.put("uid", getUserID());
                    response = JsonParser.getMyofferResponse(HttpUtil.getMsg(Url.MYOFFER + "?" + HttpUtil.getData(data)));
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
                    data.put("pagesize", pagesize);
                    data.put("page", "1");
                    data.put("uid", getUserID());
                    response = JsonParser.getMyInquiryResponse(HttpUtil.getMsg(Url.MYINQUIRY + "?" + HttpUtil.getData(data)));
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
            pullToRefreshView.onRefreshComplete();
            myEmptyLayout = LayoutInflater.from(getActivity()).inflate(R.layout.pull_listitem_empty_padding, null);
            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_RIGHT:
                    MyInquiryResponse response = (MyInquiryResponse) msg.obj;
                    if (response.isSuccess()) {
                        inquiryList = response.getData().getItems();
                        inquiryAdapter = new InquiryAdapter(getActivity(), inquiryList);
                        listView.onFinishLoading(response.getData().hasMore());
                        listView.setAdapter(inquiryAdapter);
                        pageNumber = 2;
                        pullToRefreshView.setEmptyView(inquiryList.isEmpty() ? myEmptyLayout : null);
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
                        offerList = offerresponse.getData().getItems();
                        offerAdapter = new OfferAdapter(getActivity(), offerList);
                        listView.setAdapter(offerAdapter);
                        listView.onFinishLoading(offerresponse.getData().hasMore());
                        pageNumber = 2;
                        pullToRefreshView.setEmptyView(offerList.isEmpty() ? myEmptyLayout : null);
                    } else {
                        listView.onFinishLoading(false);
                        showErrorToast(offerresponse.getError_desc());
                    }
                    break;
                case 4:
                    MyofferResponse offerpageResponse = (MyofferResponse) msg.obj;
                    if (offerpageResponse.isSuccess()) {
                        List<Myoffer> tempList = offerpageResponse.getData()
                                .getItems();
                        offerList.addAll(tempList);
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
        RadioButton radioButton = (RadioButton) getActivity().findViewById(rg.getCheckedRadioButtonId());
        switch (radioButton.getId()) {
            case R.id.rd0:
                type = "1";
                if (getUser() != null) {
                    pageNumber = 1;
                    new Thread(run).start();
                } else {
                    pullToRefreshView.onRefreshComplete();
                }

                break;
            case R.id.rd1:
                type = "2";
                if (getUser() != null) {
                    pageNumber = 1;
                    new Thread(run).start();
                } else {
                    pullToRefreshView.onRefreshComplete();
                }

                break;
        }
    }
}
