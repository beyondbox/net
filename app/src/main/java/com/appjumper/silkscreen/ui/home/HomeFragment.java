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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.appjumper.silkscreen.bean.AskBuy;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.bean.HomeData;
import com.appjumper.silkscreen.bean.HomeDataResponse;
import com.appjumper.silkscreen.bean.OfferList;
import com.appjumper.silkscreen.bean.PriceDetails;
import com.appjumper.silkscreen.bean.ScoreResponse;
import com.appjumper.silkscreen.bean.StockGoods;
import com.appjumper.silkscreen.bean.TrendArticle;
import com.appjumper.silkscreen.bean.UnRead;
import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.MainActivity;
import com.appjumper.silkscreen.ui.common.adapter.ViewPagerFragAdapter;
import com.appjumper.silkscreen.ui.dynamic.AskBuyDetailActivity;
import com.appjumper.silkscreen.ui.home.adapter.HomeAskBuyListAdapter;
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
import com.appjumper.silkscreen.ui.trend.ArticleDetailActivity;
import com.appjumper.silkscreen.ui.trend.PriceMoreActivity;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.ChartViewStraightLine;
import com.appjumper.silkscreen.view.ObservableScrollView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    @Bind(R.id.flipperOffer)
    ViewFlipper flipperOffer;
    @Bind(R.id.txtArticle0)
    TextView txtArticle0;
    @Bind(R.id.txtArticle1)
    TextView txtArticle1;
    @Bind(R.id.llChart)
    LinearLayout llChart;

    @Bind(R.id.txtTradeMoney)
    TextView txtTradeMoney;
    @Bind(R.id.txtTradeNum)
    TextView txtTradeNum;
    @Bind(R.id.txtEnterpriseNum)
    TextView txtEnterpriseNum;
    @Bind(R.id.txtAskBuyNum)
    TextView txtAskBuyNum;
    @Bind(R.id.txtFreightNum)
    TextView txtFreightNum;

    @Bind(R.id.llHotAskBuy)
    LinearLayout llHotAskBuy;
    @Bind(R.id.recyclerAskBuy)
    RecyclerView recyclerAskBuy;


    private List<AskBuy> askBuyList; //热门求购列表
    private HomeAskBuyListAdapter askBuyAdapter;

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
        view = inflater.inflate(R.layout.fragment_hom5, null);
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MyApplication.appContext.checkMobile(context);
            }
        }, 1000);


        new Thread(new HomeDataRun()).start();

        HomeDataResponse homedata = getMyApplication().getMyUserManager().getHome();
        if(homedata != null){
            data = homedata.getData();
            initView();
        }
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
         * 热门求购
         */
        askBuyList = new ArrayList<>();
        askBuyAdapter = new HomeAskBuyListAdapter(R.layout.item_recycler_line_home_askbuy, askBuyList);
        recyclerAskBuy.setLayoutManager(new LinearLayoutManager(context));
        askBuyAdapter.bindToRecyclerView(recyclerAskBuy);

        askBuyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                start_Activity(context, AskBuyDetailActivity.class, new BasicNameValuePair("id", askBuyList.get(position).getId()));
            }
        });

        /**
         * 商城
         */
        stockList = new ArrayList<>();
        stockAdapter = new StockShopListAdapter(R.layout.item_recycler_grid_stock_shop, stockList);
        recyclerStockShop.setLayoutManager(new GridLayoutManager(context, 2));
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
        /*RecommendFragment orderFrag = new RecommendFragment();
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
        });*/
    }



    private void initView() {
        l_homeview.setVisibility(View.VISIBLE);
        //mScrollView.smoothScrollTo(0, 0);

        //公告
        /*List<Notice> noticeList = data.getNotice();
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
        }*/

        setFlipper();

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


        //热门求购
        List<AskBuy> aList = data.getPurchase();
        if (aList != null && aList.size() > 0) {
            llHotAskBuy.setVisibility(View.VISIBLE);
            askBuyList.clear();
            askBuyList.addAll(aList);
            askBuyAdapter.notifyDataSetChanged();
        } else {
            llHotAskBuy.setVisibility(View.GONE);
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
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Enterprise> recommend0 = data.getRecommend();
                List<Enterprise> recommend1 = data.getRecommend_xianhuo();
                List<Enterprise> recommend2 = data.getRecommend_jiagong();

                ((RecommendFragment)recommendFragList.get(0)).refresh(recommend0);
                ((RecommendFragment)recommendFragList.get(1)).refresh(recommend1);
                ((RecommendFragment)recommendFragList.get(2)).refresh(recommend2);

                recommendAdapter.notifyDataSetChanged();
            }
        }, 200);*/


        //推荐企业悬停效果
        //final int hoverMark = llRecommend.getTop() + txtTitleRecommend.getHeight();
        mScrollView.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {

            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
                if (y > 1000) {
                    img_back_top.setVisibility(View.VISIBLE);
                } else {
                    img_back_top.setVisibility(View.GONE);
                }

                /*if (hoverMark > 0 && y >= hoverMark)
                    llHoverRecommend.setVisibility(View.VISIBLE);
                else
                    llHoverRecommend.setVisibility(View.GONE);*/
            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getVolume();
                getChartData();
                getArticle();
            }
        }, 80);

    }


    /**
     * 设置滚动控件
     */
    private void setFlipper() {
        //每日盘条快报
        /*if (flipperReport.isFlipping())
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
                if (offer.getOffer_value_tax().equals("0")) {
                    isTax = "不含税 ";
                } else {
                    isTax = "含税 ";
                }

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

            flipperReport.startFlipping();
        }*/


        if (flipperOffer.isFlipping())
            flipperOffer.stopFlipping();
        flipperOffer.removeAllViews();

        List<OfferList> offerList = data.getDynamicOffer();
        if (offerList != null && offerList.size() > 0) {
            for (int i = 0; i < offerList.size(); i = i + 2) {
                View contentView = LayoutInflater.from(context).inflate(R.layout.layout_home_offer, null);

                TextView txtTime = (TextView) contentView.findViewById(R.id.txtTime);
                TextView txtName = (TextView) contentView.findViewById(R.id.txtName);
                TextView txtWave = (TextView) contentView.findViewById(R.id.txtWave);
                TextView txtPrice = (TextView) contentView.findViewById(R.id.txtPrice);

                TextView txtTime1 = (TextView) contentView.findViewById(R.id.txtTime1);
                TextView txtName1 = (TextView) contentView.findViewById(R.id.txtName1);
                TextView txtWave1 = (TextView) contentView.findViewById(R.id.txtWave1);
                TextView txtPrice1 = (TextView) contentView.findViewById(R.id.txtPrice1);

                OfferList offer = offerList.get(i);
                txtTime.setText(offer.getOffer_time().substring(5, 10));
                txtName.setText(offer.getCompany_name());
                txtPrice.setText(offer.getOffer_value() + " " + offer.getOffer_unit());
                txtWave.setText(offer.getYesterday());
                int wave = Integer.valueOf(offer.getYesterday());
                if (wave >= 0)
                    txtWave.setTextColor(getResources().getColor(R.color.orange_color));
                else
                    txtWave.setTextColor(getResources().getColor(R.color.green_color));

                OfferList offer1 = offerList.get(i + 1);
                txtTime1.setText(offer1.getOffer_time().substring(5, 10));
                txtName1.setText(offer1.getCompany_name());
                txtPrice1.setText(offer1.getOffer_value() + " " + offer1.getOffer_unit());
                txtWave1.setText(offer1.getYesterday());
                int wave1 = Integer.valueOf(offer1.getYesterday());
                if (wave1 >= 0)
                    txtWave1.setTextColor(getResources().getColor(R.color.orange_color));
                else
                    txtWave1.setTextColor(getResources().getColor(R.color.green_color));

                flipperOffer.addView(contentView);
            }

            flipperOffer.startFlipping();
        }
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

        String driverStatus = user.getDriver_status();
        if (!TextUtils.isEmpty(driverStatus)) {
            if (driverStatus.equals("2"))
                score += 5;
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
                if (getUser() == null) return;
                if (!isDataInited) return;
                if (!isViewCreated) return;

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


    /**
     * 获取走势分析文章
     */
    private void getArticle() {
        RequestParams params = MyHttpClient.getApiParam("tender", "analysis_list");
        params.put("type", 1);
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
                        final List<TrendArticle> list = GsonUtil.getEntityList(dataObj.getJSONArray("items").toString(), TrendArticle.class);
                        if (list.size() > 0) {
                            txtArticle0.setText(list.get(0).getTitle());
                            txtArticle1.setText(list.get(1).getTitle());

                            txtArticle0.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(context, ArticleDetailActivity.class);
                                    intent.putExtra(Const.KEY_TOTAL, list.get(0).getId());
                                    intent.putExtra("id", list.get(0).getId());
                                    startActivity(intent);
                                }
                            });

                            txtArticle1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(context, ArticleDetailActivity.class);
                                    intent.putExtra(Const.KEY_TOTAL, list.get(0).getId());
                                    intent.putExtra("id", list.get(1).getId());
                                    startActivity(intent);
                                }
                            });
                        }
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
     * 获取走势图数据
     */
    private void getChartData() {
        RequestParams params = MyHttpClient.getApiParam("price", "details");
        params.put("product_id", 1);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        PriceDetails priceDetail = GsonUtil.getEntity(jsonObj.getJSONObject("data").toString(), PriceDetails.class);
                        setChart(priceDetail);
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
     * 获取交易额数量
     */
    private void getVolume() {
        final RequestParams params = MyHttpClient.getApiParam("purchase", "car_success");
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONArray("data").getJSONObject(0);
                        txtTradeMoney.setText(AppTool.addComma(dataObj.getString("purchase_order_money")));
                        txtTradeNum.setText(AppTool.addComma(dataObj.getString("purchase_order_num")));
                        txtEnterpriseNum.setText(AppTool.addComma(dataObj.getString("enterprise_num")));
                        txtAskBuyNum.setText(AppTool.addComma(dataObj.getString("purchase_num")));
                        txtFreightNum.setText(AppTool.addComma(dataObj.getString("car_num")));
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
     * 渲染走势图数据
     */
    private void setChart(PriceDetails data) {
        //BaseFundChartViewSmall v_avg_list = new BaseFundChartViewSmall(context);
        ChartViewStraightLine v_avg_list = new ChartViewStraightLine(context);

        //取横坐标日期的起始位置
        int start = -6;
        int end = 0;

        /*
         * 根据今日报价是否为0做出不同的判断
         */
        List<Float> l_y = data.getAvg_list();
        if (l_y.get(l_y.size() - 1) == 0) {
            start = -7;
            end = -1;
            l_y.remove(l_y.size() - 1);
        } else {
            l_y.remove(0);
        }

        List<List<Float>> dataXy = new ArrayList<>();
        dataXy.add(l_y);
        v_avg_list.setData(dataXy);

        /*
         * 计算横坐标日期
         */
        List<String> l_x = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, i);
            java.text.SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
            String time = sdf.format(c.getTime());
            l_x.add(time);
        }
        v_avg_list.setDataX(l_x);

        /*
         * 计算纵坐标的最小值和最大值
         */
        float max = 0;
        float min = 99999;
        for (int i = 0; i < l_y.size(); i++) {
            float val = l_y.get(i);
            if (max < val)
                max = val;

            if (min > val)
                min = val;
        }

        if (max == 0)
            max = Float.valueOf(data.getAvg());

        int offset = Integer.valueOf(data.getSpace_money());

        if (((int)max) % offset == 0) {
            max += offset;
        } else {
            String strMax = (int) max + "";
            strMax = strMax.substring(0, strMax.length() - 1);
            strMax = strMax + "0";
            int maxInt = Integer.valueOf(strMax);
            while (true) {
                maxInt += 10;
                if (maxInt % offset == 0)
                    break;
            }
            max = maxInt;
        }


        if (((int)min) % offset == 0) {
            min -= offset;
        } else {
            String strMin = (int) min + "";
            strMin = strMin.substring(0, strMin.length() - 1);
            strMin = strMin + "0";
            int minInt = Integer.valueOf(strMin);
            while (true) {
                if (minInt % offset == 0)
                    break;
                minInt -= 10;
            }
            min = minInt;
        }

        if (min < 0)
            min = 0;

        /*
         * 纵坐标分成5份，计算每份的值
         */
        int dif = (int) (max - min);
        int difAvg = dif / 5;

        List<Float> datas = new ArrayList<>();
        datas.add(min);
        datas.add(min + (difAvg * 1));
        datas.add(min + (difAvg * 2));
        datas.add(min + (difAvg * 3));
        datas.add(min + (difAvg * 4));
        datas.add(max);
        v_avg_list.setDataY(datas);

        llChart.removeAllViews();
        llChart.addView(v_avg_list);
    }


    @OnClick({R.id.rl_search, R.id.img_back_top, R.id.rlStockShop, R.id.rlCheckin, R.id.flipperReport, R.id.txtMoreGoods, R.id.rlStockConsign, R.id.llAskBuy, R.id.llFreight, R.id.llOffer, R.id.llChartGroup})
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
            case R.id.llOffer: //报价详情
                start_Activity(context, PriceMoreActivity.class, new BasicNameValuePair("id", "1"), new BasicNameValuePair("title", "盘条"));
                break;
            case R.id.llChartGroup: //报价走势图
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
