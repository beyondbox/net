package com.appjumper.silkscreen.ui.my.deliver;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.Freight;
import com.appjumper.silkscreen.bean.FreightOffer;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.my.adapter.TransportListAdapter;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 运输完成--发货厂家
 * Created by Botx on 2017/10/28.
 */

public class TransportFinishDeliverActivity extends BaseActivity {

    @Bind(R.id.llContent)
    LinearLayout llContent;

    @Bind(R.id.txtTitle)
    TextView txtTitle;
    @Bind(R.id.txtName)
    TextView txtName;
    @Bind(R.id.txtTime)
    TextView txtTime;
    @Bind(R.id.txtState)
    TextView txtState;
    @Bind(R.id.txtOrderId)
    TextView txtOrderId;
    @Bind(R.id.txtCarNum)
    TextView txtCarNum;
    @Bind(R.id.txtCarModel)
    TextView txtCarModel;
    @Bind(R.id.txtProduct)
    TextView txtProduct;
    @Bind(R.id.txtLoadTime)
    TextView txtLoadTime;
    @Bind(R.id.txtPayedType)
    TextView txtPayedType;

    @Bind(R.id.txtPayState)
    TextView txtPayState;
    @Bind(R.id.txtDriverName)
    TextView txtDriverName;
    @Bind(R.id.txtDriverPrice)
    TextView txtDriverPrice;
    @Bind(R.id.txtDriverTime)
    TextView txtDriverTime;

    @Bind(R.id.txtPremium)
    TextView txtPremium;

    @Bind(R.id.btn1)
    TextView btn1;


    private String id;
    private Freight data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_finish_deliver);
        ButterKnife.bind(context);
        initTitle("详情");
        initBack();
        initProgressDialog(false, null);

        id = getIntent().getStringExtra("id");
        getData();
    }



    /**
     * 获取数据
     */
    private void getData() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "details_car_product");
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
                        data = GsonUtil.getEntity(jsonObj.getJSONObject("data").toString(), Freight.class);
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
        txtTitle.setText(data.getFrom_name() + " - " + data.getTo_name());
        txtTime.setText(data.getCreate_time().substring(5, 16));
        txtOrderId.setText("订单编号 : " + data.getOrder_id());
        txtCarNum.setText("已发车" + data.getDepart_num() + "次");
        txtCarModel.setText(data.getLengths_name() + "/" + data.getModels_name());
        txtProduct.setText(data.getWeight() + data.getProduct_name());
        txtLoadTime.setText(data.getExpiry_date().substring(5, 16) + "装车");

        txtState.setText("运输完成");

        String uid = data.getUser_id();
        String newName = "";
        switch (uid.length()) {
            case 1:
                newName = "发货厂家000" + uid;
                break;
            case 2:
                newName = "发货厂家00" + uid;
                break;
            case 3:
                newName = "发货厂家0" + uid;
                break;
            case 4:
                newName = "发货厂家" + uid;
                break;
            default:
                newName = "发货厂家" + uid.substring(0, 1) + uid.substring(uid.length() - 2, uid.length());
                break;
        }
        txtName.setText("来自 : " + newName);


        if (data.getPay_type().equals("0"))
            txtPayedType.setText("发货厂家支付运费");
        else
            txtPayedType.setText("货主支付运费");


        txtPremium.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtPremium.getPaint().setAntiAlias(true);


        final List<FreightOffer> offerList = data.getOffer_list();
        String selectedDriverId = data.getConfirm_driver_id();
        FreightOffer selectedOffer = null;
        for (FreightOffer offer : offerList) {
            if (offer.getUser_id().equals(selectedDriverId)) {
                selectedOffer = offer;
                break;
            }
        }

        txtDriverName.setText(selectedOffer.getName().substring(0, 1) + "司机");
        txtDriverPrice.setText("运费" + selectedOffer.getMoney() + selectedOffer.getMoney_unit());
        txtDriverTime.setText(selectedOffer.getCreate_time().substring(5, 16));
        txtPayState.setText("已支付信息费、保险费");
        txtPayState.setTextColor(getResources().getColor(R.color.green_color));

        ListView lvTransport = (ListView) findViewById(R.id.lvTransport);
        lvTransport.setFocusable(false);
        TransportListAdapter transportAdapter = new TransportListAdapter(context, data.getTransport_list());
        lvTransport.setAdapter(transportAdapter);
    }


    @OnClick({R.id.txtCall, R.id.btn0, R.id.btn1})
    public void onClick(View view) {
        if (data == null)
            return;

        switch (view.getId()) {
            case R.id.txtCall: //联系客服
                AppTool.dial(context, Const.SERVICE_PHONE_FREIGHT);
                break;
            case R.id.btn0: //联系司机
                AppTool.dial(context, data.getConfirm_driver_mobile());
                break;
            case R.id.btn1: //支付运费
                btn1.setText("等待司机确认收款");
                btn1.setEnabled(false);
                break;
            default:
                break;
        }
    }

}
