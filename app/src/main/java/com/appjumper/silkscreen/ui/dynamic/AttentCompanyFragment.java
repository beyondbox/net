package com.appjumper.silkscreen.ui.dynamic;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.dynamic.adapter.AttentCompanyAdapter;
import com.appjumper.silkscreen.ui.home.CompanyDetailsActivity;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.MyAlertDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
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
 * 关注的企业
 * Created by Botx on 2017/3/28.
 */

public class AttentCompanyFragment extends BaseFragment {

    @Bind(R.id.recyclerAttentCom)
    RecyclerView recyclerAttentCom;

    private AttentCompanyAdapter companyAdapter;
    private List<Enterprise> companyList;

    private MyAlertDialog cancelDialog;
    private int cancelPosition = -1;

    private DynamicManageActivity activity;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attent_company, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initData() {
        activity = (DynamicManageActivity) getActivity();
        initRecyclerView();
        initCancelDialog();
        initProgressDialog();

        getMyAttention();
    }


    private void initRecyclerView() {
        companyList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerAttentCom.setLayoutManager(layoutManager);

        companyAdapter = new AttentCompanyAdapter(R.layout.item_recycler_line_attent_company, companyList);
        companyAdapter.bindToRecyclerView(recyclerAttentCom);
        companyAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        companyAdapter.isFirstOnly(false);

        companyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                start_Activity(context, CompanyDetailsActivity.class, new BasicNameValuePair("id", companyList.get(position).getEnterprise_id()), new BasicNameValuePair("from", "2"));
            }
        });

        companyAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.txtCancelAttent:
                        cancelPosition = position;
                        cancelDialog.show();
                        break;
                    default:
                        break;
                }
            }
        });
    }


    /**
     * 初始化取消关注对话框
     */
    private void initCancelDialog() {
        MyAlertDialog.Builder builder = new MyAlertDialog.Builder(context);
        builder.setMessage("您确定要取消关注吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelAttention();
                    }
                });

        cancelDialog = builder.create();
    }


    /**
     * 获取我关注的企业
     */
    private void getMyAttention() {
        RequestParams params = MyHttpClient.getApiParam("collection", "my");
        params.put("uid", getUserID());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        companyList.addAll(GsonUtil.getEntityList(dataObj.getJSONArray("items").toString(), Enterprise.class));
                        companyAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
                companyAdapter.setEmptyView(R.layout.layout_empty_view_common);
            }
        });
    }


    /**
     * 取消关注
     */
    private void cancelAttention() {
        RequestParams params = MyHttpClient.getApiParam("collection", "delete");
        params.put("uid", getUserID());
        params.put("enterprise_id", companyList.get(cancelPosition).getEnterprise_id());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        companyList.remove(cancelPosition);
                        companyAdapter.notifyItemRemoved(cancelPosition);
                        companyAdapter.notifyItemRangeChanged(cancelPosition, companyList.size() - cancelPosition);
                        activity.getAttentNum();
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

}
