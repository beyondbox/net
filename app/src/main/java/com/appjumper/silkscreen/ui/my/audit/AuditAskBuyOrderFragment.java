package com.appjumper.silkscreen.ui.my.audit;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.AskBuy;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 快速审核 --求购订单详情
 * Created by Botx on 2017/12/5.
 */

public class AuditAskBuyOrderFragment extends BaseFragment {

    @Bind(R.id.llContent)
    LinearLayout llContent;
    @Bind(R.id.llBottomBar)
    LinearLayout llBottomBar;
    @Bind(R.id.txtState)
    TextView txtState;

    @Bind(R.id.imgViHead)
    ImageView imgViHead;
    @Bind(R.id.txtContent)
    TextView txtContent;
    @Bind(R.id.txtProduct)
    TextView txtProduct;
    @Bind(R.id.txtPrice)
    TextView txtPrice;
    @Bind(R.id.txtNum)
    TextView txtNum;
    @Bind(R.id.txtOrderId)
    TextView txtOrderId;
    @Bind(R.id.txtTotal)
    TextView txtTotal;

    @Bind(R.id.txtConsigner)
    TextView txtConsigner;
    @Bind(R.id.txtMobile)
    TextView txtMobile;
    @Bind(R.id.txtAddress)
    TextView txtAddress;

    @Bind(R.id.txtTrans)
    TextView txtTrans;
    @Bind(R.id.txtRemark)
    TextView txtRemark;

    @Bind(R.id.txtOfferMoney)
    TextView txtOfferMoney;
    @Bind(R.id.txtOfferName)
    TextView txtOfferName;
    @Bind(R.id.txtOfferMobile)
    TextView txtOfferMobile;
    @Bind(R.id.txtOfferCompany)
    TextView txtOfferCompany;


    private AuditAskBuyOrderActivity activity;
    private Dialog dialogRefuse;
    private EditText edtTxtContent;
    private AskBuy data;
    private int offerHandle = 0; //对报价用户是否处理：0-不处理；1-拉黑1天不能报价


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audit_askbuy_order, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        activity = (AuditAskBuyOrderActivity) getActivity();
        Bundle bundle = getArguments();
        data = (AskBuy) bundle.getSerializable(Const.KEY_OBJECT);
        setData();
        initDialog();
        initProgressDialog(false, null);
    }


    private void setData() {
        Picasso.with(context)
                .load(data.getProduct_img())
                .resize(DisplayUtil.dip2px(context, 60), DisplayUtil.dip2px(context, 60))
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(imgViHead);

        txtProduct.setText(data.getProduct_name());
        txtContent.setText(data.getPurchase_content());
        txtPrice.setText(data.getOffer_money() + "元/" + data.getPurchase_unit());
        txtNum.setText("x " + data.getPurchase_num() + data.getPurchase_unit());
        txtTotal.setText("¥" + data.getPay_money());

        if (TextUtils.isEmpty(data.getOrder_id()))
            txtOrderId.setText("无");
        else
            txtOrderId.setText(data.getOrder_id());

        txtConsigner.setText(data.getName());
        txtMobile.setText(data.getMobile());
        txtAddress.setText(data.getAddress());
        txtRemark.setText(data.getRemarks());

        if (data.getSiwangjia_short().equals("0"))
            txtTrans.setText("由丝网+进行短途运输 (送到货站结运费)");
        else
            txtTrans.setText("自己负责运输");


        txtOfferName.setText(data.getOffer_name());
        txtOfferMobile.setText(data.getOffer_mobile());
        txtOfferCompany.setText(data.getEnterprise_name());
        txtOfferMoney.setText(data.getOffer_money() + "元/" + data.getPurchase_unit());

        llContent.setVisibility(View.VISIBLE);
    }


    private void initDialog() {
        dialogRefuse = new Dialog(context, R.style.CustomDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_audit_refuse_askbuy_order, null);
        dialogRefuse.setContentView(view);

        Display display = context.getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams params = dialogRefuse.getWindow().getAttributes();
        params.width = (int) (display.getWidth() * 0.8);
        dialogRefuse.getWindow().setAttributes(params);

        edtTxtContent = (EditText) view.findViewById(R.id.edtTxtContent);
        TextView txtConfirm = (TextView) view.findViewById(R.id.txtConfirm);
        RadioGroup rdoGroup = (RadioGroup) view.findViewById(R.id.rdoGroup);

        rdoGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb0:
                        offerHandle = 0;
                        break;
                    case R.id.rb1:
                        offerHandle = 1;
                        break;
                }
            }
        });

        txtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edtTxtContent.getText().toString().trim())) {
                    showErrorToast("请输入不通过理由");
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
        RequestParams params = MyHttpClient.getApiParam("purchase", "pass_purchase_order");
        params.put("uid", getUserID());
        params.put("id", data.getId());
        params.put("product_name", data.getProduct_name());
        params.put("purchase_num", data.getPurchase_num());
        params.put("purchase_unit", data.getPurchase_unit());

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
        RequestParams params = MyHttpClient.getApiParam("purchase", "refusal_purchase_order");
        params.put("id", data.getId());
        params.put("examine_refusal_reason", edtTxtContent.getText().toString().trim());
        params.put("offer_user_punishment", offerHandle);

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
                AppTool.dial(context, data.getOffer_mobile());
                break;
        }
    }

}
