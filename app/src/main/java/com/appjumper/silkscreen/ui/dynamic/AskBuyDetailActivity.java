package com.appjumper.silkscreen.ui.dynamic;

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
import com.appjumper.silkscreen.bean.AskBuy;
import com.appjumper.silkscreen.bean.AskBuyOffer;
import com.appjumper.silkscreen.bean.Avatar;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.dynamic.adapter.AskBuyImageAdapter;
import com.appjumper.silkscreen.ui.dynamic.adapter.OfferRecordAdapter;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.ShareUtil;
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
 * 求购详情
 * Created by Botx on 2017/10/19.
 */

public class AskBuyDetailActivity extends BaseActivity {

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
    @Bind(R.id.llRecord)
    LinearLayout llRecord;
    @Bind(R.id.lvRecord)
    ListView lvRecord;
    @Bind(R.id.txtOffer)
    TextView txtOffer;

    @Bind(R.id.imgViMarkSelf)
    ImageView imgViMarkSelf;
    @Bind(R.id.txtMark)
    TextView txtMark;
    @Bind(R.id.txtTitle)
    TextView txtTitle;

    public static AskBuyDetailActivity instance = null;
    private String id;
    private AskBuy data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_askbuy_detail);
        ButterKnife.bind(context);
        instance = this;
        initBack();
        initProgressDialog();

        id = getIntent().getStringExtra("id");
        getData();
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
                        llContent.setVisibility(View.VISIBLE);
                        data = GsonUtil.getEntity(jsonObj.getJSONObject("data").toString(), AskBuy.class);
                        setData();
                        addReadCount();
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
                if (isDestroyed())
                    return;

                progress.dismiss();
            }
        });
    }


    /**
     * 渲染数据
     */
    private void setData() {
        initTitle("求购" + data.getProduct_name());
        right.setImageResource(R.mipmap.icon_share);
        int state = Integer.valueOf(data.getPurchase_status());
        if (state == Const.OFFER_DEAL) {
            txtOffer.setText("已交易");
            txtOffer.setEnabled(false);
        } else {
            long expiryTime = AppTool.getTimeMs(data.getExpiry_date(), "yy-MM-dd HH:mm:ss");
            long currTime = System.currentTimeMillis();
            if (currTime < expiryTime) {
                txtOffer.setText("报价");
                if (data.getUser_id().equals(getUserID()))
                    txtOffer.setEnabled(false);
                else
                    txtOffer.setEnabled(true);
            } else {
                txtOffer.setText("报价结束");
                txtOffer.setEnabled(false);
            }
        }


        Picasso.with(context)
                .load(data.getImg())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(imgViHead);

        if (data.getPruchase_type().equals(Const.INFO_TYPE_OFFICIAL + "")) {
            txtName.setText("丝网+官方");
        } else {
            String newName = "";
            if (TextUtils.isEmpty(data.getNickname())) {
                String mobile = data.getMobile();
                newName = mobile.substring(0, 3) + "***" + mobile.substring(8, 11);
            } else {
                String nickName = data.getNickname();
                int length = nickName.length();
                switch (length) {
                    case 1:
                        newName = nickName + "***" + nickName;
                        break;
                    case 2:
                        newName = nickName.substring(0, 1) + "***" + nickName.substring(1, 2);
                        break;
                    default:
                        newName = nickName.substring(0, 1) + "***" + nickName.substring(length - 1, length);
                        break;
                }
            }

            txtName.setText(newName);
        }


        if (data.getUser_id().equals(getUserID()))
            imgViMarkSelf.setVisibility(View.VISIBLE);
        else
            imgViMarkSelf.setVisibility(View.GONE);


        int infoType = Integer.valueOf(data.getPruchase_type());
        switch (infoType) {
            case Const.INFO_TYPE_PER:
                txtMark.setText("个人");
                txtMark.setBackgroundResource(R.drawable.shape_mark_person_bg);
                break;
            case Const.INFO_TYPE_COM:
                txtMark.setText("企业");
                txtMark.setBackgroundResource(R.drawable.shape_mark_enterprise_bg);
                break;
            case Const.INFO_TYPE_OFFICIAL:
                txtMark.setText("官方");
                txtMark.setBackgroundResource(R.drawable.shape_mark_official_bg);
                break;
            default:
                break;
        }

        if (infoType != Const.INFO_TYPE_OFFICIAL) {
            String authState = data.getEnterprise_auth_status();
            if (!TextUtils.isEmpty(authState) && authState.equals("2")) {
                txtMark.setText("企业");
                txtMark.setBackgroundResource(R.drawable.shape_mark_enterprise_bg);
            } else {
                txtMark.setText("个人");
                txtMark.setBackgroundResource(R.drawable.shape_mark_person_bg);
            }
        }


        txtTime.setText(data.getCreate_time().substring(5, 16));
        txtTitle.setText("求购" + data.getProduct_name());
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

        List<AskBuyOffer> offerList = data.getOffer_list();
        if (offerList != null && offerList.size() > 0) {
            llRecord.setVisibility(View.VISIBLE);
            OfferRecordAdapter recordAdapter = new OfferRecordAdapter(context, offerList, getUserID());
            if (getUserID().equals(data.getUser_id()))
                recordAdapter.setPrivateMode(false);
            lvRecord.setAdapter(recordAdapter);

            for (AskBuyOffer offer : offerList) {
                if (getUserID().equals(offer.getUser_id())) {
                    txtOffer.setText("您已报价");
                    txtOffer.setEnabled(false);
                    break;
                }
            }
        } else {
            llRecord.setVisibility(View.GONE);
        }

    }


    /**
     * 增加浏览记录
     */
    private void addReadCount() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "consult_num");
        params.put("id", id);
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        Intent intent = new Intent(Const.ACTION_ADD_READ_NUM);
                        intent.putExtra("id", id);
                        sendBroadcast(intent);
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


    @OnClick({R.id.txtOffer, R.id.txtCall, R.id.right})
    public void onClick(View view) {
        if (data == null)
            return;

        if (!checkLogined())
            return;

        switch (view.getId()) {
            case R.id.txtOffer: //报价
                if (txtOffer.getText().toString().equals("报价")) {
                    start_Activity(context, ReleaseOfferActivity.class, new BasicNameValuePair("id", id));
                }
                break;
            case R.id.txtCall: //咨询顾问
                if (!TextUtils.isEmpty(data.getAdviser_mobile())) {
                    AppTool.dial(context, data.getAdviser_mobile());
                }
                break;
            case R.id.right: //分享
                ShareUtil.intShare(context, view, data.getPurchase_content(), "求购" + data.getProduct_name(), Const.SHARE_ASKBUY_URL + "?id=" + id);
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
