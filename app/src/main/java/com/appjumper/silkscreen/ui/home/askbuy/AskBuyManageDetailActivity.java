package com.appjumper.silkscreen.ui.home.askbuy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.appjumper.silkscreen.bean.AskBuy;
import com.appjumper.silkscreen.bean.AskBuyOffer;
import com.appjumper.silkscreen.bean.Avatar;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.home.adapter.AskBuyImageAdapter;
import com.appjumper.silkscreen.ui.my.adapter.ChooseOfferAdapter;
import com.appjumper.silkscreen.ui.my.askbuy.AskBuyEditActivity;
import com.appjumper.silkscreen.ui.my.askbuy.AskBuyMakeOrderActivity;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.appjumper.silkscreen.view.SureOrCancelDialog;
import com.appjumper.silkscreen.view.phonegridview.GalleryActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 求购管理--详情
 * Created by Botx on 2017/10/19.
 */

public class AskBuyManageDetailActivity extends BaseActivity {

    @Bind(R.id.llContent)
    LinearLayout llContent;
    @Bind(R.id.right)
    ImageView right;
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
    @Bind(R.id.txtState)
    TextView txtState;

    @Bind(R.id.imgViAvatar)
    ImageView imgViAvatar;
    @Bind(R.id.txtAdviserName)
    TextView txtAdviserName;

    @Bind(R.id.llRecord)
    LinearLayout llRecord;
    @Bind(R.id.txtRecord)
    TextView txtRecord;
    @Bind(R.id.lvRecord)
    ListView lvRecord;

    @Bind(R.id.txtHint)
    TextView txtHint;
    @Bind(R.id.txtHandle)
    TextView txtHandle;
    @Bind(R.id.txtHandle1)
    TextView txtHandle1;
    @Bind(R.id.txtTitle)
    TextView txtTitle;

    public static AskBuyManageDetailActivity instance = null;

    private String id;
    private AskBuy data;

    private SureOrCancelDialog cancelDialog;
    private SureOrCancelDialog deleteDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_askbuy_manage_detail);
        ButterKnife.bind(context);
        instance = this;
        initBack();
        initProgressDialog(false, null);
        initDialog();

        id = getIntent().getStringExtra("id");
        getData();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }


    private void initDialog() {
        cancelDialog = new SureOrCancelDialog(context, "提示", "确定要取消该求购信息吗？", "确定", "取消", new SureOrCancelDialog.SureButtonClick() {
            @Override
            public void onSureButtonClick() {
                deleteInfo();
            }
        });

        deleteDialog = new SureOrCancelDialog(context, "提示", "确定要删除该求购信息吗？", "确定", "取消", new SureOrCancelDialog.SureButtonClick() {
            @Override
            public void onSureButtonClick() {
                deleteInfo();
            }
        });
    }



    /**
     * 获取数据
     */
    private void getData() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "details");
        params.put("id", id);

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
                        data = GsonUtil.getEntity(jsonObj.getJSONObject("data").toString(), AskBuy.class);
                        setData();
                        llContent.setVisibility(View.VISIBLE);
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

            @Override
            public void onFinish() {
                super.onFinish();
                if (isDestroyed()) return;
                progress.dismiss();
            }
        });
    }


    /**
     * 渲染数据
     */
    private void setData() {
        initTitle("求购" + data.getProduct_name());

        if (data.getPruchase_type().equals(Const.INFO_TYPE_OFFICIAL + ""))
            txtName.setText("求购G" + data.getId());
        else
            txtName.setText("求购C" + data.getId());
        //right.setImageResource(R.mipmap.icon_share);

        Picasso.with(context)
                .load(data.getImg())
                .resize(DisplayUtil.dip2px(context, 50), DisplayUtil.dip2px(context, 50))
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(imgViHead);


        txtTime.setText(data.getCreate_time().substring(5, 16));
        txtContent.setText(data.getPurchase_content());

        if (data.getPurchase_num().equals("0"))
            txtTitle.setText(data.getProduct_name());
        else
            txtTitle.setText(data.getProduct_name() + data.getPurchase_num() + data.getPurchase_unit());


        int status = Integer.valueOf(data.getExamine_status());
        switch (status) {
            case Const.ASKBUY_AUDITING: //审核中
                txtState.setText("审核中");
                txtHint.setText("您发布的求购正在审核中，请耐心等待");
                txtHint.setVisibility(View.VISIBLE);
                llRecord.setVisibility(View.GONE);
                txtHandle.setText("取消求购");
                break;
            case Const.ASKBUY_REFUSE: //审核失败
                txtState.setText("审核失败");
                txtHint.setText("很抱歉，您发布的求购被驳回\n" + "原因: " + data.getExamine_refusal_reason());
                txtHint.setVisibility(View.VISIBLE);
                llRecord.setVisibility(View.GONE);
                txtHandle.setText("编辑重发");
                break;
            case Const.ASKBUY_OFFERING: //报价中或者结束
                txtHint.setVisibility(View.GONE);
                llRecord.setVisibility(View.VISIBLE);
                long expiryTime = AppTool.getTimeMs(data.getExpiry_date(), "yy-MM-dd HH:mm:ss");
                if (System.currentTimeMillis() < expiryTime) {
                    txtState.setText("报价中 " + data.getExpiry_date().substring(5, 16) + "截止");
                    txtHandle.setText("取消求购");
                } else {
                    txtState.setText("报价结束");
                    txtHandle.setText("删除信息");
                    txtHandle1.setText("重新发布");
                    txtHandle1.setVisibility(View.VISIBLE);
                }
                break;
            default:
                txtState.setText("");
                txtHint.setText("");
                txtHint.setVisibility(View.VISIBLE);
                llRecord.setVisibility(View.GONE);
                txtHandle.setVisibility(View.GONE);
                break;
        }


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

        txtAdviserName.setText("丝网+官方交易顾问-" + data.getAdviser_nicename());


        final List<AskBuyOffer> offerList = data.getOffer_list();
        if (offerList != null) {
            txtRecord.setText("报价记录" + "(" + offerList.size() + ")");

            ChooseOfferAdapter recordAdapter = new ChooseOfferAdapter(context, offerList);
            if (data.getExamine_status().equals(Const.ASKBUY_OFFERING + "")) {
                long expiryTime = AppTool.getTimeMs(data.getExpiry_date(), "yy-MM-dd HH:mm:ss");
                if (System.currentTimeMillis() > expiryTime)
                    recordAdapter.setReadMode(true);
            }

            lvRecord.setAdapter(recordAdapter);
            recordAdapter.setOnWhichClickListener(new MyBaseAdapter.OnWhichClickListener() {
                @Override
                public void onWhichClick(View view, int position, int tag) {
                    switch (view.getId()) {
                        case R.id.txtHandle: //购买
                            Intent intent = new Intent(context, AskBuyMakeOrderActivity.class);
                            intent.putExtra("id", id);
                            intent.putExtra(Const.KEY_OBJECT, offerList.get(position));
                            startActivity(intent);
                            break;
                    }
                }
            });
        }

    }


    /**
     * 删除求购
     */
    private void deleteInfo() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "cancel_purchase");
        params.put("purchase_id", id);
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
                        showErrorToast("操作成功");
                        finish();
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

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }


    @OnClick({R.id.txtHandle, R.id.txtCall, R.id.right, R.id.imgViHead, R.id.txtHandle1})
    public void onClick(View view) {
        if (data == null)
            return;

        Intent intent = null;
        switch (view.getId()) {
            case R.id.txtHandle:
                int status = Integer.valueOf(data.getExamine_status());
                switch (status) {
                    case Const.ASKBUY_AUDITING:
                        cancelDialog.show();
                        break;
                    case Const.ASKBUY_REFUSE:
                        intent = new Intent(context, AskBuyEditActivity.class);
                        intent.putExtra("id", id);
                        startActivity(intent);
                        break;
                    case Const.ASKBUY_OFFERING:
                        long expiryTime = AppTool.getTimeMs(data.getExpiry_date(), "yy-MM-dd HH:mm:ss");
                        if (System.currentTimeMillis() < expiryTime) {
                            cancelDialog.show();
                        } else {
                            deleteDialog.show();
                        }
                        break;
                }
                break;
            case R.id.txtCall: //咨询顾问
                if (!TextUtils.isEmpty(data.getAdviser_mobile())) {
                    AppTool.dial(context, data.getAdviser_mobile());
                }
                break;
            case R.id.right: //分享
                //ShareUtil.intShare(context, view, data.getPurchase_content(), "求购" + data.getProduct_name(), Const.SHARE_ASKBUY_URL + "?id=" + id);
                break;
            case R.id.imgViHead: //产品图片
                intent = new Intent(context, GalleryActivity.class);
                ArrayList<String> urls = new ArrayList<String>();
                urls.add(data.getImg());
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, 0);
                startActivity(intent);
                break;
            case R.id.txtHandle1: //重新发布
                start_Activity(context, AskBuyEditActivity.class, new BasicNameValuePair("id", id));
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }
}
