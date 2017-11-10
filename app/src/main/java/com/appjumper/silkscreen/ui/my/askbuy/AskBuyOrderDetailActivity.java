package com.appjumper.silkscreen.ui.my.askbuy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.AskBuy;
import com.appjumper.silkscreen.bean.Avatar;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.dynamic.adapter.AskBuyImageAdapter;
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
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.appjumper.silkscreen.util.Applibrary.mContext;

/**
 * 求购--选择报价
 * Created by Botx on 2017/10/19.
 */

public class AskBuyOrderDetailActivity extends BaseActivity {

    @Bind(R.id.llContent)
    LinearLayout llContent;
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

    @Bind(R.id.txtPrice)
    TextView txtPrice;
    @Bind(R.id.txtNum)
    TextView txtNum;
    @Bind(R.id.txtPayedMoney)
    TextView txtPayedMoney;
    @Bind(R.id.txtPayedType)
    TextView txtPayedType;

    @Bind(R.id.imgViAvatar)
    ImageView imgViAvatar;
    @Bind(R.id.txtAdviserName)
    TextView txtAdviserName;
    @Bind(R.id.txtCall)
    TextView txtCall;

    @Bind(R.id.llBottomBar)
    LinearLayout llBottomBar;
    @Bind(R.id.txtSurplus)
    TextView txtSurplus;

    public static AskBuyOrderDetailActivity instance = null;
    private String id;
    private AskBuy data;
    private boolean isReadMode = false; //只读模式



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_buy_order_detail);
        ButterKnife.bind(context);
        instance = this;
        initBack();
        initProgressDialog();

        id = getIntent().getStringExtra("id");
        isReadMode = getIntent().getBooleanExtra(Const.KEY_IS_READ_MODE, false);
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

        Picasso.with(mContext)
                .load(data.getImg())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(imgViHead);

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
        txtTime.setText(data.getCreate_time().substring(5, 16));
        txtContent.setText(data.getPurchase_content());

        final List<Avatar> imgList = data.getImg_list();
        if (imgList != null && imgList.size() > 0) {
            gridImg.setVisibility(View.VISIBLE);
            AskBuyImageAdapter imgAdapter = new AskBuyImageAdapter(mContext, imgList);
            gridImg.setAdapter(imgAdapter);
            gridImg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(mContext, GalleryActivity.class);
                    ArrayList<String> urls = new ArrayList<String>();
                    for (Avatar avatar : imgList) {
                        urls.add(avatar.getOrigin());
                    }
                    intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
                    intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, i);
                    mContext.startActivity(intent);
                }
            });
        } else {
            gridImg.setVisibility(View.GONE);
        }

        Picasso.with(mContext)
                .load(data.getAdviser_avatar())
                .placeholder(R.mipmap.img_error_head)
                .error(R.mipmap.img_error_head)
                .into(imgViAvatar);

        txtAdviserName.setText("报价顾问" + data.getAdviser_nicename());


        txtPrice.setText("¥" + data.getOffer_money());
        txtNum.setText(data.getPurchase_num() + data.getPurchase_unit());
        txtPayedMoney.setText("¥" + data.getPay_money());
        if (data.getPay_type().equals(Const.PAY_TYPE_ALL)) {
            llBottomBar.setVisibility(View.GONE);
            txtPayedType.setText("全额交易");
        } else {
            llBottomBar.setVisibility(View.VISIBLE);
            txtSurplus.setText(data.getSurplus_money());
            txtPayedType.setText("30%订金");
        }


        if (isReadMode)
            llBottomBar.setVisibility(View.GONE);
    }


    @OnClick({R.id.txtCall, R.id.txtConfirm})
    public void onClick(View view) {
        if (data == null)
            return;

        switch (view.getId()) {
            case R.id.txtCall:
                if (data != null && !TextUtils.isEmpty(data.getAdviser_mobile()))
                    AppTool.dial(context, data.getAdviser_mobile());
                break;
            case R.id.txtConfirm:
                Intent intent = new Intent(context, PayConfirmActivity.class);
                intent.putExtra(Const.KEY_OBJECT, data);
                intent.putExtra("pay_money", data.getSurplus_money());
                intent.putExtra("pay_type", Const.PAY_TYPE_ALL);
                intent.putExtra("purchase_num", data.getPurchase_num());
                intent.putExtra("offer_money", data.getOffer_money());
                intent.putExtra("purchase_unit", data.getPurchase_unit());
                intent.putExtra("surplus_money", "0");
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
