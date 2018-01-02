package com.appjumper.silkscreen.ui.my.audit;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.AskBuy;
import com.appjumper.silkscreen.bean.Avatar;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.dynamic.adapter.AskBuyImageAdapter;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.appjumper.silkscreen.view.phonegridview.GalleryActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 快速审核 --求购详情
 * Created by Botx on 2017/12/5.
 */

public class AuditAskBuyFragment extends BaseFragment {

    @Bind(R.id.llContent)
    LinearLayout llContent;
    @Bind(R.id.llBottomBar)
    LinearLayout llBottomBar;
    @Bind(R.id.txtState)
    TextView txtState;

    @Bind(R.id.imgViHead)
    ImageView imgViHead;
    @Bind(R.id.txtName)
    TextView txtName;
    @Bind(R.id.txtTime)
    TextView txtTime;
    @Bind(R.id.txtContent)
    TextView txtContent;
    @Bind(R.id.gridImg)
    GridView gridImg;
    @Bind(R.id.txtTitle)
    TextView txtTitle;

    @Bind(R.id.imgViAvatar)
    ImageView imgViAvatar;
    @Bind(R.id.txtAdviserName)
    TextView txtAdviserName;
    @Bind(R.id.txtChoose)
    TextView txtChoose;


    private AuditAskBuyActivity activity;
    private Dialog dialogRefuse;
    private Dialog dialogChooseService;
    private EditText edtTxtContent;
    private EditText edtTxtPhone;
    private AskBuy data;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audit_askbuy, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        activity = (AuditAskBuyActivity) getActivity();
        Bundle bundle = getArguments();
        data = (AskBuy) bundle.getSerializable(Const.KEY_OBJECT);
        setData();
        initDialog();
        initProgressDialog(false, null);
    }


    private void setData() {
        Picasso.with(context)
                .load(data.getImg())
                .resize(DisplayUtil.dip2px(context, 60), DisplayUtil.dip2px(context, 60))
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(imgViHead);

        if (data.getPruchase_type().equals(Const.INFO_TYPE_OFFICIAL + ""))
            txtName.setText("求购G" + data.getId());
        else
            txtName.setText("求购C" + data.getId());

        if (data.getPurchase_num().equals("0"))
            txtTitle.setText(data.getProduct_name());
        else
            txtTitle.setText(data.getProduct_name() + " " + data.getPurchase_num() + data.getPurchase_unit());

        txtTime.setText(data.getCreate_time().substring(5, 16));
        txtContent.setText(data.getPurchase_content());

        final List<Avatar> imgList = data.getImg_list();
        if (imgList != null && imgList.size() > 0) {
            gridImg.setVisibility(View.VISIBLE);
            AskBuyImageAdapter imgAdapter = new AskBuyImageAdapter(context, imgList);
            gridImg.setAdapter(imgAdapter);
            gridImg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(context, GalleryActivity.class);
                    ArrayList<String> urls = new ArrayList<String>();
                    for (Avatar avatar : imgList) {
                        urls.add(avatar.getOrigin());
                    }
                    intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
                    intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, i);
                    context.startActivity(intent);
                }
            });
        } else {
            gridImg.setVisibility(View.GONE);
        }


        Picasso.with(context)
                .load(data.getAdviser_avatar())
                .placeholder(R.mipmap.img_error_head)
                .error(R.mipmap.img_error_head)
                .into(imgViAvatar);

        if (TextUtils.isEmpty(data.getAdviser_nicename()))
            txtAdviserName.setText("客服 : " + data.getAdviser_mobile());
        else
            txtAdviserName.setText("客服 : " + data.getAdviser_nicename());

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


        dialogChooseService = new Dialog(context, R.style.CustomDialog);
        View viewChoose = LayoutInflater.from(context).inflate(R.layout.dialog_audit_choose_service, null);
        dialogChooseService.setContentView(viewChoose);
        dialogChooseService.getWindow().setAttributes(params);

        edtTxtPhone = (EditText) viewChoose.findViewById(R.id.edtTxtPhone);
        TextView txtConfirmChoose = (TextView) viewChoose.findViewById(R.id.txtConfirm);

        txtConfirmChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edtTxtPhone.getText().toString().trim())) {
                    showErrorToast("请输入客服手机号");
                    return;
                }
                dialogChooseService.dismiss();
                chooseService();
            }
        });
    }


    /**
     * 分配客服
     */
    private void chooseService() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "change_adviser");
        params.put("purchase_id", data.getId());
        params.put("mobile", edtTxtPhone.getText().toString().trim());

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
                    int state = jsonObj.optInt("status");
                    if (state == 1) {
                        showErrorToast("分配成功");
                        getServiceInfo();
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
     * 获取客服信息
     */
    private void getServiceInfo() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "details");
        params.put("id", data.getId());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        AskBuy askBuy = GsonUtil.getEntity(jsonObj.getJSONObject("data").toString(), AskBuy.class);
                        Picasso.with(context)
                                .load(askBuy.getAdviser_avatar())
                                .placeholder(R.mipmap.img_error_head)
                                .error(R.mipmap.img_error_head)
                                .into(imgViAvatar);

                        if (TextUtils.isEmpty(askBuy.getAdviser_nicename()))
                            txtAdviserName.setText("客服 : " + askBuy.getAdviser_mobile());
                        else
                            txtAdviserName.setText("客服 : " + askBuy.getAdviser_nicename());
                    } else {
                        showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
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


    /**
     * 通过
     */
    private void pass() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "pass_purchase");
        params.put("id", data.getId());
        params.put("product_id", data.getProduct_id());
        params.put("product_name", data.getProduct_name());

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
        RequestParams params = MyHttpClient.getApiParam("purchase", "refusal_purchase");
        params.put("id", data.getId());
        params.put("examine_refusal_reason", edtTxtContent.getText().toString().trim());

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


    @OnClick({R.id.txtRefuse, R.id.txtPass, R.id.imgViCall, R.id.txtChoose})
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
            case R.id.txtChoose: //分配客服
                dialogChooseService.show();
                break;
        }
    }

}
