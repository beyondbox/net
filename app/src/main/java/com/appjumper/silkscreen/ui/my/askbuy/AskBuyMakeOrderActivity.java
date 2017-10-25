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
import com.appjumper.silkscreen.bean.AskBuyOffer;
import com.appjumper.silkscreen.bean.Avatar;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.dynamic.adapter.AskBuyImageAdapter;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.phonegridview.GalleryActivity;
import com.bigkoo.pickerview.OptionsPickerView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.appjumper.silkscreen.util.Applibrary.mContext;

/**
 * 采购--提交订单
 * Created by Botx on 2017/10/21.
 */

public class AskBuyMakeOrderActivity extends BaseActivity {

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

    @Bind(R.id.imgViAvatar)
    ImageView imgViAvatar;
    @Bind(R.id.txtAdviserName)
    TextView txtAdviserName;
    @Bind(R.id.txtPrice)
    TextView txtPrice;
    @Bind(R.id.txtNum)
    TextView txtNum;
    @Bind(R.id.txtPayedType)
    TextView txtPayedType;
    @Bind(R.id.imgViAdd)
    ImageView imgViAdd;
    @Bind(R.id.imgViMinus)
    ImageView imgViMinus;
    @Bind(R.id.txtTotal)
    TextView txtTotal;

    private String id;
    private AskBuy data;
    private AskBuyOffer offer;
    private int buyNum = 1;
    private double payMoney;

    private String payType = Const.PAY_TYPE_ALL;
    private String [] payTypeArr = {"全额付款", "30%订金"};
    private OptionsPickerView pvPayType;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_askbuy_make_order);
        ButterKnife.bind(context);
        initBack();
        initProgressDialog(false, null);
        initPickerView();

        id = getIntent().getStringExtra("id");
        offer = (AskBuyOffer) getIntent().getSerializableExtra(Const.KEY_OBJECT);
        getData();
    }


    private void initPickerView() {
        pvPayType = new OptionsPickerView.Builder(context, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                switch (options1) {
                    case 0:
                        payType = Const.PAY_TYPE_ALL;
                        break;
                    case 1:
                        payType = Const.PAY_TYPE_SUB;
                        break;
                }
                txtPayedType.setText(payTypeArr[options1]);
                calculatePayMoney();
            }
        }).build();

        pvPayType.setPicker(Arrays.asList(payTypeArr));
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

        txtPrice.setText("¥" + offer.getMoney());
        calculatePayMoney();
    }


    /**
     * 提交订单
     */
    private void makeOrder() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "order_pay");
        params.put("uid", getUserID());
        params.put("pay_money", AppTool.df.format(payMoney));
        params.put("product_id", data.getProduct_id());
        params.put("product_name", data.getProduct_name());
        params.put("purchase_id", data.getId());
        params.put("pay_type", payType);
        params.put("purchase_num", buyNum);
        params.put("offer_money", offer.getMoney());
        params.put("purchase_unit", offer.getPrice_unit().substring(2, offer.getPrice_unit().length()));

        double surplusMoney;
        if (payType.equals(Const.PAY_TYPE_ALL)) {
            surplusMoney = 0;
        } else {
            surplusMoney = Double.valueOf(offer.getMoney()) * buyNum - payMoney;
        }

        params.put("surplus_money", AppTool.df.format(surplusMoney));

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
                        if (ChooseOfferActivity.instance != null)
                            ChooseOfferActivity.instance.finish();
                        sendBroadcast(new Intent(Const.ACTION_RELEASE_SUCCESS));
                        showErrorToast("支付成功");
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
                if (isDestroyed())
                    return;

                progress.dismiss();
            }
        });
    }


    /**
     * 计算需要支付的金额
     */
    private void calculatePayMoney() {
        double price = Double.valueOf(offer.getMoney());
        double total = price * buyNum;
        if (payType.equals(Const.PAY_TYPE_ALL))
            payMoney = total;
        else
            payMoney = total * 0.3;

        payMoney = Double.valueOf(AppTool.dfRound.format(payMoney));
        txtTotal.setText("¥" + AppTool.df.format(payMoney));
    }


    @OnClick({R.id.txtCall, R.id.txtConfirm, R.id.imgViMinus, R.id.imgViAdd, R.id.txtPayedType})
    public void onClick(View view) {
        if (data == null)
            return;

        switch (view.getId()) {
            case R.id.txtCall:
                if (!TextUtils.isEmpty(data.getAdviser_mobile()))
                    AppTool.dial(context, data.getAdviser_mobile());
                break;
            case R.id.imgViAdd:
                buyNum++;
                txtNum.setText(buyNum + "");
                calculatePayMoney();
                break;
            case R.id.imgViMinus:
                if (buyNum > 1) {
                    buyNum--;
                    txtNum.setText(buyNum + "");
                }
                calculatePayMoney();
                break;
            case R.id.txtPayedType:
                pvPayType.show();
                break;
            case R.id.txtConfirm:
                makeOrder();
                break;
            default:
                break;
        }
    }

}
