package com.appjumper.silkscreen.ui.my.audit;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.Freight;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 快速审核 --空车配货详情
 * Created by Botx on 2017/12/5.
 */

public class AuditFreightFragment extends BaseFragment {

    @Bind(R.id.llContent)
    LinearLayout llContent;
    @Bind(R.id.llBottomBar)
    LinearLayout llBottomBar;

    @Bind(R.id.txtStart)
    TextView txtStart;
    @Bind(R.id.txtEnd)
    TextView txtEnd;
    @Bind(R.id.txtName)
    TextView txtName;
    @Bind(R.id.txtTime)
    TextView txtTime;
    @Bind(R.id.txtState)
    TextView txtState;
    @Bind(R.id.txtCarModel)
    TextView txtCarModel;
    @Bind(R.id.txtProduct)
    TextView txtProduct;
    @Bind(R.id.txtLoadTime)
    TextView txtLoadTime;
    @Bind(R.id.txtRemark)
    TextView txtRemark;


    private AuditFreightActivity activity;
    private Dialog dialogRefuse;
    private EditText edtTxtContent;
    private Freight data;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audit_freight, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        activity = (AuditFreightActivity) getActivity();
        Bundle bundle = getArguments();
        data = (Freight) bundle.getSerializable(Const.KEY_OBJECT);
        setData();
        initDialog();
        initProgressDialog(false, null);
    }


    private void setData() {
        txtName.setText(data.getName());
        txtStart.setText(data.getFrom_name());
        txtEnd.setText(data.getTo_name());
        txtTime.setText(data.getCreate_time().substring(5, 16));
        txtCarModel.setText(data.getLengths_name() + "/" + data.getModels_name());
        txtProduct.setText(data.getWeight() + data.getProduct_name());
        txtLoadTime.setText(data.getExpiry_date().substring(5, 16) + "装车");

        if (TextUtils.isEmpty(data.getRemark()))
            txtRemark.setText("无");
        else
            txtRemark.setText(data.getRemark());

        llContent.setVisibility(View.VISIBLE);
    }


    private void initDialog() {
        dialogRefuse = new Dialog(context, R.style.CustomDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_audit_refuse, null);
        dialogRefuse.setContentView(view);

        Display display = context.getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams params = dialogRefuse.getWindow().getAttributes();
        params.width = (int) (display.getWidth() * 0.8);
        dialogRefuse.getWindow().setAttributes(params);

        edtTxtContent = (EditText) view.findViewById(R.id.edtTxtContent);
        TextView txtConfirm = (TextView) view.findViewById(R.id.txtConfirm);

        txtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edtTxtContent.getText().toString().trim())) {
                    showErrorToast("请输入拒绝理由");
                    return;
                }
                dialogRefuse.dismiss();
                refuse();
            }
        });
    }


    /**
     * 通过
     */
    private void pass() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "pass_car_product");
        params.put("id", data.getId());

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
                        showErrorToast("审核成功");
                        txtState.setText("已通过");
                        txtState.setTextColor(getResources().getColor(R.color.green_color));
                        llBottomBar.setVisibility(View.GONE);

                        activity.updateState();
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
            }
        });
    }


    /**
     * 拒绝
     */
    private void refuse() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "refusal_car_product");
        params.put("id", data.getId());
        params.put("refusal_reason", edtTxtContent.getText().toString().trim());

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
                        showErrorToast("审核成功");
                        txtState.setText("已拒绝");
                        txtState.setTextColor(getResources().getColor(R.color.red_color));
                        llBottomBar.setVisibility(View.GONE);

                        activity.updateState();
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
            }
        });
    }


    @OnClick({R.id.txtRefuse, R.id.txtPass, R.id.imgViCall})
    public void onClick(View view) {
        if (data == null)
            return;

        switch (view.getId()) {
            case R.id.txtRefuse: //拒绝
                dialogRefuse.show();
                break;
            case R.id.txtPass: //通过
                pass();
                break;
            case R.id.imgViCall: //打电话
                AppTool.dial(context, data.getMobile());
                break;
        }
    }

}
