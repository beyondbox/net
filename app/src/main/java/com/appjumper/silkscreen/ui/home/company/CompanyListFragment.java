package com.appjumper.silkscreen.ui.home.company;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.bean.ServiceProduct;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.ProductSelectActivity;
import com.appjumper.silkscreen.ui.home.CompanyDetailsActivity;
import com.appjumper.silkscreen.ui.home.adapter.CompanyListAdapter;
import com.appjumper.silkscreen.ui.home.adapter.GirdDropDownCenterAdapter;
import com.appjumper.silkscreen.ui.my.enterprise.EnterpriseCreateActivity;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.SureOrCancelDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yyydjk.library.DropDownMenu;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.appjumper.silkscreen.R.id.ptrResult;

/**
 * 厂家列表
 * Created by Botx on 2017/10/25.
 */

public class CompanyListFragment extends BaseFragment {

    @Bind(R.id.dropDownMenu)
    DropDownMenu dropDownMenu;
    @Bind(R.id.txtProductSelect)
    TextView txtProductSelect;

    private PtrClassicFrameLayout ptrLayt;
    private RecyclerView recyclerData;

    private List<Enterprise> dataList;
    private CompanyListAdapter adapter;

    private int page = 1;
    private int pageSize = 20;
    private int totalSize;

    private int serviceType;
    private String company[] = {"全部公司", "认证公司", "未认证公司"};
    private String auth = "";//公司 0全部公司1认证公司2未认证公司
    private String product_id = "";//产品id
    private SureOrCancelDialog comCreateDialog;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_company_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        serviceType = getArguments().getInt(Const.KEY_SERVICE_TYPE);

        initDropDownMenu();
        initRecyclerView();
        initRefreshLayout();
        initProgressDialog(false, null);
        initDialog();

        ptrLayt.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrLayt.autoRefresh();
            }
        }, 50);
    }


    /**
     * 初始化对话框
     */
    private void initDialog() {
        comCreateDialog = new SureOrCancelDialog(context, "提示", "您尚未完善企业信息，暂时不能在该板块发布信息，请完善企业信息后再继续操作", "确定", "取消",
                new SureOrCancelDialog.SureButtonClick() {
                    @Override
                    public void onSureButtonClick() {
                        start_Activity(context, EnterpriseCreateActivity.class, new BasicNameValuePair("type", "0"));
                    }
                });
    }


    /**
     * 初始化下拉菜单
     */
    private void initDropDownMenu() {
        List<String> tabTexts = new ArrayList<>();
        tabTexts.add("全部产品");
        tabTexts.add("全部公司");

        //公司筛选
        final ListView companyView = new ListView(context);
        final GirdDropDownCenterAdapter companyAdapter = new GirdDropDownCenterAdapter(context, Arrays.asList(company));
        companyView.setDividerHeight(0);
        companyView.setAdapter(companyAdapter);
        companyView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                companyAdapter.setCheckItem(position);
                dropDownMenu.setTabText(company[position]);
                dropDownMenu.closeMenu();
                auth = position + "";
                ptrLayt.autoRefresh();
            }
        });

        List<View> popupViews = new ArrayList<>();
        popupViews.add(new View(context));
        popupViews.add(companyView);

        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_content_search_result, null);
        ptrLayt = (PtrClassicFrameLayout) contentView.findViewById(ptrResult);
        recyclerData = (RecyclerView) contentView.findViewById(R.id.recyclerResult);

        dropDownMenu.setDropDownMenu(tabTexts, popupViews, contentView);
    }


    private void initRecyclerView() {
        dataList = new ArrayList<>();

        adapter = new CompanyListAdapter(R.layout.item_recycler_company_list, dataList, serviceType);
        recyclerData.setLayoutManager(new LinearLayoutManager(context));
        adapter.bindToRecyclerView(recyclerData);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                start_Activity(context, CompanyDetailsActivity.class, new BasicNameValuePair("from", "2"), new BasicNameValuePair("id", dataList.get(position).getEnterprise_id()));
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
        ptrLayt.disableWhenHorizontalMove(true);
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
        RequestParams params = MyHttpClient.getApiParam("service", "enterprise_list");
        params.put("page", page);
        params.put("pagesize", pageSize);
        params.put("type", serviceType);
        params.put("product_id", product_id);
        params.put("auth", auth);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        List<Enterprise> list = GsonUtil.getEntityList(dataObj.getJSONArray("items").toString(), Enterprise.class);
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


    @OnClick({R.id.txtRelease, R.id.txtProductSelect, R.id.txtSpecSelect})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtRelease: //发布
                if (checkLogined()) {
                    if (getUser().getEnterprise() == null) {
                        comCreateDialog.show();
                    } else {
                        CommonApi.releaseCheck(context, getUserID(), serviceType);
                    }
                }
                break;
            case R.id.txtProductSelect: //选择产品
                Intent intent = new Intent(context, ProductSelectActivity.class);
                intent.putExtra(Const.KEY_SERVICE_TYPE, serviceType);
                intent.putExtra(Const.KEY_IS_FILTER_MODE, true);
                startActivityForResult(intent, Const.REQUEST_CODE_INQUIRY_ORDER);
                break;
            case R.id.txtSpecSelect: //规格筛选

                break;
            default:
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;

        switch (requestCode) {
            case Const.REQUEST_CODE_INQUIRY_ORDER:
                ServiceProduct product = (ServiceProduct) data.getSerializableExtra(Const.KEY_OBJECT);
                product_id = product.getId();
                txtProductSelect.setText(product.getName());
                ptrLayt.autoRefresh();
                break;
            default:
                break;
        }
    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (getView() != null) {
            getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
        }
    }

}
