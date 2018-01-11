package com.appjumper.silkscreen.ui.home.logistics;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.AreaBean;
import com.appjumper.silkscreen.bean.CarLength;
import com.appjumper.silkscreen.bean.CarModel;
import com.appjumper.silkscreen.bean.Freight;
import com.appjumper.silkscreen.bean.StartPlace;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.home.adapter.AddRessRecyclerAdapter;
import com.appjumper.silkscreen.ui.home.adapter.CarLengthGridAdapter;
import com.appjumper.silkscreen.ui.home.adapter.CarModelGridAdapter;
import com.appjumper.silkscreen.ui.home.adapter.CityListViewAdapter;
import com.appjumper.silkscreen.ui.home.adapter.FreightListAdapter;
import com.appjumper.silkscreen.ui.home.adapter.StartPlaceListAdapter;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.appjumper.silkscreen.view.MyRecyclerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yyydjk.library.DropDownMenu;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.appjumper.silkscreen.R.id.ptrResult;

/**
 * 空车配货
 * Created by Botx on 2017/10/25.
 */

public class FreightFragment extends BaseFragment {

    @Bind(R.id.dropDownMenu)
    DropDownMenu dropDownMenu;
    @Bind(R.id.txtNum)
    TextView txtNum;
    @Bind(R.id.llFreightNum)
    LinearLayout llFreightNum;

    private PtrClassicFrameLayout ptrLayt;
    private RecyclerView recyclerData;

    private boolean isNumVisible = true;
    private int numHeight;
    private ObjectAnimator animHide;
    private ObjectAnimator animShow;
    private ObjectAnimator animUp;
    private ObjectAnimator animDown;

    private List<Freight> dataList;
    private FreightListAdapter adapter;
    private LinearLayoutManager layoutManager;

    private int page = 1;
    private int pageSize = 20;
    private int totalSize;

    private String startId = "1";
    private String endId = "";
    private List<StartPlace> startList = new ArrayList<>(); //始发地
    private StartPlaceListAdapter startAdapter;
    private List<AreaBean> endLeftList = new ArrayList<>(); //目的地左边
    private List<AreaBean> endRightList = new ArrayList<>(); //目的地右边
    private AddRessRecyclerAdapter endLeftAdapter;
    private CityListViewAdapter endRightAdapter;

    private String lengthsId = "";
    private String modelsId = "";
    private List<CarLength> lengthList = new ArrayList<>(); //车长
    private List<CarModel> modelList = new ArrayList<>(); //车型
    private CarLengthGridAdapter lengthAdapter;
    private CarModelGridAdapter modelAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_freight, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        initDropDownMenu();
        initRecyclerView();
        initRefreshLayout();

        getStartPlace();
        getEndPlace();
        getCarLength();
        getCarModel();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initAnim();
            }
        }, 300);

        ptrLayt.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrLayt.autoRefresh();
                getSuccessNum();
            }
        }, 80);
    }


    /**
     * 初始化下拉菜单
     */
    private void initDropDownMenu() {
        List<String> tabTexts = new ArrayList<>();
        tabTexts.add("衡水安平");
        tabTexts.add("目的地");
        tabTexts.add("车长车型");

        /**
         * 始发地
         */
        ListView startView = new ListView(context);
        startAdapter = new StartPlaceListAdapter(context, startList);
        startView.setDividerHeight(0);
        startView.setAdapter(startAdapter);
        startView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startAdapter.setCheckItem(position);
                if (position == 0)
                    dropDownMenu.setTabText("始发地");
                else
                    dropDownMenu.setTabText(startList.get(position).toString());
                dropDownMenu.closeMenu();
                startId = startList.get(position).getId();
                ptrLayt.autoRefresh();
            }
        });


        /*ListView endView = new ListView(context);
        endAdapter = new EndPlaceListAdapter(context, endList);
        endView.setDividerHeight(0);
        endView.setAdapter(endAdapter);
        endView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                endAdapter.setCheckItem(position);
                if (position == 0)
                    dropDownMenu.setTabText("目的地");
                else
                    dropDownMenu.setTabText(endList.get(position).toString());
                dropDownMenu.closeMenu();
                endId = endList.get(position).getId();
                ptrLayt.autoRefresh();
            }
        });*/

        /**
         * 目的地
         */
        LinearLayout endView = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        endView.setOrientation(LinearLayout.VERTICAL);
        endView.setBackgroundResource(R.color.while_color);
        endView.setLayoutParams(params);
        endView.setOnClickListener(null);
        View formView = LayoutInflater.from(context).inflate(R.layout.layout_choice, null);
        RecyclerView recyclerEnd = (MyRecyclerView)formView.findViewById(R.id.recycler_view);
        ListView lvEnd = (ListView)formView.findViewById(R.id.list_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerEnd.setLayoutManager(linearLayoutManager);
        endLeftAdapter = new AddRessRecyclerAdapter(context, endLeftList);
        recyclerEnd.setAdapter(endLeftAdapter);
        endRightAdapter = new CityListViewAdapter(context, endRightList);
        lvEnd.setAdapter(endRightAdapter);
        endView.addView(formView);

        endLeftAdapter.setOnItemClickLitener(new AddRessRecyclerAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 0) {
                    endRightList.clear();
                    endRightAdapter.notifyDataSetChanged();
                    dropDownMenu.setTabText("目的地");
                    dropDownMenu.closeMenu();
                    endId = endLeftList.get(position).getId();
                    ptrLayt.autoRefresh();
                } else {
                    endRightList.clear();
                    endRightList.addAll(endLeftList.get(position).getSublist());
                    endRightAdapter.setCurrentSelectPosition(0);
                    endRightAdapter.notifyDataSetChanged();
                }
            }
        });

        lvEnd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                endRightAdapter.setCurrentSelectPosition(i);
                endRightAdapter.notifyDataSetChanged();
                dropDownMenu.setTabText(endRightList.get(i).getShortname());
                dropDownMenu.closeMenu();
                endId = endRightList.get(i).getId();
                ptrLayt.autoRefresh();
            }
        });


        /**
         * 车长车型
         */
        View carModelView = LayoutInflater.from(context).inflate(R.layout.layout_dropdown_car_model, null);
        GridView gridCarLength = (GridView) carModelView.findViewById(R.id.gridCarLength);
        GridView gridCarModel = (GridView) carModelView.findViewById(R.id.gridCarModel);
        TextView txtConfirm = (TextView) carModelView.findViewById(R.id.txtConfirm);

        lengthAdapter = new CarLengthGridAdapter(context, lengthList);
        modelAdapter = new CarModelGridAdapter(context, modelList);
        gridCarLength.setAdapter(lengthAdapter);
        gridCarModel.setAdapter(modelAdapter);
        gridCarLength.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CarLength carLength = lengthList.get(i);

                if (carLength.isSelected())
                    carLength.setSelected(false);
                else
                    carLength.setSelected(true);

                lengthAdapter.notifyDataSetChanged();
            }
        });
        gridCarModel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CarModel carModel = modelList.get(i);

                if (carModel.isSelected())
                    carModel.setSelected(false);
                else
                    carModel.setSelected(true);

                modelAdapter.notifyDataSetChanged();
            }
        });
        txtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lengthsId = "";
                for (CarLength carLength : lengthList) {
                    if (carLength.isSelected())
                        lengthsId += carLength.getId() + ",";
                }
                if (!TextUtils.isEmpty(lengthsId))
                    lengthsId = lengthsId.substring(0, lengthsId.length());


                modelsId = "";
                for (CarModel carModel : modelList) {
                    if (carModel.isSelected())
                        modelsId += carModel.getId() + ",";
                }
                if (!TextUtils.isEmpty(modelsId))
                    modelsId = modelsId.substring(0, modelsId.length());

                dropDownMenu.closeMenu();
                ptrLayt.autoRefresh();
            }
        });


        List<View> popupViews = new ArrayList<>();
        popupViews.add(startView);
        popupViews.add(endView);
        popupViews.add(carModelView);

        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_content_search_result, null);
        ptrLayt = (PtrClassicFrameLayout) contentView.findViewById(ptrResult);
        recyclerData = (RecyclerView) contentView.findViewById(R.id.recyclerResult);

        dropDownMenu.setDropDownMenu(tabTexts, popupViews, contentView);
    }


    private void initRecyclerView() {
        dataList = new ArrayList<>();

        adapter = new FreightListAdapter(R.layout.item_recycler_freight_list, dataList);
        layoutManager = new LinearLayoutManager(context);
        recyclerData.setLayoutManager(layoutManager);
        adapter.bindToRecyclerView(recyclerData);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = null;
                Freight freight = dataList.get(position);
                int state = Integer.valueOf(freight.getExamine_status());
                switch (state) {
                    case Const.FREIGHT_AUDIT_PASS:
                        intent = new Intent(context, FreightDetailOfferingActivity.class);
                        break;
                    default:
                        intent = new Intent(context, FreightDetailUnderwayActivity.class);
                        break;
                }

                if (intent == null) return;
                intent.putExtra("id", dataList.get(position).getId());
                startActivity(intent);
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


        recyclerData.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private int HIDE_THRESHOLD = DisplayUtil.dip2px(context, 40);
            private int scrolledDistance = 0;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dataList.size() < 5) return;

                if (scrolledDistance > HIDE_THRESHOLD && isNumVisible) {
                    animHide.start();
                    animUp.start();
                    isNumVisible = false;
                    scrolledDistance = 0;
                }
                else if (scrolledDistance < -HIDE_THRESHOLD && !isNumVisible) {
                    if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                        llFreightNum.setVisibility(View.VISIBLE);
                        dropDownMenu.setTranslationY(-numHeight);
                        animShow.start();
                        animDown.start();
                        isNumVisible = true;
                        scrolledDistance = 0;
                    }
                }
                if ((isNumVisible && dy > 0) || (!isNumVisible && dy < 0)) {
                    scrolledDistance += dy;
                }

                /*if (isNumVisible && dy > 0) {
                    animHide.start();
                    animUp.start();
                    isNumVisible = false;
                } else if (!isNumVisible && dy < 0) {
                    if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                        llFreightNum.setVisibility(View.VISIBLE);
                        dropDownMenu.setTranslationY(-numHeight);
                        animShow.start();
                        animDown.start();
                        isNumVisible = true;
                    }
                }*/
            }
        });

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
     * 设置位移动画
     */
    private void initAnim() {
        numHeight = llFreightNum.getHeight();
        animHide = ObjectAnimator.ofFloat(llFreightNum, "translationY", -numHeight).setDuration(300);
        animShow = ObjectAnimator.ofFloat(llFreightNum, "translationY", 0).setDuration(300);
        animUp = ObjectAnimator.ofFloat(dropDownMenu, "translationY", -numHeight).setDuration(300);
        animDown = ObjectAnimator.ofFloat(dropDownMenu, "translationY", 0).setDuration(300);

        animUp.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                llFreightNum.setVisibility(View.GONE);
                dropDownMenu.setTranslationY(0);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }


    /**
     * 获取数据
     */
    private void getData() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "car_public_list");
        params.put("page", page);
        params.put("pagesize", pageSize);
        params.put("from_id", startId);
        params.put("to_id", endId);
        params.put("lengths_id", lengthsId);
        params.put("models_id", modelsId);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        List<Freight> list = GsonUtil.getEntityList(dataObj.getJSONArray("items").toString(), Freight.class);
                        totalSize = dataObj.optInt("total");

                        if (page == 1) {
                            dataList.clear();
                            recyclerData.smoothScrollToPosition(0);
                        }
                        dataList.addAll(list);
                        adapter.notifyDataSetChanged();

                        if (dataList.size() < totalSize)
                            adapter.setEnableLoadMore(true);
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
     * 获取平台成功配货车次
     */
    private void getSuccessNum() {
        final RequestParams params = MyHttpClient.getApiParam("purchase", "car_success");
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        String number = jsonObj.getJSONArray("data").getJSONObject(0).getString("car_num");
                        txtNum.setText(number);
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
     * 获取起始地
     */
    private void getStartPlace() {
        RequestParams params = MyHttpClient.getApiParam("area", "car_from_list");
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        List<StartPlace> list = GsonUtil.getEntityList(jsonObj.getJSONArray("data").toString(), StartPlace.class);
                        startList.clear();

                        StartPlace startPlace = new StartPlace();
                        startPlace.setFrom_name("不限");
                        startPlace.setId("");

                        startList.add(startPlace);
                        startList.addAll(list);
                        startAdapter.notifyDataSetChanged();

                        startAdapter.setCheckItem(1);
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
     * 获取目的地
     */
    private void getEndPlace() {
        RequestParams params = MyHttpClient.getApiParam("area", "province");
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        List<AreaBean> list = GsonUtil.getEntityList(jsonObj.getJSONArray("data").toString(), AreaBean.class);
                        endLeftList.clear();

                        AreaBean areaBean = new AreaBean();
                        areaBean.setShortname("不限");
                        areaBean.setId("");

                        endLeftList.add(areaBean);
                        endLeftList.addAll(list);
                        endLeftAdapter.notifyDataSetChanged();
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
     * 获取车长
     */
    private void getCarLength() {
        RequestParams params = MyHttpClient.getApiParam("area", "car_lengths_list");
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        List<CarLength> list = GsonUtil.getEntityList(jsonObj.getJSONArray("data").toString(), CarLength.class);
                        lengthList.clear();

                        /*CarLength carLength = new CarLength();
                        carLength.setId("");
                        carLength.setCar_lengths_name("不限");

                        lengthList.add(carLength);*/
                        lengthList.addAll(list);
                        lengthAdapter.notifyDataSetChanged();
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
     * 获取车型
     */
    private void getCarModel() {
        RequestParams params = MyHttpClient.getApiParam("area", "car_models_list");
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        List<CarModel> list = GsonUtil.getEntityList(jsonObj.getJSONArray("data").toString(), CarModel.class);
                        modelList.clear();

                        /*CarModel carModel = new CarModel();
                        carModel.setId("");
                        carModel.setCar_models_name("不限");

                        modelList.add(carModel);*/
                        modelList.addAll(list);
                        modelAdapter.notifyDataSetChanged();
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


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (getView() != null) {
            getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
        }
    }

}
