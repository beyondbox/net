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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.AuditUser;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.phonegridview.GalleryActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 快速审核 --个人认证
 * Created by Botx on 2017/12/5.
 */

public class AuditPersonFragment extends BaseFragment {

    @Bind(R.id.llContent)
    LinearLayout llContent;
    @Bind(R.id.llBottomBar)
    LinearLayout llBottomBar;
    @Bind(R.id.txtState)
    TextView txtState;

    @Bind(R.id.imgVi0)
    ImageView imgVi0;
    @Bind(R.id.imgVi1)
    ImageView imgVi1;
    @Bind(R.id.txtName)
    TextView txtName;
    @Bind(R.id.txtID)
    TextView txtID;
    @Bind(R.id.txtMobile)
    TextView txtMobile;

    private AuditPersonActivity activity;
    private Dialog dialogRefuse;
    private EditText edtTxtContent;
    private AuditUser data;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audit_person, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        activity = (AuditPersonActivity) getActivity();
        Bundle bundle = getArguments();
        data = (AuditUser) bundle.getSerializable(Const.KEY_OBJECT);
        setData();
        initDialog();
        initProgressDialog(false, null);
    }


    private void setData() {
        Picasso.with(context)
                .load(data.getIdcard_img())
                .resize(1024, 1024)
                .centerInside()
                .placeholder(R.mipmap.icon_front_01)
                .error(R.mipmap.icon_front_01)
                .into(imgVi0);

        Picasso.with(context)
                .load(data.getIdcard_img_back())
                .resize(1024, 1024)
                .centerInside()
                .placeholder(R.mipmap.icon_contrary_02)
                .error(R.mipmap.icon_contrary_02)
                .into(imgVi1);

        txtName.setText(data.getName());
        txtMobile.setText(data.getMobile());
        txtID.setText(data.getIdcard());

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
        RequestParams params = MyHttpClient.getApiParam("purchase", "pass_auth_status");
        params.put("id", data.getId());
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
        RequestParams params = MyHttpClient.getApiParam("purchase", "refusal_auth_status");
        params.put("id", data.getId());
        params.put("auth_status_reason", edtTxtContent.getText().toString().trim());

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


    @OnClick({R.id.txtRefuse, R.id.txtPass, R.id.imgViCall, R.id.imgVi0, R.id.imgVi1})
    public void onClick(View view) {
        if (data == null)
            return;

        Intent intent = null;
        ArrayList<String> urls;
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
            case R.id.imgVi0:
                intent = new Intent(context, GalleryActivity.class);
                urls = new ArrayList<String>();
                urls.add(data.getIdcard_img());
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, 0);
                startActivity(intent);
                break;
            case R.id.imgVi1:
                intent = new Intent(context, GalleryActivity.class);
                urls = new ArrayList<String>();
                urls.add(data.getIdcard_img_back());
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, 0);
                startActivity(intent);
                break;
        }
    }

}
