package com.appjumper.silkscreen.ui.home;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.bean.HomeData;
import com.appjumper.silkscreen.bean.HomeDataResponse;
import com.appjumper.silkscreen.bean.MaterProduct;
import com.appjumper.silkscreen.bean.MyInquiry;
import com.appjumper.silkscreen.bean.Notice;
import com.appjumper.silkscreen.bean.ScoreResponse;
import com.appjumper.silkscreen.bean.UnRead;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.WebViewActivity;
import com.appjumper.silkscreen.ui.common.adapter.ViewPagerFragAdapter;
import com.appjumper.silkscreen.ui.home.adapter.HotInquiryAdapter;
import com.appjumper.silkscreen.ui.home.adapter.TrendChartAdapter;
import com.appjumper.silkscreen.ui.home.equipment.EquipmentActivity;
import com.appjumper.silkscreen.ui.home.exhibition.ExhibitionActivity;
import com.appjumper.silkscreen.ui.home.logistics.LogisticsActivity;
import com.appjumper.silkscreen.ui.home.news.NewsActivity;
import com.appjumper.silkscreen.ui.home.order.OrderActivity;
import com.appjumper.silkscreen.ui.home.process.ProcessingActivity;
import com.appjumper.silkscreen.ui.home.recruit.RecruitActivity;
import com.appjumper.silkscreen.ui.home.search.SearchingActivity;
import com.appjumper.silkscreen.ui.home.stock.StockActivity;
import com.appjumper.silkscreen.ui.home.tender.TenderActivity;
import com.appjumper.silkscreen.ui.home.workshop.WorkshopActivity;
import com.appjumper.silkscreen.ui.money.MessageActivity;
import com.appjumper.silkscreen.ui.my.MyPointActivity;
import com.appjumper.silkscreen.ui.trend.AttentionManageActivity;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.db.DBManager;
import com.appjumper.silkscreen.view.ItemSpaceDecorationLine;
import com.appjumper.silkscreen.view.MyListView;
import com.appjumper.silkscreen.view.ObservableScrollView;
import com.appjumper.silkscreen.view.VerticalSwitchTextView;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import q.rorbin.badgeview.QBadgeView;


/**
 * Created by yc on 2016/11/7.
 * 首页
 */
public class HomeFragment extends BaseFragment {

    @Bind(R.id.listview1)
    MyListView listview1;

    @Bind(R.id.imageView3)//签到按钮
            ImageView imageView3;

    @Bind(R.id.img_back_top)//返回顶部
            ImageView img_back_top;

    @Bind(R.id.l_integral)//积分布局
            LinearLayout l_integral;

    @Bind(R.id.tv_integral)//积分数字
            TextView tv_integral;

    @Bind(R.id.pull_refresh_scrollview)
    PtrClassicFrameLayout mPullRefreshScrollView;

    @Bind(R.id.vertical_switch_textview1)
    VerticalSwitchTextView verticalSwitchTextView1;

    @Bind(R.id.ll_enterprise_list)//企业列表外部布局
            LinearLayout llEnterpriseList;

    @Bind(R.id.l_homeview)
    LinearLayout l_homeview;

    @Bind(R.id.tabLaytTrend)
    TabLayout tabLaytTrend;
    @Bind(R.id.pagerTrend)
    ViewPager pagerTrend;
    @Bind(R.id.tabLaytRecommend)
    TabLayout tabLaytRecommend;
    @Bind(R.id.pagerRecommend)
    ViewPager pagerRecommend;

    @Bind(R.id.scrollView)
    ObservableScrollView mScrollView;
    @Bind(R.id.recyclerHotInquiry)
    RecyclerView recyclerHotInquiry;

    @Bind(R.id.txtMsg)
    ImageView txtMsg;
    @Bind(R.id.rl_tender)
    RelativeLayout rl_tender;
    @Bind(R.id.rl_exhibition)
    RelativeLayout rl_exhibition;
    @Bind(R.id.rl_news)
    RelativeLayout rl_news;


    private QBadgeView badgeMsg; //右上角小红点
    private QBadgeView badgeTender; //招投标信息小红点
    private QBadgeView badgeExhibition; //展会信息小红点
    private QBadgeView badgeNews; //行业新闻小红点

    private List<MyInquiry> hotInquiryList;//热门产品询价
    private HotInquiryAdapter hotInquiryAdapter;

    private List<MaterProduct> materList; //关注的原材料
    private List<Fragment> trendFragList; //走势图fragment集合
    private TrendChartAdapter trendChartAdapter; //走势图适配器

    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture future;
    private int currTrendItem = 0;

    private List<Fragment> recommendFragList; //推荐企业
    private ViewPagerFragAdapter recommendAdapter;

    private HomeData data;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_home2, null);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initData() {
        initUnread();

        registerBroadcastReceiver();
        initTrendChart();
        setRecyclerView();
        setRefreshLayout();

        listview1.setFocusable(false);

        mScrollView.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {

            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
                if (y > 1000) {
                    img_back_top.setVisibility(View.VISIBLE);
                } else {
                    img_back_top.setVisibility(View.GONE);
                }
            }
        });

        HomeDataResponse homedata = getMyApplication().getMyUserManager().getHome();
        if(homedata != null){
            data = homedata.getData();
            initView();
        }

        new Thread(new HomeDataRun()).start();
    }


    /**
     * 初始化未读小红点
     */
    private void initUnread() {
        badgeMsg = new QBadgeView(context);
        badgeTender = new QBadgeView(context);
        badgeExhibition = new QBadgeView(context);
        badgeNews = new QBadgeView(context);

        badgeMsg.bindTarget(txtMsg).setBadgeBackgroundColor(0xffffffff).setBadgeTextColor(0xffff6000).setGravityOffset(0, true).setBadgeTextSize(10, true);
        badgeTender.bindTarget(rl_tender);
        badgeExhibition.bindTarget(rl_exhibition);
        badgeNews.bindTarget(rl_news);
    }


    /**
     * 设置下拉刷新
     */
    private void setRefreshLayout() {
        mPullRefreshScrollView.disableWhenHorizontalMove(true);
        mPullRefreshScrollView.setLastUpdateTimeRelateObject(this);
        mPullRefreshScrollView.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                new Thread(new HomeDataRun()).start();
            }
        });
    }


    private void setRecyclerView() {
        /**
         * 热门询价
         */
        hotInquiryList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            MyInquiry myInquiry = new MyInquiry();
            hotInquiryList.add(myInquiry);
        }

        hotInquiryAdapter = new HotInquiryAdapter(R.layout.item_recycler_line_hot_inquiry, hotInquiryList);
        recyclerHotInquiry.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerHotInquiry.addItemDecoration(new ItemSpaceDecorationLine(5,0,0,0));
        recyclerHotInquiry.setAdapter(hotInquiryAdapter);

        /**
         * 推荐
         */
        recommendFragList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            recommendFragList.add(new RecommendFragment());
        }

        String [] titleArr = {"丝网订做", "丝网现货", "丝网加工"};
        recommendAdapter = new ViewPagerFragAdapter(context.getSupportFragmentManager(), recommendFragList, Arrays.asList(titleArr));
        pagerRecommend.setOffscreenPageLimit(recommendFragList.size() - 1);
        pagerRecommend.setAdapter(recommendAdapter);
        tabLaytRecommend.setupWithViewPager(pagerRecommend);
    }



    private void initView() {
        l_homeview.setVisibility(View.VISIBLE);

        setTrendChartData();

        final List<Notice> notice = data.getNotice();
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < notice.size(); i++) {
            list.add(notice.get(i).getTitle());
        }
        verticalSwitchTextView1.setTextContent(list);
        verticalSwitchTextView1.setCbInterface(new VerticalSwitchTextView.VerticalSwitchTextViewCbInterface() {
            @Override
            public void showNext(int index) {

            }

            @Override
            public void onItemClick(int index) {
                start_Activity(HomeFragment.this.getActivity(), WebViewActivity.class,new BasicNameValuePair("url",notice.get(index).getArticle_url()),new BasicNameValuePair("title","公告详情"));
                CommonApi.addLiveness(getUserID(), 6);
            }
        });

        if (data.getCheckin().equals("1")) {
            l_integral.setVisibility(View.VISIBLE);
            imageView3.setVisibility(View.GONE);
            tv_integral.setText(data.getScore());
        } else {
            l_integral.setVisibility(View.GONE);
            imageView3.setVisibility(View.VISIBLE);
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Enterprise> recommend0 = data.getRecommend();
                List<Enterprise> recommend1 = data.getRecommend_xianhuo();
                List<Enterprise> recommend2 = data.getRecommend_jiagong();

                ((RecommendFragment)recommendFragList.get(0)).refresh(recommend0);
                ((RecommendFragment)recommendFragList.get(1)).refresh(recommend1);
                ((RecommendFragment)recommendFragList.get(2)).refresh(recommend2);
            }
        }, 200);


        /*final List<Enterprise> recommend = data.getRecommend();
        final List<NewPublic> newPublic = data.getNewpublic();
        if (recommend != null && recommend.size() > 0) {
            llEnterpriseList.setVisibility(View.VISIBLE);
            listview1.setAdapter(new HomeListview1Adapter(getActivity(), recommend));
        }else {
            llEnterpriseList.setVisibility(View.GONE);
        }


        listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                start_Activity(getActivity(), CompanyDetailsActivity.class, new BasicNameValuePair("from", "2"), new BasicNameValuePair("id", recommend.get(i).getEnterprise_id()));
            }
        });*/

    }



    //首页
    private class HomeDataRun implements Runnable {
        private HomeDataResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("uid", getUserID());
                response = JsonParser.getHomeDataResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.HOMEDATA));
            } catch (Exception e) {
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

    //签到
    private class CheckInRun implements Runnable {
        private ScoreResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("uid", getUserID());
                response = JsonParser.getScoreResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.CHECKIN));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        1, response));
            } else {
                handler.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    };

    private MyHandler handler = new MyHandler();

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            mPullRefreshScrollView.refreshComplete();
            if (progress != null) {
                progress.dismiss();
            }

            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT:
                    HomeDataResponse detailsResponse = (HomeDataResponse) msg.obj;
                    if (detailsResponse.isSuccess()) {
                        data = detailsResponse.getData();
                        initView();
                        getMyApplication().getMyUserManager().storeHomeInfo(detailsResponse);
                    } else {
                        showErrorToast(detailsResponse.getError_desc());
                    }
                    break;
                case 1://签到
                    //progress.dismiss();
                    ScoreResponse baseResponse = (ScoreResponse) msg.obj;
                    if (baseResponse.isSuccess()) {
                        showErrorToast("签到成功");
                        l_integral.setVisibility(View.VISIBLE);
                        imageView3.setVisibility(View.GONE);
                        tv_integral.setText(baseResponse.getData().getScore());
                        CommonApi.addLiveness(getUserID(), 2);
                    } else {
                        showErrorToast(baseResponse.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_DATA_ERROR:
                    showErrorToast();
                    break;
                default:
                    showErrorToast();
                    break;
            }
        }
    };

    /**
     * 初始化走势图
     */
    private void initTrendChart() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        materList = new ArrayList<>();
        trendFragList = new ArrayList<>();
        trendChartAdapter = new TrendChartAdapter(context.getSupportFragmentManager(), materList, trendFragList);
        pagerTrend.setOffscreenPageLimit(5);
        pagerTrend.setAdapter(trendChartAdapter);

        tabLaytTrend.setupWithViewPager(pagerTrend);
        tabLaytTrend.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLaytTrend.setTabGravity(TabLayout.GRAVITY_CENTER);

        pagerTrend.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int oldPosition = 0;

            @Override
            public void onPageSelected(int position) {
                currTrendItem = position;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                switch (arg0) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        if (future != null) {
                            future.cancel(true);
                        }
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        if (future != null && future.isCancelled()) {
                            startTrendPlay();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 设置走势图数据
     */
    private void setTrendChartData() {
        materList.clear();
        trendFragList.clear();
        DBManager dbHelper = new DBManager(context);
        materList.addAll(dbHelper.query());
        dbHelper.closeDB();

        for (int i = 0; i < materList.size(); i++) {
            Bundle mBundle = new Bundle();
            mBundle.putString("type", materList.get(i).getId());
            TrendChartFragment fragment = new TrendChartFragment();
            fragment.setArguments(mBundle);
            trendFragList.add(i, fragment);
        }

        trendChartAdapter.notifyDataSetChanged();
        if (future != null) {
            future.cancel(true);
            currTrendItem = 0;
            pagerTrend.setCurrentItem(currTrendItem, true);
        }
        startTrendPlay();
    }

    /**
     * 开始走势图轮播
     */
    private void startTrendPlay() {
        if (trendChartAdapter.getCount() < 2) {
            return;
        }

        future = scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                currTrendItem = (currTrendItem + 1) % trendChartAdapter.getCount();
                trendHandler.obtainMessage().sendToTarget();
            }
        }, 3000, 3500, TimeUnit.MILLISECONDS);
    }

    private Handler trendHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            pagerTrend.setCurrentItem(currTrendItem, true);
        };
    };



    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_ATTENTION_MATER_REFRESH);
        filter.addAction(Const.ACTION_UNREAD_REFRESH);
        context.registerReceiver(myReceiver, filter);
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Const.ACTION_ATTENTION_MATER_REFRESH)) {
                setTrendChartData();
            } else if (action.equals(Const.ACTION_UNREAD_REFRESH)) {
                UnRead unRead = (UnRead) intent.getSerializableExtra(Const.KEY_OBJECT);
                badgeMsg.setBadgeNumber(unRead.getReadNum());
                badgeTender.setBadgeNumber(unRead.getTenderNum());
                badgeExhibition.setBadgeNumber(unRead.getExpoNum());
                badgeNews.setBadgeNumber(unRead.getNewsNum());
            }
        }
    };


    @OnClick({R.id.tv_machine, R.id.tv_logistics, R.id.imageView, R.id.tv_orders, R.id.tv_stocks, R.id.tv_equipment,
            R.id.tv_workshop, R.id.tv_recruit, R.id.rl_tender, R.id.rl_exhibition, R.id.rl_news, R.id.imageView3,
            R.id.l_integral, R.id.img_back_top, R.id.txtMsg, R.id.imgViAdd})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back_top://返回顶部
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mScrollView.fullScroll(ScrollView.FOCUS_UP);
                    }
                });
                break;
            case R.id.l_integral://积分
                start_Activity(getActivity(), MyPointActivity.class);
                break;
            case R.id.imageView3://签到
                if (checkLogined()) {
                    initProgressDialog();
                    progress.show();
                    progress.setMessage("正在签到...");
                    new Thread(new CheckInRun()).start();
                }
                break;
            case R.id.tv_machine://加工
                start_Activity(getActivity(), ProcessingActivity.class);
                break;
            case R.id.tv_logistics://物流
                start_Activity(getActivity(), LogisticsActivity.class);
                break;
            case R.id.imageView://搜索
                start_Activity(getActivity(), SearchingActivity.class);
                break;
            case R.id.tv_orders://订做
                start_Activity(getActivity(), OrderActivity.class);
                break;
            case R.id.tv_stocks://现货
                start_Activity(getActivity(), StockActivity.class);
                break;
            case R.id.tv_equipment://设备
                start_Activity(getActivity(), EquipmentActivity.class);
                break;
            case R.id.tv_workshop://厂房
                start_Activity(getActivity(), WorkshopActivity.class);
                break;
            case R.id.tv_recruit://招聘
                start_Activity(getActivity(), RecruitActivity.class);
                break;
            case R.id.rl_tender://招标中标
                start_Activity(getActivity(), TenderActivity.class);
                break;
            case R.id.rl_exhibition://展会信息
                start_Activity(getActivity(), ExhibitionActivity.class);
                break;
            case R.id.rl_news://行业新闻
                start_Activity(getActivity(), NewsActivity.class);
                break;
            case R.id.txtMsg: //消息
                start_Activity(getActivity(), MessageActivity.class);
                break;
            case R.id.imgViAdd: //添加关注
                start_Activity(context, AttentionManageActivity.class);
                break;
            default:
                break;
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (future != null) {
            future.cancel(true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (future != null && future.isCancelled()) {
            startTrendPlay();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (future != null) {
            future.cancel(true);
            future = null;
        }

        context.unregisterReceiver(myReceiver);
    }
}
