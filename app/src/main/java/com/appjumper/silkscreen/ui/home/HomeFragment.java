package com.appjumper.silkscreen.ui.home;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.base.MyApplication;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.bean.HomeData;
import com.appjumper.silkscreen.bean.HomeDataResponse;
import com.appjumper.silkscreen.bean.Notice;
import com.appjumper.silkscreen.bean.OfferList;
import com.appjumper.silkscreen.bean.ScoreResponse;
import com.appjumper.silkscreen.bean.StockGoods;
import com.appjumper.silkscreen.bean.UnRead;
import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.MainActivity;
import com.appjumper.silkscreen.ui.common.WebViewActivity;
import com.appjumper.silkscreen.ui.common.adapter.ViewPagerFragAdapter;
import com.appjumper.silkscreen.ui.home.adapter.HomeMenuAdapter;
import com.appjumper.silkscreen.ui.home.adapter.StockShopListAdapter;
import com.appjumper.silkscreen.ui.home.equipment.EquipmentActivity;
import com.appjumper.silkscreen.ui.home.exhibition.ExhibitionActivity;
import com.appjumper.silkscreen.ui.home.logistics.LogisticsListActivity;
import com.appjumper.silkscreen.ui.home.news.NewsActivity;
import com.appjumper.silkscreen.ui.home.order.OrderActivity;
import com.appjumper.silkscreen.ui.home.process.ProcessingActivity;
import com.appjumper.silkscreen.ui.home.recruit.RecruitActivity;
import com.appjumper.silkscreen.ui.home.search.SearchingActivity;
import com.appjumper.silkscreen.ui.home.stock.StockActivity;
import com.appjumper.silkscreen.ui.home.stockshop.GoodsDetailActivity;
import com.appjumper.silkscreen.ui.home.stockshop.StockConsignActivity;
import com.appjumper.silkscreen.ui.home.stockshop.StockGoodsSelectActivity;
import com.appjumper.silkscreen.ui.home.tender.TenderActivity;
import com.appjumper.silkscreen.ui.home.workshop.WorkshopActivity;
import com.appjumper.silkscreen.ui.my.MyPointActivity;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.ObservableScrollView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

import static android.R.attr.type;


/**
 * Created by yc on 2016/11/7.
 * 首页
 */
public class HomeFragment extends BaseFragment {

    @Bind(R.id.gridViMenu)
    GridView gridViMenu;

    @Bind(R.id.llStockShop)
    LinearLayout llStockShop;

    @Bind(R.id.recyclerStockShop)
    RecyclerView recyclerStockShop;

    @Bind(R.id.img_back_top)//返回顶部
            ImageView img_back_top;

    @Bind(R.id.pull_refresh_scrollview)
    PtrClassicFrameLayout mPullRefreshScrollView;

    @Bind(R.id.l_homeview)
    LinearLayout l_homeview;

    @Bind(R.id.tabLaytRecommend)
    TabLayout tabLaytRecommend;
    @Bind(R.id.pagerRecommend)
    ViewPager pagerRecommend;

    @Bind(R.id.scrollView)
    ObservableScrollView mScrollView;
    @Bind(R.id.txtCheckin)
    TextView txtCheckin;
    @Bind(R.id.txtScore)
    TextView txtScore;
    @Bind(R.id.txtNotice)
    TextView txtNotice;
    @Bind(R.id.flipperReport)
    ViewFlipper flipperReport;
    @Bind(R.id.llHoverRecommend)
    LinearLayout llHoverRecommend;
    @Bind(R.id.tabLaytHoverRecommend)
    TabLayout tabLaytHoverRecommend;
    @Bind(R.id.txtTitleRecommend)
    TextView txtTitleRecommend;
    @Bind(R.id.llRecommend)
    LinearLayout llRecommend;


    private List<StockGoods> stockList; //现货商城列表
    private StockShopListAdapter stockAdapter;

    private List<Fragment> recommendFragList; //推荐企业
    private ViewPagerFragAdapter recommendAdapter;

    private HomeData data;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBroadcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_hom4, null);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initData() {
        if (getUser() == null) {
            txtCheckin.setText("登录");
            txtScore.setVisibility(View.GONE);
        } else {
            setScore();
        }

        setRecyclerView();
        setRefreshLayout();

        HomeDataResponse homedata = getMyApplication().getMyUserManager().getHome();
        if(homedata != null){
            data = homedata.getData();
            initView();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MyApplication.appContext.checkMobile(context);
            }
        }, 1000);


        new Thread(new HomeDataRun()).start();
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
        gridViMenu.setAdapter(new HomeMenuAdapter());

        /**
         * 商城
         */
        stockList = new ArrayList<>();
        stockAdapter = new StockShopListAdapter(R.layout.item_recycler_line_stock_shop, stockList);
        recyclerStockShop.setLayoutManager(new LinearLayoutManager(context));
        stockAdapter.bindToRecyclerView(recyclerStockShop);

        stockAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(context, GoodsDetailActivity.class);
                intent.putExtra(Const.KEY_OBJECT, stockList.get(position));
                startActivity(intent);
            }
        });


        /**
         * 推荐企业
         */
        RecommendFragment orderFrag = new RecommendFragment();
        Bundle bundle1 =  new Bundle();
        bundle1.putInt(Const.KEY_SERVICE_TYPE, Const.SERVICE_TYPE_ORDER);
        orderFrag.setArguments(bundle1);

        RecommendFragment stockFrag = new RecommendFragment();
        Bundle bundle2 =  new Bundle();
        bundle2.putInt(Const.KEY_SERVICE_TYPE, Const.SERVICE_TYPE_STOCK);
        stockFrag.setArguments(bundle2);

        RecommendFragment processFrag = new RecommendFragment();
        Bundle bundle3 =  new Bundle();
        bundle3.putInt(Const.KEY_SERVICE_TYPE, Const.SERVICE_TYPE_PROCESS);
        processFrag.setArguments(bundle3);

        recommendFragList = new ArrayList<>();
        recommendFragList.add(orderFrag);
        recommendFragList.add(stockFrag);
        recommendFragList.add(processFrag);

        String [] titleArr = {"丝网订做", "丝网现货", "丝网加工"};
        recommendAdapter = new ViewPagerFragAdapter(context.getSupportFragmentManager(), recommendFragList, Arrays.asList(titleArr));
        pagerRecommend.setOffscreenPageLimit(recommendFragList.size() - 1);
        pagerRecommend.setAdapter(recommendAdapter);
        tabLaytRecommend.setupWithViewPager(pagerRecommend);

        tabLaytHoverRecommend.setupWithViewPager(pagerRecommend);

        pagerRecommend.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mScrollView.smoothScrollTo(0, llRecommend.getTop() + txtTitleRecommend.getHeight());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }



    private void initView() {
        l_homeview.setVisibility(View.VISIBLE);

        //公告
        List<Notice> noticeList = data.getNotice();
        if (noticeList != null && noticeList.size() > 0) {
            final Notice notice = noticeList.get(0);
            txtNotice.setText("[公告] " + notice.getTitle());
            txtNotice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    start_Activity(context, WebViewActivity.class, new BasicNameValuePair("url",notice.getArticle_url()), new BasicNameValuePair("title","公告详情"));
                    CommonApi.addLiveness(getUserID(), 6);
                }
            });
        }

        //每日盘条快报
        if (flipperReport.isFlipping())
            flipperReport.stopFlipping();
        flipperReport.removeAllViews();

        List<OfferList> offerList = data.getDynamicOffer();
        if (offerList != null && offerList.size() > 0) {
            for (OfferList offer : offerList) {
                long time = AppTool.getTimeMs(offer.getOffer_time(), "yyyy-MM-dd HH:mm:ss");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(time);
                String timeStr = (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日 ";

                String isTax = "";
                /*if (offer.getOffer_value_tax().equals("0")) {
                    isTax = "不含税 ";
                } else {
                    isTax = "含税 ";
                }*/

                String report = "[每日盘条] " + timeStr + offer.getCompany_name()
                        + " "
                        + isTax
                        + offer.getOffer_value()
                        + offer.getOffer_unit();

                View contentView = LayoutInflater.from(context).inflate(R.layout.layout_home_report, null);
                TextView textView = (TextView) contentView.findViewById(R.id.textView);
                textView.setText(report);
                flipperReport.addView(contentView);
            }
        }
        flipperReport.startFlipping();


        //签到
        if (getUser() == null) {
            txtCheckin.setText("登录");
            txtScore.setVisibility(View.GONE);
        } else {
            if (data.getCheckin().equals("1"))
                txtCheckin.setText("明日");
            else
                txtCheckin.setText("签到");

            setScore();
            txtScore.setVisibility(View.VISIBLE);
        }


        //现货商品
        List<StockGoods> goodsList = data.getGoods();
        if (goodsList != null && goodsList.size() > 0) {
            stockList.clear();
            stockList.addAll(goodsList);
            stockAdapter.notifyDataSetChanged();
            llStockShop.setVisibility(View.VISIBLE);
        } else {
            llStockShop.setVisibility(View.GONE);
        }


        //推荐企业
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


        //推荐企业悬停效果
        final int hoverMark = llRecommend.getTop() + txtTitleRecommend.getHeight();
        mScrollView.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {

            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
                if (y > 1000) {
                    img_back_top.setVisibility(View.VISIBLE);
                } else {
                    img_back_top.setVisibility(View.GONE);
                }

                if (hoverMark > 0 && y >= hoverMark)
                    llHoverRecommend.setVisibility(View.VISIBLE);
                else
                    llHoverRecommend.setVisibility(View.GONE);
            }
        });
    }


    /**
     * 计算每日签到所得积分
     */
    private void setScore() {
        int score = 10;
        User user = getUser();

        if (user.getAuth_status().equals("2"))
            score += 10;
        if (user.getEnterprise() != null) {
            Enterprise enterprise = user.getEnterprise();
            if (enterprise.getEnterprise_auth_status().equals("2"))
                score += 10;
            if (enterprise.getEnterprise_productivity_auth_status().equals("2"))
                score += 20;
        }

        txtScore.setText("+" + score);
    }


    //首页数据
    private class HomeDataRun implements Runnable {
        private HomeDataResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("g", "api");
                data.put("m", "home");
                data.put("a", "data");

                data.put("uid", getUserID());

                //response = JsonParser.getHomeDataResponse(HttpUtil.postMsg(HttpUtil.getData(data), Url.HOMEDATA));
                response = JsonParser.getHomeDataResponse(HttpUtil.getMsg(Url.HOST + "?" + HttpUtil.getData(data)));
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
            if (context.isDestroyed())
                return;

            mPullRefreshScrollView.refreshComplete();
            if (progress != null) {
                progress.dismiss();
            }

            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT: //首页数据
                    HomeDataResponse detailsResponse = (HomeDataResponse) msg.obj;
                    if (detailsResponse.isSuccess()) {
                        data = null;
                        data = detailsResponse.getData();
                        initView();
                        getMyApplication().getMyUserManager().storeHomeInfo(detailsResponse);
                        Const.SHARE_APP_URL = data.getShare_app();
                    } else {
                        showErrorToast(detailsResponse.getError_desc());
                    }
                    break;
                case 1://签到
                    //progress.dismiss();
                    ScoreResponse baseResponse = (ScoreResponse) msg.obj;
                    if (baseResponse.isSuccess()) {
                        showErrorToast("签到成功+" + baseResponse.getData().getScore());
                        txtCheckin.setText("明日");

                        User user = getUser();
                        user.setScore(baseResponse.getData().getIntegral());
                        getMyApplication().getMyUserManager().storeUserInfo(user);

                        new Thread(new HomeDataRun()).start();
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



    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_UNREAD_REFRESH);
        filter.addAction(Const.ACTION_LOGIN_SUCCESS);
        getActivity().registerReceiver(myReceiver, filter);
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Const.ACTION_UNREAD_REFRESH)) {
                if (getUser() == null)
                    return;

                if (!isDataInited)
                    return;

                if (isDetached())
                    return;

                UnRead unRead = (UnRead) intent.getSerializableExtra(Const.KEY_OBJECT);

                View newsUnread = gridViMenu.getChildAt(3).findViewById(R.id.unRead);
                View tenderUnread = gridViMenu.getChildAt(4).findViewById(R.id.unRead);
                View expoUnread = gridViMenu.getChildAt(9).findViewById(R.id.unRead);

                newsUnread.setVisibility(unRead.getNewsNum() < 3 ? View.VISIBLE : View.INVISIBLE);
                expoUnread.setVisibility(unRead.getExpoNum() < 3 ? View.VISIBLE : View.INVISIBLE);

                if (unRead.getTenderNum() < 3 || unRead.getTenderSelectNum() < 3)
                    tenderUnread.setVisibility(View.VISIBLE);
                else
                    tenderUnread.setVisibility(View.INVISIBLE);

            } else if (action.equals(Const.ACTION_LOGIN_SUCCESS)) {
                new Thread(new HomeDataRun()).start();
            }
        }
    };



    @OnClick({R.id.rl_search, R.id.img_back_top, R.id.rlStockShop, R.id.rlCheckin, R.id.flipperReport, R.id.txtMoreGoods, R.id.rlStockConsign, R.id.llAskBuy, R.id.llFreight})
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
            case R.id.rl_search://搜索
                start_Activity(getActivity(), SearchingActivity.class);
                break;
            case R.id.rlStockShop: //现货商城
                start_Activity(context, StockGoodsSelectActivity.class);
                break;
            case R.id.rlStockConsign: //现货寄售
                if (checkLogined())
                    start_Activity(context, StockConsignActivity.class);
                break;
            case R.id.rlCheckin: //签到
                if (checkLogined()) {
                    if (data != null) {
                        if (data.getCheckin().equals("1")) {
                            start_Activity(getActivity(), MyPointActivity.class);
                        } else {
                            initProgressDialog();
                            progress.show();
                            progress.setMessage("正在签到...");
                            new Thread(new CheckInRun()).start();
                        }
                    }
                }
                break;
            case R.id.flipperReport: //每日盘条快报
                ((MainActivity)getActivity()).bottom_lly.check(R.id.rd_trend);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Const.ACTION_CHART_DETAIL);
                        intent.putExtra("type", type);
                        context.sendBroadcast(intent);
                    }
                }, 200);
                break;
            case R.id.txtMoreGoods: //商城-更多
                start_Activity(context, StockGoodsSelectActivity.class);
                break;
            case R.id.llAskBuy: //求购
                ((MainActivity)getActivity()).bottom_lly.check(R.id.rd_dynamic);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        context.sendBroadcast(new Intent(Const.ACTION_ASKBUY_LIST));
                    }
                }, 200);
                break;
            case R.id.llFreight: //空车配货
                Intent intent = new Intent(context, LogisticsListActivity.class);
                intent.putExtra(Const.KEY_TYPE, 2);
                startActivity(intent);
                break;
            default:
                break;
        }
    }



    @OnItemClick(R.id.gridViMenu)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: //订做
                start_Activity(getActivity(), OrderActivity.class);
                break;
            case 1: //加工
                start_Activity(getActivity(), ProcessingActivity.class);
                break;
            case 2: //现货
                start_Activity(getActivity(), StockActivity.class);
                break;
            case 3: //新闻
                start_Activity(getActivity(), NewsActivity.class);
                break;
            case 4: //招投标
                start_Activity(getActivity(), TenderActivity.class);
                break;
            case 5: //物流
                start_Activity(getActivity(), LogisticsListActivity.class);
                break;
            case 6: //设备
                start_Activity(getActivity(), EquipmentActivity.class);
                break;
            case 7: //厂房
                start_Activity(getActivity(), WorkshopActivity.class);
                break;
            case 8: //招聘
                start_Activity(getActivity(), RecruitActivity.class);
                break;
            case 9: //展会
                start_Activity(getActivity(), ExhibitionActivity.class);
                break;
            default:
                break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(myReceiver);
    }

}
