package com.appjumper.silkscreen.ui.home.askbuy;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.base.MyApplication;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.appjumper.silkscreen.bean.AskBuy;
import com.appjumper.silkscreen.bean.AskBuyOffer;
import com.appjumper.silkscreen.bean.Avatar;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.home.adapter.AskBuyImageAdapter;
import com.appjumper.silkscreen.ui.home.adapter.OfferRecordAdapter;
import com.appjumper.silkscreen.ui.my.askbuy.AskBuyMakeOrderActivity;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.appjumper.silkscreen.util.ShareUtil;
import com.appjumper.silkscreen.view.SureOrCancelDialog;
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
    @Bind(R.id.txtReadNum)
    TextView txtReadNum;
    @Bind(R.id.txtOfferNum)
    TextView txtOfferNum;
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
        initProgressDialog(false, null);
        initRightButton(R.mipmap.icon_share, new RightButtonListener() {
            @Override
            public void click() {
                if (checkLogined() && data != null)
                    ShareUtil.intShare(context, right, data.getPurchase_content(), "求购" + data.getProduct_name(), Const.SHARE_ASKBUY_URL + "?id=" + id);
            }
        });

        id = getIntent().getStringExtra("id");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 80);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        id = getIntent().getStringExtra("id");
        initProgressDialog(false, null);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 80);
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
        boolean isOffering = false; //是否是报价中状态

        long expiryTime = AppTool.getTimeMs(data.getExpiry_date(), "yy-MM-dd HH:mm:ss");
        long currTime = System.currentTimeMillis();
        if (currTime < expiryTime) {
            isOffering = true;
            txtState.setText("报价中 " + data.getExpiry_date().substring(5, 16) + "截止");
            txtOffer.setText("报价");
            if (data.getUser_id().equals(getUserID()))
                txtOffer.setEnabled(false);
            else
                txtOffer.setEnabled(true);
        } else {
            txtState.setText("报价结束");
            txtOffer.setText("报价结束");
            txtOffer.setEnabled(false);
        }

        Picasso.with(context)
                .load(data.getImg())
                .resize(DisplayUtil.dip2px(context, 50), DisplayUtil.dip2px(context, 50))
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(imgViHead);

        if (data.getPruchase_type().equals(Const.INFO_TYPE_OFFICIAL + ""))
            txtName.setText("求购G" + data.getId());
        else
            txtName.setText("求购C" + data.getId());


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
        txtContent.setText(data.getPurchase_content());
        txtReadNum.setText("浏览" + "(" + data.getConsult_num() + ")");
        txtOfferNum.setText("报价" + "(" + data.getOffer_num() + ")");

        if (data.getPurchase_num().equals("0"))
            txtTitle.setText(data.getProduct_name());
        else
            txtTitle.setText(data.getProduct_name() + " " + data.getPurchase_num() + data.getPurchase_unit());


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
        if (offerList != null && offerList.size() > 0) {
            for (int i = 0; i < offerList.size(); i++) {
                AskBuyOffer offer = offerList.get(i);
                if (getUserID().equals(offer.getUser_id())) {
                    txtOffer.setText("您已报价");
                    if (getUser().getAdmin_purchase_add().equals("1"))
                        txtOffer.setEnabled(true);
                    else
                        txtOffer.setEnabled(false);
                    offerList.remove(i);
                    offerList.add(0, offer);
                    break;
                }
            }

            llRecord.setVisibility(View.VISIBLE);
            txtRecord.setText("报价记录" + "(" + offerList.size() + ")");

            OfferRecordAdapter recordAdapter = new OfferRecordAdapter(context, offerList, getUserID(), data.getUser_id(), isOffering);
            if (getUserID().equals(data.getUser_id()))
                recordAdapter.setPrivateMode(false);
            if (getUser() != null) {
                if (getUser().getAdmin_purchase().equals("1")) {
                    recordAdapter.setPrivateMode(false);
                    lvRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            showOfferInfo(offerList.get(i));
                        }
                    });
                }
            }

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
                        case R.id.txtDelete: //删除报价
                            showDeleteDialog(offerList.get(position).getId());
                            break;
                    }
                }
            });

            lvRecord.setAdapter(recordAdapter);

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


    /**
     * 删除报价对话框
     */
    private void showDeleteDialog(final String id) {
        new SureOrCancelDialog(context, "提示", "确定要删除您的报价记录吗？", "确定", "取消", new SureOrCancelDialog.SureButtonClick() {
            @Override
            public void onSureButtonClick() {
                deleteOffer(id);
            }
        }).show();
    }


    /**
     * 删除我的报价记录
     */
    private void deleteOffer(String id) {
        RequestParams params = MyHttpClient.getApiParam("purchase", "drop_purchase_offer");
        params.put("uid", getUserID());
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
                        showErrorToast("删除成功");
                        getData();
                    } else {
                        showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
                        progress.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progress.dismiss();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
                progress.dismiss();
            }
        });
    }


    /**
     * 显示报价信息
     */
    private void showOfferInfo(AskBuyOffer offer) {
        Dialog dialog = new Dialog(context, R.style.CustomDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_askbuy_offer_info, null);
        dialog.setContentView(view);

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.8);
        dialog.getWindow().setAttributes(params);

        TextView txtMobile = (TextView) view.findViewById(R.id.txtMobile);
        TextView txtRemark = (TextView) view.findViewById(R.id.txtRemark);

        txtMobile.setText(offer.getOffer_user_mobile());
        if (TextUtils.isEmpty(offer.getOffer_content()))
            txtRemark.setText("无");
        else
            txtRemark.setText(offer.getOffer_content());

        dialog.show();
    }


    @OnClick({R.id.txtOffer, R.id.txtCall, R.id.imgViHead})
    public void onClick(View view) {
        if (data == null)
            return;

        if (!checkLogined())
            return;

        switch (view.getId()) {
            case R.id.txtOffer: //报价
                if (txtOffer.isEnabled()) {
                    if (!MyApplication.appContext.checkMobile(context)) return;
                    Intent intent = new Intent(context, ReleaseOfferActivity.class);
                    intent.putExtra(Const.KEY_OBJECT, data);
                    startActivity(intent);
                }
                break;
            case R.id.txtCall: //咨询顾问
                if (!TextUtils.isEmpty(data.getAdviser_mobile())) {
                    AppTool.dial(context, data.getAdviser_mobile());
                }
                break;
            case R.id.imgViHead:
                Intent intent = new Intent(context, GalleryActivity.class);
                ArrayList<String> urls = new ArrayList<String>();
                urls.add(data.getImg());
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, 0);
                startActivity(intent);
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
